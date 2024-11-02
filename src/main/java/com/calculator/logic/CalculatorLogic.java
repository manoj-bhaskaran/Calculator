package com.calculator.logic;

import java.util.Stack;

public class CalculatorLogic {

    private final Stack<Double> operandStack = new Stack<>();   // Stack for storing operands
    private final Stack<String> operatorStack = new Stack<>();  // Stack for storing operators

    public void pushOperand(double operand) {
        operandStack.push(operand);
    }

    public void pushOperator(String operator) {
        if (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
            evaluateStacks();
        }
        operatorStack.push(operator);
    }

    public void replaceLastOperator(String operation) {
        if (!operatorStack.isEmpty()) {
            operatorStack.pop(); // Remove the last operator
        }
        operatorStack.push(operation); // Push the new operator
    }

    public double getResult() {
        while (!operatorStack.isEmpty()) {
            evaluateStacks();
        }
        return operandStack.isEmpty() ? 0 : operandStack.pop();
    }

    private void evaluateStacks() {
        if (operandStack.size() < 2 || operatorStack.isEmpty()) {
            return;
        }

        double operand2 = operandStack.pop();
        double operand1 = operandStack.pop();
        String operator = operatorStack.pop();

        double result = switch (operator) {
            case "+" ->
                operand1 + operand2;
            case "-" ->
                operand1 - operand2;
            case "*" ->
                operand1 * operand2;
            case "/" ->
                (operand2 != 0) ? operand1 / operand2 : Double.NaN;
            default ->
                0;
        };
        operandStack.push(result);
    }

    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" ->
                1;
            case "*", "/" ->
                2;
            default ->
                -1;
        };
    }

    public void clear() {
        operandStack.clear();
        operatorStack.clear();
    }
}
