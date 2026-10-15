public class Question {

    private String enonce;
    private String reponse;
    private String theme;

    public Question(String enonce, String reponse, String theme) {
        this.enonce = enonce;
        this.reponse = reponse;
        this.theme = theme;
    }

    public String getEnonce() {
        return enonce;
    }

    public String getReponse() {
        return reponse;
    }

    public String getTheme() {
        return theme;
    }

    // Compare sans tenir compte des espaces autour ni des majuscules
    public boolean estCorrecte(String reponseDonnee) {
        return reponseDonnee.trim().equalsIgnoreCase(reponse);
    }
}
