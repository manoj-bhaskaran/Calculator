/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.calculator.UI;

import com.calculator.logic.CalculatorLogic;
import com.calculator.logic.CalculatorController;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author manoj
 */
public class CalculatorUI extends javax.swing.JFrame implements KeyListener {

    private final CalculatorController controller;

    /**
     * Creates new form CalculatorUI
     */
    public CalculatorUI() {
        initComponents();
        CalculatorLogic calculatorLogic = new CalculatorLogic();
        controller = new CalculatorController(calculatorLogic, displayField, operatorField, expField);
        attachListeners();  // Attach button listeners for UI buttons
        initializeKeyListener();  // Set up KeyListener separately
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_0, KeyEvent.VK_NUMPAD0 ->
                zeroButton.doClick();
            case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1 ->
                oneButton.doClick();
            case KeyEvent.VK_2, KeyEvent.VK_NUMPAD2 ->
                twoButton.doClick();
            case KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 ->
                threeButton.doClick();
            case KeyEvent.VK_4, KeyEvent.VK_NUMPAD4 ->
                fourButton.doClick();
            case KeyEvent.VK_5, KeyEvent.VK_NUMPAD5 ->
                fiveButton.doClick();
            case KeyEvent.VK_6, KeyEvent.VK_NUMPAD6 ->
                sixButton.doClick();
            case KeyEvent.VK_7, KeyEvent.VK_NUMPAD7 ->
                sevenButton.doClick();

            case KeyEvent.VK_8 -> {
                if (e.isShiftDown()) {
                    multiplyButton.doClick(); // Handles '*' on the main keyboard (Shift + 8)
                } else {
                    eightButton.doClick();    // Regular '8' when Shift is not pressed
                }
            }
            case KeyEvent.VK_NUMPAD8 ->
                eightButton.doClick();
            case KeyEvent.VK_9, KeyEvent.VK_NUMPAD9 ->
                nineButton.doClick();
            case KeyEvent.VK_PLUS, KeyEvent.VK_ADD ->
                plusButton.doClick();
            case KeyEvent.VK_MINUS, KeyEvent.VK_SUBTRACT ->
                minusButton.doClick();
            case KeyEvent.VK_MULTIPLY ->
                multiplyButton.doClick();
            case KeyEvent.VK_DIVIDE, KeyEvent.VK_SLASH -> {
                if (e.isShiftDown()) {
                    equalsButton.doClick();  // Shift + '/' triggers '?', acting as equals
                } else {
                    divideButton.doClick();  // '/' triggers divide
                }
            }

            case KeyEvent.VK_ENTER, KeyEvent.VK_EQUALS ->
                equalsButton.doClick();
            case KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE ->
                delButton.doClick(); // Both Backspace and Delete trigger DEL
            case KeyEvent.VK_ESCAPE ->
                allClearButton.doClick();
            case KeyEvent.VK_PERIOD ->
                decimalButton.doClick();

            // Other keys are ignored
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Required by KeyListener but not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Required by KeyListener but not used
    }

