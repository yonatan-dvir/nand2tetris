import java.io.File;

public class Main {
    public static void main(String[] args) {

        // If user didn't provide an input
        if (args.length != 1) {
            System.out.println("Please provide a .vm file or directory.");
            return;
        }

        File inputFile = new File(args[0]);

        // If single .vm file provided
        if (inputFile.isFile() && args[0].endsWith(".vm")) {
            // Create the output file with the same name as the input file but with .asm extension
            String outputFileName = inputFile.getAbsolutePath().replace(".vm", ".asm");
            File asmFile = new File(outputFileName);
            // Construct a CodeWriter to handle the output file
            CodeWriter codeWriter = new CodeWriter(asmFile);

            // Process the inputFile
            processFile(inputFile, codeWriter);
            // Close the stream
            codeWriter.close();
        }
        // Directory provided, translate all .vm files within the folder
        else if (inputFile.isDirectory()) {
            // Create the output file with the name of the directory and .asm extension
            String outputFileName = inputFile.getAbsolutePath() + File.separator + inputFile.getName() + ".asm";
            File asmFile = new File(outputFileName);
            // Construct a CodeWriter to handle the output file
            CodeWriter codeWriter = new CodeWriter(asmFile);

            // Process all .vm files within the                       directory
            File[] vmFiles = inputFile.listFiles((dir, name) -> name.endsWith(".vm"));
            if (vmFiles != null) {
                for (File vmFile : vmFiles) {
                    processFile(vmFile, codeWriter);
                }
            }
            // Close the stream
            codeWriter.close();
        }

        // If user provided unvalid input
        else {
            System.out.println("Invalid input. Please provide a valid .vm file or directory.");
        }
    }

    private static void processFile(File inputFile, CodeWriter codeWriter){
        // Construct a Parser to handle the input file
        Parser parser = new Parser(inputFile.toString());

        // Iterate through the input file, parsing each line and generating code from it.
        while (parser.hasMoreLines()){
            // Advance to the next instruction
            parser.advance();
            System.out.println(parser.commandType());
            if (parser.commandType() == Parser.CommandType.C_ARITHMETIC){
                codeWriter.writeArithmetic(parser.currentCommand);
            }
            else if (parser.commandType() == Parser.CommandType.C_PUSH || parser.commandType() == Parser.CommandType.C_POP) {
                codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
            else if (parser.commandType() == Parser.CommandType.C_LABEL) {
                codeWriter.writeLabel(parser.arg1());
            }
            else if (parser.commandType() == Parser.CommandType.C_GOTO) {
                codeWriter.writeGoto(parser.arg1());
            }
            else if (parser.commandType() == Parser.CommandType.C_IF) {
                codeWriter.writeIf(parser.arg1());
            }
            else if (parser.commandType() == Parser.CommandType.C_FUNCTION) {
                codeWriter.writeFunction(parser.arg1(), parser.arg2());
            }
            else if (parser.commandType() == Parser.CommandType.C_CALL) {
                codeWriter.writeCall(parser.arg1(), parser.arg2());
            }
            // If commandType is C_RETURN
            else{
                codeWriter.writeReturn();
            }

        }
    }
}