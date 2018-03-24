import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Node implements Serializable{
    Set<Edge> edges;
    String stationCode;
    Train train;

    public Node() {
    }

    public Node(String stationCode, Train train) {
        this.stationCode = stationCode;
        this.train = train;
    }

    public Node(Set<Edge> edges, String stationCode, Train train) {
        this.edges = edges;
        this.stationCode = stationCode;
        this.train = train;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (stationCode != null ? !stationCode.equals(node.stationCode) : node.stationCode != null) return false;
        return train != null ? train.equals(node.train) : node.train == null;
    }

    @Override
    public int hashCode() {
        int result = stationCode != null ? stationCode.hashCode() : 0;
        result = 31 * result + (train != null ? train.hashCode() : 0);
        return result;
    }

    public void addEdge(Edge edge){
        if(edge!=null){
            if(edges==null){
                edges = new HashSet<Edge>();
            }
            edges.add(edge);
        }
    }
}
