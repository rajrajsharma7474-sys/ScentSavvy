public class Main {

    public static void main(String[] args) {

        IO.println("==============================");
        IO.println("     Welcome to ScentSavvy!");
        IO.println("==============================");

        String perfumeName = IO.readln("Enter a perfume name: ");
        if(perfumeName.isBlank()){
            IO.println("Please enter a valid perfume name");
        }else {

            IO.println("You searched for: " + perfumeName);
        }
    }
}

