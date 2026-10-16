import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatBot {

    public static final String FICHIER_SAUVEGARDE = "conversation.txt";

    public static void main(String[] args) {
        // Prédiction : sans variable AI_API_KEY, le programme annonce le mode hors
        // ligne
        // et répond avec des textes factices. /quit arrête la boucle.
        AiClient client = choisirClient();
        ArrayList<String> conversation = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pose ta question (/reset pour recommencer, /save pour sauvegarder, /quit pour quitter).");

        while (true) {
            System.out.print("\nToi : ");
            if (!scanner.hasNextLine()) {
                break; // fin de saisie (Ctrl+Z) : on sort proprement
            }
            String saisie = scanner.nextLine().trim();

            if (saisie.isEmpty()) {
                continue;
            }
            if (saisie.equals("/quit")) {
                break;
            }
            if (saisie.equals("/reset")) {
                client.reset();
                conversation.clear();
                System.out.println("Historique effacé, on repart de zéro.");
                continue;
            }
            if (saisie.equals("/help")) {
                System.out.println("Commandes disponibles :");
                System.out.println("  /reset - effacer l'historique de la conversation");
                System.out.println("  /save  - sauvegarder la conversation dans " + FICHIER_SAUVEGARDE);
                System.out.println("  /quit  - quitter le programme");
                continue;
            }
            if (saisie.equals("/save")) {
                sauvegarder(conversation);
                continue;
            }

            try {
                String reponse = client.ask(saisie);
                System.out.println("IA : " + pourConsole(reponse));
                conversation.add("Toi : " + saisie);
                conversation.add("IA : " + reponse);
            } catch (AiException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }

        System.out.println("À bientôt !");
        scanner.close();
    }

    // La clé est lue dans une variable d'environnement : elle n'apparaît jamais
    // dans le code. Méthode publique : ChatFenetre (version graphique) l'utilise aussi.
    public static AiClient choisirClient() {
        String cle = System.getenv("AI_API_KEY");
        if (cle == null || cle.isBlank()) {
            System.out.println("Variable AI_API_KEY absente : mode hors ligne avec MockAiClient (réponses factices).");
            return new MockAiClient();
        }
        System.out.println("Clé trouvée : connexion à l'IA Groq avec HttpAiClient.");
        return new HttpAiClient(cle.trim());
    }

    // La console Windows ne sait pas afficher certains caractères typographiques de
    // l'IA
    // (ils apparaissent en "?") : on les remplace par leurs équivalents simples
    private static String pourConsole(String texte) {
        return texte.replace('\u202F', ' ')  // espace fine insécable (avant ! ? :)
                .replace('\u00A0', ' ')      // espace insécable
                .replace('\u2019', '\'')     // apostrophe courbe
                .replace('\u2018', '\'')
                .replace('\u201C', '"')      // guillemets anglais courbes
                .replace('\u201D', '"')
                .replace('\u2013', '-')      // tirets longs
                .replace('\u2014', '-')
                .replace("\u2026", "...");   // points de suspension
    }

    private static void sauvegarder(ArrayList<String> conversation) {
        try {
            ecrireConversation(conversation);
            System.out.println("Conversation sauvegardée dans " + FICHIER_SAUVEGARDE + ".");
        } catch (IOException e) {
            System.out.println("Impossible de sauvegarder : " + e.getMessage());
        }
    }

    // Sans affichage : c'est l'appelant (console ou fenêtre) qui prévient l'utilisateur
    public static void ecrireConversation(ArrayList<String> conversation) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FICHIER_SAUVEGARDE))) {
            for (String ligne : conversation) {
                writer.write(ligne);
                writer.newLine();
            }
        }
    }
}
