/**
 *
 * @author luan_
 */

public class Maquina {
    
    //Indice de registradores
    private static final int A = 0,
            X = 1,
            L = 2,
            B = 3,
            S = 4,
            T = 5,
            PC = 6,
            SW = 7;
    
    //Bit masks
    private static final int OPCODE_MASK = 0xFC;
    private static final int NI_MASK = 0x03;
    private static final int XBPE_MASK = 0xF0;
    private static final int X_MASK = 0x80;
    Registers registers;
    Memoria memory;
    public Maquina() {
        registers = new Registers();
        memory = new Memoria(1048576); // 1megabyte de memória
    }
    public boolean loadF2(int opcode,int operands) {

        int eR1 = (operands & 0xF0) >> 4; // op1 pega os 4 bits mais significativos do byte
        int eR2 = operands & 0x0F; //op2 pega os 4 bits menos significativos do byte
        int vR1 = -1;
        int vR2 = -1;
        switch (opcode) {
            case 0x90:
                //AddR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR1 + vR2, eR2); // r2 -> r1 + r2;
                break;
            case 0xB4:
                //Clear
                registers.setRegValue(0, eR1); //r2 -> 0;
                break;
            case 0xA0:
                //COMPR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                if (vR1 < vR2) {
                    registers.setRegValue((int) '<', SW);
                } else if (vR1 == vR2) {
                    registers.setRegValue((int) '=', SW);
                } else if (vR1 > vR2) {
                    registers.setRegValue((int) '>', SW);
                }
            case 0x9C:
                //DIVR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR2 / vR1, eR2); // r2 -> r2/r1;
                break;
            case 0x98:
                //MULR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR2 * vR1, eR2);
                break;
            case 0xAC:
                //RMO
                vR1 = registers.getRegValue(eR1);
                registers.setRegValue(vR1, eR2);
                break;
            case 0xA4:
                //SHIFTL
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR1 << vR2, eR1);
                break;
            case 0xA8:
                //SHIFTR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR1 >> vR2, eR1);
                break;
            case 0x94:
                //SUBR
                vR1 = registers.getRegValue(eR1);
                vR2 = registers.getRegValue(eR2);
                registers.setRegValue(vR2 - vR1, eR2);
                break;
            case 0xB8:
                //TIXR
                vR1 = registers.getRegValue(eR1);
                registers.setRegValue(registers.getRegValue(X) + 1, X);

