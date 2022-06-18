package Processador;


import Processador.MaquinaVirtual.MvInterface;
import Processador.MaquinaVirtual.Maquina;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Lord-Mark
 */
public class Main {

    public static void main(String[] args) {
//        Macros macro = new Macros("./entrada.txt", "./saida.txt");
//        macro.processar();
        Maquina xp = new Maquina();
        MvInterface visual = new MvInterface(xp);
        
    }
}
