package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

/**
 * Controller for calculator operations that interacts with the CalculatorLogic
 * and manages display updates.
 */
public class CalculatorController {

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private final JTextField expField;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;
    private boolean lastWasOperator = false;

    /**
     * Constructor for CalculatorController. Initializes display fields and
     * calculator logic.
     *
     * @param calculatorLogic the core logic for calculations
     * @param displayField the field displaying numbers
     * @param operatorField the field displaying operators
     * @param expField the field displaying exponents
     */
    public CalculatorController(CalculatorLogic calculatorLogic, JTextField displayField, JTextField operatorField, JTextField expField) {
        this.calculatorLogic = calculatorLogic;
        this.displayField = displayField;
        this.operatorField = operatorField;
        this.expField = expField;
        resetDisplay(); // Refactored: separate initial display reset for better readability
    }

    // Initializes the display fields to starting state
    private void resetDisplay() {
        displayField.setText("0");
        operatorField.setText("");
        expField.setText("");
    }

    /**
     * Appends text to the display, handling constraints like maximum length and
     * decimal point.
     *
     * @param text the text to append to the display
     */
    public void appendToDisplay(String text) {
        // Reset display if overflow or result already shown
        if ("OvFlow".equals(displayField.getText()) || isResultDisplayed) {
            resetDisplay();
        }

        // Enforce a maximum length of 15 characters for input
        if (displayField.getText().length() >= 15 && !isOperatorPending) {
            return;
        }

        // Handle decimal input separately to avoid duplicate decimals
        if (".".equals(text)) {
            handleDecimalInput();
            return;
        }

        // Handle general input for digits
        handleGeneralInput(text);
    }

    // Refactored decimal handling for clarity
    private void handleDecimalInput() {
        if (isResultDisplayed || isOperatorPending) {
            displayField.setText("0.");  // Start new decimal input
            resetFlags();
        } else if (!displayField.getText().contains(".")) {
            displayField.setText(displayField.getText() + ".");
        }
    }

    // Refactored general input handling for readability
    private void handleGeneralInput(String text) {
        if (isResultDisplayed || isOperatorPending) {
            displayField.setText(text);
            resetFlags();
        } else if ("0".equals(displayField.getText())) {
            displayField.setText(text.equals("0") ? "0" : text);
        } else {
            displayField.setText(displayField.getText() + text);
        }
    }

    // Resets flags to default state after processing input
    private void resetFlags() {
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
        expField.setText("");
    }

    /**
     * Processes an operation input.
     *
     * @param operation the operation to handle, such as +, -, *, /
     */
    public void handleOperation(String operation) {
        if (lastWasOperator) {
            calculatorLogic.replaceLastOperator(operation);
        } else if (!displayField.getText().isEmpty()) {
            double operand = parseOperandFromFields();
            calculatorLogic.pushOperand(operand);
            calculatorLogic.pushOperator(operation);
            operatorField.setText(SymbolFormatter.getDisplaySymbol(operation));
            isOperatorPending = true;
            lastWasOperator = true;
        }
    }

    // Parses operand from display fields, handling exponents
    private double parseOperandFromFields() {
        if (!expField.getText().isEmpty()) {
            String combinedValue = displayField.getText() + "E" + expField.getText().substring(1);
            return Double.parseDouble(combinedValue);
        } else {
            return Double.parseDouble(displayField.getText());
        }
    }

    /**
     * Calculates and displays the result.
     */
    public void calculateResult() {
        if (!displayField.getText().isEmpty()) {
            calculatorLogic.pushOperand(parseOperandFromFields());
            double result = calculatorLogic.getResult();

            // Display result based on thresholds for underflow and overflow
            handleResultDisplay(result);
        }
    }

    // Displays result based on underflow and overflow thresholds
    private void handleResultDisplay(double result) {
        final double underflowThreshold = 1e-13;
        final double overflowThreshold = 1e15;

        if (Math.abs(result) < underflowThreshold && result != 0) {
            formatScientificDisplay(result);
        } else if (Math.abs(result) >= overflowThreshold) {
            formatScientificDisplay(result);
        } else {
            formatStandardDisplay(result);
        }

        operatorField.setText("");
        calculatorLogic.clear();
        isResultDisplayed = true;
        lastWasOperator = false;
    }

    // Handles scientific formatting for display with exponent
    private void formatScientificDisplay(double result) {
        String[] parts = String.format("%.13e", result).split("e");
        displayField.setText(parts[0].replaceAll("0*$", "").replaceAll("\\.$", ""));
        expField.setText("E" + formatExponent(Integer.parseInt(parts[1])));
    }

    // Formats standard numbers within 15 characters, defaulting to exponential if too long
    private void formatStandardDisplay(double result) {
        String resultString = (result == (int) result) ? Integer.toString((int) result)
                : String.format("%.15f", result).replaceAll("0*$", "").replaceAll("\\.$", "");

        if (resultString.length() > 15) {
            formatScientificDisplay(result);
        } else {
            displayField.setText(resultString);
            expField.setText("");
        }
    }

    /**
     * Formats an exponent to three characters with a sign.
     *
     * @param exponent the exponent to format
     * @return formatted exponent string
     */
    private String formatExponent(int exponent) {
        return String.format("%+d", Math.min(Math.max(exponent, -999), 999));
    }

    /**
     * Handles backspace/delete functionality, clearing the last character.
     */
    public void handleDelete() {
        String currentText = displayField.getText();
        if (isResultDisplayed || isOperatorPending || "0".equals(currentText) || currentText.isEmpty()) {
            return;
        }

        displayField.setText((currentText.length() > 1) ? currentText.substring(0, currentText.length() - 1) : "0");
    }

    /**
     * Clears all calculator fields and resets the state.
     */
    public void handleAllClear() {
        resetDisplay();
        calculatorLogic.clear();
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
    }
}
