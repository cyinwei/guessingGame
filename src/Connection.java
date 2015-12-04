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
    private Socket socket;
    private PrintWriter out;
    private boolean command;
    public boolean gameIsStarted; //to transition from lobby to game

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
            while ((clientMessage = bReader.readLine()) != null && !clientMessage.equals("\\disconnect")) {
                //this is the \help command
                if(clientMessage.equals("\\help")){
                    out.println("Current working commands: \\disconnect and \\setname");
                    command = true; //don't want to show other players that a command was entered
                }
                if(clientMessage.equals("\\startgame") && isHost){
                    for (PrintWriter writer : writers) {
                        writer.println("Game is starting");
                    }
                    gameIsStarted = true;
                    command = true; //don't want to show other players that a command was entered
                }
                //this is the \setname command
                if(clientMessage.equals("\\setname")){
                    out.println("Enter your new name");
                    String newName = bReader.readLine();
                    for (PrintWriter writer: writers){
                        writer.println(getName() + " changed name to " + newName);
                    }
                    System.out.println(getName() + " changed name to " + newName);
                    setName(newName);
                    command = true;
                }
                //this is just regular pre-game chat
                //skip this and go through loop again if a command was issued
                //this lets the reader be set back to clientMessage
                if(!command){
                    for (PrintWriter writer: writers) {
                        writer.println(getName() + " : " + clientMessage);
                    }
                    System.out.println(getName() + " : " + clientMessage);
                }
                command=false;
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

    private String getName() { return this.name; }
    private void setName(String name) { this.name = name; }
}
