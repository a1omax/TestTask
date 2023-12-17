package gfl.calculator;

/**
 * Node of Formula Tree
 */
public class Node {

    private final TokenName tokenName;
    private double value;
    private String variableName;

    /**
     * Left node
     */
    Node left;
    /**
     * Right node
     */
    Node right;


    Node(TokenName tokenName) {
        this.tokenName = tokenName;
    }

    Node(TokenName tokenName, double value) {
        this.tokenName = tokenName;
        this.setValue(value);
    }


    Node(TokenName tokenName, String variableName) {
        this.tokenName = tokenName;
        this.setVariable(variableName);
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    private void setValue(double value) {
        this.value = value;
    }

    private void setVariable(String variableName) {
        this.variableName = variableName;
    }

    public TokenName getTokenName() {
        return tokenName;
    }

    public double getValue() {
        return value;
    }

    public String getVariableName() {
        return variableName;
    }
}
