import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {

    public BufferedWriter bufferedWriter;
    JackTokenizer jackTokenizer;
    private boolean bFirstRoutine;

    // A constructor for the class. Creates a compilation engine with the given input and output.
    public CompilationEngine(File inputFile, File outputFile) {
        this.jackTokenizer = new JackTokenizer(inputFile.toString());
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bFirstRoutine = true;

    }

    // compiles a complete class
    public void compileClass() {
        try {
            jackTokenizer.advance();
            jackTokenizer.advance();
            bufferedWriter.write("<class>\n");
            bufferedWriter.write("<keyword> class </keyword>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<symbol> { </symbol>\n");
            compileClassVarDec();
            compileSubRoutine();
            bufferedWriter.write("<symbol> } </symbol>\n");
            bufferedWriter.write("</class>\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // compiles a static declaration or a field declaration
    public void compileClassVarDec() {
        jackTokenizer.advance();
        try {
            //bufferedWriter.write(jackTokenizer.keyWord() + " ---- is the current item!!!!!! " + jackTokenizer.pointer+"\n");
            while (jackTokenizer.keyWord().equals("static") || jackTokenizer.keyWord().equals("field")) {
                bufferedWriter.write("<classVarDec>\n");
                // field or static
                bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                jackTokenizer.advance();
                // if for example, field Square square (Square)
                if (jackTokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                    bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                }
                // if for example, field int square (int)
                else {
                    bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");

                }
                jackTokenizer.advance();
                // third word of the classvardec - e.g. square in the above - field int square
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
                // if there are multiple in 1 line - e.g. field int x, y
                if (jackTokenizer.symbol() == ',') {
                    bufferedWriter.write("<symbol> , </symbol>\n");
                    jackTokenizer.advance();
                    bufferedWriter.write(("<identifier> " + jackTokenizer.identifier() + " </identifier>\n"));
                    jackTokenizer.advance();
                }
                // semicolon
                bufferedWriter.write("<symbol> ; </symbol>\n");
                jackTokenizer.advance();
                bufferedWriter.write(" </classVarDec>\n");
            }

            // if reach a subroutine, go back in the arraylist to accommodate for advance in the next call
            if (jackTokenizer.keyWord().equals("function") || jackTokenizer.keyWord().equals("method") || jackTokenizer.keyWord().equals("constructor")) {
                jackTokenizer.pointer--;
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // compiles a complete method, function, or a constructor
    public void compileSubRoutine() {
        boolean hasSubRoutines = false;

        jackTokenizer.advance();
        try {
            // once reach the end, return  - no more subroutines - base case for the recursive call
            if (jackTokenizer.symbol() == '}' && jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL) {
                return;
            }
            // subroutinedec tag
            if ((bFirstRoutine) && (jackTokenizer.keyWord().equals("function") || jackTokenizer.keyWord().equals("method") || jackTokenizer.keyWord().equals("constructor"))) {
                bFirstRoutine = false;
                bufferedWriter.write("<subroutineDec>\n");
                hasSubRoutines = true;
            }
            // function ,e
            if (jackTokenizer.keyWord().equals("function") || jackTokenizer.keyWord().equals("method") || jackTokenizer.keyWord().equals("constructor")) {
                hasSubRoutines = true;
                bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                jackTokenizer.advance();
            }
            // if there is an identifier in the subroutine statement position 2 e.g. function Square getX()
            if (jackTokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
            }
            // if keyword instead for subroutine statement position 2 e.g. function int getX()
            else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD) {
                bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                jackTokenizer.advance();
            }
            // name of the subroutine
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.IDENTIFIER) {
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
            }
            // get parameters, or lack there of
            if (jackTokenizer.symbol() == '(') {
                bufferedWriter.write("<symbol> ( </symbol>\n");
                bufferedWriter.write("<parameterList>\n");

                compileParameterList();
                bufferedWriter.write("</parameterList>\n");
                bufferedWriter.write("<symbol> ) </symbol>\n");

            }
            jackTokenizer.advance();
            // start subroutine body
            if (jackTokenizer.symbol() == '{') {
                bufferedWriter.write("<subroutineBody>\n");
                bufferedWriter.write("<symbol> { </symbol>\n");
                jackTokenizer.advance();
            }
            // get all var declarations in the subroutine
            while (jackTokenizer.keyWord().equals("var") && (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<varDec>\n ");
                jackTokenizer.pointer--;
                compileVarDec();
                bufferedWriter.write(" </varDec>\n");
            }
            bufferedWriter.write("<statements>\n");
            compileStatements();
            bufferedWriter.write("</statements>\n");
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
            if (hasSubRoutines) {
                bufferedWriter.write("</subroutineBody>\n");
                bufferedWriter.write("</subroutineDec>\n");
                bFirstRoutine = true;
            }

            // recursive call
            compileSubRoutine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    // compiles a (possibly empty) parameter list including the "()"
    public void compileParameterList() {
        jackTokenizer.advance();
        try {
            // until reach the end - )
            while (!(jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == ')')) {
                if (jackTokenizer.tokenType()== JackTokenizer.TokenType.IDENTIFIER) {
                    bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                    jackTokenizer.advance();
                } else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD) {
                    bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                    jackTokenizer.advance();
                }
                // commas separate the list, if there are multiple
                else if ((jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == ',')) {
                    bufferedWriter.write("<symbol> , </symbol>\n");
                    jackTokenizer.advance();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles a var declaration
    public void compileVarDec() {
        jackTokenizer.advance();
        try {

            if (jackTokenizer.keyWord().equals("var") && (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<keyword> var </keyword>\n");
                jackTokenizer.advance();
            }
            // type of var, if identifier, e.g. Square or Array
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.IDENTIFIER) {
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
            }
            // type of var, if keyword, e.g. int or boolean
            else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD) {
                bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                jackTokenizer.advance();
            }
            // name of var
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.IDENTIFIER) {
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
            }
            // if there are mutliple in 1 line
            if ((jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == ',')) {
                bufferedWriter.write("<symbol> , </symbol>\n");
                jackTokenizer.advance();
                bufferedWriter.write(("<identifier> " + jackTokenizer.identifier() + " </identifier>\n"));
                jackTokenizer.advance();
            }
            // end of var line
            if ((jackTokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == ';')) {
                bufferedWriter.write("<symbol> ; </symbol>\n");
                jackTokenizer.advance();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // compiles a sequence of statements, not including the enclosing "{}" - do, let, if, while or return
    public void compileStatements() {
        try {
            if (jackTokenizer.symbol() == '}' && (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL)) {
                return;
            } else if (jackTokenizer.keyWord().equals("do") && (jackTokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<doStatement>\n ");
                compileDo();
                bufferedWriter.write((" </doStatement>\n"));

            } else if (jackTokenizer.keyWord().equals("let") && (jackTokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<letStatement>\n ");
                compileLet();
                bufferedWriter.write((" </letStatement>\n"));
            } else if (jackTokenizer.keyWord().equals("if") && (jackTokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<ifStatement>\n ");
                compileIf();
                bufferedWriter.write((" </ifStatement>\n"));
            } else if (jackTokenizer.keyWord().equals("while") && (jackTokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<whileStatement>\n ");
                compileWhile();
                bufferedWriter.write((" </whileStatement>\n"));
            } else if (jackTokenizer.keyWord().equals("return") && (jackTokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD)) {
                bufferedWriter.write("<returnStatement>\n ");
                compileReturn();
                bufferedWriter.write((" </returnStatement>\n"));
            }
            jackTokenizer.advance();
            compileStatements();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // compiles a do statement
    public void compileDo() {
        try {
            if (jackTokenizer.keyWord().equals("do")) {
                bufferedWriter.write("<keyword> do </keyword>\n");
            }
            // function call
            compileCall();
            // semi colon
            jackTokenizer.advance();
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void compileCall() {
        jackTokenizer.advance();
        try {
            // first part
            bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            jackTokenizer.advance();
            // if . - then is something like Screen.erase()
            if ((jackTokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == '.')) {
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                jackTokenizer.advance();
                bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
                jackTokenizer.advance();
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                // parameters in the parentheses
                bufferedWriter.write("<expressionList>\n");
                compileExpressionList();
                bufferedWriter.write("</expressionList>\n");
                jackTokenizer.advance();
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");


            }
            // if ( then is something like erase()
            else if ((jackTokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == '(')) {
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                bufferedWriter.write("<expressionList>\n");
                compileExpressionList();
                bufferedWriter.write("</expressionList>\n");
                // parentheses )
                jackTokenizer.advance();
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles a let statement
    public void compileLet() {
        try {
            bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<identifier> " + jackTokenizer.identifier() + " </identifier>\n");
            jackTokenizer.advance();
            if ((jackTokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL) && (jackTokenizer.symbol() == '[')) {
                // there is an expression -- because we have x[5] for example
                bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                compileExpression();
                jackTokenizer.advance();
                if ((jackTokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL) && ((jackTokenizer.symbol() == ']'))) {
                    bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                }
                // only advance if there is an expression
                jackTokenizer.advance();

            }

            // = sign
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");

            compileExpression();
            // semi colon
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
            jackTokenizer.advance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles a while statement
    public void compileWhile() {
        try {
            // while
            bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
            jackTokenizer.advance();
            // (
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
            // compile inside of () - expression
            compileExpression();
            // )
            jackTokenizer.advance();
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
            jackTokenizer.advance();
            // {
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
            // inside of while statement
            bufferedWriter.write("<statements>\n");
            compileStatements();
            bufferedWriter.write("</statements>\n");
            // }
            bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles a return statement
    public void compileReturn() {
        try {
            bufferedWriter.write("<keyword> return </keyword>\n");
            jackTokenizer.advance();
            if (!((jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == ';'))) {
                jackTokenizer.pointer--;
                compileExpression();
            }
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == ';') {
                bufferedWriter.write("<symbol> ; </symbol>\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // compiles an if statement, possibly with a trailing else clause
    public void compileIf() {
        try {
            bufferedWriter.write("<keyword> if </keyword>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<symbol> ( </symbol>\n");
            // expression within if () condition
            compileExpression();
            bufferedWriter.write("<symbol> ) </symbol>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<symbol> { </symbol>\n");
            jackTokenizer.advance();
            bufferedWriter.write("<statements>\n");
            // compile statements within if clause { }
            compileStatements();
            bufferedWriter.write("</statements>\n");
            bufferedWriter.write("<symbol> } </symbol>\n");
            jackTokenizer.advance();
            // if there is an else clause of the if statement
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD && jackTokenizer.keyWord().equals("else")) {
                bufferedWriter.write("<keyword> else </keyword>\n");
                jackTokenizer.advance();
                bufferedWriter.write("<symbol> { </symbol>\n");
                jackTokenizer.advance();
                bufferedWriter.write("<statements>\n");
                // compile statements within else clause
                compileStatements();
                bufferedWriter.write("</statements>\n");
                bufferedWriter.write("<symbol> } </symbol>\n");
            } else {
                // keep placeholder correct
                jackTokenizer.pointer--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // compiles an expression
    public void compileExpression() {
        try {
            bufferedWriter.write("<expression>\n");
            compileTerm();
            while (true) {
                jackTokenizer.advance();
                if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.isOperation()) {
                    // < > & = have different xml code
                    if (jackTokenizer.symbol() == '<') {
                        bufferedWriter.write("<symbol> &lt; </symbol>\n");
                    } else if (jackTokenizer.symbol() == '>') {
                        bufferedWriter.write("<symbol> &gt; </symbol>\n");
                    } else if (jackTokenizer.symbol() == '&') {
                        bufferedWriter.write("<symbol> &amp; </symbol>\n");
                    } else {
                        bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                    }
                    compileTerm();
                } else {
                    jackTokenizer.pointer--;
                    break;
                }
            }
            bufferedWriter.write("</expression>\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles a term - if current token is an identifier, must distinguish between variable, array entry, and subroutine call
    // single look ahead token which may be "{" "(" or "." to distinguish between the three possibilities
    public void compileTerm() {
        try {
            bufferedWriter.write("<term>\n");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER) {
                String prevIdentifier = jackTokenizer.identifier();
                jackTokenizer.advance();
                // for [] terms
                if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == '[') {
                    bufferedWriter.write("<identifier> " + prevIdentifier + " </identifier>\n");
                    bufferedWriter.write("<symbol> [ </symbol>\n");
                    compileExpression();
                    jackTokenizer.advance();
                    bufferedWriter.write("<symbol> ] </symbol>\n");
                }
                // for ( or . - subroutine calls
                else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && (jackTokenizer.symbol() == '(' || jackTokenizer.symbol() == '.')) {
                    jackTokenizer.pointer--;
                    jackTokenizer.pointer--;
                    compileCall();

                } else {
                    bufferedWriter.write("<identifier> " + prevIdentifier + " </identifier>\n");
                    jackTokenizer.pointer--;
                }
            } else {
                // integer
                if (jackTokenizer.tokenType()== JackTokenizer.TokenType.INT_CONST) {
                    bufferedWriter.write("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n");

                }
                // strings
                else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.STRING_CONST) {
                    bufferedWriter.write("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n");
                }
                // this true null or false
                else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.KEYWORD && (jackTokenizer.keyWord().equals("this") || jackTokenizer.keyWord().equals("null")
                        || jackTokenizer.keyWord().equals("false") || jackTokenizer.keyWord().equals("true"))) {
                    bufferedWriter.write("<keyword> " + jackTokenizer.keyWord() + " </keyword>\n");
                }
                // parenthetical separation
                else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == '(') {
                    bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                    compileExpression();
                    jackTokenizer.advance();
                    bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                }
                // unary operators
                else if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && (jackTokenizer.symbol() == '-' || jackTokenizer.symbol() == '~')) {
                    bufferedWriter.write("<symbol> " + jackTokenizer.symbol() + " </symbol>\n");
                    // recursive call
                    compileTerm();
                }
            }
            bufferedWriter.write("</term>\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // compiles (possibly empty) comma separated list of expressions
    public void compileExpressionList() {
        jackTokenizer.advance();
        // end of list
        if (jackTokenizer.symbol() == ')' && jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL) {
            jackTokenizer.pointer--;
        } else {
            jackTokenizer.pointer--;
            compileExpression();
        }
        while (true) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType()== JackTokenizer.TokenType.SYMBOL && jackTokenizer.symbol() == ',') {
                try {
                    bufferedWriter.write("<symbol> , </symbol>\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } else {
                jackTokenizer.pointer--;
                break;
            }
        }

    }
}
