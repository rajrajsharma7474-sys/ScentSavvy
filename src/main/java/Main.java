import java.net.URI;
import java.net.URLEncoder;
import java.io.IOException;
import com.google.gson.Gson;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;
import java.net.http.HttpRequest;


public class Main {

    // Main method controls the overall flow of ScentSavvy
    public static void main(String[] args) throws IOException, InterruptedException {

        IO.println("===============================");
        IO.println("     Welcome to ScentSavvy!");
        IO.println("===============================");

        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        while (true) {

            String perfumeName = IO.readln("Enter a perfume name: ");

            while (perfumeName == null || perfumeName.isBlank()) {
                perfumeName = IO.readln("Please enter a valid perfume name: ");
            }

            // Search for the perfume
            PerfumeSearchResponse searchResponse =
                    searchPerfumes(client, gson, perfumeName);

            if (searchResponse == null) {
                return;
            }

            if (searchResponse.detail != null
                    && searchResponse.detail.matches != null) {

                IO.println("Total matches: " + searchResponse.detail.total);

                if (searchResponse.detail.matches.isEmpty()) {
                    IO.println("No matching perfumes found.");
                    IO.println("Please try another perfume name.\n");
                    // Let the user try another search instead of ending the application.
                    continue;
                }
                // Limit the results to five so the user isn't overwhelmed by too many matches.
                int resultCount = Math.min(5, searchResponse.detail.matches.size());

                // Display the matching perfumes
                displaySearchResults(searchResponse, resultCount);

                IO.println();

                // Get the user's selection
                int choice = getUserChoice(resultCount);

                PerfumeSearchResponse.PerfumeMatch selectedPerfume =
                        searchResponse.detail.matches.get(choice - 1);

                if (selectedPerfume == null) {
                    IO.println("Perfume information is unavailable.");
                    IO.println("Please try another perfume name.\n");
                    continue;

                } else if (selectedPerfume.page == null
                        || selectedPerfume.page.isBlank()) {

                    IO.println("This perfume does not have an available detail page.");
                    IO.println("Please try another perfume name.\n");
                    continue;

                } else {
                    IO.println("\nYou selected: " + selectedPerfume.name);
                    IO.println("Brand: " + selectedPerfume.house);
                    IO.println("Page: " + selectedPerfume.page);

                    // Get the selected perfume's details
                    getPerfumeDetails(client, gson, selectedPerfume);

                    break;
                }

            } else {
                IO.println("No perfume information available.");
                IO.println("Please try another perfume name.\n");
            }
        }
    }



    // Sends the search request and gets the matching perfumes.
    public static PerfumeSearchResponse searchPerfumes(
            HttpClient client, Gson gson, String perfumeName) {
        // Encode the input so spaces and special characters don't break the API URL.
        String encodedName = URLEncoder.encode(perfumeName, StandardCharsets.UTF_8);

        String apiUrl = "https://www.scento.com/api/perfume/v1/search?q="
                + encodedName + "&kind=perfume";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString()
            );

        } catch (IOException e) {
            IO.println("Unable to connect to the API. Please check your internet connection.");
            return null;

        } catch (InterruptedException e) {
            // Preserve the interruption signal so other code can detect it.
            Thread.currentThread().interrupt();
            IO.println("The API request was interrupted.");
            return null;
        }

        IO.println("API Status Code: " + response.statusCode());

        if (response.statusCode() == 200) {

            // Use Gson to map the JSON into Java objects instead of extracting each value manually.
            PerfumeSearchResponse searchResponse =
                    gson.fromJson(response.body(), PerfumeSearchResponse.class);
            // Return the parsed data so main() can display the search results.
            return searchResponse;

        } else {
            IO.println("API request failed.");
            return null;
        }
    }


    // Displays up to five matching perfumes.
    public static void displaySearchResults(
            PerfumeSearchResponse searchResponse, int resultCount) {

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
    }


    // Gets the user's selection and checks if it is valid.
    public static int getUserChoice(int resultCount) {

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
                        IO.println("Invalid selection. Please choose a number from 1 to " + resultCount + ".");
                        continue;
                    } else {
                        break;
                    }

                } catch (NumberFormatException e) {
                    IO.println("Please enter a valid number.");
                }
            }
        }

        return choice;
    }


    // Retrieves the details of the selected perfume.
    public static void getPerfumeDetails(
            HttpClient client, Gson gson,
            PerfumeSearchResponse.PerfumeMatch selectedPerfume) {

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

                // Convert JSON response into a Java object
                PerfumeDetailsResponse details =
                        gson.fromJson(detailsResponse.body(),
                                PerfumeDetailsResponse.class);

                if (details != null && details.detail != null) {
                    displayPerfumeDetails(details);
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
        }
    }


    // Displays the available information about the selected perfume.
    public static void displayPerfumeDetails(PerfumeDetailsResponse details) {

        IO.println("\n--- Perfume Details ---");
        IO.println("Fragrance Family: " + details.detail.family);
        IO.println("Fragrance Subfamily: " + details.detail.subfamily);

        IO.println("Character: " + details.detail.character);

        if (details.detail.character_words != null
                && !details.detail.character_words.isEmpty()) {
            IO.println("Character Words: " + details.detail.character_words);
        }

        if (details.detail.wears != null) {

            if (details.detail.wears.seasons != null
                    && !details.detail.wears.seasons.isEmpty()) {
                IO.println("Recommended Seasons: " + details.detail.wears.seasons);
            }

            if (details.detail.wears.hours != null
                    && !details.detail.wears.hours.isEmpty()) {
                IO.println("Recommended Time: " + details.detail.wears.hours);
            }
        }

        if (details.detail.signature_materials != null
                && !details.detail.signature_materials.isEmpty()) {
            IO.println("Signature Materials: " + details.detail.signature_materials);
        }
        // The API doesn't always provide notes in top, heart, and base categories.
        if (details.detail.pyramid != null) {
            IO.println("Top Notes: " + details.detail.pyramid.top);
            IO.println("Heart Notes: " + details.detail.pyramid.heart);
            IO.println("Base Notes: " + details.detail.pyramid.base);

        } else if (details.detail.notes != null) {
            IO.println("Fragrance Notes: " + details.detail.notes);

        } else {
            IO.println("No fragrance notes available.");
        }
    }
}




