import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

public class CodeWriter{
    private static BufferedWriter bufferedWriter;

    private String file;
    private int returnCount;

    // Opens the file and gets ready to write into it
    public CodeWriter (File asmFile){
        try{
            this.bufferedWriter = new BufferedWriter(new FileWriter(asmFile));
            returnCount = 0;
            // Set this.file to be the asmFile without the .asm ending
            int start = asmFile.toString().lastIndexOf("/") + 1;
            int end = asmFile.toString().length();
            this.file = asmFile.toString().substring(start, end).split("\\.")[0];
            // Init RAM[0] to be 256
            bufferedWriter.write("@256\nD=A\n@SP\nM=D\n");
            // Call Sys.init
            writeCall("Sys.init", 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes to the output file the assembly code that implements the given arithmetic-logical command
    public void writeArithmetic(String currentCommand){
        try{
            if (currentCommand.trim().equals("add")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nM=M+D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("sub")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nM=M-D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("neg")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\nM=-D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("and")) {
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D&M\nM=D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("or")) {
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D|M\nM=D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("not")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\nM=!D\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("eq")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D-M\nM=-1\n@EQUAL\nD;JEQ\n@SP\nA=M\nM=0\n(EQUAL)\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("gt")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=M-D\nM=-1\n@GREATER\nD;JLT\n@SP\nA=M\nM=0\n(GREATER)\n@SP\nM=M+1\n");
            }
            else if (currentCommand.trim().equals("lt")){
                bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@SP\nM=M-1\nA=M\nD=D-M\nM=-1\n@LOWER\nD;JGT\n@SP\nA=M\nM=0\n(LOWER)\n@SP\nM=M+1\n");
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    // Writes to the output file the assembly code that implements the given push or pop command
    public void writePushPop(Parser.CommandType commandType, String segment, int index){
        Map<String, String> segments = new HashMap<>();
        segments.put("local", "LCL");
        segments.put("argument", "ARG");
        segments.put("this", "THIS");
        segments.put("that", "THAT");
        try{
            // Generate assembly for POP command
            if (commandType == Parser.CommandType.C_POP){
                if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")){
                    bufferedWriter.write("@" + segments.get(segment) + "\nD=M\n@" + index + "\nD=D+A\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@addr\nA=M\nM=D\n");
                }
                else if (segment.equals("temp")){
                    bufferedWriter.write("@5" + "\nD=A\n@" + index + "\nD=D+A\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@addr\nA=M\nM=D\n");
                }
                else if (segment.equals("static")){
                    bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@" + this.file + "." + index + "\nM=D\n");
                }
                // If segment is "pointer"
                else{
                    // to pop pointer 0, generate assembly code that executes pop THIS
                    if (index == 0) {
                        bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@THIS\nM=D\n");
                    }
                    // to pop pointer 1, generate assembly code that executes pop THAT
                    else {
                        bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@THAT\nM=D\n");
                    }
                }
            }

            // Generate assembly for PUSH command
            else {
                if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")){
                    bufferedWriter.write("@" + segments.get(segment) + "\nD=M\n@" + index + "\nD=D+A\n@addr\nM=D\nA=M\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                }
                else if (segment.equals("temp")){
                    bufferedWriter.write("@5" + "\nD=A\n@" + index + "\nD=D+A\n@addr\nM=D\nA=M\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                }
                else if (segment.equals("constant")) {
                    bufferedWriter.write("@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                }
                else if (segment.equals("static")){
                    bufferedWriter.write("@" + file + "." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1");

                }
                // If segment is "pointer"
                else{
                    // to push pointer 0, generate assembly code that executes push THIS
                    if (index == 0) {
                        bufferedWriter.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    }
                    // to push pointer 1, generate assembly code that executes push THAT
                    else {
                        bufferedWriter.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    }
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the label command
    public void writeLabel(String label){
        try{
            bufferedWriter.write("(" + label + ")\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the goto command
    public void writeGoto(String label){
        try{
            bufferedWriter.write("@" + label + "\n0;JMP\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the if-goto command
    public void writeIf(String label){
        try{
            bufferedWriter.write("@SP\nAM=M-1\nD=M\nA=A-1\n@" + label + "\nD;JNE\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the call command
    public void writeCall(String functionName, int nArgs){
        try{
            // push retAddrLabel
            bufferedWriter.write("@RETURN" + returnCount + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            // push LCL
            bufferedWriter.write("@LCL\nD=M\n@SP\nAM=M+1\nA=A-1\nM=D\n");
            // push ARG
            bufferedWriter.write("@ARG\nD=M\n@SP\nAM=M+1\nA=A-1\nM=D\n");
            // push THIS
            bufferedWriter.write("@THIS\nD=M\n@SP\nAM=M+1\nA=A-1\nM=D\n");
            // push THAT
            bufferedWriter.write("@THAT\nD=M\n@SP\nAM=M+1\nA=A-1\nM=D\n");
            // ARG = SP - 5 - nArgs = SP - (5 + nArgs)
            bufferedWriter.write("@" + (5 + nArgs) + "\nD=A\n@SP\nD=M-D\n@ARG\nM=D\n");
            // LCL = SP
            bufferedWriter.write("@SP\nD=M\n@LCL\nM=D\n");
            // goto functionName
            bufferedWriter.write("@" + functionName + "\n0;JMP\n");
            // (retAddrLabel)
            bufferedWriter.write("(RETURN" + returnCount +")\n");
            returnCount++;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the function command
    public void writeFunction(String functionName, int nVars){
        // (functionName)
        writeLabel(functionName);
        // push nVars 0 values
        for (int i = 0 ; i < nVars ; i++){
            writePushPop(Parser.CommandType.C_PUSH, "constant", 0);
        }
    }

    // Writes assembly code that effects the return command
    public void writeReturn(){
        try{
            // endFrame = LCL
            bufferedWriter.write("@LCL\nD=M\n@endFrame\nM=D\n");
            // retAddr = *(endFrame - 5)
            bufferedWriter.write("@endFrame\nD=M\n@5\nA=D-A\nD=M\n@retAddr\nM=D\n");
            // *ARG = pop()
            writePushPop(Parser.CommandType.C_POP, "argument", 0);
            // SP = ARG + 1
            bufferedWriter.write("@ARG\nD=M\nD=D+1\n@SP\nM=D\n");
            // THAT = *(endFrame - 1)
            bufferedWriter.write("@endFrame\nD=M\n@1\nA=D-A\nD=M\n@THAT\nM=D\n");
            // THIS = *(endFrame - 2)
            bufferedWriter.write("@endFrame\nD=M\n@2\nA=D-A\nD=M\n@THIS\nM=D\n");
            // ARG = *(endFrame - 3)
            bufferedWriter.write("@endFrame\nD=M\n@3\nA=D-A\nD=M\n@ARG\nM=D\n");
            // LCL = *(endFrame - 4)
            bufferedWriter.write("@endFrame\nD=M\n@4\nA=D-A\nD=M\n@LCL\nM=D\n");
            // goto retAddr
            bufferedWriter.write("@retAddr\nA=M\n0;JMP\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Closes the output file
    public void close() {
        try{
            this.bufferedWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes the given vm command as a comment in the output asm file
    public void writeComment(String comment){
        try{
            bufferedWriter.write("// " + comment + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }



}