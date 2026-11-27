import java.util.List;

// This class stores the response we get from the Scento API.
public class PerfumeSearchResponse {

    // Stores the search details.
    public Detail detail;

    // Contains the total results and the matching perfumes.
    public static class Detail {

        // Total number of perfumes found.
        public int total;

        // Stores the list of matching perfumes.
        public List<PerfumeMatch> matches;
    }

    // Stores information about one perfume.
    public static class PerfumeMatch {

        public String name;  // Perfume name
        public String house; // Brand name
        public String page;  // Perfume page on Scento
    }
}
