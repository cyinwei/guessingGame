import java.net.Socket;

/**
 * Created by cyinwei on 12/3/15.
 */

public class Player extends Connection {
    private String category;
    private String hint;
    private String secret;

    public Player(Socket socket, String serverMessage) {
        super(socket, serverMessage);
    }

    private String getCategory() { return category; }
    private String getHint() { return hint; }
    private String getSecret() { return secret; }


}
