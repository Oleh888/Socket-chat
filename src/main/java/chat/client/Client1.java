package chat.client;

import java.io.IOException;

public class Client1 {
    public static final String IP_ADDR = "localhost";
    public static final int PORT = 8080;

    public static void main(String[] args) {
            new Client(IP_ADDR, PORT);
    }
}
