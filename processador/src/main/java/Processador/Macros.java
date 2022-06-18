package Processador;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lord-Mark
 */
public final class Macros {

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
     * Salva todas as linhas expandidas e processadas dos diferentes níveis da
     * macro
     */
    private ArrayList<String> completeOutFile;

    /**
     * Receberá as linhas da macro com os parâmetros já alterados
     */
    ArrayList<ArrayList<String>> linhasMacroExpandida;

    // Último indice da defTab
    private int dtLastIndex = -1;

    // Último indice da macros
    private int mLastIndex = -1;
    BufferedWriter fileWriter;
    
    private String fileNameInput;
    /**
     * Carrega um arquivo de macros para ser processado
     *
     * @param fileNameInput
     * @param fileNameOutput
     */
    public Macros(String fileNameInput, String fileNameOutput) {
        lines = new ArrayList<>();
        this.fileNameInput = fileNameInput;
        loadInputFile();

        defTab = new ArrayList<>();
        macrosParams = new ArrayList<>();
        macroScopo = new ArrayList<>();
        macroHierarchy = new ArrayList<>();
        actualParam = new ArrayList<>();
        linhasMacroExpandida = new ArrayList<>();
        completeOutFile = new ArrayList<>();

        File fileOut = new File(fileNameOutput);
        try {
            FileOutputStream fos = new FileOutputStream(fileOut);
            fileWriter = new BufferedWriter(new OutputStreamWriter(fos));
        } catch (FileNotFoundException e) {
            System.out.println("Não conseguiu abrir o arquivo :(");
        }

    }
    
    public void loadInputFile(){
        try {
            lines.clear();
            File arquivo = new File(fileNameInput);
            Scanner reader = new Scanner(arquivo);
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
            reader.close();

        } catch (IOException e) {
            System.out.println("Não leu o arquivo ;-;");
        }
    }

    public void processar() {

        // Se d == true, então está processando macro, false caso contrário
        boolean d = false;
        // Se e == true, então está expandindo uma macro, false caso contrário
        boolean e = false;

        String line;

        // Percorre todo arquivo e processa as macros
        for (int i = 0; i < lines.size(); i++) {

            line = removeComments(lines.get(i));

            System.out.println("---- " + line);

            if (isMacroName(line) && !d) {
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
                    macroScopo.get(getScopoID(defTab.get(dtLastIndex - 1).get(0))).add("###SubMacro:" + getScopoID(defTab.get(dtLastIndex).get(0)));
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
                    completeOutFile.add(line);
                }
            }
            // Modo expansão
            if (e && !d) {
                expandMacro(line);
                e = false;
            }

        }

