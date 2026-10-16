// Faux client : répond un texte prévu à l'avance, sans réseau ni clé.
// Utile pour développer et tester le reste du programme sans dépendre de l'API.
public class MockAiClient implements AiClient {

    private int nombreQuestions = 0;

    @Override
    public String ask(String question) {
        nombreQuestions++;
        String texte = question.toLowerCase();

        if (texte.contains("interface")) {
            return "(réponse factice) Une interface est un contrat : elle liste des méthodes "
                    + "que les classes qui l'implémentent doivent écrire.";
        }
        if (texte.contains("bonjour") || texte.contains("salut")) {
            return "(réponse factice) Bonjour ! Je suis le faux tuteur Java. Pose-moi une question.";
        }
        return "(réponse factice n°" + nombreQuestions + ") Je ne suis pas une vraie IA, "
                + "mais j'ai bien reçu ta question : \"" + question + "\"";
    }

    @Override
    public void reset() {
        nombreQuestions = 0;
    }
}
