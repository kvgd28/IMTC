import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphUtils {

    public static void createNodesForOneTrain(List<String> stationCodes, Map<String, Station> existingStations, Train train,
                                       Map<Tuple2<String,String>, Double> ticketCostMap,
                                       Map<Tuple2<String,String>, Double> travelTimeMap){
        List<Node> prevNodes = new ArrayList<Node>();
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


    public static void main(String[] args){
        List<String> stationCodes = new ArrayList<String>();
        stationCodes.add("s1");
        stationCodes.add("s2");
        stationCodes.add("s3");
        stationCodes.add("s4");
        Train train = new Train("123");
        Map<String,Station> existingStations = new HashMap<String, Station>();
        Map<Tuple2<String,String>, Double> ticketCostMap = new HashMap<Tuple2<String, String>, Double>();
        ticketCostMap.put(new Tuple2<String, String>("s1","s2"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s3"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s3"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s3","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s3.5","s4"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s1","s3.5"),1.0);
        ticketCostMap.put(new Tuple2<String, String>("s2","s3.5"),1.0);
        Map<Tuple2<String,String>, Double> travelTimeMap = new HashMap<Tuple2<String, String>, Double>();
        travelTimeMap.put(new Tuple2<String, String>("s1","s2"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s3"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s3"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s3","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s3.5","s4"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s1","s3.5"),1.0);
        travelTimeMap.put(new Tuple2<String, String>("s2","s3.5"),1.0);

        List<String> stationCodes2 = new ArrayList<String>();
        stationCodes2.add("s1");
        stationCodes2.add("s2");
        stationCodes2.add("s3.5");
        stationCodes2.add("s4");
        Train train2 = new Train("345");

        createNodesForOneTrain(stationCodes, existingStations, train, ticketCostMap, travelTimeMap);

        createNodesForOneTrain(stationCodes2, existingStations, train2, ticketCostMap, travelTimeMap);

        System.out.println(existingStations.get("s2").getNode(train).edges.size());
    }
}
