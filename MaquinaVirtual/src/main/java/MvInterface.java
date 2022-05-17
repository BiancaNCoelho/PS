/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author luan_
 */
public class MvInterface implements ActionListener {

    protected Maquina xp;
    protected Memoria ram;
    protected Registers reg;
    protected JFrame frame;
    protected JPanel panel;
    protected JPanel memFrame;
    protected JPanel regFrame;

    ArrayList<JTextField> txtMemEnd1;
    ArrayList<JTextField> txtMemEnd2;

    ArrayList<JLabel> lblMemEnd1;
    ArrayList<JLabel> lblMemEnd2;

    ArrayList<JTextField> txtRegList;

    public MvInterface() {
        Maquina xp = new Maquina();
        
        this.ram = xp.memory;
        this.reg = xp.registers;
        this.xp = xp;

        frame = new JFrame();
        memFrame = new JPanel();
        regFrame = new JPanel(new GridLayout(4, 2, 2, 2));
        panel = new JPanel(new FlowLayout());

        JPanel aux = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton attEnd = new JButton("Atualizar endereços");
        JButton loadMem = new JButton("Carregar para memória");
        JButton loadReg = new JButton("Carregar registradores");
        JButton execute = new JButton("Executar");
        ActionListener evento = this;

        frame.setTitle("Máquina Virtual - Grupo 2");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(1000, 500));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);

        panel.add(memFrame);

        gbc.gridx = 0;
        gbc.gridy = 0;
        aux.add(attEnd, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        aux.add(loadMem, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        aux.add(regFrame, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        aux.add(loadReg, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        aux.add(execute, gbc);

        panel.add(aux);

        attEnd.setActionCommand("btn-1");
        loadMem.setActionCommand("btn-2");
        loadReg.setActionCommand("btn-3");
        execute.setActionCommand("exec");

        attEnd.addActionListener(evento);
        loadMem.addActionListener(evento);
        loadReg.addActionListener(evento);
        execute.addActionListener(evento);

        this.mountMemFrame();
        this.mountRegFrame();

        this.atualizarEndMem(100, 200);

        frame.setVisible(true);
    }

    public void atualizarEndMem(int end1, int end2) {

        lblMemEnd1.get(0).setText("End Inicial: ");
        lblMemEnd2.get(0).setText("End Inicial: ");
        txtMemEnd1.get(0).setText("" + end1);
        txtMemEnd2.get(0).setText("" + end2);

        txtMemEnd1.get(1).setText(Integer.toHexString(ram.getByte(end1 + 30)));
        lblMemEnd1.get(1).setText("" + (end1 + 30));

        txtMemEnd2.get(1).setText(Integer.toHexString(ram.getByte(end2 + 30)));

        lblMemEnd2.get(1).setText("" + (end2 + 30));

        for (int i = 1; i < txtMemEnd1.size() / 2; i++) {
            txtMemEnd1.get(i * 2 + 1).setText(Integer.toHexString(ram.getByte(end1 + 30 + i)));

            txtMemEnd2.get(i * 2 + 1).setText(Integer.toHexString(ram.getByte(end2 + 30 + i)));

            lblMemEnd1.get(i * 2 + 1).setText("" + (end1 + i + 30));

            lblMemEnd2.get(i * 2 + 1).setText("" + (end2 + i + 30));
        }

        for (int i = 1; i < txtMemEnd1.size() / 2; i++) {
            txtMemEnd1.get(i * 2).setText(Integer.toHexString(ram.getByte(end1 + i - 1)));

            txtMemEnd2.get(i * 2).setText(Integer.toHexString(ram.getByte(end2 + i - 1)));

            lblMemEnd1.get(i * 2).setText("" + (end1 + i - 1));

            lblMemEnd2.get(i * 2).setText("" + (end2 + i - 1));
        }
    }

    protected void mountMemFrame() {
        txtMemEnd1 = new ArrayList<>();
        txtMemEnd2 = new ArrayList<>();

        lblMemEnd1 = new ArrayList<>();
        lblMemEnd2 = new ArrayList<>();

        JPanel txtBox1;
        JPanel txtBox2;
        JPanel mem1 = new JPanel(new GridLayout(31, 2));
        JPanel mem2 = new JPanel(new GridLayout(31, 2));

        memFrame.add(mem1);
        memFrame.add(mem2);

        txtBox1 = new JPanel(new GridLayout(1, 2));
        JTextField txt = new JTextField();
        JLabel label = new JLabel("End inicial:");

        txtBox1.add(label);
        txtBox1.add(txt);

        lblMemEnd1.add(label);
        txtMemEnd1.add(txt);
        mem1.add(txtBox1);

        txtBox2 = new JPanel(new GridLayout(1, 2));
        label = new JLabel("End inicial:");
        txt = new JTextField();
        txtBox2.add(label);
        txtBox2.add(txt);
        txtMemEnd2.add(txt);
        lblMemEnd2.add(label);
        mem2.add(txtBox2);

        for (int i = 0; i < 61; i++) {

            label = new JLabel();
            txt = new JTextField("", 6);
            txtBox1 = new JPanel(new GridLayout(1, 2));
            txtBox1.add(label);
            txtBox1.add(txt);
            lblMemEnd1.add(label);
            txtMemEnd1.add(txt);
            mem1.add(txtBox1);

            label = new JLabel();
            txt = new JTextField("", 6);
            txtBox2 = new JPanel(new GridLayout(1, 2));
            txtBox2.add(label);
            txtBox2.add(txt);
            lblMemEnd2.add(label);
            txtMemEnd2.add(txt);
            mem2.add(txtBox2);
        }

        for (int i = 0; i < txtMemEnd1.size(); i++) {
            txtMemEnd1.get(i).setHorizontalAlignment(JTextField.RIGHT);
            txtMemEnd2.get(i).setHorizontalAlignment(JTextField.RIGHT);
            lblMemEnd1.get(i).setHorizontalAlignment(JLabel.RIGHT);
            lblMemEnd2.get(i).setHorizontalAlignment(JLabel.RIGHT);
        }
    }

    protected void mountRegFrame() {
        JPanel aux;
        JTextField txt;
        aux = new JPanel();
        txtRegList = new ArrayList<>();
        int i = 0;

        aux.add(new JLabel("A(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();
        aux.add(new JLabel("X(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("L(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("B(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("S(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("T(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("PC(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("SW(" + i + ")"));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "btn-1":
                this.atualizarEndMem(Integer.parseInt(txtMemEnd1.get(0).getText()), Integer.parseInt(txtMemEnd2.get(0).getText()));
                break;
            case "btn-2":
                for (int i = 1; i < txtMemEnd1.size(); i++) {
                    ram.setByte(Integer.parseInt(lblMemEnd1.get(i).getText()), Integer.parseInt(txtMemEnd1.get(i).getText(), 16));
                    ram.setByte(Integer.parseInt(lblMemEnd2.get(i).getText()), Integer.parseInt(txtMemEnd2.get(i).getText(), 16));
//                    System.out.println(Integer.parseInt(lblMemEnd1.get(i).getText()) + ": " + Integer.parseInt(txtMemEnd1.get(i).getText(), 16));
//                    System.out.println(Integer.parseInt(lblMemEnd2.get(i).getText()) + ": " + Integer.parseInt(txtMemEnd2.get(i).getText(), 16));
//                    System.out.println("ram "+ Integer.parseInt(lblMemEnd1.get(i).getText())+": " + ram.getByte(Integer.parseInt(lblMemEnd1.get(i).getText())));
//                    System.out.println("ram "+ Integer.parseInt(lblMemEnd2.get(i).getText())+": " + ram.getByte(Integer.parseInt(lblMemEnd2.get(i).getText())));

                }
                break;
            case "btn-3":
                for (int i = 0; i < txtRegList.size(); i++) {
                    reg.setRegValue(Integer.parseInt(txtRegList.get(i).getText()), i);
                }
                System.out.println("=============");
                reg.show_registers();
                break;
            case "exec":
                xp.exec();
                this.atualizarEndMem(Integer.parseInt(lblMemEnd1.get(2).getText()), Integer.parseInt(lblMemEnd2.get(2).getText()));
                for (int i = 0; i < txtRegList.size(); i++) {
                    txtRegList.get(i).setText("" + reg.getRegValue(i));
                }
                break;
            default:
                throw new AssertionError();
        }
    }
}
