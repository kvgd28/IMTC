import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    Set<Edge> edges;
    Station station;
    Train train;

    public Node() {
    }

    public Node(Station station, Train train) {
        this.station = station;
        this.train = train;
    }

    public Node(Set<Edge> edges, Station station, Train train) {
        this.edges = edges;
        this.station = station;
        this.train = train;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
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

        if (!station.equals(node.station)) return false;
        return train.equals(node.train);
    }

    @Override
    public int hashCode() {
        int result = station.hashCode();
        result = 31 * result + train.hashCode();
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
