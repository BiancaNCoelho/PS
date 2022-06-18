package Processador.Montador;
public class ParserLine {
    
    public String label;
    public String opcode;
    public String[] operands = new String[2];
    public String prefix;
    public boolean extended;
    public boolean constant = false;
    public int tamanho_instr;


    
    
    
    public void parser(String Line){
        String[] loo = Line.split(" ");
        if(loo.length <3){
            label = "";
            opcode = loo[0];
            String[] aux = loo[1].split(",");
            if(aux.length >1){
                operands[1] = aux[1];
            }
            else{
                operands[1] = "";
            }
            operands[0] = aux[0];
        }
        else{
            label = loo[0];
            opcode = loo[1];
            String[] aux = loo[2].split(",");
            if(aux.length >1){
                operands[1] = aux[1];
            }
            else{
                operands[1] = "";
            }
            operands[0] = aux[0];
        }
        //remover prefix if exists
        if(operands[0].contains("#")){
            prefix = "#";
            StringBuilder sb = new StringBuilder(operands[0]); 
            sb.deleteCharAt(0);
            operands[0] = sb.toString();

        }
        else if(operands[0].contains("@")){
            prefix = "@";
            StringBuilder sb = new StringBuilder(operands[0]); 
            sb.deleteCharAt(0);
            operands[0] = sb.toString();
        }
        else{
            prefix = "";
        }
        //remove prefix from instruction
        if(opcode.contains("+")){
            extended = true;
            StringBuilder sb = new StringBuilder(opcode); 
            sb.deleteCharAt(0);
            opcode = sb.toString();
        }

    }
    public void set_tamanho_instr(int LOCCTR){
        tamanho_instr = LOCCTR;
    }
}
