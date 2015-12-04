/**
 * Created by cyinwei on 12/3/15.
 * Edited by deaversp on 12/3/15.
 * <p>
 * Our "base class" for anyone who joins the server to play Guessing Game.
 * <p>
 * It'll handle default options, messages, stuff like that.
 *
 * I added the hashset for handling broadcasts - p
 *
 */

import java.io.*;
import java.lang.Runnable;
import java.net.Socket;
import java.util.HashSet;

public class Connection implements Runnable {
    private boolean isHost;
    private String name;
    private static String secret = null;

    private Socket socket;
    private PrintWriter out;
    public static boolean secretIsSet; //to transition from lobby to game
    boolean isChat;

    //The set of all the print writers for all the clients.
    //This set is kept so we can easily broadcast messages.
    private static HashSet<PrintWriter> writers = new HashSet<>();

    //buffered reader takes in a stream, allowing us to get the stream line by line (strings!)
    private BufferedReader bReader;
    private String serverMessage; //default server message

    public Connection(Socket socket, String serverMessage, boolean isHost) {
        if (socket == null) {
            return;
        }

        this.socket = socket;
        this.serverMessage = serverMessage;
        this.isHost = isHost;

        try {
            //initialize our writers and readers
            //this.writer = new PrintWriter(this.socket.getOutputStream());
            InputStreamReader isReader = new InputStreamReader(this.socket.getInputStream());
            this.bReader = new BufferedReader(isReader);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            writers.add(out);

            System.out.print("Client at: " + socket.getInetAddress().getHostAddress() + " has connected.\n");
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        if (socket == null) {
            System.out.println("Error: in SocketManager, input socket is null.");
            return;
        }
        try {
            //first write our default server message
            OutputStream output = socket.getOutputStream();
            output.write((this.serverMessage).getBytes());
            String defaultName = socket.getInetAddress().getHostAddress(); //default name will be the connection's address
            setName(defaultName);
            String clientMessage;
            if(isHost) {out.println( "You are the host"); }
            while ((clientMessage = bReader.readLine()) != null && !clientMessage.equals("\\disconnect")) {
                //this is just regular pre-game chat
                //skip this and go through loop again if a command was issued
                //this lets the reader be set back to clientMessage
                isChat = !parseBasicConnection(clientMessage);

                if (isHost && isChat) {
                    isChat = !parseIfHost(clientMessage);
                }

                if (checkIfSecretGuessed(clientMessage)) {
                    if (!isHost) {
                        for (PrintWriter writer : writers) {
                            writer.println(getName() + " has guessed the secret, " + getSecret() + "!");
                        }
                    }
                    else {
                        this.out.println("Hey, don't give away the secret!");
                    }
                    isChat = false;
                }

                if(isChat){
                    for (PrintWriter writer: writers) {
                        if(isHost){ writer.print("HOST "); }
                        if(!isHost){ writer.print("PLAYER ");}
                        writer.println(getName() + " : " + clientMessage);
                    }
                    if(isHost){ System.out.print("HOST "); }
                    if (!isHost) { System.out.print("PLAYER ");}
                    System.out.println(getName() + " : " + clientMessage);
                }
            }

            //tell everyone when someone disconnects
            System.out.print(getName() + " has disconnected.\n");
            for (PrintWriter writer: writers) {
                writer.println(getName() + " has disconnected.\n");
            }
            //done communicating
            bReader.close();
            if (out != null) {
                writers.remove(out);
            }
            socket.close();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean parseBasicConnection(String input) {
        //this is the \help command
        if(input.equals("\\help")){
            out.println("Current working commands: \\disconnect \\setname");
            if(isHost){
                out.println("Host commands: \\startgame");
            }
            return true; //don't want to show other players that a command was entered
        }
        //this is the \setname command
        if(input.equals("\\setname")){
            try {
                out.println("Enter your new name");
                String newName = bReader.readLine();
                for (PrintWriter writer : writers) {
                    writer.println(getName() + " changed name to " + newName);
                }
                System.out.println(getName() + " changed name to " + newName);
                setName(newName);
                return true;
            }
            catch (IOException e) {
                throw new RuntimeException("Error setting name.", e);
            }
        }
        return false;
    }

    public boolean parseIfHost(String input) {
        //this is the \setsecret command for the host
        if(input.equals("\\setsecret")){
            try {
                out.println("Set secret");
                String newSecret = bReader.readLine();
                setSecret(newSecret);
                for (PrintWriter writer : writers) {
                    writer.println("Secret is set!");
                }
                if (isHost) {
                    out.println("Secret set to (" + getSecret() + ")");
                }
                System.out.println("Secret set to (" + getSecret() + ")");
                return true;
            } catch (IOException e) {
                throw new RuntimeException("Error getting secret", e);
            }
        }
        //this is the \endgame command for the host
        if(input.equals("\\endgame")) {
            for (PrintWriter writer : writers) {
                writer.println("Game over");
            }
            this.setSecret(null);
            return true;
        }
        return false;
    }

    public boolean checkIfSecretGuessed(String input) {
        if (input.equals(this.getSecret())) {
            return true;
        }
        else return false;
    }

    private String getName() { return this.name; }
    private void setName(String name) { this.name = name; }
    private String getSecret() { return this.secret; }
    private void setSecret(String secret) { this.secret = secret; }
}
