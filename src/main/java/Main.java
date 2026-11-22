import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException{

        IO.println("==============================");
        IO.println("     Welcome to ScentSavvy!");
        IO.println("==============================");

        String perfumeName = IO.readln("Enter a perfume name: ");
        if(perfumeName.isBlank()){
            IO.println("Please enter a valid perfume name");
        }else {

            String encodedName = URLEncoder.encode(perfumeName, StandardCharsets.UTF_8);

            String apiUrl = "https://www.scento.com/api/perfume/v1/search?q="
                    + encodedName + "&kind=perfume";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            IO.println("API Status Code: " + response.statusCode());
            IO.println("API Response:");
            IO.println(response.body());
        }
    }
}

