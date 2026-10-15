import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final String FICHIER = "taches.txt";

    public static void main(String[] args) {
        // Prédiction : au premier lancement, la liste est vide et il n'y a pas d'erreur.
        // Après avoir ajouté des tâches et quitté, elles réapparaissent au lancement suivant.
        GestionnaireTaches gestionnaire = new GestionnaireTaches();
        Scanner scanner = new Scanner(System.in);

        try {
            int ignorees = gestionnaire.charger(FICHIER);
            if (ignorees > 0) {
                System.out.println("Attention : " + ignorees + " ligne(s) illisible(s) ignorée(s) dans " + FICHIER);
            }
        } catch (IOException e) {
            System.out.println("Impossible de lire " + FICHIER + " : " + e.getMessage());
        }

        boolean continuer = true;
        while (continuer) {
            System.out.println();
            System.out.println("=== Mes tâches ===");
            System.out.println("1. Ajouter");
            System.out.println("2. Lister");
            System.out.println("3. Marquer comme faite");
            System.out.println("4. Supprimer");
            System.out.println("5. Quitter");
            System.out.print("Ton choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> ajouter(gestionnaire, scanner);
                case "2" -> lister(gestionnaire);
                case "3" -> {
                    int numero = lireNumero(gestionnaire, scanner);
                    if (numero != -1 && gestionnaire.marquerFaite(numero)) {
                        System.out.println("Tâche marquée comme faite.");
                    }
                }
                case "4" -> {
                    int numero = lireNumero(gestionnaire, scanner);
                    if (numero != -1 && gestionnaire.supprimer(numero)) {
                        System.out.println("Tâche supprimée.");
                    }
                }
                case "5" -> continuer = false;
                default -> System.out.println("Choix inconnu, tape un chiffre entre 1 et 5.");
            }
        }

        try {
            gestionnaire.sauvegarder(FICHIER);
            System.out.println("Tâches sauvegardées dans " + FICHIER + ". À bientôt !");
        } catch (IOException e) {
            System.out.println("Erreur pendant la sauvegarde : " + e.getMessage());
        }
        scanner.close();
    }

    private static void ajouter(GestionnaireTaches gestionnaire, Scanner scanner) {
        System.out.print("Titre : ");
        String titre = scanner.nextLine();

        System.out.print("Priorité (1 = basse, 2 = moyenne, 3 = haute) : ");
        Priorite priorite;
        switch (scanner.nextLine().trim()) {
            case "1" -> priorite = Priorite.BASSE;
            case "3" -> priorite = Priorite.HAUTE;
            default -> priorite = Priorite.MOYENNE;
        }

        if (gestionnaire.ajouter(titre, priorite)) {
            System.out.println("Tâche ajoutée.");
        } else {
            System.out.println("Le titre ne peut pas être vide.");
        }
    }

    private static void lister(GestionnaireTaches gestionnaire) {
        ArrayList<Tache> taches = gestionnaire.getTaches();
        if (taches.isEmpty()) {
            System.out.println("Aucune tâche pour l'instant.");
            return;
        }
        for (int i = 0; i < taches.size(); i++) {
            System.out.println((i + 1) + ". " + taches.get(i));
        }
    }

    // Renvoie le numéro saisi, ou -1 s'il est invalide (après avoir affiché pourquoi)
    private static int lireNumero(GestionnaireTaches gestionnaire, Scanner scanner) {
        lister(gestionnaire);
        System.out.print("Numéro de la tâche : ");
        String saisie = scanner.nextLine().trim();
        try {
            int numero = Integer.parseInt(saisie);
            if (!gestionnaire.numeroValide(numero)) {
                System.out.println("Pas de tâche numéro " + numero + ".");
                return -1;
            }
            return numero;
        } catch (NumberFormatException e) {
            System.out.println("\"" + saisie + "\" n'est pas un nombre.");
            return -1;
        }
    }
}
