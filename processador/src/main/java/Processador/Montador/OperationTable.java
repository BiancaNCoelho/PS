package Processador.Montador;
import java.util.HashMap;

public class OperationTable{

    public HashMap<String,Mnemonico> OPTAB = new HashMap<>();
    private Mnemonico add = new Mnemonico(0x18,3);
    private Mnemonico addr = new Mnemonico(0x90,2);
    private Mnemonico clear = new Mnemonico(0xB4,2);
    private Mnemonico and = new Mnemonico(0x40,3);
    private Mnemonico comp = new Mnemonico(0x28,3);
    private Mnemonico compr = new Mnemonico(0xA0,2);
    private Mnemonico div = new Mnemonico(0x24,3);
    private Mnemonico divr = new Mnemonico(0x9C,2);
    private Mnemonico j = new Mnemonico(0x3C,3);
    private Mnemonico jeq = new Mnemonico(0x30,3);
    private Mnemonico jgt = new Mnemonico(0x34,3);
    private Mnemonico jlt = new Mnemonico(0x38,3);
    private Mnemonico jsub = new Mnemonico(0x48,3);
    private Mnemonico stl = new Mnemonico(0x14,3);
    private Mnemonico ldb = new Mnemonico(0x68,3);
    private Mnemonico lda = new Mnemonico(0x00,3);
    private Mnemonico lds = new Mnemonico(0x6C,3);
    private Mnemonico sta = new Mnemonico(0x0C,3);
    private Mnemonico ldch = new Mnemonico(0x50, 3);
    private Mnemonico ldl = new Mnemonico(0x08, 3);
    private Mnemonico ldt = new Mnemonico(0x74, 3);
    private Mnemonico ldx = new Mnemonico(0x04, 3);
    private Mnemonico mul = new Mnemonico(0x20, 3);
    private Mnemonico mulr = new Mnemonico(0x98, 2);
    private Mnemonico or = new Mnemonico(0x44, 3);
    private Mnemonico rmo = new Mnemonico(0xAC, 2);
    private Mnemonico rsub = new Mnemonico(0x4c, 3);
    private Mnemonico shiftl = new Mnemonico(0xA4, 2);
    private Mnemonico shiftr = new Mnemonico(0xA8, 2);
    private Mnemonico stb = new Mnemonico(0x78, 3);
    private Mnemonico stch = new Mnemonico(0x54, 3);
    private Mnemonico sts = new Mnemonico(0x7C, 3);
    private Mnemonico stt = new Mnemonico(0x84, 3);
    private Mnemonico stx = new Mnemonico(0x10, 3);
    private Mnemonico sub = new Mnemonico(0x1C, 3);
    private Mnemonico subr = new Mnemonico(0x94, 2);
    //falta tixr e tix




    // ADICIONAR APARTIR DE LDA

    public OperationTable(){
        this.OPTAB.put("ADD", add);
        this.OPTAB.put("ADDR",addr);
        this.OPTAB.put("CLEAR",clear);
        this.OPTAB.put("AND", and);
        this.OPTAB.put("COMP", comp);
        this.OPTAB.put("COMPR", compr);
        this.OPTAB.put("DIV",div);
        this.OPTAB.put("DIVR", divr);
        this.OPTAB.put("J", j);
        this.OPTAB.put("JEQ",jeq);
        this.OPTAB.put("JGT", jgt);
        this.OPTAB.put("JLT", jlt);
        this.OPTAB.put("JSUB", jsub);
        this.OPTAB.put("STL", stl);
        this.OPTAB.put("LDB", ldb);
        this.OPTAB.put("LDA", lda);
        this.OPTAB.put("LDS", lds);
        this.OPTAB.put("STA", sta);
        this.OPTAB.put("LDCH", ldch);
        this.OPTAB.put("LDL", ldl);
        this.OPTAB.put("LDT", ldt);
        this.OPTAB.put("LDX", ldx);
        this.OPTAB.put("MUL", mul);
        this.OPTAB.put("MULR", mulr);
        this.OPTAB.put("OR", or);
        this.OPTAB.put("RMO", rmo);
        this.OPTAB.put("RSUB", rsub);
        this.OPTAB.put("SHIFTL", shiftl);
        this.OPTAB.put("SHIFTR", shiftr);
        this.OPTAB.put("STB", stb);
        this.OPTAB.put("STCH", stch);
        this.OPTAB.put("STS", sts);
        this.OPTAB.put("STT", stt);
        this.OPTAB.put("STX", stx);
        this.OPTAB.put("SUB", sub);
        this.OPTAB.put("SUBR", subr);
    }
}
