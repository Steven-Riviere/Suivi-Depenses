package org.example;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

public class ExpenseTrackerManagementGUI {
    private JFrame frame;
    private JTextField nameField, amountField;
    private JComboBox<String> categoryComboBox;
    private ExpenseTracker expenseTracker;
    private CategoryManager categoryManager;
    private DefaultListModel<String> listModel;
    private JList<String> expenseList;
    private JButton updateExpense, cancelExpense, deleteExpense, addButton;
    private int selectedExpenseIndex = -1;
    private String originalExpenseName = null;

    public ExpenseTrackerManagementGUI() {
        expenseTracker = new ExpenseTracker();
        categoryManager = new CategoryManager();

        //création de la fenêtre d'user
        frame = new JFrame("Suivi des dépenses");
        frame.setSize(600,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        //taille du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //permettre d'ajouter une dépense
        nameField = new JTextField(15);
        amountField = new JTextField(15);

        // ComboBox pour les catégories
        Set<String> categories = categoryManager.readCategories();
        categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        categoryComboBox.setEditable(true);
        categoryComboBox.setSelectedItem("");

        //bouton d'ajout
        addButton = new JButton("Ajouter une dépense");

        //bouton pour le graphique
        JButton chartButton = new JButton("Voir le graphique");

        //bouton pour mettre à jour
        updateExpense = new JButton("Mettre à jour");
        updateExpense.setVisible(false);

        //bouton pour annuler la mise à jour en cours
        cancelExpense = new JButton("Annuler");
        cancelExpense.setVisible(false);

        //bouton suppression d'une dépense
        deleteExpense = new JButton("Supprimer la dépense");
        deleteExpense.setVisible(false);
        //Couleur pour le bouton supprimer
        deleteExpense.setBackground(Color.RED);
        deleteExpense.setForeground(Color.WHITE);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nom de la dépense :"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Montant :"), gbc);
        gbc.gridx = 1; formPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Catégorie :"), gbc);
        gbc.gridx = 1; formPanel.add(categoryComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(addButton, gbc);
        gbc.gridx = 1; formPanel.add(chartButton, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(updateExpense, gbc);
        gbc.gridx = 1; formPanel.add(cancelExpense, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(deleteExpense, gbc);

        frame.add(formPanel, BorderLayout.NORTH);

        //affiche les dépenses
        listModel = new DefaultListModel<>();
        expenseList = new JList<>(listModel);
        expenseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseList.setVisibleRowCount(10);
        expenseList.setFixedCellWidth(500);

        JScrollPane scrollPane = new JScrollPane(expenseList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des dépenses"));
        frame.add(scrollPane, BorderLayout.CENTER);

        expenseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedExpenseIndex = expenseList.getSelectedIndex();
                String selectedExpense = expenseList.getSelectedValue();
                if (selectedExpense != null) {
                    String[] parts = selectedExpense.split(",");
                    if (parts.length >= 3) {
                        originalExpenseName = parts[0].trim();
                        nameField.setText(parts[0].trim());
                        amountField.setText(parts[1].trim());
                        categoryComboBox.setSelectedItem(parts[2].trim());

                        updateExpense.setVisible(true);
                        cancelExpense.setVisible(true);
                        deleteExpense.setVisible(true);
                    }
                }
            }
        });

        //action lié au bouton annuler
        cancelExpense.addActionListener(e -> {
            clearFields();
            addButton.setVisible(true);
        });

        //action du bouton graphique
        chartButton.addActionListener(e -> showExpenseChart());

        // Action à réaliser lorsque l'utilisateur clique sur "Ajouter Dépense"
        addButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText();
            double amount;

            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String category = (String) categoryComboBox.getSelectedItem();

            // Ajouter la dépense via le ExpenseTracker
            expenseTracker.addExpense(name, amount, category);

            // Afficher les dépenses mises à jour dans la zone de texte
            refreshExpenseList();
            clearFields();
        });

        //permettre que la mise à jour soit enregistrer
        updateExpense.addActionListener(e -> {
            String newName = nameField.getText();
            double newAmount;
            try {
                newAmount = Double.parseDouble(amountField.getText());
            }  catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String newCategory = (String) categoryComboBox.getSelectedItem();
            if(originalExpenseName !=null) {
                expenseTracker.updateExpense(selectedExpenseIndex, newName, newAmount, newCategory);
            }

            refreshExpenseList();
            clearFields();
            addButton.setVisible(true);
        });

        deleteExpense.addActionListener(e -> {
            if (selectedExpenseIndex >= 0) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Etes-vous sûr de vouloir supprimer cette dépense ?", "Confirmation de la suppression", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);

                if(confirm == JOptionPane.YES_OPTION) {
                    expenseTracker.deleteExpense(selectedExpenseIndex);
                    refreshExpenseList();
                    clearFields();
                    addButton.setVisible(true);
                }
            }
        });
        refreshExpenseList();
        // Afficher la fenêtre
        frame.setVisible(true);
    }

    // après avoir mis à jour, les champs redeviennent vierges
    private void clearFields() {
        nameField.setText("");
        amountField.setText("");
        categoryComboBox.setSelectedItem("");
        originalExpenseName = null;
        selectedExpenseIndex = -1;
        expenseList.clearSelection();
        updateExpense.setVisible(false);
        cancelExpense.setVisible(false);
        deleteExpense.setVisible(false);
    }

    // Lire les dépenses et les afficher dans la zone prévue
    private void refreshExpenseList() {
        List<String> expenses = expenseTracker.readExpenses();
        listModel.clear();
        for (String expense : expenses) {
            listModel.addElement(expense);
        }
    }

    //permet d'avoir un graphique lié aux dépenses
    private void showExpenseChart() {
        List<String> expenses = expenseTracker.readExpenses();
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();

        for (String expense : expenses) {
            String[] parts = expense.split(",");
            if (parts.length >= 3) {
                String category = parts[2].trim();
                double amount;
                try {
                    amount = Double.parseDouble(parts[1].replace("€", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (dataset.getKeys().contains(category)) {
                    Number existing = dataset.getValue(category);
                    dataset.setValue(category, existing.doubleValue() + amount);
                } else {
                    dataset.setValue(category, amount);
                }
            }
        }

        //création du graphique
        org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart(
                "répartition des dépenses par catégorie",
                dataset,
                true,
                true,
                false
        );

        // affichage dans une nouvelle fenêtre
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Graphique des dépenses");
        chartFrame.setContentPane(chartPanel);
        chartFrame.setSize(600, 400);
        chartFrame.setVisible(true);
    }

    public static void main(String[] args) {
        //lancer l'appli avec le superbe graphique
        new ExpenseTrackerManagementGUI();
    }
}
