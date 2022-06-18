package Processador.Montador;
import java.util.ArrayList;

public class ListingObject {
    

    public int startingAddress;
    private int length;
    public int endAddress;
    public ArrayList<String> TextRecord;
    
    public void set_length(){
        this.length = startingAddress - endAddress;
    }
    public int get_length(){
        return this.length;
    }
}
