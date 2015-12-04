/**
 * Default "telnet" -like client for us to use in order to play "Guessing Game."
 */
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class telnetClient {


    public static void main(String args[]) throws Exception
    {

        Scanner input = new Scanner(System.in);
        int portNumber;
        String ipAddress;

        System.out.print("Enter IP Address: ");
        ipAddress = input.next();

        System.out.print("Enter Port Number: ");
        portNumber = input.nextInt();

        String userIn = "howdy";
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(ipAddress, portNumber);

        String serverMessage;
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.print("Server says: ");
        if ((serverMessage = inFromServer.readLine()) != null) System.out.println(serverMessage);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        input.nextLine();


        while (!userIn.equals("\\disconnect")) {
            userIn = inFromUser.readLine();
            outToServer.writeBytes(userIn + '\n');
            if (userIn.equals("\\disconnect")) {
                System.out.println("Connection closed \nExiting");
                break;
            }
        }
        clientSocket.close();
    }
}
