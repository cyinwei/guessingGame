import java.io.*;
import java.net.Socket;
import java.util.HashSet;

/**
 * The host is the source of the game.  The host chooses a word, a category, and a hint.  The word is the secret, and
 *  the category and hint will be public.  The name of the game is for all the players (all other connections) to guess
 *  the word.
 */
public class Host extends Connection {
    private Socket socket;
    private String secret; //current secret word
    private String category; //another "hint" for the players
    private String hint; //description given to other players on what to guess, can be changed
    public boolean gameIsStarted; //to transition from lobby to game
    private PrintWriter out;
    private boolean command;
    private BufferedReader bReader;
    private String serverMessage; //default server message
    private static HashSet<PrintWriter> writers = new HashSet<>();

    public Host(Socket socket, String serverMessage) {
        super(socket, serverMessage);
        this.socket = socket;
        this.serverMessage = serverMessage;
        try {
            //initialize our writers and readers
            //this.writer = new PrintWriter(this.socket.getOutputStream());
            InputStreamReader isReader = new InputStreamReader(this.socket.getInputStream());
            this.bReader = new BufferedReader(isReader);
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run(){
        if (socket == null) {
            System.out.println("Error: in SocketManager, input socket is null.");
            return;
        }
        try {
            if(bReader.readLine().equals("\\startgame")){
                gameIsStarted=true;
                for (PrintWriter writer: writers){
                    writer.println("Game is starting");
                }
                System.out.println("Game started by" + getName());
                command=true;
            }

        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //setting up the functions unique to Host
    public String getSecret() { return this.secret; } //this is used to return the word that other players are guessing for within our code
    private void setSecret(String secret) { this.secret = secret; } //this function is called when we write the \setsecret command
    public String getCategory() { return this.category; } //this is used to return the category that the Host sets for the secret
    private void setCategory(String category) { this.category = category; } //this function is called when we write the \setcategory command
    public String getHint() { return this.hint; } //this is used to return the hint that the Host sets for the secret
    private void setHint(String hint) { this.hint = hint; } //this function is called when we write the \sethint command
}
