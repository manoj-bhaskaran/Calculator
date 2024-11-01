package com.calculator.logic;

import javax.swing.JTextField;
import java.text.DecimalFormat;

public class CalculatorController {

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private boolean isOperatorPressed = false;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;

    public CalculatorController(CalculatorLogic calculatorLogic, JTextField displayField, JTextField operatorField) {
        this.calculatorLogic = calculatorLogic;
        this.displayField = displayField;
        this.operatorField = operatorField;
        displayField.setText("0");  // Initial state display
    }

    public void appendToDisplay(String text) {
        if (isResultDisplayed || isOperatorPending) {
            displayField.setText(text); 
            isResultDisplayed = false;
            isOperatorPending = false;
        } else if ("0".equals(displayField.getText())) {
            displayField.setText(text.equals("0") ? "0" : text);
        } else {
            displayField.setText(displayField.getText() + text);
        }
    }

    public void handleOperation(String operation) {
        if (!displayField.getText().isEmpty()) {
            double currentOperand = Double.parseDouble(displayField.getText());
            calculatorLogic.pushOperand(currentOperand);
            calculatorLogic.pushOperator(operation);
            operatorField.setText(operation);
            isOperatorPressed = true;
            isOperatorPending = true;
        }
    }

    public void calculateResult() {
        if (!displayField.getText().isEmpty()) {
            calculatorLogic.pushOperand(Double.parseDouble(displayField.getText()));
            double result = calculatorLogic.getResult();
            DecimalFormat format = new DecimalFormat("0.#");
            displayField.setText(format.format(result));
            operatorField.setText("");
            calculatorLogic.clear();
            isResultDisplayed = true;
        }
    }
}
