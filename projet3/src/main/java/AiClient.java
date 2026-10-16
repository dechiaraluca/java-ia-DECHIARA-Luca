// Le contrat de tout client d'IA : on lui pose une question, il renvoie la réponse.
// ChatBot ne sait pas s'il parle à la vraie API ou au faux client : il ne connaît que cette interface.
public interface AiClient {

    // Lance une AiException (avec un message lisible) en cas de problème
    String ask(String question);

    // Oublie la conversation en cours
    void reset();
}
