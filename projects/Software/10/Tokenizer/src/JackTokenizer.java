import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JackTokenizer {
    private BufferedReader bufferedReader;
    public static String currentToken;
    public static TokenType tokenType;
    public static int counter;
    private int initialMarkLimit = 10000000;

    public enum TokenType {
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST

    }

    // A constructor to the class - opens the input file and get ready to tokenize it
    public JackTokenizer(String file) {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(file));
            this.bufferedReader.mark(initialMarkLimit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if there are more tokens in the input
    public boolean hasMoreTokens() {
        try {
            return bufferedReader.ready(); // Check if the stream is ready to be read
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Skips over whitespaces and comments if necessary.
    // Reads next token from the input and set it as the current token.
    public void advance() {
        try {
            String line;
            // Skip over whitespaces and comments
            while ((line = bufferedReader.readLine()) != null && (line.trim().startsWith("//")|| line.trim().startsWith("/**") || line.trim().isEmpty())) {
            }
            currentToken = line.trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns the type of the current token
    public TokenType tokenType() {
        return null;
    }

    // Restart the tokenizer to be at the top of the file
    public void restartTokenizer() {
        try {
            this.bufferedReader.reset();
            this.bufferedReader.mark(initialMarkLimit);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
