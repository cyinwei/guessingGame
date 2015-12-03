/**
 * The host is the source of the game.  The host chooses a word, a category, and a hint.  The word is the secret, and
 *  the category and hint will be public.  The name of the game is for all the players (all other connections) to guess
 *  the word.
 */
public class Host extends Connection {
    private String secret; //current secret word
    private String category; //another "hint" for the players
    private String hint; //description given to other players on what to guess, can be changed
}
