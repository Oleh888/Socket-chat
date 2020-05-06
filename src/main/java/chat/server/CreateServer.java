package chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class CreateServer extends Thread {
    private Socket socket;
    private BufferedReader serverReader;
    private BufferedWriter serverWriter;

    public CreateServer(Socket socket) throws IOException {
        this.socket = socket;
        serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Server.story.printStory(serverWriter);
        start();
    }

    @Override
    public void run() {
        try {
            String word = serverReader.readLine();
            serverWriter.write(word + "\n");
            serverWriter.flush();
            while (true) {
                word = serverReader.readLine();
                if (word.equals("stop")) {
                    this.downService();
                    break;
                }
                System.out.println("new message: " + word);
                Server.story.addStoryMessage(word);
                for (CreateServer vr : Server.serverList) {
                    vr.send(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String msg) {
        try {
            serverWriter.write(msg + "\n");
            serverWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                serverReader.close();
                serverWriter.close();
                for (CreateServer vr : Server.serverList) {
                    if (vr.equals(this)) {
                        vr.interrupt();
                    }
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
