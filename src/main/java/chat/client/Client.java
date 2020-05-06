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
    private BufferedReader clientReader;
    private BufferedWriter clientWriter;
    private BufferedReader inputUser;
    private String nickname;

    public Client(String addr, int port) {
        try {
            this.socket = new Socket(addr, port);
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.startChat();
        } catch (IOException e) {
            downService();
        }
    }

    private void startChat() throws IOException {
        System.out.print("Please, provide your nickname: ");
        nickname = inputUser.readLine();
        clientWriter.write("Hello " + nickname + "!\n");
        clientWriter.flush();
        new Reader().start();
        new Writer().start();
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                clientReader.close();
                clientWriter.close();
            }
        } catch (IOException e) {
            System.out.println("client left the chat");
        }
    }

    private class Reader extends Thread {

        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = clientReader.readLine();
                    if (str.equals("stop")) {
                        Client.this.downService();
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                downService();
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
                        clientWriter.write("stop" + "\n");
                        Client.this.downService();
                        break;
                    } else {
                        clientWriter.write("(" + time + ") " + nickname + ": " + userWord + "\n");
                    }
                    clientWriter.flush();
                } catch (IOException e) {
                    downService();
                }
            }
        }
    }
}
