package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExpenseTracker {
    private static final  String FILE_PATH ="src/main/resources/depenses.txt";
    private CategoryManager categoryManager;

    public ExpenseTracker() {
        this.categoryManager = new CategoryManager();
    }

    //pour ajouter une dépense
    public void addExpense(String name, double amount, String category) {
        // on va vérifier si la catégorie existe, sinon on l'ajoute
        Set<String> categories = categoryManager.readCategories();
        if (!categories.contains(category)) {
            categoryManager.addCategory(category); // Ajouter la nouvelle catégorie
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(name + "," + amount + "," + category);
            writer.newLine();
            System.out.println("✅ Dépense ajoutée : " + name + " - " + amount + "€ (" + category + ")");
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ajout de la dépense.");
        }
    }

    //lire les dépenses
    public List<String> readExpenses() {
        List<String> expenses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                expenses.add(line);
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la lecture des dépenses.");
        }
        return expenses;
    }

    //update une dépense
    public void updateExpense(int index, String newName, double newAmount, String newCategory) {
        List<String> expenses = readExpenses();
        if (index < 0 || index >= expenses.size()) {
            System.out.println("❌ Index invalide.");
            return;
        }

        expenses.set(index, newName + "," + newAmount + "," + newCategory);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String expense : expenses) {
                writer.write(expense);
                writer.newLine();
            }
            System.out.println("✅ Dépense mise à jour.");
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la mise à jour.");
        }
    }

    //delete une dépense
    public void deleteExpense(int index) {
        List<String> expenses = readExpenses();

        if (index < 0 || index >= expenses.size()) {
            System.out.println("❌ Index invalide.");
            return;
        }

        expenses.remove(index);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String expense : expenses) {
                writer.write(expense);
                writer.newLine();
            }
            System.out.println("✅ Dépense supprimée.");
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la suppression.");
        }
    }

}