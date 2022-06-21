package Processador.MaquinaVirtual.GUI;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import Processador.Macros;
import Processador.MaquinaVirtual.Maquina;
import Processador.MaquinaVirtual.Memoria;
import Processador.MaquinaVirtual.Registers;
import Processador.Montador.Loader;
import Processador.Montador.Montador;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Lord-Mark
 */
public final class MvInterfaceLog implements ActionListener {

    private Maquina maquinaSICXE;
    private Memoria ram;
    private Registers reg;
    private JFrame frame;
    private JPanel panel;
    private JPanel memFrame;
    private JPanel regFrame;
    private JTextArea codeArea;
    private JPanel codeAreaFrame;
    private JTextArea extendArea;
    private JPanel extendAreaFrame;
    private JTextArea consoleArea;
    private JPanel consoleAreaFrame;
    private String arq;
    private Loader loader;
    BufferedWriter fileWriter;

    ArrayList<JTextField> txtMemEnd1;
    ArrayList<JLabel> lblMemEnd1;
    ArrayList<JTextField> txtRegList;

    public MvInterfaceLog(Maquina xp) {
        this.ram = xp.getMemory();
        this.reg = xp.getRegisters();
        this.maquinaSICXE = xp;

        int vGap = 1;
        int hGap = 1;
        codeAreaFrame = new JPanel(new CardLayout(hGap, vGap));
        codeAreaFrame.setBorder(BorderFactory.createTitledBorder("Código de entrada"));

        extendAreaFrame = new JPanel(new CardLayout(hGap, vGap));
        extendAreaFrame.setBorder(BorderFactory.createTitledBorder("Código extendido"));

        consoleAreaFrame = new JPanel(new CardLayout(hGap, vGap));
        consoleAreaFrame.setBorder(BorderFactory.createTitledBorder("Saída do console"));

        frame = new JFrame();
        memFrame = new JPanel();
        memFrame.setBorder(BorderFactory.createTitledBorder("Memória"));
        regFrame = new JPanel(new GridLayout(4, 2, 2, 2));
        regFrame.setBorder(BorderFactory.createTitledBorder("Registradores"));
        panel = new JPanel(new GridLayout(0, 1, hGap, vGap));
        panel.setBorder(BorderFactory.createEmptyBorder(hGap, vGap, hGap, vGap));
        codeArea = new JTextArea(40, 80);
        extendArea = new JTextArea(40, 80);
        extendArea.setEditable(false);


        consoleArea = new JTextArea(40, 80);
        consoleArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(consoleArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        ConsoleOutput.redirectOutput(consoleArea);
        ConsoleOutput.redirectErr(consoleArea);

        loader = new Loader();

        arq = "CodigoSalvo.txt";
        loadText(arq, codeArea);

        JPanel auxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton attEnd = new JButton("Atualizar endereços");
        JButton save = new JButton("Salvar e montar");
        JButton execute = new JButton("Executar");
        JButton load = new JButton("Carregar arquivo");
        ActionListener evento = this;

        frame.setTitle("Processador Assembly - Grupo 2");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setMinimumSize(new Dimension(720, 480));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);

        codeArea.setMargin(new Insets(10, 15, 10, 10));
        extendArea.setMargin(new Insets(10, 15, 10, 10));

        consoleAreaFrame.add(scroll);
        codeAreaFrame.add(codeArea);
        extendAreaFrame.add(extendArea);
        JPanel textArea = new JPanel(new GridLayout(0, 2, hGap, vGap));

        textArea.add(codeAreaFrame);
        textArea.add(extendAreaFrame);

        addComp(auxPanel, load, 1, 0, 1, 1, GridBagConstraints.BOTH, 0, 0.0);
        addComp(auxPanel, textArea, 0, 1, 3, 5, GridBagConstraints.BOTH, 0.33, 1.0);
        addComp(auxPanel, consoleAreaFrame, 0, 6, 3, 3, GridBagConstraints.BOTH, 0.33, 0.5);
        addComp(auxPanel, attEnd, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL, 0.05, 0.0);
        addComp(auxPanel, memFrame, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL, 0.05, 0.0);
        addComp(auxPanel, save, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL, 0.05, 0.0);
        addComp(auxPanel, regFrame, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL, 0.05, 0.0);
        addComp(auxPanel, execute, 4, 4, 1, 1, GridBagConstraints.HORIZONTAL, 0.05, 0.0);

        panel.add(auxPanel);

        attEnd.setActionCommand("atualizarEnd");
        save.setActionCommand("salvar");
        execute.setActionCommand("exec");
        load.setActionCommand("carreg");

        attEnd.addActionListener(evento);
        save.addActionListener(evento);
        execute.addActionListener(evento);
        load.addActionListener(evento);

        this.mountMemFrame();
        this.mountRegFrame();

        this.atualizarEndMem(0);

        frame.pack();
        frame.setVisible(true);
    }

    public void atualizarEndMem(int end1) {

        lblMemEnd1.get(0).setText("End Inicial: ");
        txtMemEnd1.get(0).setText("" + end1);

        txtMemEnd1.get(1).setText(Integer.toHexString(ram.getByte(end1 + 20)));
        lblMemEnd1.get(1).setText("" + (end1 + 20));

        for (int i = 1; i < txtMemEnd1.size() / 2; i++) {
            txtMemEnd1.get(i * 2 + 1).setText(Integer.toHexString(ram.getByte(end1 + 20 + i)));
            lblMemEnd1.get(i * 2 + 1).setText("" + (end1 + i + 20));
        }

        for (int i = 1; i < txtMemEnd1.size() / 2; i++) {
            txtMemEnd1.get(i * 2).setText(Integer.toHexString(ram.getByte(end1 + i - 1)));
            lblMemEnd1.get(i * 2).setText("" + (end1 + i - 1));
        }
    }

