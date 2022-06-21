package Processador.Montador;
import Processador.MaquinaVirtual.*;
import java.util.ArrayList;

public class Loader {
    
    Registers reg;
    
    //NAO FUI EU QUEM ESCREVI ESSE MÉTODO, CÓDIGO DO STACKOVERFLOW
    //https://stackoverflow.com/a/140861
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public void loadObjToMachine(Maquina sic, ListingObject listing_program){

        //Setting Start address
        int start = listing_program.startingAddress;
        Memoria ram = sic.getMemory();
        reg = sic.getRegisters();
        reg.setRegValue(start, 6);
        
        //System.out.println(machine.registers.getRegValue(6)[0]);
        ArrayList<String> hex_obj = listing_program.TextRecord;
        int addr = start;
        for(int i=0;i<hex_obj.size();i++){
            //converte cada String do text record para um byte array
            byte[] bcode = hexStringToByteArray(hex_obj.get(i));
            //junta todos os bytes arrays em uma única memoria
            for(int j=0;j<bcode.length;j++){
                ram.set_raw_byte(addr, bcode[j]);
                addr++;
            }
        }
        int instruction_start = listing_program.startingInstruction;
        reg.setRegValue(instruction_start, 6);
    }
    
}
