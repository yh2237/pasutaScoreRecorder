package net._2237yh.pasuta_ScoreRecorder.config;

public class MessageData {
    private final String message;
    private final String sound;

    public MessageData(String message, String sound) {
        this.message = message;
        this.sound = sound;
    }

    public String getMessage() {
        return message;
    }

    public String getSound() {
        return sound;
    }
}
