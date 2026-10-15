public class Tache {

    private String titre;
    private Priorite priorite;
    private boolean faite;

    public Tache(String titre, Priorite priorite, boolean faite) {
        this.titre = titre;
        this.priorite = priorite;
        this.faite = faite;
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

    public void marquerFaite() {
        faite = true;
    }

    // Format d'une ligne du fichier : PRIORITE;faite;titre
    // Le titre est en dernier pour qu'il puisse contenir des ";" sans casser la lecture
    public String versLigne() {
        return priorite + ";" + faite + ";" + titre;
    }

    @Override
    public String toString() {
        String caseCochee = faite ? "[x]" : "[ ]";
        return caseCochee + " " + titre + " (" + priorite + ")";
    }
}
