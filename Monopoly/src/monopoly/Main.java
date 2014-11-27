package monopoly;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException, IOException {
        // TODO code application logic here
        new GameStarterUI(new Game());
    }

}
