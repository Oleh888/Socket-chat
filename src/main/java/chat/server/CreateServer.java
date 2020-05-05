package chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class CreateServer extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public CreateServer(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Server.story.printStory(out);
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    word = in.readLine();
                    if (word.equals("stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println("new message: " + word);
                    Server.story.addStoryEl(word);
                    for (CreateServer vr : Server.serverList) {
                        vr.send(word);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            this.downService();
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
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