        writeData(completeOutFile);
        try {
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println("Não fechou o arquivo");
        }
    }

    /**
     * Expande a macro, substituindo os parâmetros da macro pelos parâmetros
     * passados na chamada da macro
     *
     * @param line
     */
    public void expandMacro(String line) {
        //Salva a linha da definição da submacro na completeMacro
        int submacroID;
        String macroName = getMacroName(line);

        int scopoID = getScopoID(macroName);
        ArrayList<Integer> hierarchy = macroHierarchy.get(scopoID);
        ArrayList<String> linhasMacro = macroScopo.get(scopoID);

        actualParam.set(scopoID, getMacroParams(line));

        System.out.println("Parametros passados: " + actualParam.get(scopoID));

        System.out.println("Nivel de Hierarquia: " + hierarchy.size());
        ArrayList<String> split = new ArrayList<>();

        boolean paramFound;

        int nvl = 0;

        for (int mIndex = 0; mIndex < linhasMacro.size(); mIndex++) {
            boolean macroCall = false;
            if (isMacroName(linhasMacro.get(mIndex))) {
                split = getMacroParams(linhasMacro.get(mIndex));
                macroCall = true;
            } else {
                split.addAll(Arrays.asList(linhasMacro.get(mIndex).split(" ")));
            }

            for (int a = 0; a < split.size(); a++) {

                paramFound = false;

                for (int b = 1; b < macrosParams.get(hierarchy.get(nvl)).size(); b++) {
                    if (split.get(a).equals(macrosParams.get(hierarchy.get(nvl)).get(b))) {
                        System.out.println("Encontrou: " + split.get(a) + " = " + actualParam.get(hierarchy.get(nvl)).get(b - 1) + " na linha: " + linhasMacro.get(mIndex));
                        split.set(a, actualParam.get(hierarchy.get(nvl)).get(b - 1));
                        paramFound = true;
                        break;
                    }
                }
                /**
                 * Se o parâmetro não foi encontrado nesse nível e existe um
                 * nível acima, então o parâmetro será buscado no nível seguinte
                 * da hierarquia, o contador será decrementado para que o código
                 * não siga em frente até que toda a hierarquia seja percorrida
                 * ou até que o parâmetro seja encontrado
                 */
                if (!paramFound && (nvl + 1) < hierarchy.size()) {
                    a--;
                    nvl++;
                } else {
                    nvl = 0;
                }
            }
            String stringUnifiedLine = "";

            // Se tem a chamada de uma macro dentro desta macro, então a mesma será expandida
            if (macroCall) {

                stringUnifiedLine = getMacroName(linhasMacro.get(mIndex)) + " " + split.get(0);
                for (int i = 1; i < split.size(); i++) {
                    stringUnifiedLine = stringUnifiedLine + ", " + split.get(i);
                }

                System.out.println("Expandindo macro dentro da macro: " + stringUnifiedLine);

                expandMacro(stringUnifiedLine);
                split.clear();
                continue;
            }

            // Reconstrói a linha anteriormente separada
            for (String splited : split) {
                stringUnifiedLine = stringUnifiedLine + " " + splited;
            }
            split.clear();

            /**
             * Substring(1) serve para remover o primeiro caractere que será um
             * expaço em branco devido à forma que a linha foi reconstruida
             */
            linhasMacroExpandida.get(scopoID).add(stringUnifiedLine.substring(1));
        }

        if (hierarchy.size() > 1) {
            submacroID = completeOutFile.indexOf("###SubMacro:" + scopoID);
            if (submacroID != -1) {
                System.out.println("####|Submacro definida e encontrada|####");
                completeOutFile.remove(submacroID);
                for (String ln : linhasMacroExpandida.get(scopoID)) {
                    completeOutFile.add(submacroID, ln);
                    submacroID++;
                }
            } else {
                System.out.println("A submacro " + macrosParams.get(scopoID).get(0) + " não foi previamente definida, ela depende da macro: " + macrosParams.get(hierarchy.get(1)).get(0));
                return;
            }

        } else {
            for (String ln : linhasMacroExpandida.get(scopoID)) {
                completeOutFile.add(ln);
            }
        }

        /**
         * Limpa a macro expandida pois ela já foi utilizada e se uma nova
         * chamada for feita, então esta nova chamada não terá as linhas das
         * expansões anteriores
         */
        linhasMacroExpandida.get(scopoID).clear();

        //Printa todas as linhas expandidas da macro
        for (String aux : completeOutFile) {
            System.out.println("| " + aux);
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

        // Salva a hierarquia atual da defTab na lista de hierarquia
        for (int i = dtLastIndex; i >= 0; i--) {
            hierarchyMacro.add(getScopoID(defTab.get(i).get(0)));
        }
        macroHierarchy.add(hierarchyMacro);
    }

    //============== Funções auxiliares sem muita importância ==============\\
    /**
     * Checa se a linha atual é composta de mais que uma palavra, método
     * utilizado para verificar se MCDEF está sozinho na definição ou se é uma
     * definição de macro em conjunto com o protótipo da mesma
     *
     * @param line
     * @return Se tem apenas uma palavra na linha -> true || Caso contrário ->
     * false
     */
    private boolean isSingleWord(String line) {
        return !(line.split(" ").length > 1);
    }

    /**
     * Se houver um ponto seguido de um espaço, então é um comentário e tudo
     * após este ponto será ignorado
     *
     * @param line
     * @return Linha sem comentários
     */
    private String removeComments(String line) {
        String nLine;

        if (line.contains(" . ")) {
            nLine = line.substring(0, line.indexOf(" . "));
        } else if (line.contains(". ")) {
            nLine = line.substring(0, line.indexOf(". "));
        } else {
            nLine = line;
        }
        return nLine;
    }

    /**
     * Escreve no arquivo toda a lista passada
     *
     * @param lines
     */
    private void writeData(ArrayList<String> lines) {
        try {
            for (String aux : lines) {
                fileWriter.write(aux);
                fileWriter.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Macros.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Não conseguiu escrever no arquivo");
        }
    }

    /**
     * Retorna o id da macroParams e da macroScopo referente ao nome passado da
     * macro
     *
     * @return id
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
     * Recebe uma linha e identifica se há uma chamada de macro nela
     *
     * @param line
     * @return Se há uma chamada de macro na linha passado -> true || Caso
     * contrário -> false
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
     * @return String nome da macro
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
     * @return ArrayList parametros;
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
