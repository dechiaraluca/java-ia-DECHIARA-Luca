# Notes – Atelier Java + IA

## Projet 1 : Quiz en console

- **Ce que j'ai compris :** J'ai compris les tableaux
- **Ce que l'IA a mal fait ou inventé :** test PowerShell faussé
- **Ce que je n'ai pas encore compris :** Static

## Projet 2 : Gestionnaire de tâches

- **Ce que j'ai compris :** Séparer la logique (`GestionnaireTaches`, sans `System.out`) de l'affichage permet de réutiliser la même logique dans la console et dans la fenêtre Swing sans la modifier. Pendant la chasse au bug, j'ai trouvé que `reader.readLine()` était appelé deux fois par tour de boucle (une ligne sur deux perdue, et `NullPointerException` à la fin).
- **Ce que l'IA a mal fait ou inventé :** En testant, l'IA a supprimé le dossier `bin` de VS Code et compilé ailleurs : au lancement de `TachesFenetre`, j'ai eu une `ClassNotFoundException`. Elle avait aussi oublié des cas limites (liste vide avant de demander un numéro, fermeture de la fenêtre sans sauvegarde) que je lui ai fait relever.
- **Ce que je n'ai pas encore compris :** Les écritures comme `Tache::estFaite` et `Comparator.comparing(...)` utilisées pour la suppression et le tri.

## Projet 3 : Chatbot

- **Ce que j'ai compris :** L'interface `AiClient` permet de passer de `HttpAiClient` à `MockAiClient` sans changer le reste du code. Après un 401 il ne sert à rien de réessayer (une clé fausse reste fausse), alors qu'après un 429 il suffit d'attendre. Gson échappe les guillemets : construire le JSON à la main avec `dis "bonjour"` aurait donné une erreur 400.
- **Ce que l'IA a mal fait ou inventé :** L'IA a utilisé le modèle `llama-3.1-8b-instant` du sujet sans vérifier qu'il existait encore : Groq l'a arrêté le 16/08/2026, d'où des erreurs 404. Son premier message d'erreur n'affichait que le statut, sans le détail de l'API ; il a fallu l'améliorer pour comprendre. Elle n'avait pas non plus prévu les caractères typographiques de la réponse (apostrophes, espaces fines) affichés en `?` dans la console Windows.
- **Ce que je n'ai pas encore compris :** Le détail de `HttpClient` / `HttpRequest.newBuilder()` (l'enchaînement des `.header(...)`, `.POST(...)`, `.build()`).
