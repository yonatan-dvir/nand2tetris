import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JackTokenizer {
    private BufferedReader bufferedReader;
    private String currentLine;
    public static String currentToken;
    public static TokenType tokenType;
    public static int counter;
    private int initialMarkLimit = 10000000;
    private ArrayList<String> tokens;
    private String jackCode;
    static int pointer;
    private char symbolToken;
    private int intvalToken;
    private boolean bFirst;

    private List<String> keyWords = new ArrayList<>(Arrays.asList(
            "class", "constructor", "function", "method", "field",
            "static", "var", "int", "char", "boolean", "void", "true",
            "false", "null", "this", "do", "if", "else", "while",
            "return", "let"
    ));
    private String operations = "+-*/&|<>=";
    private String symbols = "{}()[].,;+-*/&|<>=-~";

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
            System.out.println("bro");
            // Set jackCode to be all the relevant code in the jack file
            this.advanceLine();
            while (this.hasMoreLines()){
                this.jackCode += this.currentLine.trim().split("/")[0];
                this.advanceLine();
            }
            // initialize the tokens arrayList
            this.initializeTokensArray(this.jackCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if there are more lines in the input
    public boolean hasMoreLines() {
        try {
            return bufferedReader.ready(); // Check if the stream is ready to be read
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Skips over whitespaces and comments if necessary.
    // Reads next line from the input and set it as the current line.
    public void advanceLine() {
        try {
            String line;
            // Skip over whitespaces and comments
            while ((line = bufferedReader.readLine()) != null && (line.trim().startsWith("//")|| line.trim().startsWith("/**") || line.trim().startsWith("*") ||line.trim().isEmpty())) {
            }
            this.currentLine = line;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeTokensArray(String jackCode){
        this.tokens = new ArrayList<String>();
        while (jackCode.length() > 0) {
            while (jackCode.charAt(0) == ' ') {
                jackCode = jackCode.substring(1);
            }
            //jackCode = jackCode.trim();
            // Check if keyword and insert it to the tokens list
            for (int i = 0; i < keyWords.size(); i++) {
                if (jackCode.startsWith(keyWords.get(i).toString())) {
                    String keyword = keyWords.get(i).toString();
                    this.tokens.add(keyword);
                    jackCode = jackCode.substring(keyword.length());
                }

            }
            // Check if symbol and insert it to the tokens list
            if (symbols.contains(jackCode.substring(0, 1))) {
                char symbol = jackCode.charAt(0);
                tokens.add(Character.toString(symbol));
                jackCode = jackCode.substring(1);
            }
            // Check if integer constant and insert it to the tokens list
            else if (Character.isDigit(jackCode.charAt(0))) {
                String value = jackCode.substring(0, 1);
                jackCode = jackCode.substring(1);
                while (Character.isDigit(jackCode.charAt(0))) {
                    value += jackCode.substring(0, 1);
                    jackCode = jackCode.substring(1);

                }
                tokens.add(value);

            }
            // Check if string constant and insert it to the tokens list
            else if (jackCode.substring(0, 1).equals("\"")) {
                jackCode = jackCode.substring(1);
                String strString = "\"";
                while ((jackCode.charAt(0) != '\"')) {
                    strString += jackCode.charAt(0);
                    jackCode = jackCode.substring(1);

                }
                strString = strString + "\"";
                tokens.add(strString);
                jackCode = jackCode.substring(1);

            }
            // Check if identifier and insert it to the tokens list
            else if (Character.isLetter(jackCode.charAt(0)) || (jackCode.substring(0, 1).equals("_"))) {
                String strIdentifier = jackCode.substring(0, 1);
                jackCode = jackCode.substring(1);
                while ((Character.isLetter(jackCode.charAt(0))) || (jackCode.substring(0, 1).equals("_"))) {
                    strIdentifier += jackCode.substring(0, 1);
                    jackCode = jackCode.substring(1);
                }

                tokens.add(strIdentifier);

            }
            // start with pointer at position 0
            this.pointer = 0;
            bFirst = true;

        }
    }

    // Check if there are more tokens we didn't check yet
    public boolean hasMoreTokens() {
        return this.pointer < this.tokens.size() - 1;
    }

    // Reads next token from the tokens list and set it as the current line.
    public void advance() {
        if (hasMoreTokens()) {
            if (!bFirst) {
                pointer++;
            }
            // if at position 0 of tokens, we do not want to increment yet
            else if (bFirst) {
                bFirst = false;
                return;
            }
            String currentItem = tokens.get(pointer);
            // assign current token type and corresponding field variable (keyword, symbol, intval, stringval, or identifier)
            // for this current token - position of where we are in the tokens array
            if (keyWords.contains(currentItem)) {
                this.tokenType = TokenType.KEYWORD;
                this.currentToken = currentItem;
            } else if (symbols.contains(currentItem)) {
                this.tokenType = TokenType.SYMBOL;
                this.currentToken = currentItem.charAt(0) + "";
                symbolToken = currentItem.charAt(0);
            } else if (Character.isDigit(currentItem.charAt(0))) {
                this.tokenType = TokenType.INT_CONST;
                this.currentToken = Integer.parseInt(currentItem) + "";
                intvalToken = Integer.parseInt(currentItem);
            } else if (currentItem.substring(0, 1).equals("\"")) {
                this.tokenType = TokenType.STRING_CONST;
                this.currentToken = currentItem.substring(1, currentItem.length() - 1);
            } else if ((Character.isLetter(currentItem.charAt(0))) || (currentItem.charAt(0) == '_')) {
                this.tokenType = TokenType.IDENTIFIER;
                this.currentToken = currentItem;
            }
        }
        else {
            return;
        }
    }


    // Returns the type of the current token
    public TokenType tokenType() {
        return this.tokenType;
    }

    // returns the keyword which is the current token, should be called only when tokenType() is keyword
    public String keyWord() {
        return this.currentToken;
    }

    // returns character which is current token, should be called only when tokenType() is symbol
    public char symbol() {
        return this.symbolToken;
    }

    // returns identifier which is the current token - should be called only when tokenType() is identifier
    public String identifier() {
        return this.currentToken;
    }

    // returns integer value of the current token - should be called only when tokenType() is INT_CONST
    public int intVal() {
        return this.intvalToken;
    }

    // returns string value of current token without double quotes, should be called only when tokenType() is string_const
    public String stringVal() {
        return this.currentToken;
    }


    // indicates if a symbol is an operation, i.e., =, +, -, &, |, etc.
    public boolean isOperation() {
        for (int i = 0; i < operations.length(); i++) {
            if (operations.charAt(i) == this.symbolToken) {
                return true;
            }
        }
        return false;
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
