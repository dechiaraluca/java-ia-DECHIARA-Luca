import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

// Version graphique du chatbot : même AiClient que ChatBot, affiché sous forme de bulles
public class ChatFenetre extends JFrame {

    private static final Color FOND = new Color(240, 242, 245);
    private static final Color BLEU = new Color(37, 99, 235);
    private static final Color BULLE_IA = Color.WHITE;
    private static final Color BULLE_ERREUR = new Color(254, 226, 226);
    private static final Color TEXTE = new Color(30, 30, 30);
    private static final int LARGEUR_BULLE = 340;

    private AiClient client = ChatBot.choisirClient();
    private ArrayList<String> conversation = new ArrayList<>();

    private JPanel zoneMessages = new JPanel();
    private JScrollPane defilement = new JScrollPane(zoneMessages);
    private JTextField champ = new JTextField();
    private JButton boutonEnvoyer = new JButton("Envoyer");
    private JLabel labelEtat = new JLabel(" ");

    public ChatFenetre() {
        super("Tuteur Java");

        // En haut : le titre et le mode (vraie IA ou mode hors ligne)
        boolean horsLigne = client instanceof MockAiClient;
        JLabel titre = new JLabel("Tuteur Java");
        titre.setFont(titre.getFont().deriveFont(Font.BOLD, 18f));
        titre.setForeground(Color.WHITE);
        JLabel mode = new JLabel(horsLigne ? "Mode hors ligne (réponses factices)" : "Connecté à Groq");
        mode.setForeground(new Color(219, 234, 254));

        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU);
        entete.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        entete.add(titre, BorderLayout.WEST);
        entete.add(mode, BorderLayout.EAST);

        // Au centre : les bulles, empilées de haut en bas
        zoneMessages.setLayout(new BoxLayout(zoneMessages, BoxLayout.Y_AXIS));
        zoneMessages.setBackground(FOND);
        zoneMessages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        defilement.setBorder(null);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        // En bas : boutons, état ("L'IA réfléchit...") et zone de saisie
        JButton boutonReset = new JButton("Nouvelle conversation");
        JButton boutonSave = new JButton("Sauvegarder");
        JButton boutonAide = new JButton("Aide");
        JPanel outils = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outils.add(boutonReset);
        outils.add(boutonSave);
        outils.add(boutonAide);
        outils.add(labelEtat);
        labelEtat.setForeground(Color.GRAY);

        JPanel saisie = new JPanel(new BorderLayout(8, 0));
        saisie.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        champ.setFont(champ.getFont().deriveFont(14f));
        saisie.add(champ, BorderLayout.CENTER);
        saisie.add(boutonEnvoyer, BorderLayout.EAST);

        JPanel bas = new JPanel(new BorderLayout());
        bas.add(outils, BorderLayout.NORTH);
        bas.add(saisie, BorderLayout.SOUTH);

        add(entete, BorderLayout.NORTH);
        add(defilement, BorderLayout.CENTER);
        add(bas, BorderLayout.SOUTH);

