import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectUploaderThread implements Runnable{
    public void run() {
        while(!Thread.interrupted() && MainServer.allTrains!=null && MainServer.allStations!=null && MainServer.seatAvailabilityMap!=null){
            try {
                Thread.sleep(120000l);
                MainServer.exportToFile(MainServer.allTrains,"allTrains.sav");
                MainServer.exportToFile(MainServer.allStations,"allStations.sav");
                MainServer.exportToFile(MainServer.trainsBetweenStations,"trainsBetweenStations.sav");
                MainServer.exportToFile(MainServer.seatAvailabilityMap,"seatAvailabilityMap.sav");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
