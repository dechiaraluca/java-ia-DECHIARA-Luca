import java.util.Scanner;

public class Quiz {

    public static void main(String[] args) {
        // Prédiction : le programme pose les 5 questions une par une, redemande si
        // je ne tape rien, dit "Bonne réponse" ou donne la correction, puis affiche
        // mon score sur 5 avec un message qui dépend du résultat.
        Question[] questions = {
            new Question("Quel mot-clé permet de créer un objet en Java ?", "new"),
            new Question("Que veut dire IA ?", "intelligence artificielle"),
            new Question("Quelle méthode est le point de départ d'un programme Java ?", "main"),
            new Question("Quelle entreprise a créé ChatGPT ?", "OpenAI"),
            new Question("Quel type Java sert à stocker du texte ?", "String")
        };

        Scanner scanner = new Scanner(System.in);
        int score = 0;

        System.out.println("=== Quiz IA et Java ===");

        for (int i = 0; i < questions.length; i++) {
            System.out.println();
            System.out.println("Question " + (i + 1) + " : " + questions[i].getEnonce());

            // On redemande tant que la saisie est vide
            String reponse = "";
            while (reponse.trim().isEmpty()) {
                System.out.print("Ta réponse : ");
                reponse = scanner.nextLine();
                if (reponse.trim().isEmpty()) {
                    System.out.println("Tu n'as rien écrit, essaie encore.");
                }
            }

            if (questions[i].estCorrecte(reponse)) {
                System.out.println("Bonne réponse !");
                score++;
            } else {
                System.out.println("Raté, la bonne réponse était : " + questions[i].getReponse());
            }
        }

        System.out.println();
        System.out.println("Score final : " + score + " / " + questions.length);

        if (score == questions.length) {
            System.out.println("Parfait, tu maîtrises le sujet !");
        } else if (score >= questions.length / 2.0) {
            System.out.println("Pas mal, encore un petit effort.");
        } else {
            System.out.println("Il faut réviser, mais ne lâche rien !");
        }

        scanner.close();
    }
}
