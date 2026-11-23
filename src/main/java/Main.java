
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import com.google.gson.Gson;
import java.net.URLDecoder;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        int resultCount = 0;

        IO.println("===============================");
        IO.println("     Welcome to ScentSavvy!");
        IO.println("===============================");

        String perfumeName = IO.readln("Enter a perfume name: ");
        if (perfumeName == null || perfumeName.isBlank()) {
            IO.println("Please enter a valid perfume name");
            return;
        } else {

            String encodedName = URLEncoder.encode(perfumeName, StandardCharsets.UTF_8);

            String apiUrl = "https://www.scento.com/api/perfume/v1/search?q="
                    + encodedName + "&kind=perfume";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response;

            try {
                response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );
            } catch (IOException e) {
                IO.println("Unable to connect to the API. Please check your internet connection.");
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                IO.println("The API request was interrupted.");
                return;
            }

            IO.println("API Status Code: " + response.statusCode());

            if (response.statusCode() == 200) {

                Gson gson = new Gson();

                PerfumeSearchResponse searchResponse =
                        gson.fromJson(response.body(), PerfumeSearchResponse.class);

                if (searchResponse != null && searchResponse.detail != null
                        && searchResponse.detail.matches != null) {
                    IO.println("Total matches: " + searchResponse.detail.total);

                    if (searchResponse.detail.matches.isEmpty()) {
                        IO.println("No matching perfumes found.");
                        return;
                    } else {

                        resultCount = Math.min(5, searchResponse.detail.matches.size());

                        IO.println("\nShowing up to " + resultCount + " results:");

                        for (int i = 0; i < resultCount; i++) {

                            PerfumeSearchResponse.PerfumeMatch perfume =
                                    searchResponse.detail.matches.get(i);

                            if (perfume != null) {
                                IO.println("\n" + (i + 1) + ". " + perfume.name);
                                IO.println("   Brand: " + perfume.house);
                                IO.println("   Page: " + perfume.page);
                            } else {
                                IO.println("\n" + (i + 1) + ". Perfume information unavailable.");
                            }
                        }

                        IO.println();
                        int choice = 0;

                        while (true) {
                            String selectionIndex = IO.readln("Enter the number of the perfume you want to know about: ");
                            if (selectionIndex == null || selectionIndex.isBlank()) {
                                IO.println("Please select a number");
                                continue;
                            } else {
                                try {
                                    choice = Integer.parseInt(selectionIndex);
                                    if (choice < 1 || choice > resultCount) {
                                        IO.println("Invalid selection. Please choose a number from 1 to "
                                                + resultCount + ".");
                                        continue;
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    IO.println("Please enter a valid number.");
                                }
                            }
                        }

                        PerfumeSearchResponse.PerfumeMatch selectedPerfume =
                                searchResponse.detail.matches.get(choice - 1);

                        if (selectedPerfume == null) {
                            IO.println("Perfume information is unavailable.");
                        } else if (selectedPerfume.page == null || selectedPerfume.page.isBlank()) {
                            IO.println("This perfume does not have an available detail page.");
                        } else {
                            IO.println("\nYou selected: " + selectedPerfume.name);
                            IO.println("Brand: " + selectedPerfume.house);
                            IO.println("Page: " + selectedPerfume.page);

                            String perfumeIdentifier = selectedPerfume.name.toLowerCase()
                                    .replace(" ", "-");

                            String decodedIdentifier = URLDecoder.decode(
                                    perfumeIdentifier, StandardCharsets.UTF_8);

                            String detailsUrl = "https://www.scento.com/api/perfume/v1/perfumes/"
                                    + decodedIdentifier;

                            HttpRequest detailsRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(detailsUrl))
                                    .GET()
                                    .build();

                            try {
                                HttpResponse<String> detailsResponse = client.send(
                                        detailsRequest,
                                        HttpResponse.BodyHandlers.ofString()
                                );

                                if (detailsResponse.statusCode() == 200) {
                                    PerfumeDetailsResponse details =
                                            new Gson().fromJson(detailsResponse.body(),
                                                    PerfumeDetailsResponse.class);

                                    if (details != null && details.detail != null) {
                                        IO.println("\n--- Perfume Details ---");
                                        IO.println("Fragrance Family: " + details.detail.family);

                                        if (details.detail.pyramid != null) {
                                            IO.println("Top Notes: " + details.detail.pyramid.top);
                                            IO.println("Heart Notes: " + details.detail.pyramid.heart);
                                            IO.println("Base Notes: " + details.detail.pyramid.base);
                                        } else if (details.detail.notes != null) {
                                            IO.println("Fragrance Notes: " + details.detail.notes);
                                        } else {
                                            IO.println("No fragrance notes available.");
                                        }
                                    } else {
                                        IO.println("No perfume details available.");
                                    }
                                } else {
                                    IO.println("Unable to retrieve perfume details.");
                                }

                            } catch (IOException e) {
                                IO.println("Unable to connect to the perfume details API.");
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                IO.println("The perfume details request was interrupted.");
                                return;
                            }


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




