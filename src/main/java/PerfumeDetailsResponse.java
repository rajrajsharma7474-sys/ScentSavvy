
import java.util.List;

public class PerfumeDetailsResponse {

    public Boolean found;
    public Detail detail;

    public static class Detail {
        public String name;
        public String house;
        public String family;
        public Pyramid pyramid;
        public List<String> notes;
    }

    public static class Pyramid {
        public List<String> top;
        public List<String> heart;
        public List<String> base;
    }
}
