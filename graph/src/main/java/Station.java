import java.util.HashSet;
import java.util.Set;

public class Station {
    String stationCode;
    Set<Node> nodes;

    public Station() {
    }

    public Station(String stationCode) {
        this.stationCode = stationCode;
    }

    public Station(String stationCode, Set<Node> nodes) {
        this.stationCode = stationCode;
        this.nodes = nodes;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node node){
        if(node!=null) {
            if (nodes == null) {
                nodes = new HashSet<Node>();
            }
            nodes.add(node);
        }
    }

    public Node getNode(Train train){
        for(Node node : nodes){
            if(node.train==train){
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return stationCode.equals(station.stationCode);
    }

    @Override
    public int hashCode() {
        return stationCode.hashCode();
    }

    public Boolean hasNode(Node node){
        return this.nodes.contains(node);
    }
}
