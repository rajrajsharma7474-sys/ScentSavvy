import java.util.List;

// Stores the detailed perfume information returned by the Scento API.
public class PerfumeDetailsResponse {

    // Tells us whether the perfume was found.
    public Boolean found;

    // Contains the perfume's information.
    public Detail detail;


    // Stores the main details about a perfume.
    public static class Detail {

        public String name;
        public String house;

        public String family;
        public String subfamily;

        public String character;
        public List<String> character_words;

        public Wears wears;

        public List<String> signature_materials;

        public Pyramid pyramid;
        public List<String> notes;
    }


    // Stores recommended seasons and times to wear the perfume.
    public static class Wears {

        public List<String> seasons;
        public List<String> hours;
    }


    // Stores the top, heart, and base fragrance notes.
    public static class Pyramid {

        public List<String> top;
        public List<String> heart;
        public List<String> base;
    }
}
