
import java.util.*;

public class DAG {

    private Set<String> vertices;
    private Map<String, List<String>> adjList = new HashMap<>();
    private Map<String, Integer> processingTimeMap;


    public DAG(final Set<String> vertices, final List<String[]> edges, final Map<String, Integer> processingTimeMap) {

        this.vertices = vertices;
        edges.forEach(this::addEdge);
        this.processingTimeMap = processingTimeMap;
    }

    private void addEdge(final String[] edge) {

        if (edge.length == 2 && vertices.contains(edge[0]) && vertices.contains(edge[1])) {
            final String source = edge[0];
            final String sink = edge[1];
            if (!adjList.containsKey(source))
                adjList.put(source, new ArrayList<>());
            adjList.get(source).add(sink);
        } else {
            throw new IllegalArgumentException(String.format("Edge %s have vertices length not equal to 2 or vertices not present in graph", Arrays.toString(edge)));
        }
    }

    private List<String> getAdjacentVertices(final String id) {

        return adjList.getOrDefault(id, new ArrayList<>());
    }

    private DAG reversed() {

        final DAG reversedGraph = new DAG(vertices, new ArrayList<>(), processingTimeMap);
        for (String source : vertices) {
            for (String sink : getAdjacentVertices(source)) {
                reversedGraph.addEdge(new String[] {sink, source});
            }
        }
        return reversedGraph;
    }

    public List<String> getDependencies(final String output) {

        final DAG reversed = reversed();

        Queue<String> front = new LinkedList<>();
        Map<String, Integer> timeStamp = new HashMap<>();

        front.offer(output);

        while (! front.isEmpty()) {
            final String current = front.poll();
            for(String adj : reversed.getAdjacentVertices(current)) {

                    front.offer(adj);
                    timeStamp.put(adj, timeStamp.getOrDefault(current, 0) + 1);
            }
        }

        final List<String> sortedKeys = new ArrayList(timeStamp.keySet());
        sortedKeys.sort((a, b) -> timeStamp.get(b) - timeStamp.get(a));
        return sortedKeys;
    }

    public int getTime(final List<String> vertices) {

        int elapsedTime = 0;
        for (String node : vertices) {
            final int processTime = processingTimeMap.getOrDefault(node, 0);
            if (processTime > 0)
                elapsedTime += processTime;
        }
        return elapsedTime;
    }

    // print the DAG if any user is interested in the DAG structure, not related to the problem
    @Override
    public String toString() {

        final StringBuilder serializedGraph = new StringBuilder();
        for(String node: adjList.keySet()){
            serializedGraph.append(String.format("%s -> %s \n", node, adjList.get(node)));
        };
        return serializedGraph.toString();
    }

    // Alternative demo in case the unit test cannot run
    public static void main(String[] args) {

        final Set<String> vertices = new HashSet<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"));
        final Map<String, Integer> timeMap = new HashMap<String, Integer>(){{
            put("A", -1);
            put("B", 2);
            put("C", 7);
            put("D", 4);
            put("F", 2);
            put("G", -1);
        }};
        final List<String[]> edges = new ArrayList<>(Arrays.asList(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"},
                new String[]{"D", "E"},
                new String[]{"E", "F"},
                new String[]{"E", "G"},
                new String[]{"D", "F"},
                new String[]{"B", "D"},
                new String[]{"H", "B"}
                ));
        final DAG testDag = new DAG(vertices, edges, timeMap);
        final List<String> actualResponseOfG = testDag.getDependencies("G");
        System.out.println(actualResponseOfG);
        System.out.println(testDag.getTime(actualResponseOfG));
    }
}
