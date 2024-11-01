package com.calculator.UI;

public class SymbolFormatter {
    // Maps operations to their display symbols
    public static String getDisplaySymbol(String operation) {
        return switch (operation) {
            case "*" -> "\u00D7";
            case "/" -> "\u00F7"; // Example: division symbol
            default -> operation; // For other operators, return as-is
        };
    }
}
