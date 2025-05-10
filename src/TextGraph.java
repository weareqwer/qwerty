import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class TextGraph {
    static int[][] E, D, path;
    static String[] TxtWordArray;
    static int wordNum = 0;
    static boolean flag = true;
    static final int INFINITY = 10000;
    static StringBuffer preStr = new StringBuffer();
    static StringBuffer pathWay = new StringBuffer();
    static StringBuffer randomPath = new StringBuffer();
    static List<String> wordList = new ArrayList<>();
    static List<String> edgePairList = new ArrayList<>();
    static HashMap<String, List<String>> bridgeMap = new HashMap<>();
    static Pattern p = Pattern.compile("[.,\"\\?!:' ]");
    static Random random = new Random();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of the text file: ");
        String fileAdr = scanner.nextLine();
        InputStream fi = new FileInputStream(fileAdr);

        int c;
        while ((c = fi.read()) != -1) {
            Character m = new Character((char) c);
            if (Character.isLetter(m)) {
                preStr.append(m.toString());
            } else if (p.matcher(m.toString()).matches()) {
                preStr.append(" ");
            }
        }

        TxtWordArray = preStr.toString().toLowerCase().trim().split("\\s+");
        for (String word : TxtWordArray) {
            if (!wordList.contains(word)) {
                wordList.add(word);
                wordNum++;
            }
        }

        E = new int[wordNum][wordNum];
        buildEdge();
        createBridgeMap();
        Floyd();

        while (true) {
            System.out.println("\nChoose a function:");
            System.out.println("1. Show Directed Graph");
            System.out.println("2. Query Bridge Words");
            System.out.println("3. Generate New Text");
            System.out.println("4. Calculate Shortest Path");
            System.out.println("5. Random Walk");
            System.out.println("6. Calculate PageRank");
            System.out.println("7. Exit");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                showDirectedGraph();
            } else if (choice == 2) {
                System.out.print("Enter two words separated by space: ");
                String[] input = scanner.nextLine().split("\\s+");
                if (input.length == 2) {
                    System.out.println(queryBridgeWords(input[0], input[1]));
                } else {
                    System.out.println("Invalid input!");
                }
            } else if (choice == 3) {
                System.out.print("Enter a new sentence: ");
                String newText = scanner.nextLine();
                System.out.println(generateNewText(newText));
            } else if (choice == 4) {
                System.out.print("Enter two words separated by space: ");
                String[] input = scanner.nextLine().split("\\s+");
                if (input.length == 2) {
                    System.out.println(calcShortestPath(input[0], input[1]));
                } else {
                    System.out.println("Invalid input!");
                }
            } else if (choice == 5) {
                System.out.println("Random walk result:");
                String randomText = randomWalk();
                System.out.println(randomText);
                saveRandomWalk(randomText);
            } else if (choice == 6) {
                System.out.print("Enter a word: ");
                String word = scanner.nextLine();
                System.out.println("PageRank: " + calPageRank(word));
            } else if (choice == 7) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
        fi.close();
    }

    protected static void showDirectedGraph() {
        System.out.println("=== Directed Graph ===");
        for (int i = 0; i < wordNum; i++) {
            for (int j = 0; j < wordNum; j++) {
                if (E[i][j] != INFINITY) {
                    String word1 = wordList.get(i);
                    String word2 = wordList.get(j);
                    System.out.printf("%s -> %s (weight: %d)\n", word1, word2, E[i][j]);
                }
            }
        }
    }

    protected static void buildEdge() {
        int preNum, curNum;
        String pre = "#";
        for (String word : TxtWordArray) {
            if (!pre.equals("#")) {
                preNum = wordList.indexOf(pre);
                curNum = wordList.indexOf(word);
                E[preNum][curNum]++;
            }
            pre = word;
        }
        for (int i = 0; i < wordNum; i++) {
            for (int j = 0; j < wordNum; j++) {
                if (E[i][j] == 0) {
                    E[i][j] = INFINITY;
                }
            }
        }
    }

    protected static void createBridgeMap() {
        for (int i = 0; i < TxtWordArray.length - 2; i++) {
            String key = TxtWordArray[i] + "#" + TxtWordArray[i + 2];
            bridgeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(TxtWordArray[i + 1]);
        }
    }

    protected static String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!wordList.contains(word1) || !wordList.contains(word2)) {
            return "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";
        }
        String key = word1 + "#" + word2;
        if (!bridgeMap.containsKey(key)) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridgeMap.get(key)) + ".";
    }

    protected static String generateNewText(String inputText) {
        String[] TextWord = inputText.toLowerCase().trim().split("\\s+");
        StringBuilder newText = new StringBuilder();
        newText.append(TextWord[0]);
        for (int i = 0; i < TextWord.length - 1; i++) {
            String key = TextWord[i] + "#" + TextWord[i + 1];
            if (bridgeMap.containsKey(key)) {
                String bridge = bridgeMap.get(key).get(0);
                newText.append(" ").append(bridge).append(" ").append(TextWord[i + 1]);
            } else {
                newText.append(" ").append(TextWord[i + 1]);
            }
        }
        return newText.toString();
    }

    protected static void Floyd() {
        D = new int[wordNum][wordNum];
        path = new int[wordNum][wordNum];
        for (int i = 0; i < wordNum; i++) {
            for (int j = 0; j < wordNum; j++) {
                D[i][j] = E[i][j];
                path[i][j] = -1;
            }
        }
        for (int k = 0; k < wordNum; k++) {
            for (int i = 0; i < wordNum; i++) {
                for (int j = 0; j < wordNum; j++) {
                    if (D[i][k] + D[k][j] < D[i][j]) {
                        D[i][j] = D[i][k] + D[k][j];
                        path[i][j] = k;
                    }
                }
            }
        }
    }

    protected static String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!wordList.contains(word1) || !wordList.contains(word2)) {
            return "Invalid input!";
        }
        int start = wordList.indexOf(word1);
        int end = wordList.indexOf(word2);

        if (D[start][end] == INFINITY) {
            return "No path between \"" + word1 + "\" and \"" + word2 + "\"!";
        }

        pathWay.setLength(0);
        pathWay.append(word1 + " -> ");
        getPath(start, end);
        pathWay.append(word2);
        return "Shortest path: " + pathWay.toString() + "\nLength: " + D[start][end];
    }

    protected static void getPath(int start, int end) {
        if (path[start][end] == -1) {
            return;
        }
        getPath(start, path[start][end]);
        pathWay.append(wordList.get(path[start][end]) + " -> ");
    }

    protected static String randomWalk() {
        if (wordNum == 0) return "Empty graph!";
        int ranNum = random.nextInt(wordNum);
        String ranWord = wordList.get(ranNum);
        randomPath.setLength(0);
        randomPath.append(ranWord);

        flag = true;
        walkFrom(ranNum);
        return randomPath.toString();
    }

    protected static void walkFrom(int s) {
        for (int i = 0; i < wordNum; i++) {
            if (flag) {
                String edgePair = s + "#" + i;
                if (E[s][i] != INFINITY && !edgePairList.contains(edgePair)) {
                    edgePairList.add(edgePair);
                    randomPath.append(" -> ").append(wordList.get(i));
                    walkFrom(i);
                } else if (isEnd(s)) {
                    flag = false;
                }
            }
        }
    }

    protected static boolean isEnd(int s) {
        for (int i = 0; i < wordNum; i++) {
            String edgePair = s + "#" + i;
            if (E[s][i] != INFINITY && !edgePairList.contains(edgePair)) {
                return false;
            }
        }
        return true;
    }

    protected static void saveRandomWalk(String content) {
        try {
            File fo = new File("C:\\Users\\123qw\\qwerty\\software\\untitled\\randomwalk\\2.txt");
            FileWriter fileWriter = new FileWriter(fo);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PageRank 계산
    protected static Double calPageRank(String word) {
        word = word.toLowerCase();

        final double d = 0.85;
        final int maxIterations = 100;
        final double tolerance = 1.0e-6;

        Set<String> nodes = new HashSet<>(wordList);
        Map<String, Double> pr = new HashMap<>();

        for (String node : nodes) {
            pr.put(node, 1.0 / nodes.size());
        }

        Set<String> danglingNodes = new HashSet<>();
        for (String node : nodes) {
            boolean hasOutEdge = false;
            int idx = wordList.indexOf(node);
            for (int i = 0; i < wordNum; i++) {
                if (E[idx][i] != INFINITY) {
                    hasOutEdge = true;
                    break;
                }
            }
            if (!hasOutEdge) {
                danglingNodes.add(node);
            }
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> nextPr = new HashMap<>();
            double danglingSum = 0.0;

            for (String dn : danglingNodes) {
                danglingSum += pr.get(dn);
            }

            for (String node : nodes) {
                double rank = (1 - d) / nodes.size();
                rank += d * danglingSum / nodes.size();

                for (String other : nodes) {
                    int from = wordList.indexOf(other);
                    int to = wordList.indexOf(node);
                    if (E[from][to] != INFINITY) {
                        int outDegree = 0;
                        for (int k = 0; k < wordNum; k++) {
                            if (E[from][k] != INFINITY) outDegree++;
                        }
                        if (outDegree > 0) {
                            rank += d * pr.get(other) / outDegree;
                        }
                    }
                }
                nextPr.put(node, rank);
            }

            double diff = 0.0;
            for (String node : nodes) {
                diff += Math.abs(nextPr.get(node) - pr.get(node));
            }
            pr = nextPr;
            if (diff < tolerance) break;
        }

        return pr.getOrDefault(word, 0.0);
    }
    public void buildGraphFromFile(String filePath) throws IOException {
        preStr = new StringBuffer();
        wordList.clear();
        bridgeMap.clear();
        edgePairList.clear();
        wordNum = 0;

        InputStream fi = new FileInputStream(filePath);
        int c;
        while ((c = fi.read()) != -1) {
            Character m = new Character((char) c);
            if (Character.isLetter(m)) {
                preStr.append(m.toString());
            } else if (p.matcher(m.toString()).matches()) {
                preStr.append(" ");
            }
        }
        fi.close();

        TxtWordArray = preStr.toString().toLowerCase().trim().split("\\s+");
        for (String word : TxtWordArray) {
            if (!wordList.contains(word)) {
                wordList.add(word);
                wordNum++;
            }
        }

        E = new int[wordNum][wordNum];
        buildEdge();
        createBridgeMap();
        Floyd();
    }
    public String getDirectedGraphText() {
        StringBuilder result = new StringBuilder();
        result.append("=== Directed Graph ===\n");
        for (int i = 0; i < wordNum; i++) {
            for (int j = 0; j < wordNum; j++) {
                if (E[i][j] != INFINITY) {
                    String word1 = wordList.get(i);
                    String word2 = wordList.get(j);
                    result.append(word1).append(" -> ").append(word2)
                            .append(" (weight: ").append(E[i][j]).append(")\n");
                }
            }
        }
        return result.toString();
    }
}

