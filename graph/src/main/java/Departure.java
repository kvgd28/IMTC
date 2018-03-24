import java.io.Serializable;

public class Departure implements Serializable{
    Double[] departureTimes;

    Departure(){
        departureTimes = new Double[7];
    }

    public Departure(Double[] departureTimes) {
        this.departureTimes = departureTimes;
    }

    public Double getDepartureTimeOnADay(int day){
        return departureTimes[day];
    }


}
