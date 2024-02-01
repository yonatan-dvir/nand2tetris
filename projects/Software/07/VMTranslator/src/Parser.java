import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private BufferedReader bufferedReader;
    public static String currentCommand;
    private int initialMarkLimit = 10000000;

    // All possible commands types
    public enum CommandType{
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

    // A constructor to the class - opens the input file and get ready to parse it
    public Parser(String file) {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(file));
            this.bufferedReader.mark(initialMarkLimit);
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
    // Reads next instruction from the input and make it the current instruction.
    public void advance() {
        try {
            String line;
            // Skip over whitespaces and comments
            while ((line = bufferedReader.readLine()) != null && (line.trim().startsWith("//") || line.trim().isEmpty())) {
            }
            this.currentCommand = line;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns a constant representing the type of the current command
    public CommandType commandType(){
        String trimmedCommand = this.currentCommand.trim();
        /* Dictionary <String, CommandType> dict = {"push":C_PUSH, "pop":C_POP, "add":C_ARITHMETIC,
                "sub":C_ARITHMETIC, "neg":C_ARITHMETIC, "eq":C_ARITHMETIC, "gt":C_ARITHMETIC,
                "lt":C_ARITHMETIC, "and":C_ARITHMETIC, "or":C_ARITHMETIC, "not":C_ARITHMETIC} */
        if (trimmedCommand.startsWith("push")){
            return CommandType.C_PUSH;
        }
        else if (trimmedCommand.startsWith("pop")) {
            return CommandType.C_POP;
        }
        else if (trimmedCommand.startsWith("add") || trimmedCommand.startsWith("sub") || trimmedCommand.startsWith("neg") ||
                trimmedCommand.startsWith("eq") || trimmedCommand.startsWith("gt") || trimmedCommand.startsWith("lt") ||
                trimmedCommand.startsWith("and") || trimmedCommand.startsWith("or") || trimmedCommand.startsWith("not")){
            return CommandType.C_ARITHMETIC;
        }
        else if (trimmedCommand.startsWith("goto")) {
            return CommandType.C_GOTO;
        }
        else if (trimmedCommand.startsWith("if-goto")) {
            return CommandType.C_IF;
        }
        else if (trimmedCommand.startsWith("label")) {
            return CommandType.C_LABEL;
        }
        else if (trimmedCommand.startsWith("call")) {
            return CommandType.C_CALL;
        }
        else if (trimmedCommand.startsWith("function")) {
            return CommandType.C_FUNCTION;
        }
        // else (if the command is return...)
        return CommandType.C_RETURN;    }

    /*public void SetCommandType(){
        String trimmedCommand = this.currentCommand.trim();
        if (trimmedCommand.startsWith("push")){
            this.currentCommandType = CommandType.C_PUSH;
        }
        else if (trimmedCommand.startsWith("pop")) {
            this.currentCommandType = CommandType.C_POP;
        }
        else if (trimmedCommand.startsWith("add") || trimmedCommand.startsWith("sub") || trimmedCommand.startsWith("neg") ||
                trimmedCommand.startsWith("eq") || trimmedCommand.startsWith("gt") || trimmedCommand.startsWith("lt") ||
                trimmedCommand.startsWith("and") || trimmedCommand.startsWith("or") || trimmedCommand.startsWith("not")){
            this.currentCommandType = CommandType.C_ARITHMETIC;
        }
    }*/

    // Returns the first argument of the current command.
    public String arg1(){
        // In the case of C_ARITHMETIC, the command itself is returned.
        if (this.commandType() == CommandType.C_ARITHMETIC){
            return this.currentCommand;
        }
        // Should not be called if the current command is C_RETURN.
        else if (this.commandType() == CommandType.C_RETURN){
            return "Should not be called - Error";
        }
        // Else, return the first argument of the current command.
        return this.currentCommand.split(" ")[1];
    }

    // Returns the second argument of the current command.
    public int arg2(){
        // Should be called only if the current command is C_PUSH, C_POP, C_FUNCTION
        if (this.commandType() != CommandType.C_PUSH && this.commandType() != CommandType.C_POP &&
                this.commandType() != CommandType.C_FUNCTION && this.commandType() != CommandType.C_CALL){
            return -1;
        }
        return Integer.parseInt(this.currentCommand.split(" ")[2]);
    }
}


