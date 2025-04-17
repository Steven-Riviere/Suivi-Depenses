package org.example;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

    ExpenseTracker tracker = new ExpenseTracker();

    //on ajoute des dépenses pour tester
        tracker.addExpense("ordinateur", 1400, "boulot");
        tracker.addExpense("courses", 170, "alimentation");

        // les lire
        System.out.println("dépenses existantes :");
        List<String> expenses = tracker.readExpenses();
        for (String expense : expenses) {
            System.out.println(expense);
        }
    }
}