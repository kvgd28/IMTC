import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebConnector {

    public static Client client ;

    static{
        client = ClientBuilder.newClient();
    }

    public static String getResponseFromWeb2(String url){
        WebTarget webTarget = client.target(url);
        String response = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
        return response;
    }

    public static String getResponseFromWeb(String link){
        HttpURLConnection connection=null;
        try{
            URL url=new URL(link);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line,response="";
            while((line=in.readLine())!=null)
                response+=(line+"\n");
            in.close();
            response = response.replaceAll("day-code","day_code");
            response = response.replaceAll("class-name","class_name");
            response = response.replaceAll("class-code","class_code");
            response = response.replaceAll("\"class\"","\"trainClass\"");
            return response;
        }catch(Exception e){
            System.out.println("Error while connecting to url " + link + " " + e);
            return null;
        }
    }

    public static void main(String[] args){
        System.out.println(getResponseFromWeb("https://api.railwayapi.com/v2/live/train/16382/date/16-03-2018/apikey/g1q2qto7gy/"));
        //System.out.println(getResponseFromWeb2("http://www.google.com"));
    }
}
