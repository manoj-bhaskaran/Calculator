package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

/**
 * Controller for calculator operations that interacts with the CalculatorLogic
 * and manages display updates.
 */
public class CalculatorController {

    private static final String UNDERFLOW_SCIENTIFIC_FORMAT = "%.13e"; // Constant for scientific notation

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
        resetDisplay();
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
        if ("OvFlow".equals(displayField.getText()) || isResultDisplayed) {
            resetDisplay();
        }

        if (displayField.getText().length() >= 15 && !isOperatorPending) {
            return;
        }

        if (".".equals(text)) {
            handleDecimalInput();
        } else {
            handleGeneralInput(text);
        }
    }

    // Handles decimal input
    private void handleDecimalInput() {
        if (isResultDisplayed || isOperatorPending) {
            displayField.setText("0.");
            resetFlags();
        } else if (!displayField.getText().contains(".")) {
            displayField.setText(displayField.getText() + ".");
        }
    }

    // Handles general digit input
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

    // Resets display-related flags
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

            final double underflowThreshold = 1e-13;
            String resultString;

            if (Math.abs(result) < underflowThreshold && result != 0) {
                resultString = formatToScientific(result);
                displayResultWithExponent(resultString);
            } else {
                resultString = trimTrailingZeros(String.format("%.15f", result));
                resultString = removeTrailingDecimalPoint(resultString);

                if (shouldUseScientificNotation(resultString)) {
                    resultString = formatToScientific(result);
                    displayResultWithExponent(resultString);
                } else {
                    displayField.setText(resultString);
                    expField.setText("");
                }
            }

            operatorField.setText("");
            calculatorLogic.clear();
            isResultDisplayed = true;
            lastWasOperator = false;
        }
    }

    // Determines if scientific notation is necessary
    private boolean shouldUseScientificNotation(String resultString) {
        String[] integerAndDecimal = resultString.split("\\.");
        String integerPart = integerAndDecimal[0];
        return (integerPart.length() > 15 || (integerPart.equals("0") && integerAndDecimal.length > 1 && integerAndDecimal[1].startsWith("0000000000000")));
    }

    // Formats to scientific notation with constants
    private String formatToScientific(double value) {
        return String.format(UNDERFLOW_SCIENTIFIC_FORMAT, value);
    }

    // Displays formatted result with exponent field
    private void displayResultWithExponent(String resultString) {
        String[] parts = resultString.split("e");
        displayField.setText(trimTrailingZeros(parts[0]));
        expField.setText("E" + formatExponent(Integer.parseInt(parts[1])));
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
        return value.endsWith(".") ? value.substring(0, value.length() - 1) : value;
    }

    // Helper method to format the exponent part
    private String formatExponent(int exponent) {
        return String.format("%+d", Math.min(Math.max(exponent, -999), 999));
    }

    /**
     * Handles backspace/delete functionality.
     */
    public void handleDelete() {
        String currentText = displayField.getText();
        if (isResultDisplayed || isOperatorPending || "0".equals(currentText) || currentText.isEmpty()) {
            return;
        }
        displayField.setText((currentText.length() > 1) ? currentText.substring(0, currentText.length() - 1) : "0");
    }

    /**
     * Toggles the sign of the current number.
     */
    public void handleSignChange() {
        double currentValue = Double.parseDouble(displayField.getText());
        if (currentValue != 0.0) {
            currentValue = -currentValue;
            displayField.setText(formatValue(currentValue));
        }
    }

    // Helper method to format the value, reusing existing methods
    private String formatValue(double value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }
        String valueStr = String.format("%.15f", value);
        return removeTrailingDecimalPoint(trimTrailingZeros(valueStr));
    }

    /**
     * Clears all fields and resets the state.
     */
    public void handleAllClear() {
        resetDisplay();
        calculatorLogic.clear();
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
    }
}
