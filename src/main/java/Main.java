import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import com.google.gson.Gson;

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

            if (response.statusCode() == 200) {

                Gson gson = new Gson();

                PerfumeSearchResponse searchResponse =
                        gson.fromJson(response.body(), PerfumeSearchResponse.class);

                if (searchResponse.detail != null) {
                    IO.println("Total matches: " + searchResponse.detail.total);

                    if (searchResponse.detail.matches.isEmpty()) {
                        IO.println("No matching perfumes found.");
                    } else {

                        int resultCount = Math.min(5, searchResponse.detail.matches.size());

                        IO.println("\nShowing up to " + resultCount + " results:");

                        for (int i = 0; i < resultCount; i++) {

                            PerfumeSearchResponse.PerfumeMatch perfume =
                                    searchResponse.detail.matches.get(i);

                            IO.println("\n" + (i + 1) + ". " + perfume.name);
                            IO.println("   Brand: " + perfume.house);
                            IO.println("   Page: " + perfume.page);
                        }
                    }

                } else {
                    IO.println("No perfume information available.");
                }

            } else {
                IO.println("API request failed.");
            }
        }
    }
}

