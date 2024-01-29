import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            HackAssembler.main(args);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}