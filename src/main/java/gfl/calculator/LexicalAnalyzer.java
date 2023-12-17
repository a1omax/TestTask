package gfl.calculator;

import gfl.exceptions.IncorrectFormulaException;
import gfl.exceptions.LexicalException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class LexicalAnalyzer {
    // String that used to replace found tokens with it (in order to keep string length consistent)
    static String REPLACE_STRING = " ";

    static final String alphabet = "x0123456789-+*/()=.";
    static final TokenName[] TOKEN_NAMES = TokenName.values();

    /**
     * Create tokens from formula
     *
     * @param formula formula to tokenize
     * @return Array of tokens
     * @throws IncorrectFormulaException Formula has incorrect chars
     */
    public static ArrayList<Token> tokenize(String formula) throws IncorrectFormulaException, LexicalException {

        ArrayList<Token> tokens = new ArrayList<>();

        for (int i = 0; i < TOKEN_NAMES.length; i++) {
            formula = fillArrayWithTokens(formula, TOKEN_NAMES[i], tokens);
        }
        formula = formula.replace(REPLACE_STRING, "");
        if (formula.length() != 0) {
            throw new IncorrectFormulaException("Characters:  " + formula + "  are not acceptable");
        }

        if (tokens.isEmpty()) {
            throw new IncorrectFormulaException("Formula is empty");
        }

        // Sort array in order in which token has been found in line
        Collections.sort(tokens);

        replaceUnary(tokens);
        getErrors(tokens);

        return tokens;
    }

    /**
     * Get tokens from line and fill array with tokens
     *
     * @param line        String formula
     * @param tokenName   TokenName to get from line
     * @param arrayToFill Array of tokens
     * @return Line, found tokens in line replaced with REPLACE_STRING constant
     */
    private static String fillArrayWithTokens(String line, TokenName tokenName, ArrayList<Token> arrayToFill) {
        // Use regex of TokenName
        Pattern pattern = Pattern.compile(tokenName.getRegex());
        // Create matcher from compiled pattern
        Matcher matcher = pattern.matcher(line);

        // Create a list to store the tokens found in the line
        ArrayList<Token> tokens = new ArrayList<>();

        int posStart;
        int posEnd;

        // Loop through the matches in the line
        String tempSubstring;
        while (matcher.find()) {
            // Start position of the match
            posStart = matcher.start();
            // End position of the match
            posEnd = matcher.end();
            // Extract the matched substring
            tempSubstring = line.substring(posStart, posEnd);
            // Create a Token object and add it to the tokens list
            tokens.add(new Token(tokenName, tempSubstring, posStart, posEnd));
        }

        // Replace string with REPLACE STRING after in tokens positions
        StringBuilder sb = new StringBuilder(line);
        Token tempToken;
        for (int i = 0; i < tokens.size(); i++) {
            tempToken = tokens.get(i);
            sb.replace(tempToken.getStartPosition(), tempToken.getEndPosition(), REPLACE_STRING.repeat(tempToken.getContent().length()));
        }
        line = sb.toString();
        // Fill array with tokens
        arrayToFill.addAll(tokens);
        // Return line with replaces
        return line;
    }

    /**
     * Replaces specific instances of the minus operator with the unary minus operator
     * based on the operators that can appear before a minus operator
     *
     * @param tokens The list of tokens to process.
     */
    private static void replaceUnary(ArrayList<Token> tokens) {


        // If minus is first token then it always unary minus
        if (tokens.get(0).getTokenName() == TokenName.OPERATOR_MINUS) {
            tokens.get(0).setTokenName(TokenName.UNARY_MINUS);
        }

        Token previousToken;
        Token currentToken;
        // Start with i=2, (previous token = 1, current token = 2)
        for (int i = 2; i < tokens.size(); i++) {
            previousToken = tokens.get(i - 1);
            currentToken = tokens.get(i);
            // Replace token name from minus to unary minus if operator before is number, variable or close parenthesis
            if (currentToken.getTokenName() == TokenName.OPERATOR_MINUS &&
                    (previousToken.getTokenName() != TokenName.NUMBER
                            && previousToken.getTokenName() != TokenName.VARIABLE
                            && previousToken.getTokenName() != TokenName.CLOSE_PARENTHESIS)
            ) {
                currentToken.setTokenName(TokenName.UNARY_MINUS);
            }
        }

    }


    /**
     * Collect lexical errors and print them into System.err, and throw LexicalException if there are errors at the end of this function
     *
     * @param tokens Tokens to check for errors
     * @throws LexicalException Exception for that tells that there is an error in formula
     */
    private static void getErrors(ArrayList<Token> tokens) throws LexicalException {
        StringBuilder errors = new StringBuilder();

        int n_operands = (int) tokens.stream()
                .filter(token -> token.getTokenName().equals(TokenName.NUMBER) || token.getTokenName().equals(TokenName.VARIABLE))
                .count();
        int n_binary_operators = (int) tokens.stream().filter((token) -> TokenName.isBinaryOperator(token.getTokenName())).count();

        if (n_operands - 1 > n_binary_operators) {
            errors.append("Too many numbers for binary operators\n");
        } else if (n_operands - 1 < n_binary_operators) {
            errors.append("Not enough numbers for binary operators\n");
        }


        int parenthesis = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getTokenName() == TokenName.OPEN_PARENTHESIS) {
                parenthesis++;
            } else if (tokens.get(i).getTokenName() == TokenName.CLOSE_PARENTHESIS) {
                parenthesis--;
            }
        }
        if (parenthesis > 0) {
            errors.append("Not enough close parenthesis");
        } else if (parenthesis < 0) {
            errors.append("Not enough open parenthesis");
        }

        if (!errors.toString().equals("")) {
            //System.err.println(errors);
            throw new LexicalException(errors.toString());
        }
    }

    public static boolean checkAlphabet(String fullFormula) {
        for (Character c : fullFormula.toCharArray()) {
            if (alphabet.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }
}
