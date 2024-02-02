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

    private static int countEqGtLt = 0;

    // Opens the file and gets ready to write into it
    public CodeWriter (File asmFile){
        try{
            this.bufferedWriter = new BufferedWriter(new FileWriter(asmFile));

            // Set this.file to be the asmFile without the .asm ending
            int start = asmFile.toString().lastIndexOf("/") + 1;
            int end = asmFile.toString().length();
            this.file = asmFile.toString().substring(start, end).split("\\.")[0];
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
            else{
                // Next week...
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
                    // to pop pinter 0, generate assembly code that executes push/pop THIS
                    if (index == 0) {
                        bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@THIS\nM=D\n");
                    }
                    // to pop pinter 1, generate assembly code that executes push/pop THAT
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
                    // to push pinter 0, generate assembly code that executes push/pop THIS
                    if (index == 0) {
                        bufferedWriter.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    }
                    // to push pinter 1, generate assembly code that executes push/pop THAT
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
            bufferedWriter.write("(" + label.toUpperCase() + ")\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the goto command
    public void writeGoto(String label){
        try{
            bufferedWriter.write("@" + label + "\n0;JMP");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the if-goto command
    public void writeIf(String label){
        try{
            bufferedWriter.write("@SP\nM=M-1\nA=M\nD=M\n@" + label + "\nD;JGT\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    // Writes assembly code that effects the function command
    public void writeFunction(String functionName, int nVars){
        try{
            bufferedWriter.write("\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the call command
    public void writeCall(String functionName, int nArgs){
        try{
            bufferedWriter.write("\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // Writes assembly code that effects the return command
    public void writeReturn(){
        try{
            bufferedWriter.write("\n");
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



}