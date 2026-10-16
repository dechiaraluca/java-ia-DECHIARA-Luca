import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

// Version graphique : elle utilise le même GestionnaireTaches que Main, sans le modifier.
// C'est l'intérêt d'avoir séparé la logique (GestionnaireTaches) de l'affichage.
public class TachesFenetre extends JFrame {

    private static final String FICHIER = "taches.txt";

    private GestionnaireTaches gestionnaire = new GestionnaireTaches();

    // La JList affiche le contenu du modèle ; on remplit le modèle avec les tâches
    private DefaultListModel<Tache> modele = new DefaultListModel<>();
    private JList<Tache> liste = new JList<>(modele);

    private JTextField champTitre = new JTextField(18);
    private JComboBox<Priorite> choixPriorite = new JComboBox<>(Priorite.values());
    private JTextField champDate = new JTextField(9);

    public TachesFenetre() {
        super("Mes tâches");

        // En haut : le formulaire d'ajout
        choixPriorite.setSelectedItem(Priorite.MOYENNE);
        JButton boutonAjouter = new JButton("Ajouter");
        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaire.add(new JLabel("Titre :"));
        formulaire.add(champTitre);
        formulaire.add(choixPriorite);
        formulaire.add(new JLabel("Échéance (AAAA-MM-JJ) :"));
        formulaire.add(champDate);
        formulaire.add(boutonAjouter);

        // Au centre : la liste des tâches, avec une barre de défilement
        liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane defilement = new JScrollPane(liste);
        defilement.setBorder(BorderFactory.createTitledBorder("Sélectionne une tâche dans la liste"));

        // En bas : les actions
        JButton boutonFaite = new JButton("Marquer comme faite");
        JButton boutonSupprimer = new JButton("Supprimer");
        JButton boutonSupprimerTerminees = new JButton("Supprimer les terminées");
        JButton boutonTriPriorite = new JButton("Trier par priorité");
        JButton boutonTriDate = new JButton("Trier par date");
        JButton boutonRetard = new JButton("Tâches en retard");

        JPanel actions = new JPanel(new GridLayout(2, 3, 5, 5));
        actions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actions.add(boutonFaite);
        actions.add(boutonSupprimer);
        actions.add(boutonSupprimerTerminees);
        actions.add(boutonTriPriorite);
        actions.add(boutonTriDate);
        actions.add(boutonRetard);

        add(formulaire, BorderLayout.NORTH);
        add(defilement, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        // Chaque bouton appelle une méthode du gestionnaire, puis on rafraîchit la liste
        boutonAjouter.addActionListener(e -> ajouter());
        champTitre.addActionListener(e -> ajouter());
        boutonFaite.addActionListener(e -> {
            int numero = numeroSelectionne();
            if (numero != -1) {
                gestionnaire.marquerFaite(numero);
                apresModification();
            }
        });
        boutonSupprimer.addActionListener(e -> {
            int numero = numeroSelectionne();
            if (numero != -1) {
                gestionnaire.supprimer(numero);
                apresModification();
            }
        });
        boutonSupprimerTerminees.addActionListener(e -> {
            int nombre = gestionnaire.supprimerTerminees();
            apresModification();
            JOptionPane.showMessageDialog(this, nombre + " tâche(s) terminée(s) supprimée(s).");
        });
        boutonTriPriorite.addActionListener(e -> {
            gestionnaire.trierParPriorite();
            apresModification();
        });
        boutonTriDate.addActionListener(e -> {
            gestionnaire.trierParDate();
            apresModification();
        });
        boutonRetard.addActionListener(e -> afficherRetards());

        charger();
        rafraichir();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 450);
        setLocationRelativeTo(null); // centre la fenêtre à l'écran
    }

    private void charger() {
        try {
            int ignorees = gestionnaire.charger(FICHIER);
            if (ignorees > 0) {
                JOptionPane.showMessageDialog(this,
                        ignorees + " ligne(s) illisible(s) ignorée(s) dans " + FICHIER,
                        "Attention", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            erreur("Impossible de lire " + FICHIER + " : " + e.getMessage());
        }
    }

    private void ajouter() {
        Priorite priorite = (Priorite) choixPriorite.getSelectedItem();
        try {
            gestionnaire.ajouter(champTitre.getText(), priorite, champDate.getText());
            champTitre.setText("");
            champDate.setText("");
            apresModification();
        } catch (TacheInvalideException e) {
            erreur("Tâche refusée : " + e.getMessage());
        }
    }

    // Renvoie le numéro (à partir de 1) de la tâche sélectionnée, ou -1 si aucune
    private int numeroSelectionne() {
        int index = liste.getSelectedIndex();
        if (index == -1) {
            erreur("Sélectionne d'abord une tâche dans la liste.");
            return -1;
        }
        return index + 1;
    }

    private void afficherRetards() {
        ArrayList<Tache> enRetard = gestionnaire.tachesEnRetard();
        if (enRetard.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune tâche en retard, bravo !");
            return;
        }
        String texte = "";
        for (Tache tache : enRetard) {
            texte += "- " + tache + "\n";
        }
        JOptionPane.showMessageDialog(this, texte, "Tâches en retard", JOptionPane.WARNING_MESSAGE);
    }

    // On sauvegarde après chaque modification : rien n'est perdu si on ferme la fenêtre
    private void apresModification() {
        rafraichir();
        try {
            gestionnaire.sauvegarder(FICHIER);
        } catch (IOException e) {
            erreur("Erreur pendant la sauvegarde : " + e.getMessage());
        }
    }

    private void rafraichir() {
        modele.clear();
        for (Tache tache : gestionnaire.getTaches()) {
            modele.addElement(tache); // la JList affiche tache.toString()
        }
    }

    private void erreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        // Swing demande de créer la fenêtre sur son propre fil d'exécution
        SwingUtilities.invokeLater(() -> new TachesFenetre().setVisible(true));
    }
}
