package Processador.MaquinaVirtual;

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
    private static final int X_MASK = 0x8;
    Registers registers;
    Memoria memory;

    public Maquina() {
        registers = new Registers();
        memory = new Memoria(1048576); // 1megabyte de memória
    }

    public boolean loadF2(int opcode, int operands) {

        int eR1 = (operands & 0xF0) >> 4; // op1 pega os 4 bits mais significativos do byte
        int eR2 = operands & 0x0F; //op2 pega os 4 bits menos significativos do byte
        int vR1;
        int vR2;
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
        return true;
    }
public int armazenaNoEndereco(int ni, int operando){
    
    //Se Endr indireto realiza um acesso a memoria.
    return ni==0x02 ? memory.getWord(operando) : operando;
}
public int carregaPalavra(int ni,int operando){
    if(ni == 0x01)return operando;
    operando = memory.getWord(operando);
    if(ni == 0x02) operando = memory.getWord(operando);
    return operando;
}
public int carregaUmByte(int ni,int operando){
    if(ni == 0x01)return operando;
    if(ni == 0x02) operando = memory.getByte(memory.getWord(operando));
    return memory.getByte(operando);
}
    public boolean loadF3F4(int opcode,int ni,int operand) {
switch (opcode) {
     case 0x0: //LDA
         registers.setRegValue(carregaPalavra(ni, operand), A);
         break;
     case 0x68: //LDB
        registers.setRegValue(carregaPalavra(ni, operand), B);
         break;
     case 0x50://LDCH
         int vA = registers.getRegValue(A); //carrega conteúdo em A
         vA = vA & 0xFFFF00 | carregaUmByte(ni, operand) & 0xFF;
         registers.setRegValue(vA,A);
         break;
     case 0x08: //LDL
         registers.setRegValue(carregaPalavra(ni, operand),L);
         break;
     case 0x6C: //LDS
         registers.setRegValue(carregaPalavra(ni, operand), S);
         break;
     case 0x74: //LDT
         registers.setRegValue(carregaPalavra(ni, operand), T);
         break;
     case 0x04: //LDX
         registers.setRegValue(carregaPalavra(ni, operand), X);
         break;
    //INSTRUÇÕES DE ARMAZENAMENTO - STORES
     case 0x0C://STA
         int VA = registers.getRegValue(A);
         memory.setword(armazenaNoEndereco(ni, operand),VA);
     case 0x3C: //J
        

     default:
         break;
 }
 
 
 return true;
}
        

    public int loadByte() { //trocar o nome por Fetch !???
        int b = memory.getByte(registers.getRegValue(6));
        registers.inccr_PC();
        return b;
    }

    public boolean isExtendedAddr(int xbpe) {
        return (xbpe & 0x01) == 1;
    }

    public int calculaStandardTA(int sbyte, int tbyte) {
        //15-bit target address
        int sicTA = ((sbyte & 0x7F) << 8) + tbyte;
        return sicTA;

    }

    public int calculaTA(int bp, int disp) {
        // 12-bit target address 
        int TA;
        if (bp == 2) {
            TA = disp + registers.getRegValue(PC);
        } else if (bp == 4) {
            TA = disp + registers.getRegValue(B);
        } else {
            TA = disp;
        }
        return TA;
    }

    //Define o modo como target address será utilizado baseado nos valores de n.i
    public int getOperand(int ni, int TA) {
        // simple addr
        if (ni == 0x0 || ni == 0x3) {
            return memory.getWord(TA); // operando == (TA)
        } //indirect addr
        else if (ni == 0x2) {
            return memory.getWord(memory.getWord(TA)); // operando == ((TA))
        } //immediate addr
        else {
            return TA; // operando = TA
        }
    }
    
    public Memoria getMemory(){
        return this.memory;
    }
    
    public Registers getRegisters(){
        return this.registers;
    }
    
    public void exec(){
 //busca o primeiro byte do object code
 int fbyte = loadByte(); //Incluí opcode e ni
 //busca o 2' byte do object code
 int sbyte = loadByte(); //incluí xbpe e 4 bits do disp

 //Tenta executar F2. Só necessita dos 2 primeiros bytes
 if(loadF2(fbyte, sbyte)) return;
 
 //Tenta executar F3 ou F4
 int tbyte = loadByte(); //inclui restante do disp ou parte do addr
 int ni = fbyte & NI_MASK;
 int xbpe = (sbyte & XBPE_MASK) >> 4;
 //int operando;
 int TA;
// 12 bits displacement, formado por ultimos 4 bits de sbyte e 8 bits de tbyte
 int disp;
 
 //OBTENDO O CALCULO DO TA
 if(ni ==0){
     //formato SIC Standard - 15 bits TA
     TA = calculaStandardTA(sbyte, tbyte);
 }
 else if(isExtendedAddr(xbpe)){ //formato extendido addr = 20bits
     
     //Formato extendido nao permitido com (B) ou (PC) relative
     if((xbpe & 0x06) == 2 || (xbpe & 0x06) == 4){
         System.out.println("Formato nao suportado");
         return;
     }
     int ftbyte = loadByte(); // Carrega o 4º
//20 bits addr, formado por 12 +signitivativos do disp + 8 bits do fbyte
     int addr = ((((sbyte &0x0F) << 8) + tbyte) << 8) + ftbyte;
     TA = addr;
 }
 //Formato 12 bits TA: disp + (B) ou disp + (PC)
 else{
     // TA = disp + (B) + (PC) nao permitido
     disp = ((sbyte &0x0F) << 8) + tbyte;
     if(disp >=2048){
        disp -=4096;
     }
     TA = calculaTA((xbpe & 0x06), disp);
 }
 //Uso do registrador indexador (x)
 if((xbpe & X_MASK) == 0x8){
     //Suportado apenas no modo de enderecamento simples
     if(ni == 0x0 || ni == 0x3){
         TA += registers.getRegValue(X);
     }
     else{
         System.out.println("Formato nao suportado");
         return;
     }

 }
 //operando = getOperand(ni,TA);
 //executa instrução de formato f3 ou f4
 if(loadF3F4((fbyte & OPCODE_MASK), ni, TA))return;
 else{
     System.out.println("Opcode Invalido! (na máquina)");
 } 
 
}

}
