package com.calculator.logic;

import java.util.Stack;

public class CalculatorLogic {

    // Stack to store operands (numbers)
    private Stack<Double> operandStack = new Stack<>();
    
    // Stack to store operators (+, -, *, /)
    private Stack<String> operatorStack = new Stack<>();

    /**
     * Pushes a given operand (number) onto the operand stack.
     *
     * @param operand the number to be added to the operand stack
     */
    public void pushOperand(double operand) {
        operandStack.push(operand);
    }

    /**
     * Pushes an operator onto the operator stack. If there is an operator
     * already on the stack with equal or higher precedence, it evaluates
     * the stacks before pushing the new operator.
     *
     * @param operator the operator to be pushed onto the operator stack
     */
    public void pushOperator(String operator) {
        // Check if existing operator on stack has equal or higher precedence
        if (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
            evaluateStacks();
        }
        // Push the new operator onto the operator stack
        operatorStack.push(operator);
    }

    /**
     * Calculates the result of the expression by evaluating any remaining
     * operators in the stack. Returns the final result.
     *
     * @return the final result of all operations
     */
    public double getResult() {
        // Continue evaluating while there are operators on the stack
        while (!operatorStack.isEmpty()) {
            evaluateStacks();
        }
        // Return the result or 0 if the operand stack is empty
        return operandStack.isEmpty() ? 0 : operandStack.pop();
    }

    /**
     * Evaluates the top two operands with the top operator in the stacks.
     * Pops the operands and operator, performs the operation, and pushes
     * the result back onto the operand stack.
     */
    private void evaluateStacks() {
        // Ensure there are at least two operands and one operator to evaluate
        if (operandStack.size() < 2 || operatorStack.isEmpty()) {
            return;
        }

        // Pop the top two operands and the top operator
        double operand2 = operandStack.pop();
        double operand1 = operandStack.pop();
        String operator = operatorStack.pop();

        // Perform the operation based on the operator type
        double result = switch (operator) {
            case "+" -> operand1 + operand2;
            case "-" -> operand1 - operand2;
            case "*" -> operand1 * operand2;
            case "/" -> operand2 != 0 ? operand1 / operand2 : Double.NaN; // Handle division by zero
            default -> 0;
        };

        // Push the result back onto the operand stack for further evaluation
        operandStack.push(result);
    }

    /**
     * Determines the precedence of an operator.
     * Higher numbers mean higher precedence.
     *
     * @param operator the operator to check precedence for
     * @return the precedence level as an integer
     */
    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1; // Lowest precedence
            case "*", "/" -> 2; // Higher precedence
            default -> -1; // Undefined operators
        };
    }

    /**
     * Clears both the operand and operator stacks, resetting the calculator state.
     */
    public void clear() {
        operandStack.clear();
        operatorStack.clear();
    }
}
