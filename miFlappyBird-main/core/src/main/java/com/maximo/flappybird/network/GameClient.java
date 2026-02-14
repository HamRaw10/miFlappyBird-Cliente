package com.maximo.flappybird.network;

import java.io.*;
import java.net.Socket;

public class GameClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private NetworkListener listener;

    public GameClient(String host, int port, NetworkListener listener) {
        this.listener = listener;

        try {
            socket = new Socket(host, port);

            in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(
                socket.getOutputStream(), true);

            new Thread(this::listen).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        try {
            String message;

            while ((message = in.readLine()) != null) {
                listener.onMessageReceived(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        out.println(message);
    }
}
