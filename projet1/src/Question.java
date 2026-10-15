public class Question {

    private String enonce;
    private String reponse;

    public Question(String enonce, String reponse) {
        this.enonce = enonce;
        this.reponse = reponse;
    }

    public String getEnonce() {
        return enonce;
    }

    public String getReponse() {
        return reponse;
    }

    // Compare sans tenir compte des espaces autour ni des majuscules
    public boolean estCorrecte(String reponseDonnee) {
        return reponseDonnee.trim().equalsIgnoreCase(reponse);
    }
}
