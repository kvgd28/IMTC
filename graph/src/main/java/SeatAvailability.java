import scala.Serializable;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class SeatAvailability implements Serializable{
    Map<Tuple2<String,String>,Integer> ticketAvailability;

    public SeatAvailability(Map<Tuple2<String, String>, Integer> ticketAvailability) {
        this.ticketAvailability = ticketAvailability;
    }

    public Map<Tuple2<String, String>, Integer> getTicketAvailability() {
        return ticketAvailability;
    }

    public void setTicketAvailability(Map<Tuple2<String, String>, Integer> ticketAvailability) {
        this.ticketAvailability = ticketAvailability;
    }
}
