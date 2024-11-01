package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class CalculatorController {

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;
    private boolean lastWasOperator = false; // Flag to track if the last entry was an operator

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
            lastWasOperator = false;
        } else if ("0".equals(displayField.getText())) {
            displayField.setText(text.equals("0") ? "0" : text);
        } else {
            displayField.setText(displayField.getText() + text);
        }
    }

    public void handleOperation(String operation) {
        if (lastWasOperator) {
            // Replace the last operator
            calculatorLogic.replaceLastOperator(operation);
            // Use helper to get the display symbol
            operatorField.setText(SymbolFormatter.getDisplaySymbol(operation));
        } else {
            // If last entry was not an operator, proceed normally
            if (!displayField.getText().isEmpty()) {
                double currentOperand = Double.parseDouble(displayField.getText());
                calculatorLogic.pushOperand(currentOperand);
                calculatorLogic.pushOperator(operation);
                // Use helper to get the display symbol
                operatorField.setText(SymbolFormatter.getDisplaySymbol(operation));
                isOperatorPending = true;
                lastWasOperator = true; // Set flag as last was an operator
            }
        }
    }

    public void calculateResult() {
        if (!displayField.getText().isEmpty()) {
            calculatorLogic.pushOperand(Double.parseDouble(displayField.getText()));
            double result = calculatorLogic.getResult();

            // Convert result to string for length and character limit checks
            String resultString = Double.toString(result);

            if (resultString.length() > 15) {
                int maxWholeDigits = result < 0 ? 14 : 15; // Reserve a character for the sign if negative
                int wholeDigits = resultString.contains(".") ? resultString.indexOf('.') : resultString.length();

                if (wholeDigits > maxWholeDigits) {
                    // If the integer part alone exceeds 15 characters, display "OvFlow"
                    displayField.setText("OvFlow");
                } else {
                    // Calculate maximum decimal places to fit within 15 characters
                    int decimalPlaces = 15 - wholeDigits - 1; // 1 reserved for decimal point
                    String formatPattern = "0." + "0".repeat(decimalPlaces);

                    // Create a formatter with the calculated pattern
                    DecimalFormat format = new DecimalFormat(formatPattern);
                    format.setRoundingMode(RoundingMode.HALF_UP);

                    // Format the result to fit the display limit
                    resultString = format.format(result);

                    // Final check to ensure result fits within 15 characters
                    if (resultString.length() > 15) {
                        displayField.setText("OvFlow");
                    } else {
                        displayField.setText(resultString);
                    }
                }
            } else {
                // Display directly if within 15-character limit
                displayField.setText(resultString);
            }

            operatorField.setText("");
            calculatorLogic.clear();
            isResultDisplayed = true;
            lastWasOperator = false;
        }
    }
}
