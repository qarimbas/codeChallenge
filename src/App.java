import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

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

            long T = (long) json_properties.get("time");

            String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(T)); //* 1000L https://stackoverflow.com/questions/29713781/convert-jsonobject-into-string-and-long-return-null

            System.out.println(json_properties.get("place") + " " + json_properties.get("mag") + " " + dateAsText);
            JSONObject json_geometry = (JSONObject) json_feature.get("geometry");
            System.out.println(json_geometry.get("coordinates"));
        }

        Scanner in = new Scanner(System.in);

        System.out.println("Please enter a country:");
        String country = in.nextLine();

        System.out.println("Please enter count of days:");
        int countOfDays = in.nextInt();

        LocalDate endTime = LocalDate.now();
        LocalDate startTime = endTime.minusDays(countOfDays);

        System.out.println("Start time: " + startTime + " End Time: " + endTime);

        // Country, Place of the earthquake, magnitude, date and time of the earthquake
    }
}