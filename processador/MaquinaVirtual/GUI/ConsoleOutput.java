package Processador.MaquinaVirtual.GUI;

import javax.swing.*;
import java.io.*;

//NAO FUI EU QUEM ESCREVI ESSE MÉTODO, CÓDIGO DO STACKOVERFLOW
//https://stackoverflow.com/questions/4443878/redirecting-system-out-to-jtextpane

public class ConsoleOutput implements Runnable {
    JTextArea displayPane;
    BufferedReader reader;

    private ConsoleOutput(JTextArea displayPane, PipedOutputStream pos)
    {
        this.displayPane = displayPane;

        try
        {
            PipedInputStream pis = new PipedInputStream( pos );
            reader = new BufferedReader( new InputStreamReader(pis) );
        }
        catch(IOException e) {}
    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
//              displayPane.replaceSelection( line + "\n" );
                displayPane.append(line + "\n");
                displayPane.setCaretPosition(displayPane.getDocument().getLength());
            }

            System.err.println("im here");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null,
                    "Error redirecting output : " + ioe.getMessage());
        }
    }
    public static void redirectOutput(JTextArea displayPane)
    {
        ConsoleOutput.redirectOut(displayPane);
        ConsoleOutput.redirectErr(displayPane);
    }

    public static void redirectOut(JTextArea displayPane)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut( new PrintStream(pos, true) );

        ConsoleOutput console = new ConsoleOutput(displayPane, pos);
        new Thread(console).start();
    }

    public static void redirectErr(JTextArea displayPane)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr( new PrintStream(pos, true) );

        ConsoleOutput console = new ConsoleOutput(displayPane, pos);
        new Thread(console).start();
    }
}
