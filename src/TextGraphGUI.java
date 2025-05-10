import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TextGraphGUI extends JFrame {
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton runButton, loadButton;
    private JComboBox<String> functionBox;

    private TextGraph textGraph;
    private String filePath;

    public TextGraphGUI() {
        setTitle("TextGraph GUI");
        setSize(420, 460);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel functionPanel = new JPanel(new BorderLayout(5, 5));
        functionBox = new JComboBox<>(new String[]{
                "Show Directed Graph",
                "Query Bridge Words",
                "Generate New Text",
                "Calculate Shortest Path",
                "Random Walk",
                "Calculate PageRank"
        });
        functionBox.setPreferredSize(new Dimension(300, 30));
        functionBox.setMaximumSize(new Dimension(300, 30));
        functionPanel.add(functionBox, BorderLayout.CENTER);

        loadButton = new JButton("Load File");
        loadButton.setPreferredSize(new Dimension(100, 30));
        loadButton.setMaximumSize(new Dimension(100, 30));
        JPanel loadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        loadPanel.add(loadButton);
        functionPanel.add(loadPanel, BorderLayout.EAST);

        topPanel.add(functionPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(300, 30));
        inputField.setMaximumSize(new Dimension(300, 30));
        inputPanel.add(inputField, BorderLayout.CENTER);

        runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(100, 30));
        runButton.setMaximumSize(new Dimension(100, 30));
        JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        runPanel.add(runButton); //변경
        inputPanel.add(runPanel, BorderLayout.EAST);

        topPanel.add(inputPanel);

        add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(420, 220));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadFile());
        runButton.addActionListener(e -> runFunction());
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filePath = file.getAbsolutePath();
            try {
                textGraph = new TextGraph();
                textGraph.buildGraphFromFile(filePath);
                JOptionPane.showMessageDialog(this, "File loaded successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    private void runFunction() {
        if (textGraph == null) {
            outputArea.setText("Please load a text file first.");
            return;
        }
        String input = inputField.getText().trim();
        int selectedFunction = functionBox.getSelectedIndex();
        try {
            switch (selectedFunction) {
                case 0 -> outputArea.setText(textGraph.getDirectedGraphText());
                case 1 -> {
                    String[] bridgeWords = input.split("\\s+");
                    if (bridgeWords.length != 2) {
                        outputArea.setText("Please enter exactly two words separated by space.");
                    } else {
                        outputArea.setText(textGraph.queryBridgeWords(bridgeWords[0], bridgeWords[1]));
                    }
                }
                case 2 -> outputArea.setText(textGraph.generateNewText(input));
                case 3 -> {
                    String[] shortestPathWords = input.split("\\s+");
                    if (shortestPathWords.length != 2) {
                        outputArea.setText("Please enter exactly two words separated by space.");
                    } else {
                        outputArea.setText(textGraph.calcShortestPath(shortestPathWords[0], shortestPathWords[1]));
                    }
                }
                case 4 -> outputArea.setText(textGraph.randomWalk());
                case 5 -> outputArea.setText("PageRank: " + textGraph.calPageRank(input));
                default -> outputArea.setText("Invalid function selected.");
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        SwingUtilities.invokeLater(() -> new TextGraphGUI().setVisible(true));
    }
}