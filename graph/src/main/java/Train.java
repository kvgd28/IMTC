import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Train {
    String trainNumber;
    Map<String,Departure> departureTimes;
    List<String> stations;
    Set<String> classes;
    Map<Integer,Boolean> days;
    Map<Tuple2<String,String>, Double> ticketCostMap;
    Map<Tuple2<String,String>, Double> travelTimeMap;

    public Train(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Map<Tuple2<String, String>, Double> getTicketCostMap() {
        return ticketCostMap;
    }

    public void setTicketCostMap(Map<Tuple2<String, String>, Double> ticketCostMap) {
        this.ticketCostMap = ticketCostMap;
    }

    public Map<Tuple2<String, String>, Double> getTravelTimeMap() {
        return travelTimeMap;
    }

    public void setTravelTimeMap(Map<Tuple2<String, String>, Double> travelTimeMap) {
        this.travelTimeMap = travelTimeMap;
    }

    public Map<Integer, Boolean> getDays() {
        return days;
    }

    public void setDays(Map<Integer, Boolean> days) {
        this.days = days;
    }

    public Set<String> getClasses() {
        return classes;
    }

    public void setClasses(Set<String> classes) {
        this.classes = classes;
    }

    public Map<String, Departure> getDepartureTimes() {
        return departureTimes;
    }

    public void setDepartureTimes(Map<String, Departure> departureTimes) {
        this.departureTimes = departureTimes;
    }

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Double getDepTimeFromStationOnADay(String fromStation, int weekDay){
        if(this.departureTimes!=null) {
            return this.departureTimes.get(fromStation).getDepartureTimeOnADay(weekDay);
        }
        return null;
    }

    public Boolean isTrainRunsOnWday(int wday){
        if(days!=null) {
            return days.get(wday);
        }
        return false;
    }
}
