import java.util.List;

public class DynamicNode {
    Node node;
    double costFromSource;
    List<String> pathTaken;
    int numberOfSeatChanges;
    int weekDay;


    public DynamicNode() {
    }

    public DynamicNode(Node node, double costFromSource, List<String> pathTaken, int numberOfSeatChanges) {
        this.node = node;
        this.costFromSource = costFromSource;
        this.pathTaken = pathTaken;
        this.numberOfSeatChanges = numberOfSeatChanges;
    }

    public DynamicNode(Node node, double costFromSource, List<String> pathTaken, int numberOfSeatChanges, int weekDay) {
        this.node = node;
        this.costFromSource = costFromSource;
        this.pathTaken = pathTaken;
        this.numberOfSeatChanges = numberOfSeatChanges;
        this.weekDay = weekDay;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getNumberOfSeatChanges() {
        return numberOfSeatChanges;
    }

    public void setNumberOfSeatChanges(int numberOfSeatChanges) {
        this.numberOfSeatChanges = numberOfSeatChanges;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public double getCostFromSource() {
        return costFromSource;
    }

    public void setCostFromSource(double costFromSource) {
        this.costFromSource = costFromSource;
    }

    public List<String> getPathTaken() {
        return pathTaken;
    }

    public void setPathTaken(List<String> pathTaken) {
        this.pathTaken = pathTaken;
    }
}
