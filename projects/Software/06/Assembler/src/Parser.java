import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private BufferedReader bufferedReader;
    public static String currentInstruction;
    public static InstructionType instructionType;
    public int counter;
    private int initialMarkLimit = 10000000;

    public enum InstructionType {
        A_INSTRUCTION,
        L_INSTRUCTION,
        C_INSTRUCTION

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
    public void advance(){
        try{
            String line;
            // Skip over whitespaces and comments
            while ((line = bufferedReader.readLine()) != null && (line.trim().startsWith("//") || line.trim().isEmpty())){
            }
            currentInstruction = line;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns the type of the current instruction
    public InstructionType instructionType(){
        if(currentInstruction.trim().startsWith("("))
        {
            instructionType = InstructionType.L_INSTRUCTION;
            return instructionType;
        }
        else if(currentInstruction.trim().startsWith("@"))
        {
            instructionType = InstructionType.A_INSTRUCTION;
            return instructionType;
        }
        instructionType = InstructionType.C_INSTRUCTION;
        return instructionType;
    }

    // Returns the current instruction symbol
    public String symbol() {
        switch (this.instructionType()){
            case A_INSTRUCTION -> {
                return currentInstruction.split("@")[1].trim();
            }
            case L_INSTRUCTION -> {
                return currentInstruction.split("[(]")[1].split("[)]")[0].trim();
            }
        }
        return currentInstruction.trim();
    }

    // Returns the dest field of the current instruction
    public String dest() {
        String[] isDestArray = currentInstruction.split("=");
        if (isDestArray.length == 1) return "";
        return isDestArray[0];
    }

    // Returns the comp field of the current instruction
    public String comp() {

        String[] isCompArray = currentInstruction.split("=");
        if (isCompArray.length == 1){
            String[] isJumpArray = currentInstruction.split(";");
            if (isJumpArray.length == 1){
                return "";
            }
            return isJumpArray[0];
        }
        return isCompArray[1].split(";")[0];
    }

    // Returns the jump field of the current instruction
    public String jump() {
        String[] isJumpArray = currentInstruction.split(";");
        if (isJumpArray.length == 1) return "";
        return isJumpArray[1];
    }

    // Restart the parser to be at the top of the file
    public void restartParser(){
        try{
            this.bufferedReader.reset();
            this.bufferedReader.mark(initialMarkLimit);
        } catch (IOException e){
            e.printStackTrace();

        }
    }
}
