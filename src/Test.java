import java.io.IOException;
import java.nio.file.Paths;

import fr.umlv.graph.Edge;
import fr.umlv.graph.Graph;

public class Test {
  public static void dfs(Graph graph, int vertex, boolean[] visit) {
    visit[vertex] = true;
    System.out.println("Je visite " + vertex);
    
    for (Edge e : graph.adjacent(vertex)) {
      /* Si on prend l'arête dans le bon sens */
      if (e.getFrom() == vertex) {
        if (!visit[e.getTo()]) {
          dfs(graph, e.getTo(), visit);
        }
      }
    }
  }

  public static void testGraph() throws IOException {
    int n = 5;
    Graph graph = new Graph(n * n + 2);

    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n; j++) {
        graph.addEdge(new Edge(n * i + j, n * (i + 1) + j, 1664 - (i + j), 10 * j));
      }
    }

    for (int j = 0; j < n; j++) {
      graph.addEdge(new Edge(n * (n - 1) + j, n * n, 666, 10 * j));
    }

    for (int j = 0; j < n; j++) {
      graph.addEdge(new Edge(n * n + 1, j, 10 * j, 10 * j));
    }

    graph.addEdge(new Edge(13, 17, 1337, 0));
    graph.writeFile(Paths.get("test.dot"));

    // dfs à partir du sommet 3
    boolean[] visit = new boolean[n * n + 2];
    dfs(graph, 3, visit);
  }

  public static void main(String[] args) throws IOException {
    testGraph();
  }
}
