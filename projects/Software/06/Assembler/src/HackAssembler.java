import java.io.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class HackAssembler {

    // Translates a decimal number to its binary value and return it as a 16 chars string
    public static String toBinary(int decimal) {
        // Handle the special case of zero
        if (decimal == 0) {
            return "0000000000000000";
        }

        // Use StringBuilder for efficient string concatenation
        StringBuilder binaryResult = new StringBuilder();

        // Convert decimal to binary
        while (decimal > 0) {
            int remainder = decimal % 2;
            binaryResult.insert(0, remainder); // Insert at the beginning
            decimal /= 2;
        }

        // Add leading zeros to ensure a total length of 16 bits
        while (binaryResult.length() < 16) {
            binaryResult.insert(0, '0'); // Insert at the beginning
        }

        return binaryResult.toString();
    }


    // Return true if the given string represents an integer
    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java HackAssembler <file or directory>");
            return;
        }

        File inputFile = new File(args[0]);

        if (inputFile.isFile() && args[0].endsWith(".asm")) {
            // Single .asm file provided
            processFile(inputFile);
        } else if (inputFile.isDirectory()) {
            // Directory provided, translate all .asm files within the folder
            File[] asmFiles = inputFile.listFiles((dir, name) -> name.endsWith(".asm"));
            if (asmFiles != null) {
                for (File asmFile : asmFiles) {
                    processFile(asmFile);
                }
            }
        } else {
            System.out.println("Invalid input. Please provide a valid .asm file or directory.");
        }
    }
    private static void processFile(File inputFile) throws IOException {
        Parser parser = new Parser(inputFile.toString());
        // Create the output file with the same name as the input file but with .hack extension
        String outputFileName = inputFile.getAbsolutePath().replace(".asm", ".hack");
        File outputFile = new File(outputFileName);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            SymbolTable symbolTable = new SymbolTable();
            for(int i = 0; i <= 15; i++){
                symbolTable.addEntry("R"+i, i);
            }
            symbolTable.addEntry("SCREEN", 16384);
            symbolTable.addEntry("KBD", 24576);
            symbolTable.addEntry("SP", 0);
            symbolTable.addEntry("LCL", 1);
            symbolTable.addEntry("ARG", 2);
            symbolTable.addEntry("THIS", 3);
            symbolTable.addEntry("THAT", 4);
            int nextFreeCell = 16;

            int labels=0;
            // First pass - reads the program lines, one by one and adds the found labels to the symbol table
            parser.counter = 0;
            while (parser.hasMoreLines()){
                // Advance to the next instruction
                parser.advance();
                if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION){
                    symbolTable.addEntry(parser.symbol(), parser.counter);
                    labels++;
                }
                else {
                    parser.counter++;
                }
            }

            symbolTable.printTable();


            // Reset the parser to be at the top of the file again
            parser.restartParser();

            // Second pass - reads the program lines, one by one and translate instructions to binary strings
            while (parser.hasMoreLines()){
                // Advance to the next instruction
                parser.advance();
                String binaryInstruction;

                // If the instruction is @ symbol
                if (parser.instructionType() == Parser.InstructionType.A_INSTRUCTION){
                    // If symbol is an integer get it's number to be the address
                    if (isNumber(parser.symbol())){
                        binaryInstruction = toBinary(Integer.parseInt(parser.symbol()));
                    }
                    // If symbol is not in the symbol table, adds it to the table and translates value to its binary value
                    else if (!symbolTable.contains(parser.symbol())){
                        symbolTable.addEntry(parser.symbol(),nextFreeCell);
                        nextFreeCell++;
                        binaryInstruction = toBinary(symbolTable.getAddress(parser.symbol()));
                    }
                    // if the symbol is in the symbol table translate value to its binary value
                    else{
                        binaryInstruction = toBinary(symbolTable.getAddress(parser.symbol()));
                    }

                    // Write the bunary value to a new line in the hack file and open a new line
                    bufferedWriter.write(binaryInstruction);
                    bufferedWriter.newLine();
                }

                // If the instruction is dest = comp ; jump
                else if (parser.instructionType() == Parser.InstructionType.C_INSTRUCTION) {
                    binaryInstruction = "111" + Code.comp(parser.comp()) + Code.dest(parser.dest()) + Code.jump(parser.jump());
                    bufferedWriter.write(binaryInstruction);
                    bufferedWriter.newLine();

                }
                // L Instruction - does nothing

            }
            bufferedWriter.close();


            System.out.println("Translation complete for " + inputFile.getName());
        }
    }
}
