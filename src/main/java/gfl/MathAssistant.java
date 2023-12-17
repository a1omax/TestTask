package gfl;

import gfl.calculator.Calculator;
import gfl.calculator.FormulaTree;
import gfl.calculator.LexicalAnalyzer;
import gfl.exceptions.IncorrectFormulaException;
import gfl.exceptions.LexicalException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MathAssistant {

    private Calculator calculator;
    JFrame mainFrame;

    JTextField formulaInputField;

    JTextField parameterInputField;
    JTextField searchInputTextField;

    JLabel resultLabel;

    JLabel currentFormulaLabel = new JLabel("");
    JLabel errorMessageLabel;

    CalculatorDB db;
    JButton calculateBtn;

    JList<String> searchJList;
    DefaultListModel<String> searchDefaultListModel;

    FormulaTree leftFormulaTree = null;
    FormulaTree rightFormulaTree = null;

    final static int APPLICATION_HEIGHT = 400;
    final static int APPLICATION_WIDTH = 600;

    final static double accuracy = 1e-9;

    private void init() {

        calculator = new Calculator();

        setupMainFrame();
        addFormulaInputPanel();
        addErrorMessagePanel();
        addCurrentFormula();
        addParameterInputPanel();
        addFormulaSearch();
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    /**
     * Creates database and start program
     */
    public void run() {
        db = new CalculatorDB();
        init();
    }


    public static void main(String[] args) {
        MathAssistant mathAssistant = new MathAssistant();
        mathAssistant.run();
    }

    private void setFormula(ActionEvent a) {
        String fullFormula = this.formulaInputField.getText();

        if (!LexicalAnalyzer.checkAlphabet(fullFormula)) {
            errorMessageLabel.setText("Wrong characters in formula");
            return;
        }

        try {
            String[] leftAndRightFormula = splitFormulaIntoLeftAndRightParts(fullFormula);
            leftFormulaTree = calculator.createOrReturnFormulaTree(leftAndRightFormula[0]);
            rightFormulaTree = calculator.createOrReturnFormulaTree(leftAndRightFormula[1]);
        } catch (IncorrectFormulaException | LexicalException e) {
            errorMessageLabel.setText(e.getMessage());
            return;
        }
        errorMessageLabel.setText(null);
        db.createEquation(formulaInputField.getText());


        currentFormulaLabel.setText(formulaInputField.getText());
    }

    private void setParameter() {
        if (leftFormulaTree == null || rightFormulaTree == null) {
            return;
        }

        // Todo: check in db

        HashMap<String, Double> variables = new HashMap<String, Double>();
        double parameterValue;

        try {
            parameterValue = Double.parseDouble(parameterInputField.getText());
        } catch (NumberFormatException e) {
            return;
        }

        variables.put("x", parameterValue);

        double leftResult;
        double rightResult;
        try {
            leftResult = calculator.calculateFormulaThree(leftFormulaTree, variables);
            rightResult = calculator.calculateFormulaThree(rightFormulaTree, variables);
        } catch (IncorrectFormulaException | LexicalException | ArithmeticException e) {
            resultLabel.setText(null);
            return;
        }
        if (Math.abs(leftResult - rightResult) < accuracy) {
            resultLabel.setForeground(Color.green);
            resultLabel.setText("correct");
            Integer equationId = db.readEquationId(currentFormulaLabel.getText());
            if (equationId != null) {
                db.createRoot(equationId, parameterValue);
            }
        } else {
            resultLabel.setForeground(Color.red);
            resultLabel.setText("wrong");
        }
    }

    private void search(ActionEvent a) {
        String searchStringValue = searchInputTextField.getText();
        double searchDoubleValue;
        try {
            searchDoubleValue = Double.valueOf(searchStringValue);
        } catch (NumberFormatException e){
            return;
        }
        Integer[] equationIdArray = db.readAllEquationIdMatchingRoot(searchDoubleValue);
        ArrayList<String> equations = new ArrayList<>();

        for (Integer equationId : equationIdArray){
            equations.add(db.readEquation(equationId));
        }
        searchDefaultListModel.clear();
        searchDefaultListModel.addAll(equations);

    }

    private void addFormulaInputPanel() {
        JLabel formulaLabel = new JLabel("Input new formula: ");
        formulaInputField = new JTextField(25);

        calculateBtn = new JButton("Set formula");
        calculateBtn.addActionListener(this::setFormula);

        JPanel formulaPanel = new JPanel();
        formulaPanel.add(formulaLabel);
        formulaPanel.add(formulaInputField);
        formulaPanel.add(calculateBtn);

        mainFrame.add(formulaPanel);
    }

    private void addErrorMessagePanel() {
        JPanel panel = new JPanel();
        errorMessageLabel = new JLabel();
        errorMessageLabel.setForeground(Color.red);
        panel.add(errorMessageLabel);
        mainFrame.add(panel);
    }


    private void addParameterInputPanel() {
        JLabel parameterLabel = new JLabel("Input X: ");
        parameterInputField = new JTextField(6);
        parameterInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setParameter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setParameter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setParameter();
            }
        });


        resultLabel = new JLabel();

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(parameterLabel);
        panel.add(parameterInputField);
        panel.add(resultLabel);


        Border borderLine = BorderFactory.createLineBorder(Color.black);
        panel.setBorder(borderLine);

        mainFrame.add(panel);
    }

    private void addCurrentFormula() {
        JLabel formulaNameLabel = new JLabel("Formula: ");

        JPanel panel = new JPanel();
        panel.add(formulaNameLabel);
        panel.add(this.currentFormulaLabel);


        Border borderLine = BorderFactory.createLineBorder(Color.black);
        panel.setBorder(borderLine);

        mainFrame.add(panel);

    }

    private void addFormulaSearch() {
        JLabel searchLabel = new JLabel("Input your root: ");
        searchInputTextField = new JTextField(6);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(this::search);


        searchDefaultListModel = new DefaultListModel<>();
        searchJList = new JList<>(searchDefaultListModel);
        JScrollPane searchScrollPane = new JScrollPane(searchJList);
        searchScrollPane.setPreferredSize(new Dimension(250, 40));



        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(searchLabel);
        panel.add(searchInputTextField);
        panel.add(searchBtn);
        panel.add(searchScrollPane);

        Border borderLine = BorderFactory.createLineBorder(Color.black);
        panel.setBorder(borderLine);

        mainFrame.add(panel);

    }


    private void setupMainFrame() {

        mainFrame = new JFrame();

        mainFrame.setLayout(new GridLayout(5, 1));

        mainFrame.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        mainFrame.setTitle("Calculator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }


    private String[] splitFormulaIntoLeftAndRightParts(String formula) throws IncorrectFormulaException {

        String[] leftAndRightPart = formula.split("=");
        if (leftAndRightPart.length != 2) {
            throw new IncorrectFormulaException("Formula must be made of 2 parts split with \"=\"");
        }
        return leftAndRightPart;
    }


}
