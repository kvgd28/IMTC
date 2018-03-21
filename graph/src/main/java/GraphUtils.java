import scala.Tuple2;

import java.util.*;

public class GraphUtils {

    public static void createNodesForOneTrain(Map<String, Station> existingStations, Train train){
        List<Node> prevNodes = new ArrayList<Node>();
        List<String> stationCodes = train.getStations();
        Map<Tuple2<String,String>, Double> ticketCostMap = train.getTicketCostMap();
        Map<Tuple2<String,String>, Double> travelTimeMap = train.getTravelTimeMap();
        if(stationCodes==null || travelTimeMap==null || ticketCostMap==null){
            throw new IllegalArgumentException("Train doesn't has enough information to create nodes");
        }
        for(int i=0;i<stationCodes.size();i++){
            String stationCode = stationCodes.get(i);
            if(!existingStations.keySet().contains(stationCode)){
                Station station = new Station(stationCode);
                existingStations.put(stationCode,station);
            }
            Node node = new Node(existingStations.get(stationCode), train);
            existingStations.get(stationCode).addNode(node);
            prevNodes.add(node);

            for(int j=0;j<i;j++) {
                Node prevNode = prevNodes.get(j);
                if (prevNode != null) {
                    Double ticketCost = ticketCostMap.get(new Tuple2<String, String>(prevNode.getStation().getStationCode(), stationCode));
                    Double travelTime = travelTimeMap.get(new Tuple2<String, String>(prevNode.getStation().getStationCode(), stationCode));
                    Edge edge = new Edge(prevNode, existingStations.get(stationCode), travelTime, ticketCost);
                    prevNode.addEdge(edge);
                }
            }
        }
    }


    public static List<String> applyBFS(String startStationCode, String endStationCode, Map<String,Station> existingStations, Map<Tuple2<String,String>,List<Train>> ticketAvailability,int weekDay){
        PriorityQueue<DynamicNode> queue = new PriorityQueue<DynamicNode>(
                new Comparator<DynamicNode>() {
                    public int compare(DynamicNode o1, DynamicNode o2) {
                        return Double.compare(o1.costFromSource,o2.costFromSource);
                    }
                }
        );

        Set<Node> encounteredNodes = new HashSet<>();

        //initialisation
        for(Node node : existingStations.get(startStationCode).getNodes()){
            List<String> pathTaken = new ArrayList<>();
            pathTaken.add(startStationCode);
            DynamicNode dynamicNode = new DynamicNode(node,0,pathTaken,0,weekDay);
            queue.add(dynamicNode);
        }

        while(queue.size()>0){
            DynamicNode top = queue.poll();
            Node currentNode = top.getNode();
            if(currentNode.getStation().getStationCode().equals(endStationCode)){
                return top.getPathTaken();
            }
            if(encounteredNodes.contains(currentNode)){
                continue;
            }
            Set<Edge> edges = top.getNode().getEdges();
            for(Edge edge : edges){
                if(isTicketAvailable(edge,ticketAvailability)) {
                    for (Node nextLevelNode : edge.getEndStation().getNodes()) {
                        if (!encounteredNodes.contains(nextLevelNode)) {
                            int nextWeekDay = (top.getWeekDay() + (int)((currentNode.getTrain().getDepTimeFromStationOnADay(currentNode.getStation().getStationCode(),weekDay) +
                                    edge.getTravelTime())/86400))%7;
                            if(isWithinTime(currentNode,nextLevelNode,edge.getTravelTime(),top.getWeekDay(),nextWeekDay)) {
                                int numberOfSeatChanges = top.getNumberOfSeatChanges() + 1;
                                List<String> pathTaken = new ArrayList<>();
                                pathTaken.addAll(top.pathTaken);
                                pathTaken.add(edge.getEndStation().getStationCode());
                                Double trainChangeCost = 0.0;
                                if (currentNode.getTrain() != nextLevelNode.getTrain()) {
                                    trainChangeCost = 1000.0;
                                }
                                Double seatChangeCost = 500.0;

                                Double waitingTimeCost = (nextLevelNode.getTrain().getDepTimeFromStationOnADay(nextLevelNode.getStation().getStationCode(),nextWeekDay)-
                                        (int)((currentNode.getTrain().getDepTimeFromStationOnADay(currentNode.getStation().getStationCode(),top.getWeekDay())+edge.getTravelTime())/86400)) * 0.1;
                                Double costFromSource = top.getCostFromSource() + edge.costAcrossTheEdge() + trainChangeCost + seatChangeCost + waitingTimeCost;


                                DynamicNode newDynamicNode = new DynamicNode(nextLevelNode, costFromSource, pathTaken, numberOfSeatChanges,nextWeekDay);
                                queue.add(newDynamicNode);
                            }
                        }
                    }
                }
            }
            encounteredNodes.add(currentNode);
        }

        return null;
    }


