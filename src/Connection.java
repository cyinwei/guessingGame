/**
 * Created by cyinwei on 12/3/15.
 * Edited by deaversp on 12/3/15.
 * <p>
 * Our "base class" for anyone who joins the server to play Guessing Game.
 * <p>
 * It'll handle default options, messages, stuff like that.
 *
 * I added the hashet for handling broadcasts - p
 *
 */

import java.io.*;
import java.lang.Runnable;
import java.net.Socket;
import java.util.HashSet;

public class Connection implements Runnable {
    private String name;
    private Socket socket;
    private PrintWriter out;

    //The set of all the print writers for all the clients.
    //This set is kept so we can easily broadcast messages.
    private static HashSet<PrintWriter> writers = new HashSet<>();

    //input stream reads sequences of bytes
    private InputStreamReader isReader;
    //buffered reader takes in a stream, allowing us to get the stream line by line (strings!)
    private BufferedReader bReader;

    private String clientMessage; //message from client
    private String serverMessage; //default server message

    public Connection(Socket socket, String serverMessage) {
        if (socket == null) {
            return;
        }

        this.socket = socket;
        this.serverMessage = serverMessage;

        try {
            //initialize our writers and readers
            //this.writer = new PrintWriter(this.socket.getOutputStream());
            this.isReader = new InputStreamReader(this.socket.getInputStream());
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

            while ((clientMessage = bReader.readLine()) != null && !clientMessage.equals("\\disconnect")) {
                //print the message out
                //we don't need to see disconnect commands, so throw those out
                for (PrintWriter writer: writers) {
                    writer.println(socket.getInetAddress().getHostAddress() + " : " + clientMessage);
                    System.out.println(socket.getInetAddress().getHostAddress() + " : " + clientMessage);
                }
            }

            System.out.print(socket.getInetAddress().getHostAddress() + " has disconnected.\n");
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

    private void read() {
        try {
            if ((clientMessage = bReader.readLine()) != null){
                clientMessage = bReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getName() { return this.name; }
    private void setName(String name) { this.name = name; }
}
