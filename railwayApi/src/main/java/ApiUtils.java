import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import scala.Tuple2;

import java.util.*;

public class ApiUtils {
    public static final String myApiKey = "1ppsmkl7jm";
    public static final Map<String,Integer> dayMap;
    static{
        dayMap = new HashMap<String, Integer>();
        dayMap.put("MON",1);
        dayMap.put("TUE",2);
        dayMap.put("WED",3);
        dayMap.put("THU",4);
        dayMap.put("FRI",5);
        dayMap.put("SAT",6);
        dayMap.put("SUN",0);
    }
    public static Set<String> getTrainsBetweenStations(String sourceStationCode, String destStationCode, String dateOfJourney){
        Set<String> result = new HashSet<String>();
        String url = "https://api.railwayapi.com/v2/between/source/%s/dest/%s/date/%s/apikey/%s/";
        url = String.format(url,sourceStationCode,destStationCode,dateOfJourney,myApiKey);
        String json = WebConnector.getResponseFromWeb(url);
        JsonParser jsonParser = new JsonParser();
        try {
            JsonObject mainJson = jsonParser.parse(json).getAsJsonObject();
            JsonArray trains = mainJson.getAsJsonArray("trains");
            for(JsonElement train : trains){
                result.add(train.getAsJsonObject().get("number").getAsString());
            }
            return result;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Train getTrainDetails(String trainNumber){
        Train train = new Train(trainNumber);
        String url = "https://api.railwayapi.com/v2/route/train/%s/apikey/%s/";
        url = String.format(url,train.getTrainNumber(),myApiKey);
        String json = WebConnector.getResponseFromWeb(url);
        JsonParser jsonParser = new JsonParser();
        try {
            JsonObject mainJson = jsonParser.parse(json).getAsJsonObject();
            JsonObject trainInfo = mainJson.getAsJsonObject("train");
            train.setTrainNumber(trainInfo.get("number").getAsString());

            JsonArray classes = trainInfo.getAsJsonArray("classes");
            Set<String> classesAvailable = new HashSet<String>();
            for(JsonElement classElement : classes){
                String classCode = classElement.getAsJsonObject().get("code").getAsString();
                classesAvailable.add(classCode);
            }
            train.setClasses(classesAvailable);

            JsonArray days = trainInfo.getAsJsonArray("days");
            Map<Integer,Boolean> daysTrainRuns = new HashMap<Integer, Boolean>();
            for(JsonElement daysElement : days){
                String daysCode = daysElement.getAsJsonObject().get("code").getAsString();
                String runs = daysElement.getAsJsonObject().get("runs").getAsString();
                daysTrainRuns.put(dayMap.get(daysCode),runs.equals("Y"));
            }
            train.setDays(daysTrainRuns);

            JsonArray route = mainJson.getAsJsonArray("route");
            List<String> stationCodes = new ArrayList<String>();
            Map<Tuple2<String,String>, Double> travelTimeMap = new HashMap<Tuple2<String, String>, Double>();
            List<Double> prevStationsDepartureTimes = new ArrayList<Double>();
            List<String> prevStationsWeekDays = new ArrayList<String>();
            for(JsonElement station : route){
                String stationId = station.getAsJsonObject().getAsJsonObject("station").get("code").getAsString();
                String currDepTimeString = station.getAsJsonObject().get("schdep").getAsString();
                String currArrTimeString = station.getAsJsonObject().get("scharr").getAsString();
                if(currArrTimeString.equals("SOURCE")){
                    currArrTimeString = currDepTimeString;
                }
                if(currDepTimeString.equals("DEST")){
                    currDepTimeString = currArrTimeString;
                }
                Double currDepTime = convertTimeStringToDouble(currDepTimeString);
                Double currArrTime = convertTimeStringToDouble(currArrTimeString);
                String currWeekDay = station.getAsJsonObject().get("day").getAsString();
                for(int i=0;i<prevStationsDepartureTimes.size();i++){
                    Double prevStationDepartureTime = prevStationsDepartureTimes.get(i);
                    String prevStationWeekDay = prevStationsWeekDays.get(i);
                    String prevStationId = stationCodes.get(i);
                    Double currTravelTime = computeTravelTime(prevStationDepartureTime,currArrTime,prevStationWeekDay,currWeekDay);
                    travelTimeMap.put(new Tuple2<String, String>(prevStationId,stationId),currTravelTime);
                }
                stationCodes.add(stationId);
                prevStationsDepartureTimes.add(currDepTime);
                prevStationsWeekDays.add(currWeekDay);
            }
            train.setStations(stationCodes);
            train.setTravelTimeMap(travelTimeMap);

            train.setTicketCostMap(new HashMap<Tuple2<String, String>, Double>());

            return train;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static Double computeTravelTime(Double prevDepTime,Double currArrTime,String prevWeekDay,String currWeekDay){
        if(prevWeekDay.equals(currWeekDay)){
            return (currArrTime - prevDepTime);
        }else{
            return (1440*(Integer.parseInt(currWeekDay) - Integer.parseInt(prevWeekDay)) + currArrTime - prevDepTime);
        }
    }

    public static Double convertTimeStringToDouble(String timeString){
        String hoursString = timeString.split(":")[0];
        String minsString = timeString.split(":")[1];
        return (Integer.parseInt(hoursString)*60 + Integer.parseInt(minsString) + 0.0);
    }

    public static Map<Tuple2<String,String>,Integer> getSeatAvailabilityForATrain(Train train,String date){
        if(train==null){
            return null;
        }
        Map<Tuple2<String,String>,Integer> ticketAvailability = new HashMap<Tuple2<String, String>, Integer>();
        int threshold = 0;
        int numberOfThresholdSeats = 0;
        for(int i=0;i<train.getStations().size();i++){
            for(int j=train.getStations().size()-1;j>i;j--){
                if(j<=threshold){
                    ticketAvailability.put(new Tuple2<String, String>(train.getStations().get(i),train.getStations().get(j)),numberOfThresholdSeats);
                    continue;
                }
                Integer seatsAvailableBetweenTwoStations = getSeatAvailabilityFromWeb(train.getStations().get(i), train.getStations().get(j), train.getTrainNumber(), date);
                if(seatsAvailableBetweenTwoStations>0){
                    threshold = j;
                    numberOfThresholdSeats = seatsAvailableBetweenTwoStations;
                }
            }
        }
        return ticketAvailability;
    }

    public static Integer getSeatAvailabilityFromWeb(String startStation, String endStation, String trainNumber, String date){
        String url = "https://api.railwayapi.com/v2/check-seat/train/%s/source/%s/dest/%s/date/%s/pref/SL/quota/GN/apikey/%s/";
        url = String.format(url,trainNumber,startStation,endStation,date,myApiKey);
        String json = WebConnector.getResponseFromWeb(url);
        JsonParser jsonParser = new JsonParser();
        try {
            JsonObject mainJson = jsonParser.parse(json).getAsJsonObject();
            JsonArray availabilityArray = mainJson.getAsJsonArray("availability");
            for(JsonElement availabilty : availabilityArray){
                String availabilityDate = availabilty.getAsJsonObject().get("date").getAsString();
                if(compareDates(availabilityDate,date)){
                    String status = availabilty.getAsJsonObject().get("status").getAsString();
                    if(status.contains("AVAILABLE")){
                        Integer noOfSeats = Integer.parseInt(status.split("-")[1]);
                        return noOfSeats;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static Boolean compareDates(String date1,String date2){
        String[] split1 = date1.split("-");
        String[] split2 = date2.split("-");
        if((Integer.parseInt(split1[0])!=Integer.parseInt(split2[0])) || (Integer.parseInt(split1[1])!=Integer.parseInt(split2[1])) ||
                (Integer.parseInt(split1[2])!=Integer.parseInt(split2[2]))){
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        System.out.println(getSeatAvailabilityFromWeb("HX","DR","16382","30-05-2018"));
    }

}


