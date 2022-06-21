package Processador.Montador;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
public class Montador {
    
        /// Código de entrada
    private ArrayList<String> lines; 
    private HashMap<String,Symbols> SYMTAB = new HashMap<>(); //symtab
    private OperationTable sicxe_table = new OperationTable();
    private HashMap<String,Mnemonico> OPTAB = sicxe_table.OPTAB;
    public ListingObject listing_output = new ListingObject();
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);


    //  INCLUI OS REGISTRADOR NA TABELA DE SIMBOLOS DO MONTADOR
    public void include_registers(){

        Symbols regA = new Symbols(0, 2);
        Symbols regX = new Symbols(1,2);
        Symbols regL = new Symbols(2,2);
        Symbols regB = new Symbols(3,2);
        Symbols regS = new Symbols(4,2);
        Symbols regT = new Symbols(5,2);
        Symbols regPC = new Symbols(6,2);
        Symbols regSW = new Symbols(7,2);
        SYMTAB.put("A", regA);
        SYMTAB.put("X", regX);
        SYMTAB.put("L", regL);
        SYMTAB.put("B", regB);
        SYMTAB.put("S", regS);
        SYMTAB.put("T", regT);
        SYMTAB.put("PC", regPC);
        SYMTAB.put("SW", regSW);
    }
    public String montarF2(ParserLine line){
        //Busca o código de operação
        String operation_code = String.format("%1$02X", OPTAB.get(line.opcode).OPCODE& 0xFF);
        String operando1 = line.operands[0];
        String operando2 = line.operands[1];
        //GUARDA ENDEREÇO DE R1 E R2
        String r1= "0";
        String r2= "0";
        
        //GUARDA OBJCODE
        String object_code;
        String prefixo = line.prefix;
        //Usando Endereço númerico do Registrador
        if(prefixo.equals("#")){
            //trata do primeiro operando
            try {
                r1 = operando1;
                
            } catch (NumberFormatException e) {
                System.out.println("O endereço passsado não pode ser convertido para constante do tipo inteiro");
            }
            //trata o 2 operando se existir
            //retorna o segundo operando se ele existir
            if(!operando2.isEmpty()){
                try {
                    r2 = operando2;
                    
                } catch (NumberFormatException e) {
                    System.out.println("O endereço passado não pode ser convertido para constante do tipo inteiro");
                }
            }
        }
        //se o primeiro operando não for uma constante númerica
        else{
            if(SYMTAB.containsKey(operando1)){
                r1 = String.format("%1$01X", SYMTAB.get(operando1).address & 0xF);
                if(SYMTAB.containsKey(operando2)){
                    r2 = String.format("%1$01X", SYMTAB.get(operando2).address & 0xF);
                }
            }
            else{
                System.out.println("simbolo não definido");
            }
        }
        object_code = operation_code + r1+r2;
        return object_code;
    }
    public String montarF3F4(ParserLine line,boolean base,int PC){
        int ni =0;
        int opcode = OPTAB.get(line.opcode).OPCODE;
        int operand;
        int disp=0;
        int xbpe;
        int obj;
        String prefixo = line.prefix;
        
        //setting n.i flags
        if(prefixo.isEmpty()){
            ni = 0x03;
        }
        else if(prefixo.equals("#")){
            ni = 0x01;
        }
        else if(prefixo.equals("@")){
            ni = 0x02;
        }
        else{
            System.out.println("Erro desconhecido - Prefixo inválido");
        }
        //setting xbpe and displacement
        //Caso o operando é UMA CONSTANTE NÃO ROTULADA
        if(!SYMTAB.containsKey(line.operands[0])){
            try {
                disp = Integer.parseInt(line.operands[0]);
                
            } catch (NumberFormatException e) {
                System.out.println("Input String cannot be parsed to Integer.");
            }
            xbpe =0;
            obj = ((opcode & 0xFC) <<16) + (ni<< 16) + (xbpe << 12)+ disp;
        }
        //formato extendido
        else if(line.extended == true){
            operand = SYMTAB.get(line.operands[0]).address;
            xbpe = 0x01;
            disp = operand;
            obj = ((opcode & 0xFC) <<24) + (ni<< 24) + (xbpe << 20)+ disp;
        }
        //formato 3
        else{
            //formato utilizando B + X + DISP
            if(base){
                operand = SYMTAB.get(line.operands[0]).address;
                if(line.operands[1].equals("X")){
                    disp = operand - SYMTAB.get("B").address + SYMTAB.get("X").address;
                    xbpe = 0xC;
                }
            //formato utilizado B + DISP
                else{
                    disp = operand - SYMTAB.get("B").address;
                    xbpe = 0x4;
                }
            }
            // formato usando (PC) relative displacement
            else{
                operand = SYMTAB.get(line.operands[0]).address;
                if(line.operands[1].equals("X")){
                    disp = operand - PC + SYMTAB.get("X").address;
                    xbpe = 0xA;
                }
                else{
                    disp = operand - PC;
                    xbpe = 0x2;
                }
                
            }
            String string_disp;
            if(disp <0){
                string_disp = String.format("%1$01X", disp & 0xFFF);
            }
            else{
                string_disp = String.format("%1$03X", disp & 0xFFF);
            }
            String string_xbpe = String.format("%1$01X", xbpe & 0xF);
            String hexAddress = string_xbpe + string_disp;
            String firstByte = String.format("%1$02X", (opcode + ni) & 0xFF);
            String code = firstByte + hexAddress;
            return code;
        }
        String hexAddress = String.format("%1$04X",obj & 0xFFFF);
        //String.format("%1$02X",obj);
        String firstByte = String.format("%1$02X", (opcode + ni) & 0xFF);
        String code = firstByte + hexAddress;
        return code;
    }
    

        public void Montar(){
        /**
         * *************
         * **************
         * INICIO DO PASSO 1
         * **************
         * *************
         */
        int lc = 0;
        int LOCCTR = 0;
        ArrayList<ParserLine> medfile = new ArrayList<>();
        include_registers(); // inclui os registrador na tabela de símbolos
        ParserLine line = new ParserLine();
        line.parser(lines.get(lc));
        if(line.opcode.equals("START")){
            //save #operands as starting address
            LOCCTR = Integer.parseInt(line.operands[0]);
            medfile.add(line); //alterar
            lc +=1; //lê a próxima linha
            line = new ParserLine();
            line.parser(lines.get(lc));
        }
        else{
            LOCCTR = 0;
        }
        listing_output.startingAddress = LOCCTR;
        while(!(line.opcode.equals("END"))){
            if(!(line.label.isEmpty())){ //se existir um label na linha
                
                if(SYMTAB.containsKey(line.label)){
                    System.out.println("Erro de multiplo definição");// adiciona o erro de multiple defined
                    return;
                }
                //adiciona esse label na tabela de simbolos
                    Symbols symbol_object = new Symbols(LOCCTR);
                    SYMTAB.put(line.label,symbol_object);
            }
            //busca na tabela de operações pelo opcode da linha
            if(OPTAB.containsKey(line.opcode)){
                //verifica se o tipo de instrução é f2
                if(OPTAB.get(line.opcode).length == 2){
                    //instr tem 2 bytes
                    LOCCTR += 2;
                    line.set_tamanho_instr(2);
                }
                //Se a instrução for formato 3,4 verificar prefixo do operando
                else if(OPTAB.get(line.opcode).length == 3){
                    //formato extendido -> locctr = 4
                    if(line.extended){
                        LOCCTR += 4;
                        line.set_tamanho_instr(4);
                    }
                    //formato 3 -> locctr = 3
                    else{
                        LOCCTR +=3;
                        line.set_tamanho_instr(3);
                    }
                }
            }
            else if(line.opcode.equals("WORD")){
                LOCCTR +=3;
                line.set_tamanho_instr(3);
            }
            else if(line.opcode.equals("RESW")){
                int aux = Integer.parseInt(line.operands[0]); 
                LOCCTR = LOCCTR + (3*aux);
                line.set_tamanho_instr(3*aux);
            }
            else if(line.opcode.equals("RESB")){
                int aux = Integer.parseInt(line.operands[0]); 
                LOCCTR += aux;
                line.set_tamanho_instr(aux);
            }
            else if(line.opcode.equals("BYTE")){
                //reserva espaço para uma constante ou caractere
                //foi improvisado o valor de 1 byte para representar caracteres
                LOCCTR += 3; // alteração provisoria
                line.set_tamanho_instr(3);
            }
            else if(line.opcode.equals("BASE") || line.opcode.equals("NOBASE")){
                //setar flag de utilizar registrador base
                line.set_tamanho_instr(0);
            }
            else{
                //SET FLAG ERROR (INVALID OPERATION CODE)
                System.out.println("operation code invalid!");
                return;
            }
            medfile.add(line);
            line = new ParserLine();
            lc +=1;
            line.parser(lines.get(lc));
        }
        medfile.add(line);
        System.out.println("checkpoint");
        /**
         * *************
         * **************
         * INICIO DO PASSO 2
         * **************
         * *************
         */
        lc =0;
        String obj;
        boolean base_relative = false;
        ArrayList<String> machine_code = new ArrayList<>();
        if(medfile.get(lc).opcode.equals("START")){
            LOCCTR = Integer.parseInt(medfile.get(lc).operands[0]);
            lc +=1;
        }
        else{
            LOCCTR = 0;
        }
        while(!medfile.get(lc).opcode.equals("END")){
            //SE ENCONTRAR O OPCODE NA TABELA DE INSTRUÇÕES
            if(OPTAB.containsKey(medfile.get(lc).opcode)){
                if(medfile.get(lc).tamanho_instr == 2){
                    //MONTANDO UMA INSTRUÇÃO FORMATO 2
                    //OBJ_CODE = 1 BYTE OPCODE + 1/2 BYTE R1 + 1/2 R2
                    LOCCTR += 2;
                    obj = montarF2(medfile.get(lc));
                    machine_code.add(obj);
                }
                //MONTANDO UMA INSTRUÇÃO FORMATO 3/4
                else if(medfile.get(lc).tamanho_instr > 2){
                    if(medfile.get(lc).extended){
                        LOCCTR +=4;
                    }
                    else{
                        LOCCTR +=3;
                    }
                    obj = montarF3F4(medfile.get(lc),base_relative,LOCCTR);
                    machine_code.add(obj);
                }
            }
            else if(medfile.get(lc).opcode.equals("BASE")){
                base_relative = true;
            }
            else if(medfile.get(lc).opcode.equals("NOBASE")){
                base_relative = false;
            }
            else if(medfile.get(lc).opcode.equals("BYTE")){
                LOCCTR +=1;
                char c = medfile.get(lc).operands[0].charAt(0);
                obj = c+"";
                machine_code.add(obj);
                
            }
            else if(medfile.get(lc).opcode.equals("WORD")){
                LOCCTR +=3;
                int word = Integer.parseInt(medfile.get(lc).operands[0]);
                obj = String.format("%1$06X",word & 0xFFFFFF);
                machine_code.add(obj);
            }
            else if(medfile.get(lc).opcode.equals("RESW")){
                LOCCTR += medfile.get(lc).tamanho_instr;
                int numero_palavras = (medfile.get(lc).tamanho_instr)/3;
                for(int i=0; i < numero_palavras;i++){
                    obj = String.format("%1$06X",0x0 & 0xFFFFFF);
                    machine_code.add(obj);
                }
            }
            lc+=1;
        }
        listing_output.endAddress = LOCCTR;
        listing_output.startingInstruction = SYMTAB.get(medfile.get(lc).operands[0]).address;
        listing_output.set_length();
        listing_output.TextRecord = machine_code;
        System.out.println("checkpoint2");
        String Hex;
        for(int i=0; i<listing_output.TextRecord.size();i++){
            Hex = listing_output.TextRecord.get(i);
            System.out.println("Valor Instr: "+ Hex);
        }
    }

    public Montador(String fileNameInput){
        lines = new ArrayList<>();
        try {
            File arquivo = new File(fileNameInput);
            Scanner reader = new Scanner(arquivo);
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
            reader.close();

        } catch (IOException e) {
            System.out.println("Não leu o arquivo ;-;");
        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
    
}
