package com.maximo.flappybird.network;

import java.net.*;

public class GameClient {

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    private NetworkListener listener;

    public GameClient(String host, int port, NetworkListener listener) {
        this.listener = listener;
        this.serverPort = port;

        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(host);

            new Thread(this::listen).start();

            send("HELLO");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void listen() {
        try {
            byte[] buffer = new byte[1024];
            //El cliente espera paquetes del server, y al recibirlos los traduce y envia
            // al NetworkListener para que el juego procese esos paquetes*/
            while (true) {

                DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                String message = new String(
                    packet.getData(),
                    0,
                    packet.getLength()
                );

                listener.onMessageReceived(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {

            byte[] data = message.getBytes();

            DatagramPacket packet =
                new DatagramPacket(
                    data,
                    data.length,
                    serverAddress,
                    serverPort
                );

            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
