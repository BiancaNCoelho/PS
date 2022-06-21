package Processador.MaquinaVirtual;


import java.util.ArrayList;

public class Registers {
    private ArrayList<Register> registers = new ArrayList<>();
    public Registers() {
        for (int i = 0; i < 8; i++) {
                registers.add(new Register(0, 24));
        }
    }
    
    public int getRegValue(int i) {
        return registers.get(i).value;
    }

    public void setRegValue(int value, int pos) {
        registers.get(pos).value = value;
    }

    public void resetRegisters(){
        for(int i=0;i<8;i++){
            setRegValue(0,i);
        }
    }

    public void show_registers(){
        for (int i = 0; i < 8; i++) {

            int v = getRegValue(i);
            System.out.println("Pos: " + i + " Valor: " + v);
        }
    }
    public void inccr_PC(){
        int incr = getRegValue(6) + 1;
        setRegValue(incr, 6);
    }

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        for (Register r : registers) {
            retVal.append(r.toString()).append("\n");
        }
        return retVal.toString();
    }
}
