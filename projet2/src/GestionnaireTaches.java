import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

// Toute la logique, aucun affichage : c'est Main qui parle à l'utilisateur
public class GestionnaireTaches {

    // Format AAAA-MM-JJ : 4 chiffres, tiret, 2 chiffres, tiret, 2 chiffres
    private static final String REGEX_DATE = "\\d{4}-\\d{2}-\\d{2}";

    private ArrayList<Tache> taches = new ArrayList<>();

    public ArrayList<Tache> getTaches() {
        return taches;
    }

    // dateTexte peut être vide : la tâche n'a alors pas d'échéance
    public void ajouter(String titre, Priorite priorite, String dateTexte) throws TacheInvalideException {
        if (titre == null || titre.trim().isEmpty()) {
            throw new TacheInvalideException("Le titre ne peut pas être vide.");
        }
        LocalDate echeance = null;
        if (dateTexte != null && !dateTexte.trim().isEmpty()) {
            echeance = lireDate(dateTexte.trim());
        }
        taches.add(new Tache(titre.trim(), priorite, false, echeance));
    }

    // La regex vérifie la forme, LocalDate.parse vérifie que la date existe (pas de 2026-13-45)
    private LocalDate lireDate(String texte) throws TacheInvalideException {
        if (!texte.matches(REGEX_DATE)) {
            throw new TacheInvalideException("La date doit être au format AAAA-MM-JJ.");
        }
        try {
            return LocalDate.parse(texte);
        } catch (DateTimeParseException e) {
            throw new TacheInvalideException("La date " + texte + " n'existe pas.");
        }
    }

    // Les numéros commencent à 1 pour l'utilisateur, les indices à 0 pour l'ArrayList
    public boolean numeroValide(int numero) {
        return numero >= 1 && numero <= taches.size();
    }

    public boolean marquerFaite(int numero) {
        if (!numeroValide(numero)) {
            return false;
        }
        taches.get(numero - 1).marquerFaite();
        return true;
    }

    public boolean supprimer(int numero) {
        if (!numeroValide(numero)) {
            return false;
        }
        taches.remove(numero - 1);
        return true;
    }

    // Renvoie le nombre de tâches supprimées
    public int supprimerTerminees() {
        int avant = taches.size();
        taches.removeIf(Tache::estFaite);
        return avant - taches.size();
    }

    // HAUTE en premier : on inverse l'ordre de l'enum (BASSE, MOYENNE, HAUTE)
    public void trierParPriorite() {
        taches.sort(Comparator.comparing(Tache::getPriorite).reversed());
    }

    // Les tâches sans date passent à la fin
    public void trierParDate() {
        taches.sort(Comparator.comparing(Tache::getEcheance,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public ArrayList<Tache> tachesEnRetard() {
        ArrayList<Tache> enRetard = new ArrayList<>();
        for (Tache tache : taches) {
            if (tache.estEnRetard()) {
                enRetard.add(tache);
            }
        }
        return enRetard;
    }

    public void sauvegarder(String nomFichier) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
            for (Tache tache : taches) {
                writer.write(tache.versLigne());
                writer.newLine();
            }
        }
    }

    // Renvoie le nombre de lignes ignorées car illisibles
    public int charger(String nomFichier) throws IOException {
        if (!Files.exists(Path.of(nomFichier))) {
            return 0; // premier lancement : pas de fichier, pas d'erreur
        }

        int lignesIgnorees = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] morceaux = ligne.split(";", 4);
                if (morceaux.length != 4 || morceaux[3].trim().isEmpty()) {
                    lignesIgnorees++;
                    continue;
                }
                try {
                    Priorite priorite = Priorite.valueOf(morceaux[0]);
                    boolean faite = Boolean.parseBoolean(morceaux[1]);
                    LocalDate echeance = morceaux[2].isEmpty() ? null : lireDate(morceaux[2]);
                    taches.add(new Tache(morceaux[3], priorite, faite, echeance));
                } catch (IllegalArgumentException | TacheInvalideException e) {
                    lignesIgnorees++; // priorité inconnue ou date invalide
                }
            }
        }
        return lignesIgnorees;
    }
}
