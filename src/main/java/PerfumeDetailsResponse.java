
import java.util.List;

public class PerfumeDetailsResponse {

    public Boolean found;
    public Detail detail;

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

    public static class Wears {
        public List<String> seasons;
        public List<String> hours;
    }

    public static class Pyramid {
        public List<String> top;
        public List<String> heart;
        public List<String> base;
    }
}

