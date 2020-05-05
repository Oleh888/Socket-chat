package chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;
    private String addr;
    private int port;
    private String nickname;

    public Client(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.inputNick();
            new Reader().start();
            new Writer().start();
        } catch (IOException e) {
            Client.this.downService();
        }
    }

    private void inputNick() {
        System.out.print("Please, provide your nickname: ");
        try {
            nickname = inputUser.readLine();
            out.write("Hello " + nickname + "!\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Reader extends Thread {

        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.equals("stop")) {
                        Client.this.downService();
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                Client.this.downService();
            }
        }
    }

    public class Writer extends Thread {

        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    userWord = inputUser.readLine();
                    if (userWord.equals("stop")) {
                        out.write("stop" + "\n");
                        Client.this.downService();
                        break;
                    } else {
                        out.write("(" + time + ") " + nickname + ": " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    Client.this.downService();
                }
            }
        }
    }
}