        boutonEnvoyer.addActionListener(e -> envoyer());
        champ.addActionListener(e -> envoyer());
        boutonReset.addActionListener(e -> nouvelleConversation());
        boutonSave.addActionListener(e -> sauvegarder());
        boutonAide.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Tape ta question puis Entrée (ou « Envoyer »).\n"
                + "Nouvelle conversation : l'IA oublie ce qui a été dit.\n"
                + "Sauvegarder : écrit la conversation dans " + ChatBot.FICHIER_SAUVEGARDE + "."));

        ajouterBulle("Bonjour ! Pose-moi une question sur Java.", false, BULLE_IA);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setLocationRelativeTo(null); // centre la fenêtre à l'écran
    }

    private void envoyer() {
        String question = champ.getText().trim();
        if (question.isEmpty()) {
            return;
        }
        champ.setText("");
        ajouterBulle(question, true, BLEU);
        occupe(true);

        // L'appel à l'API peut prendre plusieurs secondes : on le fait en arrière-plan
        // avec un SwingWorker, sinon la fenêtre resterait figée pendant l'attente
        SwingWorker<String, Void> tache = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return client.ask(question);
            }

            @Override
            protected void done() {
                try {
                    String reponse = get();
                    ajouterBulle(reponse, false, BULLE_IA);
                    conversation.add("Toi : " + question);
                    conversation.add("IA : " + reponse);
                } catch (Exception e) {
                    // get() emballe l'AiException : son message lisible est dans getCause()
                    Throwable cause = (e.getCause() != null) ? e.getCause() : e;
                    ajouterBulle("Erreur : " + cause.getMessage(), false, BULLE_ERREUR);
                }
                occupe(false);
            }
        };
        tache.execute();
    }

    private void nouvelleConversation() {
        client.reset();
        conversation.clear();
        zoneMessages.removeAll();
        ajouterBulle("Historique effacé, on repart de zéro.", false, BULLE_IA);
    }

    private void sauvegarder() {
        try {
            ChatBot.ecrireConversation(conversation);
            JOptionPane.showMessageDialog(this, "Conversation sauvegardée dans " + ChatBot.FICHIER_SAUVEGARDE + ".");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de sauvegarder : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pendant que l'IA réfléchit, on bloque l'envoi d'une autre question
    private void occupe(boolean enAttente) {
        champ.setEnabled(!enAttente);
        boutonEnvoyer.setEnabled(!enAttente);
        labelEtat.setText(enAttente ? "L'IA réfléchit..." : " ");
        if (!enAttente) {
            champ.requestFocus();
        }
    }

    // Une bulle à droite (moi, en bleu) ou à gauche (l'IA)
    private void ajouterBulle(String texte, boolean deMoi, Color couleur) {
        JLabel label = new JLabel(enHtml(texte));
        label.setForeground(deMoi ? Color.WHITE : TEXTE);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 14f));

        Bulle bulle = new Bulle(couleur);
        bulle.add(label);

        // Une ligne qui pousse la bulle à droite ou à gauche
        JPanel ligne = new JPanel();
        ligne.setLayout(new BoxLayout(ligne, BoxLayout.X_AXIS));
        ligne.setOpaque(false);
        if (deMoi) {
            ligne.add(Box.createHorizontalGlue());
            ligne.add(bulle);
        } else {
            ligne.add(bulle);
            ligne.add(Box.createHorizontalGlue());
        }

        zoneMessages.add(ligne);
        zoneMessages.add(Box.createRigidArea(new Dimension(0, 8)));
        zoneMessages.revalidate();
        zoneMessages.repaint();

        // On descend tout en bas pour voir le dernier message
        SwingUtilities.invokeLater(() -> {
            JScrollBar barre = defilement.getVerticalScrollBar();
            barre.setValue(barre.getMaximum());
        });
    }

    // Un JLabel sait afficher du HTML : on s'en sert pour le retour à la ligne automatique
    // et pour montrer les blocs de code (```...```) en police à chasse fixe
    private static String enHtml(String texte) {
        String echappe = texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        String[] morceaux = echappe.split("```");
        // Largeur fixe seulement pour les textes longs, sinon les petites bulles seraient trop larges
        boolean long_ = texte.length() > 45 || texte.contains("\n");
        String style = long_ ? " style='width:" + LARGEUR_BULLE + "px'" : "";
        StringBuilder html = new StringBuilder("<html><div" + style + ">");
        for (int i = 0; i < morceaux.length; i++) {
            if (i % 2 == 1) {
                // Morceau impair = à l'intérieur d'un bloc de code ; on enlève le nom du langage
                String code = morceaux[i].replaceFirst("^[a-zA-Z]*\\n", "");
                html.append("<pre style='font-family:monospace; background:#f3f4f6'>")
                        .append(code).append("</pre>");
            } else {
                html.append(morceaux[i].replace("\n", "<br>"));
            }
        }
        return html.append("</div></html>").toString();
    }

    // Un panneau aux coins arrondis qui sert de fond aux bulles
    private static class Bulle extends JPanel {

        private final Color couleur;

        Bulle(Color couleur) {
            this.couleur = couleur;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(couleur);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }

        // Sans ça, BoxLayout étire la bulle sur toute la largeur
        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
    }

    public static void main(String[] args) {
        // Swing demande de créer la fenêtre sur son propre fil d'exécution
        SwingUtilities.invokeLater(() -> {
            ChatFenetre fenetre = new ChatFenetre();
            fenetre.setVisible(true);
            fenetre.champ.requestFocusInWindow(); // on peut taper tout de suite, sans cliquer
        });
    }
}
