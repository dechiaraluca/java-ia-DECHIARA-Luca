// Exception personnalisée : levée quand on essaie de créer une tâche incorrecte
public class TacheInvalideException extends Exception {

    public TacheInvalideException(String message) {
        super(message);
    }
}
