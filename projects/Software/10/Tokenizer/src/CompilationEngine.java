import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    public BufferedWriter bufferedWriter;

    // A constructor for the class. Creates a compilation engine with the given input and output.
    public CompilationEngine(File inputFile, File outputFile) {
        JackTokenizer tokenizer = new JackTokenizer(inputFile.toString());
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Compiles a complete class.
    public void compileClass(){

    }
}
