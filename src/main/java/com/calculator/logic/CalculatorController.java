package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

public class CalculatorController {
    
    private static final String UNDERFLOW_SCIENTIFIC_FORMAT = "%.13e";  // Define a constant for scientific notation format

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private final JTextField expField;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;
    private boolean lastWasOperator = false; // Flag to track if the last entry was an operator

    public CalculatorController(CalculatorLogic calculatorLogic, JTextField displayField, JTextField operatorField, JTextField expField) {
        this.calculatorLogic = calculatorLogic;
        this.displayField = displayField;
        this.operatorField = operatorField;
        this.expField = expField;
        displayField.setText("0");  // Initial state display
        operatorField.setText("");
        expField.setText("");
    }

    public void appendToDisplay(String text) {
        // Clear "OvFlow" if it is currently displayed and start fresh with new input
        if ("OvFlow".equals(displayField.getText())) {
            displayField.setText(""); // Clear the display to accept new input
            expField.setText(""); // Clear exponent field as well
        }

        // Allow 15 characters limit per operand
        if (displayField.getText().length() >= 15 && !isOperatorPending && !isResultDisplayed) {
            return; // Prevent further input if display is at the limit and not starting a new operand
        }

        if (text.equals(".")) {
            // If decimal is pressed after a result or operator, start fresh with "0."
            if (isResultDisplayed || isOperatorPending) {
                displayField.setText("0.");
                expField.setText(""); // Clear exponent field
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
            expField.setText(""); // Clear exponent field
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
                double currentOperand;
                if (!expField.getText().isEmpty()) {
                    // If there's an exponent, combine display and exponent fields
                    String combinedValue = displayField.getText() + "E" + expField.getText().substring(1); // Remove "E" symbol
                    currentOperand = Double.parseDouble(combinedValue);
                } else {
                    // No exponent, parse normally
                    currentOperand = Double.parseDouble(displayField.getText());
                }
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

            // Define thresholds for underflow and overflow
            final double underflowThreshold = 1e-13;

            String resultString;
            String exponentString = "";

            // Check if result is too small for standard display
            if (Math.abs(result) < underflowThreshold && result != 0) {
                // Display scientific notation for very small numbers
                resultString = String.format(UNDERFLOW_SCIENTIFIC_FORMAT, result);
                String[] parts = resultString.split("e");
                resultString = trimTrailingZeros(parts[0]);
                exponentString = "E" + formatExponent(Integer.parseInt(parts[1]));
                displayField.setText(resultString);
                expField.setText(exponentString);
            } else {
                // Convert result to a plain string for analysis
                resultString = trimTrailingZeros(String.format("%.15f", result));
                resultString = removeTrailingDecimalPoint(resultString);

                // Check if exponential notation is needed based on conditions
                String[] integerAndDecimal = resultString.split("\\.");
                String integerPart = integerAndDecimal[0];

                if ((integerPart.length() > 15) || (integerPart.equals("0") && integerAndDecimal.length > 1 && integerAndDecimal[1].startsWith("0000000000000"))) {
                    // Case: Whole number part exceeds 15 digits or leading zeros in decimal part exceed 13
                    resultString = String.format(UNDERFLOW_SCIENTIFIC_FORMAT, result);
                    String[] parts = resultString.split("e");
                    resultString = trimTrailingZeros(parts[0]);
                    exponentString = "E" + formatExponent(Integer.parseInt(parts[1]));
                } else {
                    // Standard display: Round to fit within 15 characters
                    if (result == (int) result) {
                        // Display as integer if result is whole
                        resultString = Integer.toString((int) result);
                    } else {
                        resultString = trimTrailingZeros(String.format("%.15f", result));
                        resultString = removeTrailingDecimalPoint(resultString);
                    }
                }

                displayField.setText(resultString);
                expField.setText(exponentString);
            }

            operatorField.setText("");
            calculatorLogic.clear();
            isResultDisplayed = true;
            lastWasOperator = false;
        }
    }

    // Helper function to trim trailing zeros without regex
    private String trimTrailingZeros(String value) {
        int i = value.length() - 1;
        while (i > 0 && value.charAt(i) == '0') {
            i--;
        }
        return value.substring(0, i + 1);
    }

    // Helper function to remove trailing decimal point if it exists
    private String removeTrailingDecimalPoint(String value) {
        if (value.endsWith(".")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    // Helper method to format the exponent part
    private String formatExponent(int exponent) {
        // Limits the exponent to three digits plus a sign if necessary
        return String.format("%+d", Math.min(Math.max(exponent, -999), 999));
    }

    public void handleDelete() {
        String currentText = displayField.getText();

        // Do nothing if a result is displayed, or if display contains an operator or single zero
        if (isResultDisplayed || isOperatorPending || "0".equals(currentText) || currentText.isEmpty()) {
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

    public void handleAllClear() {
        // Clear display fields
        displayField.setText("0");
        operatorField.setText("");
        expField.setText("");  // Ensure exponent field is cleared

        // Reset calculator logic and flags
        calculatorLogic.clear();  // Assuming clear method resets all stored operands and operators
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
    }
}
