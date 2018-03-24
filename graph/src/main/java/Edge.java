import java.io.Serializable;

public class Edge implements Serializable{
    Node startStationNode;
    Station endStation;
    Double travelTime;
    Double ticketCost;

    public Edge() {
    }

    public Edge(Node startStationNode, Station endStation, double travelTime, double ticketCost) {
        this.startStationNode = startStationNode;
        this.endStation = endStation;
        this.travelTime = travelTime;
        this.ticketCost = ticketCost;
    }

    public Node getStartStationNode() {
        return startStationNode;
    }

    public void setStartStationNode(Node startStationNode) {
        this.startStationNode = startStationNode;
    }

    public Station getEndStation() {
        return endStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(double travelTime) {
        this.travelTime = travelTime;
    }

    public double getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(double ticketCost) {
        this.ticketCost = ticketCost;
    }

    public double costAcrossTheEdge(){
        return travelTime*0.1 + ticketCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!startStationNode.equals(edge.startStationNode)) return false;
        return endStation.equals(edge.endStation);
    }

    @Override
    public int hashCode() {
        int result = startStationNode.hashCode();
        result = 31 * result + endStation.hashCode();
        return result;
    }
}
