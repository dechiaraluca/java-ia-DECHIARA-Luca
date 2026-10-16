import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

// Version graphique du quiz : mêmes questions et même logique que Quiz, mais dans une fenêtre
public class QuizFenetre extends JFrame {

    private static final Color VERT = new Color(0, 130, 60);
    private static final Color ROUGE = new Color(190, 30, 30);

    private Question[] questions = Quiz.creerQuestions();
    private ArrayList<Question> selection;
    private int indexQuestion;
    private int score;
    private long debut;

    // Les composants de la fenêtre
    private JComboBox<String> choixTheme = new JComboBox<>(new String[] {"Tout", "Java", "IA"});
    private JLabel labelProgression = new JLabel(" ");
    private JLabel labelEnonce = new JLabel("Choisis un thème puis clique sur « Commencer ».");
    private JTextField champReponse = new JTextField(25);
    private JButton boutonValider = new JButton("Valider");
    private JButton boutonCommencer = new JButton("Commencer");
    private JLabel labelResultat = new JLabel(" ");

    public QuizFenetre() {
        super("Quiz IA et Java");

        // En haut : choix du thème et bouton pour lancer une partie
        JPanel haut = new JPanel(new FlowLayout());
        haut.add(new JLabel("Thème :"));
        haut.add(choixTheme);
        haut.add(boutonCommencer);

        // Au centre : la question, la zone de réponse et le résultat
        labelEnonce.setFont(labelEnonce.getFont().deriveFont(Font.BOLD, 16f));
        JPanel ligneReponse = new JPanel(new FlowLayout());
        ligneReponse.add(champReponse);
        ligneReponse.add(boutonValider);

        JPanel centre = new JPanel(new GridLayout(4, 1, 5, 5));
        centre.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        centre.add(labelProgression);
        centre.add(labelEnonce);
        centre.add(ligneReponse);
        centre.add(labelResultat);

        add(haut, BorderLayout.NORTH);
        add(centre, BorderLayout.CENTER);

        // Ce qui se passe quand on clique (ou qu'on appuie sur Entrée dans le champ)
        boutonCommencer.addActionListener(e -> commencer());
        boutonValider.addActionListener(e -> valider());
        champReponse.addActionListener(e -> valider());

        activerReponse(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 300);
        setLocationRelativeTo(null); // centre la fenêtre à l'écran
    }

    private void commencer() {
        String theme = ((String) choixTheme.getSelectedItem()).toLowerCase();
        selection = Quiz.selectionner(questions, theme);
        indexQuestion = 0;
        score = 0;
        debut = System.currentTimeMillis();
        labelResultat.setText(" ");
        activerReponse(true);
        afficherQuestion();
    }

    private void afficherQuestion() {
        Question question = selection.get(indexQuestion);
        labelProgression.setText("Question " + (indexQuestion + 1) + " / " + selection.size()
                + "   |   Score : " + score);
        labelEnonce.setText(question.getEnonce());
        champReponse.setText("");
        champReponse.requestFocus();
    }

    private void valider() {
        String reponse = champReponse.getText();

        // Saisie vide : on prévient et on reste sur la même question
        if (reponse.trim().isEmpty()) {
            labelResultat.setForeground(Color.DARK_GRAY);
            labelResultat.setText("Tu n'as rien écrit, essaie encore.");
            return;
        }

        Question question = selection.get(indexQuestion);
        if (question.estCorrecte(reponse)) {
            score++;
            labelResultat.setForeground(VERT);
            labelResultat.setText("Bonne réponse !");
        } else {
            labelResultat.setForeground(ROUGE);
            labelResultat.setText("Raté, la bonne réponse était : " + question.getReponse());
        }

        indexQuestion++;
        if (indexQuestion < selection.size()) {
            afficherQuestion();
        } else {
            terminer();
        }
    }

    private void terminer() {
        long secondes = (System.currentTimeMillis() - debut) / 1000;
        int total = selection.size();
        labelProgression.setText("Partie terminée en " + secondes + " secondes");
        labelEnonce.setText("Score final : " + score + " / " + total + " - " + Quiz.messageScore(score, total));
        activerReponse(false);
        boutonCommencer.setText("Rejouer");
    }

    // Pendant une partie on peut répondre, mais pas changer de thème (et inversement)
    private void activerReponse(boolean actif) {
        champReponse.setEnabled(actif);
        boutonValider.setEnabled(actif);
        choixTheme.setEnabled(!actif);
        boutonCommencer.setEnabled(!actif);
    }

    public static void main(String[] args) {
        // Swing demande de créer la fenêtre sur son propre fil d'exécution
        SwingUtilities.invokeLater(() -> new QuizFenetre().setVisible(true));
    }
}
