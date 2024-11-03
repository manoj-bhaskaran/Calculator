package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

/**
 * Controller for calculator operations that interacts with CalculatorLogic and
 * manages display updates. Supports handling various operations, displaying
 * results, and toggling number signs.
 */
public class CalculatorController {

    private static final String SCIENTIFIC_FORMAT = "%.13e";  // Constant for scientific notation format
    private static final int DISPLAY_MAX_LENGTH = 15; // Max display length for mantissa

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private final JTextField expField;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;
    private boolean lastWasOperator = false;
    private boolean isExponentMode = false;  // Flag for exponent entry mode

    /**
     * Initializes display fields and calculator logic.
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

    /**
     * Resets display fields and flags to their default state.
     */
    private void resetDisplay() {
        displayField.setText("0");
        operatorField.setText("");
        expField.setText("");
        resetFlags();
    }

    /**
     * Appends text to the display, managing constraints like max length,
     * decimal points, and exponent mode.
     *
     * @param text the text to append to the display
     */
    public void appendToDisplay(String text) {
        if ("OvFlow".equals(displayField.getText()) || isResultDisplayed) {
            resetDisplay();
        }

        if (displayField.getText().length() >= DISPLAY_MAX_LENGTH && !isOperatorPending) {
            return;
        }

        if (isExponentMode) {
            handleExponentInput(text);
        } else if (".".equals(text)) {
            handleDecimalInput();
        } else {
            handleGeneralInput(text);
        }
    }

    private void handleDecimalInput() {
        if (isResultDisplayed || isOperatorPending) {
            displayField.setText("0.");
            resetFlags();
        } else if (!displayField.getText().contains(".")) {
            displayField.setText(displayField.getText() + ".");
        }
    }

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

    private void handleExponentInput(String text) {
        if ("E+0".equals(expField.getText())) {
            expField.setText(text.equals("0") ? "E+0" : "E+" + text);
        } else if (expField.getText().length() < 5) {
            expField.setText(expField.getText() + text);
        }
    }

    private void resetFlags() {
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
        isExponentMode = false;
    }

    public void handleExp() {
        if (!isExponentMode && !isResultDisplayed && !isOperatorPending) {
            expField.setText("E+0");
            isExponentMode = true;
        }
    }

    public void handleOperation(String operation) {
        if (lastWasOperator) {
            calculatorLogic.replaceLastOperator(operation);
        } else if (!displayField.getText().isEmpty()) {
            double operand = parseOperandFromFields();
            calculatorLogic.pushOperand(operand);
            calculatorLogic.pushOperator(operation);
            operatorField.setText(SymbolFormatter.getDisplaySymbol(operation));
            resetFlags();
        }
    }

    private double parseOperandFromFields() {
        if (!expField.getText().isEmpty()) {
            String combinedValue = displayField.getText() + "E" + expField.getText().substring(1);
            return Double.parseDouble(combinedValue);
        } else {
            return Double.parseDouble(displayField.getText());
        }
    }

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
                resultString = formatForStandardDisplay(result);

                if (needsScientificNotation(resultString)) {
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
        }
    }

    private boolean needsScientificNotation(String resultString) {
        String[] integerAndDecimal = resultString.split("\\.");
        String integerPart = integerAndDecimal[0];
        return (integerPart.length() > DISPLAY_MAX_LENGTH || (integerPart.equals("0") && integerAndDecimal.length > 1 && integerAndDecimal[1].startsWith("0000000000000")));
    }

    private String formatToScientific(double value) {
        return String.format(SCIENTIFIC_FORMAT, value);
    }

    private void displayResultWithExponent(String resultString) {
        String[] parts = resultString.split("e");
        displayField.setText(trimTrailingZeros(parts[0]).replaceAll("\\.$", ""));
        expField.setText("E" + formatExponent(Integer.parseInt(parts[1])));
    }

    private String trimTrailingZeros(String value) {
        int eIndex = value.indexOf('E');
        if (eIndex == -1) {
            eIndex = value.indexOf('e');
        }

        String mantissa = eIndex >= 0 ? value.substring(0, eIndex) : value;
        String exponent = eIndex >= 0 ? value.substring(eIndex) : "";

        int i = mantissa.length() - 1;
        while (i > 0 && mantissa.charAt(i) == '0') {
            i--;
        }
        mantissa = mantissa.substring(0, i + 1);
        return mantissa.endsWith(".") ? mantissa.substring(0, mantissa.length() - 1) + exponent : mantissa + exponent;
    }

    private String formatExponent(int exponent) {
        return String.format("%+d", Math.min(Math.max(exponent, -999), 999));
    }

    public void handleDelete() {
        if (isExponentMode) {
            String currentText = expField.getText();
            if ("E+0".equals(currentText)) {
                return;
            }
            expField.setText((currentText.length() > 3) ? currentText.substring(0, currentText.length() - 1) : "E+0");
        } else {
            String currentText = displayField.getText();
            if (isResultDisplayed || isOperatorPending || "0".equals(currentText)) {
                return;
            }
            displayField.setText((currentText.length() > 1) ? currentText.substring(0, currentText.length() - 1) : "0");
        }
    }

    public void handleSignChange() {
        if (isExponentMode) {
            int exponent = Integer.parseInt(expField.getText().substring(1));
            expField.setText("E" + (exponent == 0 ? "+0" : String.format("%+d", -exponent)));
        } else {
            double currentValue = Double.parseDouble(displayField.getText());
            if (currentValue != 0.0) {
                displayField.setText(formatForStandardDisplay(-currentValue));
            }
        }
    }

    private String formatForStandardDisplay(double value) {
        return (value == (int) value) ? Integer.toString((int) value) : trimTrailingZeros(String.format("%.15f", value));
    }

    public void handleAllClear() {
        resetDisplay();
        calculatorLogic.clear();
    }
}
