package com.maximo.flappybird.network;

public class MessageParser {

    public static boolean isStart(String message) {
        return message.equals("START");
    }

    public static boolean isGameOver(String message) {
        return message.equals("GAMEOVER");
    }

    public static boolean isPlayerPosition(String message) {
        return message.startsWith("PLAYER:");
    }

    public static float getPlayerY(String message) {
        String[] parts = message.split(":");
        return Float.parseFloat(parts[1]);
    }
}
