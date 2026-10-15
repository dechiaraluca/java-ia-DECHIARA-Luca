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
            System.out.println("5. Trier par priorité");
            System.out.println("6. Trier par date");
            System.out.println("7. Tâches en retard");
            System.out.println("8. Supprimer les tâches terminées");
            System.out.println("9. Quitter");
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
                case "5" -> {
                    gestionnaire.trierParPriorite();
                    lister(gestionnaire);
                }
                case "6" -> {
                    gestionnaire.trierParDate();
                    lister(gestionnaire);
                }
                case "7" -> listerEnRetard(gestionnaire);
                case "8" -> {
                    int nombre = gestionnaire.supprimerTerminees();
                    System.out.println(nombre + " tâche(s) terminée(s) supprimée(s).");
                }
                case "9" -> continuer = false;
                default -> System.out.println("Choix inconnu, tape un chiffre entre 1 et 9.");
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

        System.out.print("Échéance (AAAA-MM-JJ, ou Entrée pour aucune) : ");
        String date = scanner.nextLine();

        try {
            gestionnaire.ajouter(titre, priorite, date);
            System.out.println("Tâche ajoutée.");
        } catch (TacheInvalideException e) {
            System.out.println("Tâche refusée : " + e.getMessage());
        }
    }

    private static void listerEnRetard(GestionnaireTaches gestionnaire) {
        ArrayList<Tache> enRetard = gestionnaire.tachesEnRetard();
        if (enRetard.isEmpty()) {
            System.out.println("Aucune tâche en retard, bravo !");
            return;
        }
        for (Tache tache : enRetard) {
            System.out.println("- " + tache);
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
        // Liste vide : inutile de demander un numéro qui sera forcément faux
        if (gestionnaire.getTaches().isEmpty()) {
            System.out.println("Aucune tâche pour l'instant, ajoute-en une d'abord.");
            return -1;
        }
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
