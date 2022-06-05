import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
public class processadorMacros {

    //*****  BUFFER DE ENTRADA/SAIDA*****/
    private List<String> lines  = Collections.emptyList(); // Código de entrada
    public ArrayList<String> codigo_expandido = new ArrayList<>(); // Código de saída
    
    //*****  ITERADORES  *****/
    private int line_index = -1;
    private int deftab_index= 0;
    

    /*****   Utilidades *****/
    String[] ct_linhas;
    private boolean Expanding = false;
    private String OPCODE;
    private String source_line;
    int level = 0;
    
    /*
    -
    -   MACRO PREPROCESSOR TABLES
    */
    private HashMap<String,int[]> NAMTAB = new HashMap<>();
    private ArrayList<String> DEFTAB = new ArrayList<>();
    private ArrayList<String> ARGTAB = new ArrayList<>();


    public void set_OPCODE(int i){
        
        //Verifica se a linha é comentário, pois linhas com comentário
        //não possuem OPCODE
        ct_linhas = lines.get(i).split(" ");
        if(ct_linhas[0].equals(".")){
            OPCODE = ".";
        }
        else{
            if(ct_linhas.length <3){
                OPCODE = ct_linhas[0];
            }
            else{
                OPCODE = ct_linhas[1];
            }
        }
        
    }
    public void set_OPCODE_from_DEFTAB(int i){
        ct_linhas = DEFTAB.get(i).split(" "); //lines.get(i).split(" ");
        if(ct_linhas.length <3){
            OPCODE = ct_linhas[0];
        }
        else{
            OPCODE = ct_linhas[1];
        }
    }
    public void substitute_arguments(){
        String temporary_line = DEFTAB.get(deftab_index);
        for(int i=0; i < ARGTAB.size();i++){
            if(temporary_line.contains("?"+i)){
                String replaced = temporary_line.replaceAll("\\?"+i,ARGTAB.get(i));
                temporary_line = replaced;
                
            }


        }
        source_line = temporary_line;
    }
    public void adiciona_rotulo(){
        String[] Rotulo = lines.get(line_index).split(" ");
        if(Rotulo.length >2){
            source_line = Rotulo[0] + " " + source_line;
        }

        
    }
    public void getLine(){
        if(Expanding){

            set_OPCODE_from_DEFTAB(deftab_index);
            //get next line of macro definition from DEFTAB
            //substitute arguments from ARGTAB for positional notation
            substitute_arguments();
            //if(deftab_index == (positions[0]+1)){
            //    adiciona_rotulo();
            //} talvez adicionar uma variavel global e referenciar positions resolva
            //source_line = DEFTAB.get(deftab_index);  (linha para ser removida)
            deftab_index +=1;
        }
        else{
            line_index +=1; // nao mecher nesse indice senao estraga todo código
            set_OPCODE(line_index);
            source_line = lines.get(line_index);
            
        }
    }
    public void setup_ARGTAB(){
        String[] arguments;
        if(ct_linhas.length < 3 ){
            arguments = ct_linhas[1].split(",");
        }
        else{
            arguments = ct_linhas[2].split(",");
        }
        ARGTAB.clear();
        for(int i =0; i<arguments.length;i++){
            ARGTAB.add(arguments[i]);
        }
    }
    public void expand2(){
        Expanding = true;
        int[] positions;
        positions = NAMTAB.get(OPCODE);
        setup_ARGTAB();
        codigo_expandido.add("."+lines.get(line_index));
        deftab_index = positions[0] + 1;
        while(deftab_index != positions[1]){
            getLine();
            processline(); 
        }
        Expanding = false;
    }
    public void expand(){
        Expanding = true;
        level +=1;
        int[] positions;
        positions = NAMTAB.get(OPCODE);
        //String first_line = DEFTAB.get(positions[0]);
        setup_ARGTAB();
        codigo_expandido.add("."+lines.get(line_index)); //adiciona o prototipo como um comentario no codigo expandido
        //salva o ultimo indice do deftab
        //int old_deftab_index = deftab_index;
        deftab_index = positions[0] + 1;
        //deftab_index = positions[0]+1;
        while(deftab_index != positions[1]){
            getLine();
            processline(); 
        }
        //deftab_index = old_deftab_index;
        level -=1;
        if(level ==0){Expanding = false;}
    }
    public void define(){
        String macro_name = ct_linhas[0];
        int ponteiros[] = new int[2];
        ponteiros[0] = deftab_index;
        
        DEFTAB.add(ct_linhas[0] + " " + ct_linhas[2]);
        deftab_index +=1;
        String[] parameters = ct_linhas[2].split(",");
        //int level = 1;

        while( !OPCODE.equals("MEND")){
            getLine();
            // se a linha nao for um comentario
            if( lines.get(line_index).charAt(0) != '.'){
                for(int i=0; i < parameters.length;i++){

                    if(lines.get(line_index).contains(parameters[i]))
                    {
                        String replaced = lines.get(line_index).replaceAll(parameters[i], "?"+i);
                        lines.set(line_index, replaced);
                    }   
                }
                
                if(OPCODE.equals("MACRO")){
                    define();
                    getLine();
                }
                DEFTAB.add(lines.get(line_index));
                deftab_index +=1;
                
            }
            
        }
        ponteiros[1] = deftab_index-1;
        NAMTAB.put(macro_name,ponteiros);
        
    }
    public void processline(){
        if((NAMTAB.containsKey(OPCODE) && Expanding == false)){
            expand2();
        }
        else if(OPCODE.equals("MACRO")){
            define();

        }
        else{

            codigo_expandido.add(source_line);

        }
    }
    public void processar_macros(){
        set_OPCODE(0);

        while(!OPCODE.equals("END")){
            getLine();
            processline();
        }

    }
    public processadorMacros(String File_name){
        try{
            this.lines = Files.readAllLines(Paths.get(File_name),StandardCharsets.UTF_8);
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        processadorMacros macro = new processadorMacros("./jojov2.txt");
//        //System.out.println(macro.lines);
//        macro.processar_macros();
//        try {
//            Files.write(Paths.get("expandedversao2.txt"), macro.codigo_expandido);
//        } catch (IOException e) {
//            System.out.println("Error");
//        }
//    }
}