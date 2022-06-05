
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lord-Mark
 */
public class Macros {

    // Salva todas as linhas do arquivo para não ter que ficar lendo o arquivo em todo acesso
    private ArrayList<String> lines; // Código de entrada

    /**
     * Salva as definições das macros, segue a seguinte lógica: Quanto maior o
     * índice do defTab, maior o "nível" da macro definida. Como é uma ArrayList
     * de ArrayList, então em cada nível estarão salvos os parâmetros da macro
     * em questão, sendo que o índice zero é reservado ao nome da macro.
     */
    private ArrayList<ArrayList<String>> defTab;
    
    /**
     * Salva o nome das macros e os parâmetros delas pela ordem que foram
     * encontradas, sem seguir qualquer noção de nível
     */
    private ArrayList<ArrayList<String>> macrosParams;
    
    /**
     * Salva o id das macros e dos "pais" delas, assim é possível saber o escopo
     * real da macro na hora da expansão, a posição zero é o nível mais alto e a
     * enésima posição é o nível mais baixo.
     */
    private ArrayList<ArrayList<Integer>> macroHierarchy;

    /**
     * Salva as linhas das macros, o primeiro índice será o mesmo do
     * macroParams, os índices seguintes serão referentes às linhas da macro
     */
    private ArrayList<ArrayList<String>> macroScopo;

    /**
     * Salva os parâmetros passados na chamada de uma macro
     */
    private ArrayList<ArrayList<String>> actualParam;

    /**
     * Receberá as linhas da macro com os parâmetros já alterados
    */
    ArrayList<ArrayList<String>> linhasMacroExpandida;

    // Último indice da defTab
    private int dtLastIndex = -1;

    // Último indice da macros
    private int mLastIndex = -1;

    // Salva o nome do arquivo de saída
    private String fileNameOutput;

    /**
     * Carrega um arquivo de macros para ser processado
     *
     * @param fileNameInput
     */
    public Macros(String fileNameInput) {
        lines = new ArrayList<>();
        try {
            File arquivo = new File(fileNameInput);
            Scanner reader = new Scanner(arquivo);
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
            reader.close();

        } catch (IOException e) {
            System.out.println("Não leu o arquivo ;-;");
        }

        defTab = new ArrayList<>();
        macrosParams = new ArrayList<>();
        macroScopo = new ArrayList<>();
        macroHierarchy = new ArrayList<>();
        actualParam = new ArrayList<>();
        linhasMacroExpandida = new ArrayList<>();
        //Printa o que tem no arquivo
//        for(String line : lines){
//            System.out.println(line);
//        }
    }

