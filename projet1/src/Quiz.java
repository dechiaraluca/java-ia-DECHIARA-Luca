import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Quiz {

    public static void main(String[] args) {
        // Prédiction : le programme demande un thème (Java, IA ou tout), pose les
        // questions de ce thème dans un ordre aléatoire, redemande si je ne tape rien,
        // puis affiche mon score, un message selon le résultat et le temps total.
        // À la fin, il propose de rejouer.
        Question[] questions = {
                new Question("Quel mot-clé permet de créer un objet en Java ?", "new", "java"),
                new Question("Que veut dire IA ?", "intelligence artificielle", "ia"),
                new Question("Quelle méthode est le point de départ d'un programme Java ?", "main", "java"),
                new Question("Quelle entreprise a créé ChatGPT ?", "OpenAI", "ia"),
                new Question("Quel type Java sert à stocker du texte ?", "String", "java"),
                new Question("Quel type Java sert à stocker des nombres entiers ?", "int", "java"),
                new Question("Que veut dire LLM (en anglais) ?", "large language model", "ia"),
                new Question("Comment s'appelle l'IA conversationnelle de Google ?", "Gemini", "ia")
        };

        Scanner scanner = new Scanner(System.in);
        String rejouer;

        do {
            String theme = choisirTheme(scanner);
            jouerPartie(questions, theme, scanner);

            System.out.print("Rejouer ? (o/n) : ");
            rejouer = scanner.nextLine().trim();
        } while (rejouer.equalsIgnoreCase("o"));

        System.out.println("À bientôt !");
        scanner.close();
    }

    private static String choisirTheme(Scanner scanner) {
        System.out.println();
        System.out.println("=== Quiz IA et Java ===");
        System.out.println("1. Java");
        System.out.println("2. IA");
        System.out.println("3. Tout");
        System.out.print("Choisis un thème : ");
        String choix = scanner.nextLine().trim();

        if (choix.equals("1")) {
            return "java";
        } else if (choix.equals("2")) {
            return "ia";
        } else {
            return "tout";
        }
    }

    private static void jouerPartie(Question[] questions, String theme, Scanner scanner) {
        // On garde seulement les questions du thème choisi, puis on les mélange
        ArrayList<Question> selection = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            if (theme.equals("tout") || questions[i].getTheme().equals(theme)) {
                selection.add(questions[i]);
            }
        }
        Collections.shuffle(selection);

        int score = 0;
        long debut = System.currentTimeMillis();

        for (int i = 0; i < selection.size(); i++) {
            Question question = selection.get(i);
            System.out.println();
            System.out.println("Question " + (i + 1) + " : " + question.getEnonce());

            // On redemande tant que la saisie est vide
            String reponse = "";
            while (reponse.trim().isEmpty()) {
                System.out.print("Ta réponse : ");
                reponse = scanner.nextLine();
                if (reponse.trim().isEmpty()) {
                    System.out.println("Tu n'as rien écrit, essaie encore.");
                }
            }

            if (question.estCorrecte(reponse)) {
                System.out.println("Bonne réponse !");
                score++;
            } else {
                System.out.println("Raté, la bonne réponse était : " + question.getReponse());
            }
        }

        long secondes = (System.currentTimeMillis() - debut) / 1000;
        int total = selection.size();

        System.out.println();
        System.out.println("Score final : " + score + " / " + total);

        if (score == total) {
            System.out.println("Parfait, tu maîtrises le sujet !");
        } else if (score >= total / 2.0) {
            System.out.println("Pas mal, encore un petit effort.");
        } else if (score > 0) {
            System.out.println("Il faut réviser, mais ne lâche rien !");
        } else {
            System.out.println("Ouch, il va falloir réviser sérieusement !");
        }

        System.out.println("Temps total : " + secondes + " secondes");
    }
}
