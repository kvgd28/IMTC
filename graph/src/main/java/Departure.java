public class Departure {
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
