package Processador.MaquinaVirtual;

public class Register {
    public int value;
    protected int tam = 24;

    public Register(int value, int tam) {
        this.value = value;
        this.tam = tam;
    }

    @Override
    public String toString() {
        return "Register{" +
                "value=" + value +
                ", tam=" + tam +
                '}';
    }
}
