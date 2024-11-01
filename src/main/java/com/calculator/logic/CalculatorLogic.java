package com.calculator.logic;

import java.util.Stack;

public class CalculatorLogic {

    private final Stack<Double> operandStack = new Stack<>();   // Stack for storing operands
    private final Stack<String> operatorStack = new Stack<>();  // Stack for storing operators

    /**
     * Pushes a number onto the operand stack.
     * 
     * @param operand the operand to push
     */
    public void pushOperand(double operand) {
        operandStack.push(operand);
    }

    /**
     * Pushes an operator onto the operator stack. If an operator with 
     * equal or higher precedence is already on the stack, evaluates the 
     * stacks before pushing the new operator.
     * 
     * @param operator the operator to push
     */
    public void pushOperator(String operator) {
        if (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
            evaluateStacks();
        }
        operatorStack.push(operator);
    }

    /**
     * Calculates and returns the result of the expression by evaluating
     * remaining operators in the stack.
     * 
     * @return the result of the evaluated expression, or 0 if no operands are present
     */
    public double getResult() {
        while (!operatorStack.isEmpty()) {
            evaluateStacks();
        }
        return operandStack.isEmpty() ? 0 : operandStack.pop();
    }

    /**
     * Evaluates the top two operands with the top operator, pushing the result
     * back onto the operand stack. Safely handles division by zero.
     */
    private void evaluateStacks() {
        if (operandStack.size() < 2 || operatorStack.isEmpty()) return;

        double operand2 = operandStack.pop();
        double operand1 = operandStack.pop();
        String operator = operatorStack.pop();

        double result = switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "*" -> operand1 * operand2;
            case "/" -> (operand2 != 0) ? operand1 / operand2 : Double.NaN; // Avoid division by zero
            default -> 0;
        };
        operandStack.push(result);
    }

    /**
     * Returns the precedence level of an operator, where higher values
     * indicate higher precedence.
     * 
     * @param operator the operator to evaluate
     * @return precedence level of the operator
     */
    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
    }

    /**
     * Resets the calculator by clearing both operand and operator stacks.
     */
    public void clear() {
        operandStack.clear();
        operatorStack.clear();
    }
}
