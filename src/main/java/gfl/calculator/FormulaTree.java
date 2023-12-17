package gfl.calculator;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Binary/Unary Tree of Nodes
 */
public class FormulaTree {
    Node firstNode;

    FormulaTree(Node firstNode){
        this.firstNode=firstNode;
    };

    /**
     * Creates FormulaTree from array of tokens
     * @param tokens array of tokens
     * @return FormulaTree
     */
    public static FormulaTree createFormulaTree(List<Token> tokens) {
        // It's polish reverse notation

        // Reversing all tokens and adding them to formula stack
        Collections.reverse(tokens);
        Stack<Token> formula = new Stack<>();
        formula.addAll(tokens);


        Stack<Node> nodes = new Stack<>();
        Stack<TokenName> operators = new Stack<>();


        Token currToken;
        while (!formula.empty()) {
            // Take element from formula stack
            currToken = formula.pop();

            // If it's a number
            if (currToken.getTokenName() == TokenName.NUMBER){
                // Parse double, create node and push it to nodes stack
                nodes.push(new Node(currToken.getTokenName(), Double.parseDouble(currToken.content)));
                continue;
            }
            // If it's variable
            if (currToken.getTokenName() == TokenName.VARIABLE) {
                // Create node and push it to nodes stack
                nodes.push(new Node(currToken.getTokenName(), currToken.content));
                continue;
            }

            // If it's open parenthesis
            if (currToken.getTokenName() == TokenName.OPEN_PARENTHESIS) {
                //  Push it to nodes operators stack
                operators.push(currToken.getTokenName());
                continue;
            }
            // If it's close parenthesis
            if (currToken.getTokenName() == TokenName.CLOSE_PARENTHESIS) {
                // Get all elements from operators until it's empty (but it should not be empty) or open parenthesis
                while (!operators.empty() && operators.peek() != TokenName.OPEN_PARENTHESIS) {
                    // Combine nodes
                    combineNodes(nodes, operators);
                }
                // After while loop the element on top of operator is open parenthesis so delete it
                // (if not empty must always be true, but if it's not it calculating anyway
                if (!operators.empty()) { // TokenName == (
                    operators.pop();
                }
                continue;
            }
            // If no operators in stack then always push it there
            if (operators.empty()) {
                operators.push(currToken.getTokenName());
                continue;
            }
            // If operators are the same and not a division  ("crutch")
            if (currToken.getTokenName()!= TokenName.OPERATOR_DIVIDE && currToken.getTokenName() == operators.peek()){
                operators.push(currToken.getTokenName());
                continue;
            }
            // If priority is bigger
            if (currToken.getTokenName().getPriority() > operators.peek().getPriority()) {
                operators.push(currToken.getTokenName());
                continue;
            }
            // If priority is less or equals
            while (!operators.empty() &&  currToken.getTokenName().getPriority() <= operators.peek().getPriority()) {
                combineNodes(nodes, operators);
            }
            operators.push(currToken.getTokenName());
        }
        // After formula stack is empty, apply all operators combinations
        while (!operators.empty()) {
            combineNodes(nodes, operators);
        }
        return new FormulaTree(nodes.pop());
    }

    /**
     * Get nodes from node stack, create operator node from operators stack, link them
     * @param nodes Stack of nodes
     * @param operators Stack of operators
     */
    private static void combineNodes(Stack<Node> nodes, Stack<TokenName> operators) {

        TokenName operationName = operators.pop();

        Node rightNode = nodes.pop();
        Node newNode = new Node(operationName);
        newNode.right = rightNode;

        if (TokenName.isBinaryOperator(operationName)) {
            newNode.left = nodes.pop();
        }

        nodes.push(newNode);
    }

}
