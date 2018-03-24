import org.eclipse.jetty.http.HttpStatus;
import scala.Tuple2;
import scala.Tuple3;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class SeatAvailabilityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String source = req.getParameter("source");
        String destination = req.getParameter("destination");
        String travelDate = req.getParameter("date");
        String numberOfSeatsRequired = req.getParameter("noOfSeats");
        if(source==null || destination==null || travelDate==null || numberOfSeatsRequired==null){
            resp.setStatus(HttpStatus.OK_200);
            resp.getWriter().println("Invalid parameters");
        }

        Set<String> trainsBetweenStations = MainServer.trainsBetweenStations.get(new Tuple3<String, String, String>(source,destination,travelDate));
        if(trainsBetweenStations==null) {
            trainsBetweenStations = ApiUtils.getTrainsBetweenStations(source, destination, travelDate);
            if(trainsBetweenStations!=null && !trainsBetweenStations.isEmpty()) {
                MainServer.trainsBetweenStations.put(new Tuple3<String, String, String>(source, destination, travelDate), trainsBetweenStations);
            }
        }
        for(String trainNumber : trainsBetweenStations){
            Train tempTrain = new Train(trainNumber);
            if(!MainServer.allTrains.containsKey(tempTrain.getTrainNumber())){
                Train train = ApiUtils.getTrainDetails(trainNumber);
                if(train!=null) {
                    MainServer.allTrains.put(trainNumber, train);
                    GraphUtils.createNodesForOneTrain(MainServer.allStations, train);
                }
            }
        }

        if(MainServer.allTrains.get("16382")!=null && MainServer.seatAvailabilityMap.get(new Tuple2<Train, String>(MainServer.allTrains.get("16382"),"28-03-2018"))!=null &&
                MainServer.seatAvailabilityMap.get(new Tuple2<Train, String>(MainServer.allTrains.get("16382"),"28-03-2018")).getTicketAvailability()!=null) {
            SeatAvailability seatAvailabilityTemp = new SeatAvailability(ApiUtils.getSeatAvailabilityForATrain(MainServer.allTrains.get("16382"), "28-03-2018"));
            MainServer.seatAvailabilityMap.put(new Tuple2<Train, String>(MainServer.allTrains.get("16382"), "28-03-2018"), seatAvailabilityTemp);
        }

        List<String> leastCostPath = null;
        if(MainServer.allTrains!=null) {
            for (Train train : MainServer.allTrains.values()) {
                SeatAvailability seatAvailability = MainServer.seatAvailabilityMap.get(new Tuple2<Train, String>(train, travelDate));
                if (seatAvailability != null) {
                    List<String> resultForThisTrain = GraphUtils.applyBFSForOneTrain(source, destination, MainServer.allStations, seatAvailability, travelDate, Integer.parseInt(numberOfSeatsRequired));
                    if (leastCostPath == null || (resultForThisTrain != null && resultForThisTrain.size() < leastCostPath.size())) {
                        leastCostPath = resultForThisTrain;
                    }
                } else {
                    System.out.println("Seat availability not available for the train : " + train.getTrainNumber());
                }
            }
        }
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(leastCostPath);
    }
}