    private void initializeKeyListener() {
        this.addKeyListener(this);  // Now safely add KeyListener after constructor finishes
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private boolean isFlashing = false;

    private void flashButton(JButton button, Color flashColor, int duration) {
        if (isFlashing) return; // Skip if flash is already in progress
        isFlashing = true;

        Color originalColor = button.getBackground();
        button.setBackground(flashColor);

        new javax.swing.Timer(duration, e -> {
            button.setBackground(originalColor);
            isFlashing = false; // Reset flash flag when done
            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }

    private void attachListeners() {
        Color flashColor = new Color(57, 255, 20);  // Custom flash color
        int flashDuration = 100;                    // Flash duration in milliseconds

        ActionListener numberButtonListener = e -> {
            JButton sourceButton = (JButton) e.getSource();
            flashButton(sourceButton, flashColor, flashDuration);
            controller.appendToDisplay(sourceButton.getText());
            this.requestFocusInWindow(); // Refocus on main window after click
        };

        // Array of all number buttons
        JButton[] numberButtons = {zeroButton, oneButton, twoButton, threeButton, fourButton,
            fiveButton, sixButton, sevenButton, eightButton, nineButton,
            decimalButton};

        // Add action listener to each number button
        for (JButton button : numberButtons) {
            button.addActionListener(numberButtonListener);
        }

        // Specific action listeners for operations
        plusButton.addActionListener(e -> {
            flashButton(plusButton, flashColor, flashDuration);
            controller.handleOperation("+");
            this.requestFocusInWindow(); // Refocus after operation
        });
        minusButton.addActionListener(e -> {
            flashButton(minusButton, flashColor, flashDuration);
            controller.handleOperation("-");
            this.requestFocusInWindow();
        });
        multiplyButton.addActionListener(e -> {
            flashButton(multiplyButton, flashColor, flashDuration);
            controller.handleOperation("*");
            this.requestFocusInWindow();
        });
        divideButton.addActionListener(e -> {
            flashButton(divideButton, flashColor, flashDuration);
            controller.handleOperation("/");
            this.requestFocusInWindow();
        });

        equalsButton.addActionListener(e -> {
            flashButton(equalsButton, flashColor, flashDuration);
            controller.calculateResult();
            this.requestFocusInWindow();
        });
        delButton.addActionListener(e -> {
            flashButton(delButton, flashColor, flashDuration); // Corrected button to `delButton`
            controller.handleDelete();
            this.requestFocusInWindow();
        });
        allClearButton.addActionListener(e -> {
            flashButton(allClearButton, flashColor, flashDuration); // Corrected button to `allClearButton`
            controller.handleAllClear();
            this.requestFocusInWindow();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fieldPanel = new javax.swing.JPanel();
        displayField = new javax.swing.JTextField();
        operatorField = new javax.swing.JTextField();
        expField = new javax.swing.JTextField();
        buttonPanel = new javax.swing.JPanel();
        sevenButton = new javax.swing.JButton();
        eightButton = new javax.swing.JButton();
        nineButton = new javax.swing.JButton();
        fourButton = new javax.swing.JButton();
        fiveButton = new javax.swing.JButton();
        sixButton = new javax.swing.JButton();
        oneButton = new javax.swing.JButton();
        twoButton = new javax.swing.JButton();
        threeButton = new javax.swing.JButton();
        zeroButton = new javax.swing.JButton();
        plusButton = new javax.swing.JButton();
        equalsButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        multiplyButton = new javax.swing.JButton();
        divideButton = new javax.swing.JButton();
        decimalButton = new javax.swing.JButton();
        delButton = new javax.swing.JButton();
        allClearButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fieldPanel.setLayout(new java.awt.BorderLayout());

        displayField.setEditable(false);
        displayField.setBackground(new java.awt.Color(204, 255, 204));
        displayField.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        displayField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        displayField.setToolTipText("");
        displayField.setBorder(null);
        displayField.setMargin(new java.awt.Insets(2, 0, 2, 0));
        displayField.setPreferredSize(new java.awt.Dimension(65, 25));
        displayField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayFieldActionPerformed(evt);
            }
        });
        fieldPanel.add(displayField, java.awt.BorderLayout.CENTER);

        operatorField.setEditable(false);
        operatorField.setBackground(new java.awt.Color(204, 255, 204));
        operatorField.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        operatorField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        operatorField.setBorder(null);
        operatorField.setMargin(new java.awt.Insets(2, 0, 2, 0));
        operatorField.setPreferredSize(new java.awt.Dimension(25, 25));
        operatorField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operatorFieldActionPerformed(evt);
            }
        });
        fieldPanel.add(operatorField, java.awt.BorderLayout.WEST);