                if (registers.getRegValue(X) < vR1) {
                    registers.setRegValue((int) '<', SW);
                } else if (registers.getRegValue(X) == vR1) {
                    registers.setRegValue((int) '=', SW);
                } else if (registers.getRegValue(X) > vR1) {
                    registers.setRegValue((int) '>', SW);
                }
                break;
            default:
                return false;
        }
        System.out.println("end R1: " + eR1);
        System.out.println("end R2: " + eR2);
        System.out.println("val R1: " + vR1);
        System.out.println("val R2: " + vR2);
        return true;
    }
    public boolean loadF3F4(int opcode,int operand){

        
        switch (opcode) {
            case 0x0 -> //LDA
                registers.setRegValue(operand, A);
            case 0x68 -> //LDB
                registers.setRegValue(operand, B);
            case 0x50 -> {
                //LDCH
                int vA = registers.getRegValue(A); //carrega conteúdo em A
                vA = vA & 0xFFFF00 | operand & 0xFF;
                registers.setRegValue(vA,A);
            }
            case 0x08 -> //LDL
                registers.setRegValue(operand,L);
            case 0x6C -> //LDS
                registers.setRegValue(operand, S);
            case 0x74 -> //LDT
                registers.setRegValue(operand, T);
            case 0x04 -> //LDX
                registers.setRegValue(operand, X);
                
            case 0x18 -> {
                //Add
                registers.setRegValue(registers.getRegValue(A) + operand, A);
            }
            case 0x40 -> {
                //AND
                registers.setRegValue(registers.getRegValue(A) & operand, A);
            }
            case 0x24 -> {
                //Div
                registers.setRegValue(registers.getRegValue(A) / operand, A);
            }
            case 0x44 -> {
                //OR
                registers.setRegValue(registers.getRegValue(A) | operand, A);
            }
            case 0x20 -> {
                // MUL
                registers.setRegValue(registers.getRegValue(A) * operand, A);
            }
            case 0x4C -> {
                // RSUB
                registers.setRegValue(registers.getRegValue(L), PC);
            }
            case 0x3C -> {
                //J
                registers.setRegValue(operand, PC);
            }
            case 0x30 -> {
                //JEQ
                if (registers.getRegValue(SW) == (int) '=') {
                    registers.setRegValue(operand, PC);
                }

            }
            case 0x34 -> {
                //JGT
                if (registers.getRegValue(SW) == (int) '>') {
                    registers.setRegValue(operand, PC);
                }
            }
            case 0x38 -> {
                //JLT
                if (registers.getRegValue(SW) == (int) '<') {
                    registers.setRegValue(operand, PC);
                }
            }
            case 0x48 -> {
                // JSUB
                registers.setRegValue(registers.getRegValue(PC), L);
                registers.setRegValue(operand, PC);
            }
            case 0x0C -> {
                //STA
                memory.setword(registers.getRegValue(A), operand);
            }
            case 0x78 -> {
                //STB
                memory.setword(registers.getRegValue(B), operand);
            }

            case 0x54 -> {
                //STCH
                String aux = Integer.toBinaryString(registers.getRegValue(A));
                String aux2;

                aux = aux.substring(0, 7);
                aux2 = Integer.toBinaryString(operand);
                aux2 = aux2.substring(8, 24);
                
                memory.setword(Integer.parseInt(aux + aux2, 2), operand);
                
            }
            case 0x14 -> {
                //STL
                memory.setword(registers.getRegValue(L), operand);
            }
            case 0x7C -> {
                //STS
                memory.setword(registers.getRegValue(S), operand);
            }
            case 0x84 -> {
                //STT
                memory.setword(registers.getRegValue(T), operand);
            }
            case 0x10 -> {
                //STX
                memory.setword(registers.getRegValue(X), operand);
            }
            case 0x1C -> {
                //SUB
                registers.setRegValue(registers.getRegValue(A) - operand, A);
            }
            default ->
                throw new AssertionError();
        }
        return true;
    }
    public int loadByte(){ //trocar o nome por Fetch !???
        int b = memory.getByte(registers.getRegValue(6));
        registers.inccr_PC();
        return b;
    }
    public void exec(){
        //busca o primeiro byte do object code
        
        int fbyte = loadByte(); //Incluí opcode e ni
        int sbyte = loadByte(); //incluí xbpe e 4 bits do disp
        //Tenta executar F2
        //Só necessita dos 2 primeiros bytes
        if(loadF2(fbyte, sbyte)) return;
        
        //Tenta executar F3 ou F4
        int tbyte = loadByte(); //inclui restante do disp ou parte do addr
        int ni = fbyte & NI_MASK;
        int xbpe = (sbyte & XBPE_MASK) >> 4;
        int operando;
        int TA;
        int disp;
        //FORMATO SIMPLES/DIRETO
        if(ni ==0){
            int x = xbpe & X_MASK;
            
            if(x==0){ // disp se torna 15 bits - agrega os 3 bits do bpe
                TA = ((sbyte & 0x7F) << 8) + tbyte;
                
            }
            else{
                // disp tam 15 bits + conteúdo regitrador X
                TA = ((sbyte & 0x7F) << 8) + tbyte + registers.getRegValue(X);
            }
            operando = memory.getWord(TA);
        }   
        else if(ni == 3){
            
            disp =  ((sbyte &0x0F) << 8) + tbyte;
            if(xbpe==0){
                //4 bits do segundo byte e todos do 3º byte
                TA = disp;
            }
            else if(xbpe==1){
                //formato extendido addr = 20bits
                int ftbyte = loadByte(); // Carrega o 4º
                TA = (disp << 8) + ftbyte; // addr = disp + fbyte - TA = addr
            }
            else if(xbpe == 2){
                TA = disp + registers.getRegValue(PC);
            }
            else if(xbpe == 4){
                TA = disp + registers.getRegValue(B);
            }
            else if(xbpe == 8){
                TA = disp + registers.getRegValue(X);
            }
            else if(xbpe ==9){
                int ftbyte = loadByte(); // Carrega o 4º
                int addr = (disp << 8) + ftbyte;
                TA = addr + registers.getRegValue(X);
            }
            else if(xbpe == 0xC){
                TA = disp + registers.getRegValue(B) + registers.getRegValue(X);
            }
            else{
                System.out.println("Algum erro :( ");
                return;
            }
            operando = memory.getWord(TA);

        }

        // FORMATO IMEDIATO OPERANDO = TA
        else if (ni == 1){
            disp =  ((sbyte &0x0F) << 8) + tbyte;
            if(xbpe==0){
                //4 bits do segundo byte e todos do 3º byte
                TA = disp;
            }
            else if(xbpe == 1){
                //formato extendido addr = 20bits
                int ftbyte = loadByte(); // Carrega o 4º
                TA = (disp << 8) + ftbyte; // addr = disp + fbyte - TA = addr
            }
            else if(xbpe == 2){
                TA = disp + registers.getRegValue(PC);
            }
            else if(xbpe == 4){
                TA = disp + registers.getRegValue(B);
            }
            else{
                System.out.println("Algum erro :( ");
                return;
            }
            operando = TA;
        }
        
        // FORMATO INDIRETO OPERANDO = get.word(get.word(TA))
        else{
            disp =  ((sbyte &0x0F) << 8) + tbyte;
            if(xbpe==0){
                //4 bits do segundo byte e todos do 3º byte
                TA = disp;
            }
            else if(xbpe==1){
                //formato extendido addr = 20bits
                int ftbyte = loadByte(); // Carrega o 4º
                TA = (disp << 8) + ftbyte; // addr = disp + fbyte - TA = addr
            }
            else if(xbpe == 2){
                TA = disp + registers.getRegValue(PC);
            }
            else if(xbpe == 4){
                TA = disp + registers.getRegValue(B);
            }
            else{
                System.out.println("Algum erro :( ");
                return;
            }
            operando = memory.getWord(memory.getWord(TA));
        }

        //executa instrução de formato f3 ou f4
        if(loadF3F4((fbyte & OPCODE_MASK), operando))return;
        //if(loadF3F4(opcode,f.get_ni(),f.get_xbpe(),f.operandF3(op1, op2))) return;
        else{
            System.out.println("A execução falhou falhou");
        } 
        
    }

}