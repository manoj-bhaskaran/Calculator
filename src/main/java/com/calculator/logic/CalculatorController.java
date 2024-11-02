package com.calculator.logic;

import com.calculator.UI.SymbolFormatter;
import javax.swing.JTextField;

/**
 * Controller for calculator operations that interacts with the CalculatorLogic
 * and manages display updates.
 */
public class CalculatorController {

    private static final String SCIENTIFIC_FORMAT = "%.13e"; // Constant for scientific notation

    private final CalculatorLogic calculatorLogic;
    private final JTextField displayField;
    private final JTextField operatorField;
    private final JTextField expField;
    private boolean isResultDisplayed = false;
    private boolean isOperatorPending = false;
    private boolean lastWasOperator = false;
    private boolean isExponentMode = false;  // New flag to track if EXP mode is active

    public CalculatorController(CalculatorLogic calculatorLogic, JTextField displayField, JTextField operatorField, JTextField expField) {
        this.calculatorLogic = calculatorLogic;
        this.displayField = displayField;
        this.operatorField = operatorField;
        this.expField = expField;
        resetDisplay();
    }

    private void resetDisplay() {
        displayField.setText("0");
        operatorField.setText("");
        expField.setText("");
    }

    public void appendToDisplay(String text) {
        if ("OvFlow".equals(displayField.getText()) || isResultDisplayed) {
            resetDisplay();
        }

        if (displayField.getText().length() >= 15 && !isOperatorPending) {
            return;
        }

        if (isExponentMode) {
            // Ignore decimal points in exponent or if length of exponent exceeds 3
            if (".".equals(text) || expField.getText().length() >= 5) {
                return;
            } else {
                handleExponentInput(text);
                return;
            }
        } 
        
        if (".".equals(text)) {
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
        } else {
            expField.setText(expField.getText() + text);
        }
    }

    private void resetFlags() {
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
        expField.setText("");
        isExponentMode = false;
    }
    
    public void handleExp() {
        if (!isExponentMode && !isResultDisplayed && !isOperatorPending) {
            expField.setText("E+0"); // Add 'E' to enter exponent mode
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
            isOperatorPending = true;
            lastWasOperator = true;
            isExponentMode = false;
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
            isExponentMode = false;
        }
    }

    private boolean shouldUseScientificNotation(String resultString) {
        String[] integerAndDecimal = resultString.split("\\.");
        String integerPart = integerAndDecimal[0];
        return (integerPart.length() > 15 || (integerPart.equals("0") && integerAndDecimal.length > 1 && integerAndDecimal[1].startsWith("0000000000000")));
    }

    private String formatToScientific(double value) {
        String formatted = String.format(SCIENTIFIC_FORMAT, value);
        return removeTrailingDecimalPoint(trimTrailingZeros(formatted));
    }

    private void displayResultWithExponent(String resultString) {
        String[] parts = resultString.split("e");
        displayField.setText(trimTrailingZeros(parts[0]).replaceAll("\\.$", "")); // Ensure no trailing dot
        expField.setText("E" + formatExponent(Integer.parseInt(parts[1]))); // Correct formatting for exponent
    }

    private String trimTrailingZeros(String value) {
        // Check if value is in exponential form
        int eIndex = value.indexOf('E');
        
        if (eIndex == -1) {
            eIndex = value.indexOf('e');
        }

        if (eIndex == -1) {
            // If not in exponential form, trim as usual
            int i = value.length() - 1;
            while (i > 0 && value.charAt(i) == '0') {
                i--;
            }
            return value.substring(0, i + 1);
        } else {
            // If in exponential form, separate mantissa and exponent
            String mantissa = value.substring(0, eIndex);
            String exponent = value.substring(eIndex);

            // Trim trailing zeros from mantissa
            int i = mantissa.length() - 1;
            while (i > 0 && mantissa.charAt(i) == '0') {
                i--;
            }
            mantissa = mantissa.substring(0, i + 1);

            // Ensure no trailing decimal point in mantissa
            if (mantissa.endsWith(".")) {
                mantissa = mantissa.substring(0, mantissa.length() - 1);
            }

            // Reassemble mantissa and exponent
            return mantissa + exponent;
        }
    }

    private String removeTrailingDecimalPoint(String value) {
        return value.endsWith(".") ? value.substring(0, value.length() - 1) : value;
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
            expField.setText((currentText.length() > 3) ? currentText.substring(0,currentText.length()-1) : "E+0");
        } else {
            String currentText = displayField.getText();
            if (isResultDisplayed || isOperatorPending || "0".equals(currentText) || currentText.isEmpty()) {
                return;
            }
            displayField.setText((currentText.length() > 1) ? currentText.substring(0, currentText.length() - 1) : "0");
        }
    }

    public void handleSignChange() {
        if (isExponentMode) {
            expField.setText("E" + String.format("%+d", -Integer.valueOf(expField.getText().substring(1))));
        } else {
            double currentValue = Double.parseDouble(displayField.getText());
            if (currentValue != 0.0) {
                currentValue = -currentValue;
                displayField.setText(formatForStandardDisplay(currentValue));
            }
        }
    }

    private String formatForStandardDisplay(double value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }
        String valueStr = String.format("%.15f", value);
        return removeTrailingDecimalPoint(trimTrailingZeros(valueStr));
    }

    public void handleAllClear() {
        resetDisplay();
        calculatorLogic.clear();
        isResultDisplayed = false;
        isOperatorPending = false;
        lastWasOperator = false;
        isExponentMode = false;
    }
}