    public static List<String> applyBFSForOneTrain(String startStationCode, String endStationCode, Map<String,Station> existingStations, Map<Tuple2<String,String>,List<Train>> ticketAvailability,int weekDay){
        PriorityQueue<DynamicNode> queue = new PriorityQueue<DynamicNode>(
                new Comparator<DynamicNode>() {
                    public int compare(DynamicNode o1, DynamicNode o2) {
                        return Double.compare(o1.costFromSource,o2.costFromSource);
                    }
                }
        );

        Set<Node> encounteredNodes = new HashSet<>();

        //initialisation
        for(Node node : existingStations.get(startStationCode).getNodes()){
            if(node.getTrain().isTrainRunsOnWday(weekDay)) {
                List<String> pathTaken = new ArrayList<>();
                pathTaken.add(startStationCode);
                DynamicNode dynamicNode = new DynamicNode(node, 0, pathTaken, 0);
                queue.add(dynamicNode);
            }
        }

        while(queue.size()>0){
            DynamicNode top = queue.poll();
            Node currentNode = top.getNode();
            if(currentNode.getStation().getStationCode().equals(endStationCode)){
                return top.getPathTaken();
            }
            if(encounteredNodes.contains(currentNode)){
                continue;
            }
            Set<Edge> edges = top.getNode().getEdges();
            for(Edge edge : edges){
                if(isTicketAvailable(edge,ticketAvailability)) {
                    for (Node nextLevelNode : edge.getEndStation().getNodes()) {
                        if(nextLevelNode.getTrain().equals(currentNode.getTrain())) {
                            if (!encounteredNodes.contains(nextLevelNode)) {
                                int numberOfSeatChanges = top.getNumberOfSeatChanges() + 1;
                                List<String> pathTaken = new ArrayList<>();
                                pathTaken.addAll(top.pathTaken);
                                pathTaken.add(edge.getEndStation().getStationCode());
                                Double seatChangeCost = 500.0;
                                Double costFromSource = top.getCostFromSource() + edge.costAcrossTheEdge() + seatChangeCost;

                                DynamicNode newDynamicNode = new DynamicNode(nextLevelNode, costFromSource, pathTaken, numberOfSeatChanges);
                                queue.add(newDynamicNode);

                            }
                        }
                    }
                }
            }
            encounteredNodes.add(currentNode);
        }

        return null;
    }

    private static Boolean isWithinTime(Node currentNode, Node nextLevelNode, Double travelTime, int currentWeekDay, int nextWeekDay){
        Double currentNodeTime = currentNode.getTrain().getDepTimeFromStationOnADay(currentNode.getStation().getStationCode(),currentWeekDay);
        Double nextNodeTime = nextLevelNode.getTrain().getDepTimeFromStationOnADay(nextLevelNode.getStation().getStationCode(),nextWeekDay);
        if(currentWeekDay==nextWeekDay) {
            if (currentNodeTime <= nextNodeTime) {
                return (currentNodeTime + travelTime) <= nextNodeTime;
            }
        }else{
            return (currentNodeTime + travelTime) - 86400 <= nextNodeTime;
        }
        return false;
    }

    private static Boolean isTicketAvailable(Edge edge, Map<Tuple2<String,String>,List<Train>> ticketAvailability){
        try {
            Tuple2<String, String> tuple = new Tuple2<>(edge.startStationNode.getStation().getStationCode(), edge.getEndStation().getStationCode());
            if (ticketAvailability.containsKey(tuple)) {
                if (ticketAvailability.get(tuple).contains(edge.getStartStationNode().getTrain())) {
                    return true;
                }
            }
        }catch(Exception e){

        }
        return false;
    }

    public static void main(String[] args){
        List<String> stationCodes = new ArrayList<String>();
        stationCodes.add("s1");
        stationCodes.add("s2");
        stationCodes.add("s3");
        stationCodes.add("s4");
        Map<Integer,Boolean> runningDays = new HashMap<>();
        runningDays.put(1,true);
        Train train = new Train("123");
        train.setDays(runningDays);
        train.setStations(stationCodes);
        Map<String,Station> existingStations = new HashMap<String, Station>();
        Map<Tuple2<String,String>, Double> ticketCostMap = new HashMap<Tuple2<String, String>, Double>();
        ticketCostMap.put(new Tuple2<String, String>("s1","s2"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s3"),2.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s4"),3.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s3"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s4"),2.0);
        ticketCostMap.put(new Tuple2<String, String>("s3","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s3.5","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s3.5"),2.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s3.5"),1.0);
        train.setTicketCostMap(ticketCostMap);
        Map<Tuple2<String,String>, Double> travelTimeMap = new HashMap<Tuple2<String, String>, Double>();
        travelTimeMap.put(new Tuple2<String, String>("s1","s2"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s3"),2.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s4"),3.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s3"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s4"),2.0);
        travelTimeMap.put(new Tuple2<String, String>("s3","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s3.5","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s3.5"),2.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s3.5"),1.0);
        train.setTravelTimeMap(travelTimeMap);

        List<String> stationCodes2 = new ArrayList<String>();
        stationCodes2.add("s1");
        stationCodes2.add("s2");
        stationCodes2.add("s3.5");
        stationCodes2.add("s4");
        Train train2 = new Train("345");
        train2.setDays(runningDays);
        train2.setStations(stationCodes2);
        train2.setTravelTimeMap(travelTimeMap);
        train2.setTicketCostMap(ticketCostMap);

        createNodesForOneTrain(existingStations, train);

        createNodesForOneTrain(existingStations, train2);

        //System.out.println(existingStations.get("s2").getNode(train).edges.size());

        Map<Tuple2<String,String>,List<Train>> ticketAvailability = new HashMap<>();
        List<Train> firstTrains = new ArrayList<>();
        firstTrains.add(train);
        ticketAvailability.put(new Tuple2<>("s1","s4"),firstTrains);
        ticketAvailability.put(new Tuple2<>("s1","s2"),firstTrains);
        ticketAvailability.put(new Tuple2<>("s1","s3"),firstTrains);
        ticketAvailability.put(new Tuple2<>("s2","s3"),firstTrains);
        ticketAvailability.put(new Tuple2<>("s2","s4"),firstTrains);
        ticketAvailability.put(new Tuple2<>("s3","s4"),firstTrains);

        List<Train> secondTrains = new ArrayList<>();
        secondTrains.add(train2);
        ticketAvailability.put(new Tuple2<>("s1","s2"),secondTrains);

        System.out.println(applyBFSForOneTrain("s1","s4",existingStations, ticketAvailability, 1));

    }
}
