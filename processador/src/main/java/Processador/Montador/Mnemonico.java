package Processador.Montador;
/**
 * Mnemonico
 */
public class Mnemonico {

    public int OPCODE;
    public int length;
    Mnemonico(int instruction_code, int instruction_length)
    {
        OPCODE = instruction_code;
        length = instruction_length;
    }

    public int get_OPCODE(){
        return OPCODE;
    }
    public int get_length(){
        return length;
    }
}