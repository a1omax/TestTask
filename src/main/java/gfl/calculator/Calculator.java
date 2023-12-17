package gfl.calculator;

import gfl.exceptions.IncorrectFormulaException;
import gfl.exceptions.LexicalException;

import java.util.HashMap;


/**
 * Calculator that can take arguments formula and variables and return result
 */
public class Calculator {

    HashMap<String, FormulaTree> storage = new HashMap<>();


    /**
     * Create FormulaTree of formula if it is not present in storage HashMap yet. If present returns it
     *
     * @param formula Math formula
     * @return Tree of formula
     * @throws IncorrectFormulaException
     * @throws LexicalException
     */
    public FormulaTree createOrReturnFormulaTree(String formula) throws IncorrectFormulaException, LexicalException {
        if (storage.containsKey(formula)) {
            return storage.get(formula);
        }

        FormulaTree formulaTree = FormulaTree.createFormulaTree(LexicalAnalyzer.tokenize(formula));
        storage.put(formula, formulaTree);
        return formulaTree;

    }

    /**
     * @param formula   Math formula
     * @param variables Variables for formula
     * @return Calculated value
     * @throws IncorrectFormulaException
     * @throws LexicalException
     * @throws ArithmeticException
     */
    public double calculate(String formula, HashMap<String, Double> variables) throws IncorrectFormulaException, LexicalException, ArithmeticException {
        FormulaTree formulaTree = createOrReturnFormulaTree(formula);

        return calculateNodes(formulaTree.firstNode, variables);
    }

    public double calculateFormulaThree(FormulaTree formulaTree, HashMap<String, Double> variables) throws IncorrectFormulaException, LexicalException, ArithmeticException {

        return calculateNodes(formulaTree.firstNode, variables);
    }


    /**
     * Recursively calculates the result of a mathematical expression represented as a tree of nodes.
     *
     * @param node      The root node of the expression tree to be evaluated.
     * @param variables Variables for formula
     * @return Result for formula with given variables
     * @throws IncorrectFormulaException
     */
    private double calculateNodes(Node node, HashMap<String, Double> variables) throws IncorrectFormulaException {
        TokenName nodeTokenName = node.getTokenName();

        // If the node is a number, return its value
        if (nodeTokenName == TokenName.NUMBER) {
            return node.getValue();
        }

        // If node is a variable, try get value from variables
        if (nodeTokenName == TokenName.VARIABLE) {

            if (!variables.containsKey(node.getVariableName())) {
                throw new IncorrectFormulaException("Argument " + "\"" + node.getVariableName() + "\" is not set");
            }
            return variables.get(node.getVariableName());
        }
        // Perform binary operation recursively calculating tree
        if (TokenName.isBinaryOperator(nodeTokenName)) {
            return performBinary(calculateNodes(node.left, variables), calculateNodes(node.right, variables), nodeTokenName);
        }
        // Perform unary operation recursively calculating tree
        if (TokenName.isUnaryOperator(nodeTokenName)) {
            return performUnary(calculateNodes(node.right, variables), nodeTokenName);
        }
        return 0;
    }

    /**
     * Perform binary operation
     *
     * @param firstValue
     * @param secondValue
     * @param binaryOperation
     * @return result of operation
     */
    private double performBinary(double firstValue, double secondValue, TokenName binaryOperation) {
        switch (binaryOperation) {
            case OPERATOR_PLUS -> {
                return firstValue + secondValue;
            }
            case OPERATOR_MINUS -> {
                return firstValue - secondValue;
            }
            case OPERATOR_MULTIPLY -> {
                return firstValue * secondValue;
            }
            case OPERATOR_DIVIDE -> {
                if (secondValue == 0.) {
                    throw new ArithmeticException("Zero division at " + firstValue + "/" + secondValue);
                }
                return firstValue / secondValue;
            }
        }

        return 0;
    }

    /**
     * Perform unary operation
     *
     * @param firstValue
     * @param unaryOperation
     * @return result of operation
     * @throws ArithmeticException
     */
    private double performUnary(double firstValue, TokenName unaryOperation) throws ArithmeticException {
        double result = 0;
        switch (unaryOperation) {
            case UNARY_MINUS -> {
                result = -firstValue;
            }

        }

        return result;
    }
}