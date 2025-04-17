package org.example;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class CategoryManager {
    private static final String CATEGORY_FILE_PATH = "src/main/resources/categories.txt";

    // Lire les catégories depuis le fichier
    public Set<String> readCategories() {
        Set<String> categories = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CATEGORY_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                categories.add(line.trim()); // ajouter la catégorie sans espaces superflus
            }
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la lecture des catégories.");
        }
        return categories;
    }

    // Ajouter une catégorie au fichier
    public void addCategory(String category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORY_FILE_PATH, true))) {
            writer.write(category);
            writer.newLine();
            System.out.println("✅ Catégorie ajoutée : " + category);
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ajout de la catégorie.");
        }
    }
}
