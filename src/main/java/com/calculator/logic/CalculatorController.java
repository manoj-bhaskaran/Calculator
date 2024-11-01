package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

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
        // Clear "OvFlow" if it is currently displayed and start fresh with new input
        if ("OvFlow".equals(displayField.getText())) {
            displayField.setText(""); // Clear the display to accept new input
        }
        // Allow 15 characters limit per operand
        if (displayField.getText().length() >= 15 && !isOperatorPending && !isResultDisplayed) {
            return; // Prevent further input if display is at the limit and not starting a new operand
        }

        if (text.equals(".")) {
            // If decimal is pressed after a result or operator, start fresh with "0."
            if (isResultDisplayed || isOperatorPending) {
                displayField.setText("0.");
                isResultDisplayed = false;
                isOperatorPending = false;
                lastWasOperator = false;
            } else if (!displayField.getText().contains(".")) {
                // Only add decimal if current input does not contain one already
                displayField.setText(displayField.getText() + ".");
            }
            return; // End here if decimal is added
        }

        // For non-decimal input
        if (isResultDisplayed || isOperatorPending) {
            // Start new number after result or operator
            displayField.setText(text);
            isResultDisplayed = false;
            isOperatorPending = false;
            lastWasOperator = false;
        } else if ("0".equals(displayField.getText())) {
            // Replace leading zero unless adding a decimal point
            displayField.setText(text.equals("0") ? "0" : text);
        } else {
            // Append input as usual
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

            // Define a threshold for underflow: smaller values will be displayed as "UndFlow"
            final double underflowThreshold = 1e-13; // Adjust as needed to fit 13 zeros for the display

            // Check for underflow: very small numbers below threshold will be shown as "UndFlow"
            if (Math.abs(result) < underflowThreshold && result != 0) {
                displayField.setText("UndFlow");
            } else {
                String resultString;
                if (result == (int) result) {
                    // If the result is an integer, display without decimal places
                    resultString = Integer.toString((int) result);
                } else {
                    // Convert result to string, limiting to 15 significant characters
                    resultString = String.format("%.15f", result)
                        .replaceAll("0*$", "") // Trim trailing zeros
                        .replaceAll("\\.$", ""); // Remove trailing decimal point if exists

                    // Ensure result fits within 15 characters, handling overflow
                    int maxWholeDigits = result < 0 ? 14 : 15; // Adjust for negative sign if needed
                    int wholeDigits = resultString.contains(".") ? resultString.indexOf('.') : resultString.length();

                    if (wholeDigits > maxWholeDigits) {
                        displayField.setText("OvFlow");
                        return;
                    } else if (resultString.length() > 15) {
                        int decimalPlaces = 15 - wholeDigits - 1;
                        resultString = String.format("%." + decimalPlaces + "f", result)
                            .replaceAll("0*$", "").replaceAll("\\.$", "");

                        if (resultString.length() > 15) {
                            displayField.setText("OvFlow");
                            return;
                        }
                    }
                }

                // Display the formatted result
                displayField.setText(resultString);
            }

            operatorField.setText("");
            calculatorLogic.clear();
            isResultDisplayed = true;
            lastWasOperator = false;
        }
    }
    
    public void handleDelete() {
        String currentText = displayField.getText();

        // Do nothing if the display contains an operator or single zero
        if (isOperatorPending || "0".equals(currentText) || currentText.isEmpty()) {
            return;
        }

        // Remove the last character if the display contains more than one character
        if (currentText.length() > 1) {
            displayField.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            // Reset to "0" if only one character remains
            displayField.setText("0");
        }
    }
}
