import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for building and analyzing a directed word graph from text.
 */
public class TextGraph {
    private int[][] edgeMatrix;
    private int[][] distance;
    private int[][] path;
    private String[] wordArray;
    private int wordCount;
    private static final int INFINITY = 10000;

    private final StringBuilder preStr = new StringBuilder();
    private final StringBuilder pathWay = new StringBuilder();
    private final StringBuilder randomPath = new StringBuilder();

    private final List<String> wordList = new ArrayList<>();
    private final List<String> edgePairList = new ArrayList<>();
    private final Map<String, List<String>> bridgeMap = new HashMap<>();
    private final Random random = new Random();

    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[.,\"\\?!:' ]");

    public void buildGraphFromFile(String filePath) throws IOException {
        wordList.clear();
        edgePairList.clear();
        bridgeMap.clear();
        preStr.setLength(0);

        try (InputStream input = new FileInputStream(filePath)) {
            int c;
            while ((c = input.read()) != -1) {
                char m = (char) c;
                if (Character.isLetter(m)) {
                    preStr.append(m);
                } else if (PUNCTUATION_PATTERN.matcher(Character.toString(m)).matches()) {
                    preStr.append(" ");
                }
            }
        }

        wordArray = preStr.toString().toLowerCase().trim().split("\\s+");
        for (String word : wordArray) {
            if (!wordList.contains(word)) {
                wordList.add(word);
            }
        }
        wordCount = wordList.size();
        edgeMatrix = new int[wordCount][wordCount];

        buildEdge();
        createBridgeMap();
        floyd();
    }

    public void buildGraphFromText(String text) {
        preStr.setLength(0);
        preStr.append(text);

        wordArray = preStr.toString().toLowerCase().trim().split("\\s+");

        wordList.clear();
        for (String word : wordArray) {
            if (!wordList.contains(word)) {
                wordList.add(word);
            }
        }

        wordCount = wordList.size();
        edgeMatrix = new int[wordCount][wordCount];

        buildEdge();
        createBridgeMap();
        floyd();
    }


    private void buildEdge() {
        for (int i = 0; i < wordCount; i++) {
            Arrays.fill(edgeMatrix[i], INFINITY);
        }

        String prev = null;
        for (String word : wordArray) {
            if (prev != null) {
                int from = wordList.indexOf(prev);
                int to = wordList.indexOf(word);
                if (edgeMatrix[from][to] == INFINITY) {
                    edgeMatrix[from][to] = 1;
                } else {
                    edgeMatrix[from][to]++;
                }
            }
            prev = word;
        }
    }

    private void createBridgeMap() {
        for (int i = 0; i < wordArray.length - 2; i++) {
            String key = wordArray[i] + "#" + wordArray[i + 2];
            bridgeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(wordArray[i + 1]);
        }
    }

    private void floyd() {
        distance = new int[wordCount][wordCount];
        path = new int[wordCount][wordCount];

        for (int i = 0; i < wordCount; i++) {
            for (int j = 0; j < wordCount; j++) {
                distance[i][j] = edgeMatrix[i][j];
                path[i][j] = -1;
            }
        }

        for (int k = 0; k < wordCount; k++) {
            for (int i = 0; i < wordCount; i++) {
                for (int j = 0; j < wordCount; j++) {
                    if (distance[i][k] + distance[k][j] < distance[i][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                        path[i][j] = k;
                    }
                }
            }
        }
    }

    public String getDirectedGraphText() {
        StringBuilder sb = new StringBuilder("=== Directed Graph ===\n");
        for (int i = 0; i < wordCount; i++) {
            for (int j = 0; j < wordCount; j++) {
                if (edgeMatrix[i][j] != INFINITY) {
                    sb.append(wordList.get(i))
                            .append(" -> ")
                            .append(wordList.get(j))
                            .append(" (weight: ")
                            .append(edgeMatrix[i][j])
                            .append(")\n");
                }
            }
        }
        return sb.toString();
    }

    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!wordList.contains(word1) || !wordList.contains(word2)) {
            return "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";
        }
        String key = word1 + "#" + word2;
        if (!bridgeMap.containsKey(key)) {
            return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
        }
        return "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " +
                String.join(", ", bridgeMap.get(key)) + ".";
    }

    public String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().trim().split("\\s+");
        StringBuilder newText = new StringBuilder(words[0]);
        for (int i = 0; i < words.length - 1; i++) {
            String key = words[i] + "#" + words[i + 1];
            if (bridgeMap.containsKey(key)) {
                String bridge = bridgeMap.get(key).get(0);
                newText.append(" ").append(bridge);
            }
            newText.append(" ").append(words[i + 1]);
        }
        return newText.toString();
    }

    public String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!wordList.contains(word1) || !wordList.contains(word2)) {
            return "Invalid input!";
        }
        int start = wordList.indexOf(word1);
        int end = wordList.indexOf(word2);
        if (distance[start][end] == INFINITY) {
            return "No path between \"" + word1 + "\" and \"" + word2 + "\"!";
        }

        pathWay.setLength(0);
        pathWay.append(word1).append(" -> ");
        getPath(start, end);
        pathWay.append(word2);

        return "Shortest path: " + pathWay + "\nLength: " + distance[start][end];
    }

    private void getPath(int start, int end) {
        if (path[start][end] == -1) {
            return;
        }
        getPath(start, path[start][end]);
        pathWay.append(wordList.get(path[start][end])).append(" -> ");
    }

    public String randomWalk() {
        if (wordCount == 0) {
            return "Empty graph!";
        }
        edgePairList.clear();
        randomPath.setLength(0);

        int start = random.nextInt(wordCount);
        String currentWord = wordList.get(start);
        randomPath.append(currentWord);
        walkFrom(start);

        return randomPath.toString();
    }

    private void walkFrom(int current) {
        for (int i = 0; i < wordCount; i++) {
            String edgeKey = current + "#" + i;
            if (edgeMatrix[current][i] != INFINITY && !edgePairList.contains(edgeKey)) {
                edgePairList.add(edgeKey);
                randomPath.append(" -> ").append(wordList.get(i));
                walkFrom(i);
                break;
            }
        }
    }

    public void saveRandomWalk(String content) {
        File output = new File("randomwalk_output.txt");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double calPageRank(String word) {
        word = word.toLowerCase();
        final double d = 0.85;
        final int maxIterations = 100;
        final double tolerance = 1.0e-6;

        Map<String, Double> rank = new HashMap<>();
        for (String node : wordList) {
            rank.put(node, 1.0 / wordCount);
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<String, Double> newRank = new HashMap<>();
            double diff = 0.0;

            for (String node : wordList) {
                double sum = 0.0;
                int j = wordList.indexOf(node);
                for (int i = 0; i < wordCount; i++) {
                    if (edgeMatrix[i][j] != INFINITY) {
                        int outDegree = 0;
                        for (int k = 0; k < wordCount; k++) {
                            if (edgeMatrix[i][k] != INFINITY) {
                                outDegree++;
                            }
                        }
                        if (outDegree > 0) {
                            sum += rank.get(wordList.get(i)) / outDegree;
                        }
                    }
                }
                newRank.put(node, (1 - d) / wordCount + d * sum);
                diff += Math.abs(newRank.get(node) - rank.get(node));
            }
            rank = newRank;
            if (diff < tolerance) {
                break;
            }
        }
        return rank.getOrDefault(word, 0.0);
    }
}