        expField.setEditable(false);
        expField.setBackground(new java.awt.Color(204, 255, 204));
        expField.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        expField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        expField.setBorder(null);
        expField.setMargin(new java.awt.Insets(2, 0, 2, 0));
        expField.setPreferredSize(new java.awt.Dimension(50, 25));
        expField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expFieldActionPerformed(evt);
            }
        });
        fieldPanel.add(expField, java.awt.BorderLayout.EAST);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        sevenButton.setBackground(new java.awt.Color(0, 0, 0));
        sevenButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        sevenButton.setForeground(new java.awt.Color(255, 255, 255));
        sevenButton.setText("7");
        sevenButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sevenButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(sevenButton, gridBagConstraints);

        eightButton.setBackground(new java.awt.Color(0, 0, 0));
        eightButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        eightButton.setForeground(new java.awt.Color(255, 255, 255));
        eightButton.setText("8");
        eightButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        eightButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(eightButton, gridBagConstraints);

        nineButton.setBackground(new java.awt.Color(0, 0, 0));
        nineButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        nineButton.setForeground(new java.awt.Color(255, 255, 255));
        nineButton.setText("9");
        nineButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        nineButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(nineButton, gridBagConstraints);

        fourButton.setBackground(new java.awt.Color(0, 0, 0));
        fourButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        fourButton.setForeground(new java.awt.Color(255, 255, 255));
        fourButton.setText("4");
        fourButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fourButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(fourButton, gridBagConstraints);

        fiveButton.setBackground(new java.awt.Color(0, 0, 0));
        fiveButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        fiveButton.setForeground(new java.awt.Color(255, 255, 255));
        fiveButton.setText("5");
        fiveButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fiveButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(fiveButton, gridBagConstraints);

        sixButton.setBackground(new java.awt.Color(0, 0, 0));
        sixButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        sixButton.setForeground(new java.awt.Color(255, 255, 255));
        sixButton.setText("6");
        sixButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sixButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(sixButton, gridBagConstraints);

        oneButton.setBackground(new java.awt.Color(0, 0, 0));
        oneButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        oneButton.setForeground(new java.awt.Color(255, 255, 255));
        oneButton.setText("1");
        oneButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        oneButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(oneButton, gridBagConstraints);

        twoButton.setBackground(new java.awt.Color(0, 0, 0));
        twoButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        twoButton.setForeground(new java.awt.Color(255, 255, 255));
        twoButton.setText("2");
        twoButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        twoButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(twoButton, gridBagConstraints);

        threeButton.setBackground(new java.awt.Color(0, 0, 0));
        threeButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        threeButton.setForeground(new java.awt.Color(255, 255, 255));
        threeButton.setText("3");
        threeButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        threeButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(threeButton, gridBagConstraints);

        zeroButton.setBackground(new java.awt.Color(0, 0, 0));
        zeroButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        zeroButton.setForeground(new java.awt.Color(255, 255, 255));
        zeroButton.setText("0");
        zeroButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        zeroButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(zeroButton, gridBagConstraints);

        plusButton.setBackground(new java.awt.Color(0, 0, 0));
        plusButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        plusButton.setForeground(new java.awt.Color(255, 255, 255));
        plusButton.setText("+");
        plusButton.setToolTipText("");
        plusButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        plusButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(plusButton, gridBagConstraints);

        equalsButton.setBackground(new java.awt.Color(0, 0, 0));
        equalsButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        equalsButton.setForeground(new java.awt.Color(255, 255, 255));
        equalsButton.setText("=");
        equalsButton.setToolTipText("");
        equalsButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        equalsButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(equalsButton, gridBagConstraints);

        minusButton.setBackground(new java.awt.Color(0, 0, 0));
        minusButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        minusButton.setForeground(new java.awt.Color(255, 255, 255));
        minusButton.setText("-");
        minusButton.setToolTipText("");
        minusButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        minusButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(minusButton, gridBagConstraints);

        multiplyButton.setBackground(new java.awt.Color(0, 0, 0));
        multiplyButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        multiplyButton.setForeground(new java.awt.Color(255, 255, 255));
        multiplyButton.setText("\u00D7");
        multiplyButton.setToolTipText("");
        multiplyButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        multiplyButton.setLabel("\u00D7");
        multiplyButton.setPreferredSize(new java.awt.Dimension(35, 35));
        multiplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(multiplyButton, gridBagConstraints);

        divideButton.setBackground(new java.awt.Color(0, 0, 0));
        divideButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        divideButton.setForeground(new java.awt.Color(255, 255, 255));
        divideButton.setText("\u00F7");
        divideButton.setToolTipText("");
        divideButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        divideButton.setLabel("\u00F7");
        divideButton.setPreferredSize(new java.awt.Dimension(35, 35));
        divideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divideButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(divideButton, gridBagConstraints);
        divideButton.getAccessibleContext().setAccessibleName("\u00F7");

        decimalButton.setBackground(new java.awt.Color(0, 0, 0));
        decimalButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        decimalButton.setForeground(new java.awt.Color(255, 255, 255));
        decimalButton.setText(".");
        decimalButton.setToolTipText("");
        decimalButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        decimalButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(decimalButton, gridBagConstraints);

        delButton.setBackground(new java.awt.Color(255, 0, 0));
        delButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        delButton.setForeground(new java.awt.Color(255, 255, 255));
        delButton.setText("DEL");
        delButton.setToolTipText("");
        delButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        delButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(delButton, gridBagConstraints);

        allClearButton.setBackground(new java.awt.Color(255, 0, 0));
        allClearButton.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        allClearButton.setForeground(new java.awt.Color(255, 255, 255));
        allClearButton.setText("AC");
        allClearButton.setToolTipText("");
        allClearButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        allClearButton.setPreferredSize(new java.awt.Dimension(35, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        buttonPanel.add(allClearButton, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 46, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 47, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(fieldPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(305, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void operatorFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operatorFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_operatorFieldActionPerformed

    private void displayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_displayFieldActionPerformed

    private void multiplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_multiplyButtonActionPerformed

    private void divideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divideButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_divideButtonActionPerformed

    private void expFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_expFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalculatorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new CalculatorUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allClearButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton decimalButton;
    private javax.swing.JButton delButton;
    private javax.swing.JTextField displayField;
    private javax.swing.JButton divideButton;
    private javax.swing.JButton eightButton;
    private javax.swing.JButton equalsButton;
    private javax.swing.JTextField expField;
    private javax.swing.JPanel fieldPanel;
    private javax.swing.JButton fiveButton;
    private javax.swing.JButton fourButton;
    private javax.swing.JButton minusButton;
    private javax.swing.JButton multiplyButton;
    private javax.swing.JButton nineButton;
    private javax.swing.JButton oneButton;
    private javax.swing.JTextField operatorField;
    private javax.swing.JButton plusButton;
    private javax.swing.JButton sevenButton;
    private javax.swing.JButton sixButton;
    private javax.swing.JButton threeButton;
    private javax.swing.JButton twoButton;
    private javax.swing.JButton zeroButton;
    // End of variables declaration//GEN-END:variables

}
