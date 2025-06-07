import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextGraphTest2 {

    private TextGraph graph;

    @BeforeEach
    public void setUp() {
        graph = new TextGraph();
        graph.buildGraphFromText("the quick brown fox jumps over the lazy dog");
    }

    @Test
    public void testCase1_wordNotExist() {
        String result = graph.queryBridgeWords("hello", "world");
        assertEquals("No \"hello\" or \"world\" in the graph!", result);
    }

    @Test
    public void testCase2_noBridgeWords() {
        String result = graph.queryBridgeWords("quick", "jumps"); // 실제 bridge 없음
        assertEquals("No bridge words from \"quick\" to \"jumps\"!", result);
    }

    @Test
    public void testCase3_bridgeExists() {
        graph.buildGraphFromText("the over lazy fox");
        // lazy 앞에는 over, the 앞에는 lazy -> "the -> lazy" 사이에는 "over" 라는 bridge 존재
        String result = graph.queryBridgeWords("the", "lazy");
        assertEquals("The bridge words from \"the\" to \"lazy\" are: over.", result);
    }

    @Test
    public void testCase4_keyExistsButEmptyList() {
        // 예외적으로 존재하지 않는 경우
        String result = graph.queryBridgeWords("fox", "dog");
        assertEquals("No bridge words from \"fox\" to \"dog\"!", result);
    }
}
