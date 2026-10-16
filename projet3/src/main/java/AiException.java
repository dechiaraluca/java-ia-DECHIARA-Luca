// Erreur lors d'un appel à l'IA (pas de réseau, clé invalide, statut HTTP inattendu...)
// Le message est déjà rédigé pour être affiché tel quel à l'utilisateur.
public class AiException extends RuntimeException {

    public AiException(String message) {
        super(message);
    }
}
