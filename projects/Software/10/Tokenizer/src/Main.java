import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            JackAnalyzer.main(args);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}