    public void processar(String fileNameOutput) {
        this.fileNameOutput = fileNameOutput;

        // Se d == true, então está processando macro, false caso contrário
        boolean d = false;
        // Se e == true, então está expandindo uma macro, false caso contrário
        boolean e = false;

        String line;

        // Percorre todo arquivo e processa as macros
        for (int i = 0; i < lines.size(); i++) {

            line = removeComments(lines.get(i));

            System.out.println("---- " + line);

            if (isMacroName(line)) {
                e = true; // Entra em modo de expansão

            } else if (line.contains("MCDEFN")) {

                loadNewMacro(i);

                System.out.println("Definicao de Macro encontrada\n==========");
                for (int a = 0; a < defTab.get(dtLastIndex).size(); a++) {
                    System.out.println(defTab.get(dtLastIndex).get(a));
                }
                
                // Se d == true então esta macro está dentro de outra macro,
                // portanto um marcador será colocado neste ponto afim de indicar
                // que há uma macro aqui, este marcador indicará o id
                // referente à macroScopo/macroParams desta macro
                if (d) {
                    macroScopo.get(getScopoID(defTab.get(dtLastIndex-1).get(0))).add("###SubMacro:" + getScopoID(defTab.get(dtLastIndex).get(0)));
                }
                d = true;
                System.out.println("==========");
                if (isSingleWord(line)) {
                    i++;
                }

            } else if (line.contains("MCEND")) {
                defTab.remove(dtLastIndex);
                dtLastIndex--;

                if (defTab.isEmpty()) {
                    d = false;
                }
            } else {
                // Modo definição
                if (d) {
                    int scopoId = getScopoID(defTab.get(dtLastIndex).get(0));
                    macroScopo.get(scopoId).add(line);
                } else if (!e) {
                    writeLine(line);
                }
            }
            // Modo expansão
            if (e) {
                String macroName = getMacroName(line);

                int scopoID = getScopoID(getMacroName(macroName));
                ArrayList<Integer> hierarchy = macroHierarchy.get(scopoID);
                ArrayList<String> linhasMacro = macroScopo.get(scopoID);

                actualParam.set(scopoID, getMacroParams(line));

                System.out.println("Parametros passados: " + actualParam.get(scopoID));

                System.out.println("Nivel de Hierarquia: " + hierarchy.size());
                String[] spaceSplit;

                boolean paramFound;

                int nvl = 0;

                for (int mIndex = 0; mIndex < linhasMacro.size(); mIndex++) {

                    spaceSplit = linhasMacro.get(mIndex).split(" ");

                    for (int a = 0; a < spaceSplit.length; a++) {

                        paramFound = false;

                        for (int b = 1; b < macrosParams.get(hierarchy.get(nvl)).size(); b++) {
                            if (spaceSplit[a].equals(macrosParams.get(hierarchy.get(nvl)).get(b))) {
                                System.out.println("Encontrou: " + spaceSplit[a] + " = " + actualParam.get(hierarchy.get(nvl)).get(b - 1) + " na linha: " + linhasMacro.get(mIndex));
                                spaceSplit[a] = actualParam.get(hierarchy.get(nvl)).get(b - 1);
                                paramFound = true;
                            }
                        }
                        if (!paramFound && (nvl + 1) < hierarchy.size()) {
                            a--;
                            nvl++;
                        } else {
                            nvl = 0;
                        }
                    }
                    String stringUnifiedLine = "";
                    for (String splited : spaceSplit) {
                        stringUnifiedLine = stringUnifiedLine + " " + splited;
                    }
                    linhasMacroExpandida.get(scopoID).add(stringUnifiedLine);
                }
                
                //Printa todas as linhas expandidas da macro
                for (String aux : linhasMacroExpandida.get(hierarchy.size() - 1)) {
                    System.out.println(aux);
                }
            }

        }
    }

    /**
     * Esse método recebe o id de uma linha contendo um MCDEFN, então encontra o
     * nome da macro e salva seus parâmetros nas ArrayLists adequadas
     *
     * @param index
     */
    private void loadNewMacro(Integer index) {
        ArrayList<String> newDefMacro = new ArrayList<>();
        ArrayList<Integer> hierarchyMacro = new ArrayList<>();

        String[] prototypeArray, aux;
        String line = removeComments(lines.get(index));
        // Se existe mais que uma palavra no delimitador, então a definição é:
        // MacroName MCDEFN Parametros
        // Se é apenas o delimitador, então a definição é:
        // MCDEFN
        // Label MacroName Parametros
        // Se for o segundo caso, então o nome da macro está na linha seguinte

        if (isSingleWord(line)) {
            line = removeComments(lines.get(index + 1));

            prototypeArray = line.split(",");
            aux = prototypeArray[0].split(" ");
            // Se aux tiver tamanho 2, significa que não tem Label e que a estrutura ficou: MacroName param#1
            // Se tiver tamanho 3 é porque tem Label e ficou: Label MacroName param#1
            if (aux.length == 2) {
                newDefMacro.add(aux[0]);
                newDefMacro.add(aux[1]);
            } else {
                newDefMacro.add(aux[1]);
                newDefMacro.add(aux[0]);
                newDefMacro.add(aux[3]);
            }
            // O que sobra são parâmetros
            for (int i = 1; i < prototypeArray.length; i++) {
                newDefMacro.add(prototypeArray[i].replaceAll(" ", ""));
            }

        } else {
            line = removeComments(lines.get(index));

            prototypeArray = line.split("MCDEFN");
            aux = prototypeArray[1].split(",");

            //Como é o primeiro formato, então a primeira palavra será o nome da Macro
            newDefMacro.add(prototypeArray[0]);

            // O que sobra são parâmetros
            for (String params : aux) {
                newDefMacro.add(params.replaceAll(" ", ""));
            }
        }

        dtLastIndex++;
        mLastIndex++;

        defTab.add(newDefMacro);
        macrosParams.add(newDefMacro);
        macroScopo.add(new ArrayList<>());
        actualParam.add(new ArrayList<>());
        linhasMacroExpandida.add(new ArrayList<>());
        for (int i = dtLastIndex; i >= 0; i--) {
            hierarchyMacro.add(getScopoID(defTab.get(i).get(0)));
        }
        macroHierarchy.add(hierarchyMacro);
    }

