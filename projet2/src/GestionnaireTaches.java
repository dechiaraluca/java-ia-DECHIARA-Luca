import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

// Toute la logique, aucun affichage : c'est Main qui parle à l'utilisateur
public class GestionnaireTaches {

    private ArrayList<Tache> taches = new ArrayList<>();

    public ArrayList<Tache> getTaches() {
        return taches;
    }

    // Renvoie false si le titre est vide
    public boolean ajouter(String titre, Priorite priorite) {
        if (titre == null || titre.trim().isEmpty()) {
            return false;
        }
        taches.add(new Tache(titre.trim(), priorite, false));
        return true;
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
                String[] morceaux = ligne.split(";", 3);
                if (morceaux.length != 3 || morceaux[2].trim().isEmpty()) {
                    lignesIgnorees++;
                    continue;
                }
                try {
                    Priorite priorite = Priorite.valueOf(morceaux[0]);
                    boolean faite = Boolean.parseBoolean(morceaux[1]);
                    taches.add(new Tache(morceaux[2], priorite, faite));
                } catch (IllegalArgumentException e) {
                    lignesIgnorees++; // priorité inconnue
                }
            }
        }
        return lignesIgnorees;
    }
}
