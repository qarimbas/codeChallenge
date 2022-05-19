import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
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

    private static final Double range = 2.0;
    private static final String API_URL_BASE = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&";
    private static final String EXAMPLE_API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2022-01-01&endtime=2022-01-02";


    public static void main(String[] args) throws IOException, InterruptedException, ParseException {


        String[] myInput = userInput();

        int countOfDays = Integer.parseInt(myInput[1]);
        LocalDate[] myInterval = timeInterval(countOfDays);
        Double[] myCoord = findCoordinates(myInput[0]);
        if(myCoord[0] != null) {
            runQuery(myInterval[0], myInterval[1], myCoord[0], myCoord[1], myInput[0], countOfDays);
        }
    }

    public static String[] userInput() {
        String[] inputArray = new String[2];

        Scanner in = new Scanner(System.in);

        System.out.println("Please enter a country:");
        String country = in.nextLine();

        while(true) {
            try {
                System.out.println("Please enter count of days:");
                int countOfDays = Integer.parseInt(in.nextLine());
                inputArray[0] = country;
                inputArray[1] = String.valueOf(countOfDays);
                break;
            }
            catch (Exception e) {
                System.out.println("You have entered a wrong count of days.");
            }

        }
        return inputArray;
    }

    public static LocalDate[] timeInterval(int countOfDays) {
        LocalDate[] timeArray = new LocalDate[2];

        LocalDate endTime = LocalDate.now();
        LocalDate startTime = endTime.minusDays(countOfDays);

        //System.out.println("Start time: " + startTime + " End Time: " + endTime);

        timeArray[0] = startTime;
        timeArray[1] = endTime;
        return timeArray;
    }

    public static Double[] findCoordinates( String countryName) {
        Double[] coor = new Double[2];
        boolean countryFound = false;
        try {
            File myObj = new File("./src/countries.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                String values[] = data.split("\\t");
                if (values[3].equals(countryName)) {

                    coor[0] = Double.parseDouble(values[1]); //latitude
                    coor[1] = Double.parseDouble(values[2]); //longitude
                    //System.out.println("Latitude, longitude found for " + countryName + " " + values[1] + " " + values[2] );
                    countryFound = true;
                    break;
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("countries.txt file not found.");
            //e.printStackTrace();
        }
        if(!countryFound)
            System.out.println("Country " + countryName + " not found!");
        return coor;
    }

    public static void runQuery(LocalDate startDate, LocalDate endDate, Double latitude, Double longitude, String countryName, int days) throws IOException, InterruptedException, ParseException {
        String API_URL;

        Double minLatitude = latitude - range;
        Double maxLatitude = latitude + range;
        Double minLongitude = longitude - range;
        Double maxLongitude = longitude + range;

        API_URL = API_URL_BASE + "&starttime=" + startDate + "&endtime=" + endDate + "&minlatitude=" + minLatitude + "&maxlatitude=" + maxLatitude + "&minlongitude=" + minLongitude + "&maxlongitude=" + maxLongitude;

        //System.out.println(API_URL);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET().header("accept", "application/json")
                .uri(URI.create(API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());
        //System.out.println("************end of response body ********************");

        // Convert JSON String to JSON Object
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.body());
        //System.out.println(jsonObject.toString());
        //System.out.println("************ end of print json  *******************");

        //System.out.println(jsonObject.get("type"));
        JSONObject json_metadata = (JSONObject) jsonObject.get("metadata");
        //System.out.println("metadata: " + json_metadata + json_metadata.get("count"));
        long count = (long) json_metadata.get("count");
        if( count == 0) {
            System.out.println("No Earthquakes were recorded past {" + String.valueOf(days) +"} days.");
        }
        else {
            JSONArray json_features_array = (JSONArray) jsonObject.get("features");
            for (Object o : json_features_array) {
                JSONObject json_feature = (JSONObject) o;
                JSONObject json_properties = (JSONObject) json_feature.get("properties");

                long T = (long) json_properties.get("time");

                String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(T)); //* 1000L https://stackoverflow.com/questions/29713781/convert-jsonobject-into-string-and-long-return-null

                System.out.println(countryName + " " + json_properties.get("place") + " " + json_properties.get("mag") + " " + dateAsText);
                // Country, Place of the earthquake, magnitude, date and time of the earthquake
            }
        }
    }

}