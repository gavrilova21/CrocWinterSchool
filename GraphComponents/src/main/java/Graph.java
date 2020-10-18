import java.util.HashSet;

public class Graph {
    int n; // число вершин
    HashSet<Integer>[] links;

    public Graph(int n){
        this.n = n;
        links = new HashSet[n];

        for(int i = 0; i < n ; i++){
            links[i] = new HashSet<Integer>();
        }
    }

    public HashSet<Integer>[] getGraph(){
        return links;
    }
    public void add(int edge1, int edge2){ // Добавление вершины
        links[edge1].add(edge2);
        links[edge2].add(edge1);
    }

    private void dfs(int e, boolean[] visited, HashSet<Integer> connected){
        visited[e] = true;
        connected.add(e);
        for (int x : links[e]) {
            if(!visited[x]) dfs(x, visited, connected);
        }
    }

    public HashSet<HashSet<Integer>> connectedComponents() {
        boolean[] visited = new boolean[n]; // создаем массив проходимых вершин
        HashSet <HashSet<Integer>> components = new HashSet<>();
        for(int v = 0; v < n; ++v) {
            if(!visited[v]) {
                HashSet<Integer> connected = new HashSet<>();
                dfs(v,visited, connected);
                components.add(connected);
            }
        }
        return components;
    }
}
