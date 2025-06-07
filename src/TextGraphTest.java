import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextGraphTest {

    private TextGraph graph;

    @BeforeEach
    public void setUp() {
        graph = new TextGraph();
    }

    @Test
    public void testBuildGraphFromText() {
        String sample = "the quick brown fox jumps over the lazy dog";
        graph.buildGraphFromText(sample);

        String directedGraph = graph.getDirectedGraphText();
        assertTrue(directedGraph.contains("the -> quick"));
        assertTrue(directedGraph.contains("fox -> jumps"));
        assertTrue(directedGraph.contains("lazy -> dog"));
    }

    @Test
    public void testCase2_emptyInput() {
        graph.buildGraphFromText("");
        assertEquals("=== Directed Graph ===\n", graph.getDirectedGraphText());
    }

    @Test
    public void testCase3_singleWord() {
        graph.buildGraphFromText("hello");
        assertEquals("=== Directed Graph ===\n", graph.getDirectedGraphText());
    }

    @Test
    public void testCase4_bridgeWordExists() {
        String input = "the quick fox jumps over the lazy dog";
        graph.buildGraphFromText(input);
        String bridge = graph.queryBridgeWords("quick", "jumps");
        assertTrue(bridge.contains("fox")); // 정확히 포함 여부만 확인
    }
}
