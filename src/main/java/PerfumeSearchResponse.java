
import java.util.List;

public class PerfumeSearchResponse {

    public Detail detail;

    public static class Detail {
        public int total;
        public List<PerfumeMatch> matches;
    }

    public static class PerfumeMatch {
        public String name;
        public String house;
        public String page;
    }
}
