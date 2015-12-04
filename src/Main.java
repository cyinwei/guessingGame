import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException{

        if (args.length < 2) {
            System.err.println("Please declare port number and number of players before running");
            System.exit(1);
        }

        System.out.print("Hello admin, welcome to the Guessing Game Server\n");

        int portNumber = Integer.parseInt(args[0]); //set this with "edit configurations" under the run menu, choose port number with program arguments
        int numOfClients = Integer.parseInt(args[0]); // set this before each game based on the number of players
        String message = "Welcome to the Guessing Game. Have Fun! To disconnect, Enter \\disconnect. For a list of other commands, Enter \\help\n";
        System.out.println("Your server is running on port: " + portNumber + "\n");

        Server server = new Server(portNumber, numOfClients, message);
        new Thread(server).start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        String rules = "A player will be selected to be the host. \n" +
                "The host will be given a word. \n" +
                "The host must give hints to the other players so that they can guess what the word is. \n" +
                "The host can describe the word in any way, but can't say the word itself. \n" +
                "The first player to guess the word gets to be the next host. \n";

        System.out.println("Enter \\kill to close the server");  //stop command is \kill
        while ((line = reader.readLine()) != null && !line.equals("\\kill")) {
            if (line.equals("rules")) {
                System.out.println(rules);
            }
        }
        reader.close();

        System.out.println("Kill command received");
        server.stop();
    }
}