    private void mountMemFrame() {
        txtMemEnd1 = new ArrayList<>();

        lblMemEnd1 = new ArrayList<>();

        JPanel txtBox1;
        JPanel mem1 = new JPanel(new GridLayout(21, 2));

        memFrame.add(mem1);

        txtBox1 = new JPanel(new GridLayout(1, 2));
        JTextField txt = new JTextField();
        JLabel label = new JLabel("End inicial:");

        txtBox1.add(label);
        txtBox1.add(txt);

        lblMemEnd1.add(label);
        txtMemEnd1.add(txt);
        mem1.add(txtBox1);

        for (int i = 0; i < 41; i++) {

            label = new JLabel();
            txt = new JTextField("", 6);
            txtBox1 = new JPanel(new GridLayout(1, 2));
            txtBox1.add(label);
            txtBox1.add(txt);
            lblMemEnd1.add(label);
            txtMemEnd1.add(txt);
            mem1.add(txtBox1);

        }

        for (int i = 0; i < txtMemEnd1.size(); i++) {
            txtMemEnd1.get(i).setEditable(false);
            txtMemEnd1.get(i).setHorizontalAlignment(JTextField.RIGHT);
            lblMemEnd1.get(i).setHorizontalAlignment(JLabel.RIGHT);
        }
        txtMemEnd1.get(0).setEditable(true);
    }

    private void mountRegFrame() {
        JPanel aux;
        JTextField txt;
        aux = new JPanel();
        txtRegList = new ArrayList<>();
        int i = 0;

        aux.add(new JLabel("  A(" + i + ") "));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();
        aux.add(new JLabel("   X(" + i + ") "));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("  L(" + i + ") "));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("   B(" + i + ") "));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("  S(" + i + ") "));
        txt = new JTextField("" + reg.getRegValue(i++), 5);
        aux.add(txt);
        regFrame.add(aux);
        txtRegList.add(txt);

        aux = new JPanel();

        aux.add(new JLabel("   T(" + i + ") "));
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

        for (JTextField jTextField : txtRegList) {
            jTextField.setEditable(false);
        }
    }

    //Atualiza a TextArea com os conteúdos de um arquivo de texto
    public void loadText(String fileInput, JTextArea tgtComponent) {
        StringBuilder txt = new StringBuilder();
        try {
            File arquivo = new File(fileInput);
            Scanner reader = new Scanner(arquivo);
            while (reader.hasNextLine()) {
                txt.append("\n").append(reader.nextLine());
            }
            tgtComponent.setText(txt.substring(1));
            reader.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Nao foi possível ler arquivo", "Erro de leitura", JOptionPane.ERROR_MESSAGE);
            System.out.println("Não leu o arquivo");
        }
    }

    //Initializa os componentes de um GridBagLayout
    public void saveCode() {
        try {
            FileOutputStream fos = new FileOutputStream(arq);
            fileWriter = new BufferedWriter(new OutputStreamWriter(fos));
            fileWriter.write(codeArea.getText());
            fileWriter.newLine();
            fileWriter.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Não conseguiu escrever no arquivo", "Erro de escrita", JOptionPane.ERROR_MESSAGE);
            System.out.println("Não conseguiu escrever no arquivo");
        }
    }

    private void addComp(JPanel panel, JComponent comp, int x, int y, int gWidth, int gHeight, int fill, double weightx, double weighty) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = gWidth;
        gbc.gridheight = gHeight;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;

        panel.add(comp, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case "carreg" -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos de texto", "txt", "text"));
                int retVal = fileChooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    loadText(file.getAbsolutePath(), codeArea);
                }
            }
            case "atualizarEnd" -> this.atualizarEndMem(Integer.parseInt(txtMemEnd1.get(0).getText()));
            case "salvar" -> {
                this.saveCode();
                ram.reset();
                reg.resetRegisters();
                Macros macro = new Macros(arq, "MacroOut.txt");
                macro.loadInputFile();
                macro.processar();
                Montador assembler = new Montador("MacroOut.txt");
                assembler.Montar();
                loader.loadObjToMachine(maquinaSICXE, assembler.listing_output);
                //Atualiza memória
                this.atualizarEndMem(Integer.parseInt(lblMemEnd1.get(2).getText()));
                //Atualiza os registradores
                for (int i = 0; i < txtRegList.size(); i++) {
                    txtRegList.get(i).setText("" + reg.getRegValue(i));
                }

                loadText("MacroOut.txt", extendArea);
            }
            case "btn-3" -> {
                for (int i = 0; i < txtRegList.size(); i++) {
                    reg.setRegValue(Integer.parseInt(txtRegList.get(i).getText()), i);
                }
                System.out.println("=============");
                reg.show_registers();
            }
            case "exec" -> {
                maquinaSICXE.exec();
                //Atualiza a memória
                this.atualizarEndMem(Integer.parseInt(lblMemEnd1.get(2).getText()));
                //Atualiza os registradores
                for (int i = 0; i < txtRegList.size(); i++) {
                    txtRegList.get(i).setText("" + reg.getRegValue(i));
                }
            }
            default -> throw new AssertionError();
        }
    }
}

