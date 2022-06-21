package Processador.Montador;

public class Symbols {
    
    public int address; //endereço
    public boolean verified = false;
    public int fm; //format of instruction
    public int tam; //size of struction
    public Symbols(int addr,int tamanho_instr){
        address = addr;
        verified = true;
        //fm = formato;
        tam = tamanho_instr;

    }
    public Symbols(int addr){
        address = addr;
        verified = true;
    }   
}
