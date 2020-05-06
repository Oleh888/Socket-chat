package chat.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

class Story {
    private LinkedList<String> story = new LinkedList<>();

    public void addStoryMessage(String message) {
        if (story.size() >= 10) {
            story.removeFirst();
        }
        story.add(message);
    }

    public void printStory(BufferedWriter writer) {
        if (story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
