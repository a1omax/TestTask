package gfl.calculator;

public enum TokenName {
    // Enumeration values for different token types

    VARIABLE("[a-zA-Z]{1}[a-zA-Z0-9]*"),
    NUMBER("[0-9]+(\\.[0-9]*)?"),
    OPERATOR_PLUS("\\+", 2),
    OPERATOR_MINUS("\\-", 2),
    OPERATOR_DIVIDE("\\/", 3),
    OPERATOR_MULTIPLY("\\*", 3),
    OPEN_PARENTHESIS("\\(", 1),
    CLOSE_PARENTHESIS("\\)", 1),
    UNARY_MINUS(5);


    // regular expression for parsing
    private final String regex;
    // priority for algorithm
    private final int priority;

    TokenName(int priority) {
        this.regex="$ "; // impossible regex
        this.priority = priority;
    }

    TokenName(String regex, int priority) {
        this.regex=regex;
        this.priority = priority;
    }
    TokenName(String regex) {
        this.regex=regex;
        this.priority = -1;
    }

    public String getRegex() {
        return regex;
    }

    public int getPriority(){
        return priority;
    }

    /**
     * Only binary operators token names
     * @return binary operators TokenName array
     */
    public static TokenName[] getBinaryOperators(){
        return new TokenName[]{
                OPERATOR_PLUS,
                OPERATOR_MINUS,
                OPERATOR_MULTIPLY,
                OPERATOR_DIVIDE,
        };
    }

    /**
     * Check if token is a binary operator
     * @param tokenName Token to check
     * @return true or false
     */
    public static boolean isBinaryOperator(TokenName tokenName) {
        TokenName[] binaryOperations = TokenName.getBinaryOperators();
        for (TokenName binaryOperation : binaryOperations) {
            if (tokenName == binaryOperation) return true;
        }
        return false;
    }

    public static TokenName[] getUnaryOperators(){
        return new TokenName[]{
                UNARY_MINUS,

        };
    }


    /**
     * Check if token is a binary operator
     * @param tokenName Token to check
     * @return true or false
     */
    public static boolean isUnaryOperator(TokenName tokenName) {
        TokenName[] unaryOperators = TokenName.getUnaryOperators();
        for (TokenName binaryOperation : unaryOperators) {
            if (tokenName == binaryOperation) return true;
        }

        return false;
    }



}