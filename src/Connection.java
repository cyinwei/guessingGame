/**
 * Created by cyinwei on 12/3/15.
 * <p>
 * Our "base class" for anyone who joins the server to play Guessing Game.
 * <p>
 * It'll handle default options, messages, stuff like that.
 */


public class Connection implements Runnable {
    private String name;

    @Override
    public void run() {
    }

    private String getName() { return this.name; }
    private void setName(String name) { this.name = name; }
}
