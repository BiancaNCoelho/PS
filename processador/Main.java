package Processador;


import Processador.MaquinaVirtual.Maquina;
import Processador.MaquinaVirtual.GUI.MvInterfaceLog;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author Lord-Mark
 */
public class Main {

    public static void main(String[] args) {
        Maquina xp = new Maquina();
//        MvInterface visual = new MvInterface(xp);
        MvInterfaceLog visual = new MvInterfaceLog(xp);

    }
}