    //============== Funções auxiliares sem muita importância ==============\\
    private boolean isSingleWord(String line) {
        return !(line.split(" ").length > 1);
    }

    /**
     * Se houver um ponto seguido de um espaço, então é um comentário e tudo
     * após este ponto será ignorado
     *
     * @param line
     * @return
     */
    private String removeComments(String line) {
        String nLine;

        if (line.contains(" . ")) {
            nLine = line.substring(0, line.indexOf(" . "));
        } else {
            nLine = line;
        }
        return nLine;
    }

    private void writeLine(String line) {
        File fout = new File(this.fileNameOutput);
        try {
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(line);
            bw.newLine();
        } catch (FileNotFoundException e) {
            System.out.println("Não conseguiu abrir o arquivo :(");
        } catch (IOException ex) {
            Logger.getLogger(Macros.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Não conseguiu escrever no arquivo");
        }
    }

    /**
     * Retorna o id da macroParams e da macroScopo referente a última macro da
     * defTab
     *
     * @return
     */
    private int getScopoID(String macroName) {
        for (int i = mLastIndex; i >= 0; i--) {
            if (macroName.equals(macrosParams.get(i).get(0))) {
                return i;
            }
        }
        System.out.println("Escopo não encontrado: " + macroName);
        return -1;
    }

    /**
     * Recebe uma linha e identifica se há uma macro nela
     *
     * @param line
     * @return
     */
    private boolean isMacroName(String line) {

        for (int i = mLastIndex; i >= 0; i--) {

            if (line.contains(macrosParams.get(i).get(0))) {
                System.out.println("\nChamada de macro encontrada");
                System.out.println("Macro: " + macrosParams.get(i).get(0) + " id macro: " + i + " Linha: " + line);
                return true;
            }
        }
        return false;
    }

    /**
     * Recebe uma linha e retorna o nome da macro que existe nessa linha, se não
     * houver macro, retorna null
     *
     * @param line
     * @return
     */
    private String getMacroName(String line) {
        String macroName = null;

        for (int i = mLastIndex; i >= 0; i--) {
            if (line.contains(macrosParams.get(i).get(0))) {
                macroName = macrosParams.get(i).get(0);
            }
        }

        return macroName;
    }

    /**
     * Recebe uma linha com uma chamada de macro e retorna todos os parâmetros
     * referentes a esta macro
     *
     * @param line
     * @return
     */
    private ArrayList<String> getMacroParams(String line) {
        String macroName = getMacroName(line);
        String auxLine;
        String[] splitMacro, aux;

        ArrayList<String> params = new ArrayList<>();

        /**
         * Se o nome da macro não é a primeira palavra, então existe uma label
         * no começo, que será considerada o primeiro parâmetro
         */
        if (line.indexOf(macroName) != 0) {
            splitMacro = line.split(macroName);
            params.add(splitMacro[0]);
            aux = splitMacro[1].split(",");

            // O que sobra são parâmetros
            for (String aux1 : aux) {
                if (!aux1.isEmpty()) {
                    params.add(aux1.replaceAll(" ", ""));
                }
            }
        } else {
            auxLine = line.replaceAll(macroName, "");
            aux = auxLine.split(",");
            // O que sobra são parâmetros
            for (String aux1 : aux) {
                if (!aux1.isEmpty()) {
                    params.add(aux1.replaceAll(" ", ""));
                }
            }
        }
        return params;
    }


}
