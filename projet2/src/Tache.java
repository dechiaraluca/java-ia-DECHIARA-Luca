import java.time.LocalDate;

public class Tache {

    private String titre;
    private Priorite priorite;
    private boolean faite;
    private LocalDate echeance; // null si pas de date

    public Tache(String titre, Priorite priorite, boolean faite, LocalDate echeance) {
        this.titre = titre;
        this.priorite = priorite;
        this.faite = faite;
        this.echeance = echeance;
    }

    public String getTitre() {
        return titre;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public boolean estFaite() {
        return faite;
    }

    public LocalDate getEcheance() {
        return echeance;
    }

    public void marquerFaite() {
        faite = true;
    }

    public boolean estEnRetard() {
        return !faite && echeance != null && echeance.isBefore(LocalDate.now());
    }

    // Format d'une ligne du fichier : PRIORITE;faite;echeance;titre (echeance vide si aucune)
    // Le titre est en dernier pour qu'il puisse contenir des ";" sans casser la lecture
    public String versLigne() {
        String date = (echeance == null) ? "" : echeance.toString();
        return priorite + ";" + faite + ";" + date + ";" + titre;
    }

    @Override
    public String toString() {
        String caseCochee = faite ? "[x]" : "[ ]";
        String texte = caseCochee + " " + titre + " (" + priorite + ")";
        if (echeance != null) {
            texte += " pour le " + echeance;
        }
        if (estEnRetard()) {
            texte += " EN RETARD";
        }
        return texte;
    }
}
