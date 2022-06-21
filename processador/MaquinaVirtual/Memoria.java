package Processador.MaquinaVirtual;

import java.util.Arrays;
public class Memoria {
    
    public final byte[] memoria;

    //capacidade == 1KB por exmeplo == 1k entradas na memoria
    public Memoria(int capacidade){
        this.memoria = new byte[capacidade];
    }

    //Zera todas as posicoes da memoria
    public void reset(){

        Arrays.fill(memoria,(byte)0);
    }

    public int getByte(int addr){
        return ((int)memoria[addr]) & 0xFF;
    }
    public void setByte(int addr, int value){
        memoria[addr] = (byte)(value & 0xFF);
    }
    
    public void set_raw_byte(int addr,byte b){
        memoria[addr] = b;
    }
    public void setword(int addr, int value){
        //Coloca o byte mais significativo em addr e menos signf no addr+2
        setByte(addr, value >> 16);
        setByte(addr + 1, value >> 8);
        setByte(addr + 2, value);
    }
    public int getWord(int addr){
        int MSB = getByte(addr) <<16;
        int MID = getByte(addr + 1) <<8;
        int LSB = getByte(addr + 2);
        // Ex: 0xE10000 + 0xE100 + 0xE1 = 0xE1E1E1
        return MSB + MID + LSB;
    }

}
