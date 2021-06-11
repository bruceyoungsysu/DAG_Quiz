
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DAGTest {

    private static final Set<String> TEST_VERTICES = new HashSet<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));
    private static final Map<String, Integer> TEST_PROCESS_TIME = new HashMap<String, Integer>(){{
        put("A", -1);
        put("B", 2);
        put("C", 7);
        put("D", 4);
        put("F", 2);
        put("G", -1);
    }};
    private static final List<String[]> TEST_VALID_EDGES = new ArrayList<>(Arrays.asList(
            new String[]{"A", "B"},
            new String[]{"A", "D"},
            new String[]{"B", "C"},
            new String[]{"D", "C"},
            new String[]{"D", "F"},
            new String[]{"C", "G"},
            new String[]{"F", "G"}
    ));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void  getDependencies_success() {

        // Arrange
        final DAG testDag = new DAG(TEST_VERTICES, TEST_VALID_EDGES, TEST_PROCESS_TIME);

        final List<String> expectedResponseOfG = new ArrayList<>(Arrays.asList(
                "A", "B", "D", "C", "F"
        ));
        final List<String> expectedResponseOfC = new ArrayList<>(Arrays.asList(
                "A", "B", "D"
        ));

        // Act
        final List<String> actualResponseOfG = testDag.getDependencies("G");
        final List<String> actualResponseOfC = testDag.getDependencies("C");

        // Assert
        assertEquals(actualResponseOfG, expectedResponseOfG);
        assertEquals(actualResponseOfC, expectedResponseOfC);
    }

    @Test
    public void getDependencies_twoOutputs_success() {

        // Arrange
        final List<String[]> TEST_EDGES_TWO_BRANCHES = new ArrayList<>(Arrays.asList(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"},
                new String[]{"A", "E"},
                new String[]{"E", "F"},
                new String[]{"F", "G"}
        ));
        final DAG testDag = new DAG(TEST_VERTICES, TEST_EDGES_TWO_BRANCHES, TEST_PROCESS_TIME);
        final List<String> expectedResponse = new ArrayList<>(Arrays.asList(
                "A", "B", "C"
        ));

        // Act
        final List<String> actualResponse = testDag.getDependencies("D");

        // Assert
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void getDependencies_vertexNotExist_success() {

        // Arrange
        final DAG testDag = new DAG(TEST_VERTICES, TEST_VALID_EDGES, TEST_PROCESS_TIME);
        final List<String> expectedResponse = new ArrayList<>();

        // Act
        final List<String> actualResponse = testDag.getDependencies("Z");

        // Assert
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void getDependencies_emptyGraph_success() {

        // Arrange
        final DAG testDag = new DAG(new HashSet<>(), new ArrayList<>(), new HashMap<>());
        final List<String> expectedResponse = new ArrayList<>();

        // Act
        final List<String> actualResponse = testDag.getDependencies("Z");

        // Assert
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void getDependencies_withInvalidEdgeLength_expectIllegalArgumentException() {
        final String[] invalidEdge = new String[]{"A", "B", "C"};

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Edge %s have vertices length not equal to 2 or vertices not present in graph", Arrays.toString(invalidEdge)));

        final List<String[]> INVALID_EDGES = new ArrayList<>(Arrays.asList(
                invalidEdge,
                new String[]{"A", "D"}
        ));
        final DAG testDag = new DAG(TEST_VERTICES, INVALID_EDGES, TEST_PROCESS_TIME);

    }

    @Test
    public void getDependencies_withInvalidVertex_expectIllegalArgumentException() {

        final String[] invalidEdge = new String[]{"A", "Z"};

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Edge %s have vertices length not equal to 2 or vertices not present in graph", Arrays.toString(invalidEdge)));

        final List<String[]> INVALID_EDGES = new ArrayList<>(Arrays.asList(
                invalidEdge,
                new String[]{"A", "D"}
        ));
        final DAG testDag = new DAG(TEST_VERTICES, INVALID_EDGES, TEST_PROCESS_TIME);

    }

    @Test
    public void getTime_success() {
        final DAG testDag = new DAG(TEST_VERTICES, TEST_VALID_EDGES, TEST_PROCESS_TIME);

        final int expectedElapsedTime = 15;

        final int actualElapsedTime = testDag.getTime(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));

        assertEquals(actualElapsedTime, expectedElapsedTime);
    }

    @Test
    public void getTime_emptyInput_success() {
        final DAG testDag = new DAG(TEST_VERTICES, TEST_VALID_EDGES, TEST_PROCESS_TIME);

        final int expectedElapsedTime = 0;

        final int actualElapsedTime = testDag.getTime(Collections.emptyList());

        assertEquals(actualElapsedTime, expectedElapsedTime);
    }
}
