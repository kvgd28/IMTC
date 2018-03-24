import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlet.ServletContextHandler;
import scala.Tuple2;
import scala.Tuple3;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer extends HttpServlet {
    public static Map<String,Train> allTrains = new HashMap<String, Train>();
    public static Map<String,Station> allStations = new HashMap<String, Station>();
    public static Map<Tuple3<String,String,String>,Set<String>> trainsBetweenStations = new HashMap<Tuple3<String, String, String>, Set<String>>();
    public static Map<Tuple2<Train,String>,SeatAvailability> seatAvailabilityMap = new HashMap<Tuple2<Train, String>, SeatAvailability>();

    public static void initialize(){
        // load all objects stored
        Map<String,Train> allTrainsTemp = importFromFile("C:\\Users\\gangu\\Documents\\IMTC\\" + "allTrains.sav");
        Map<String,Station> allStationsTemp= importFromFile("C:\\Users\\gangu\\Documents\\IMTC\\" + "allStations.sav");
        Map<Tuple3<String,String,String>,Set<String>> trainsBetweenStationsTemp = importFromFile("C:\\Users\\gangu\\Documents\\IMTC\\" + "trainsBetweenStations.sav");
        Map<Tuple2<Train,String>,SeatAvailability> seatAvailabilityMapTemp = importFromFile("C:\\Users\\gangu\\Documents\\IMTC\\" + "seatAvailabilityMap.sav");

        if(allStationsTemp!=null){
            allTrains = allTrainsTemp;
        }
        if(allStationsTemp!=null){
            allStations = allStationsTemp;
        }
        if(trainsBetweenStationsTemp!=null){
            trainsBetweenStations = trainsBetweenStationsTemp;
        }
        if(seatAvailabilityMapTemp!=null){
            seatAvailabilityMap = seatAvailabilityMapTemp;
        }
    }

    public static void main(String args[]){
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
        ServletContextHandler handler = new ServletContextHandler(server, "/imtc");
        handler.addServlet(MainServer.class, "/");
        handler.addServlet(SeatAvailabilityServlet.class,"/getSeatAvailability");
        try {
            initialize();
            server.start();
            //TODO write a thread which refreshes seat availabilty of existing trains for every particular interval of time for n days in future.
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Runnable worker = new ObjectUploaderThread();
            executor.execute(worker);
            executor.shutdown();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println("Please try getSeatAvailability endpoint here : http://localhost:8080/imtc/getSeatAvailability");
    }

    public static <T> T importFromFile(String filename){
        ObjectInputStream save = null;
        try{
            File file = new File(filename);
            if(!file.exists()){
                return null;
            }
            FileInputStream saveFile = new FileInputStream(filename);
            save = new ObjectInputStream(saveFile);
            T returnObject = (T) save.readObject();
            return returnObject;
        }
        catch(Exception exc){
            exc.printStackTrace(); // If there was an error, print the info.
        }finally {
            try {
                if(save!=null) {
                    save.close(); // This also closes saveFile.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void exportToFile(Object object, String fileName) {
        ObjectOutputStream save = null;
        try {
            FileOutputStream saveFile = new FileOutputStream("C:\\Users\\gangu\\Documents\\IMTC\\" + fileName);
            save = new ObjectOutputStream(saveFile);
            save.writeObject(object);
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            try {
                save.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
