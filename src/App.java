import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class App {

    private static final String EXAMPLE_API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2022-01-01&endtime=2022-01-02";
    private static final String API_URL = "";

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET().header("accept", "application/json")
                .uri(URI.create(EXAMPLE_API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        System.out.println("************end of response body ********************");

        // Convert JSON String to JSON Object
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.body());
        System.out.println(jsonObject.toString());
        System.out.println("************ end of print json  *******************");

        //System.out.println(jsonObject.get("type"));

        JSONArray json_features_array = (JSONArray) jsonObject.get("features");
        for (Object o : json_features_array) {
            JSONObject json_feature = (JSONObject) o;
            JSONObject json_properties = (JSONObject) json_feature.get("properties");
            System.out.println(json_properties.get("place") + " " + json_properties.get("mag") + " " + json_properties.get("time"));

            /*
            long timeStamp = json_properties.get("time")
            Timestamp stamp = new Timestamp(timeStamp);
            Date date = new Date(stamp.getTime());
            System.out.println(date);
            */

            JSONObject json_geometry = (JSONObject) json_feature.get("geometry");
            System.out.println(json_geometry.get("coordinates"));
        }

        // Country, Place of the earthquake, magnitude, date and time of the earthquake
    }
}