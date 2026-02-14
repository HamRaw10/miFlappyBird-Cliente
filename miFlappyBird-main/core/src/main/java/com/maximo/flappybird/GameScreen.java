package com.maximo.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maximo.flappybird.network.GameClient;
import com.maximo.flappybird.network.NetworkListener;
import com.maximo.flappybird.network.MessageParser;

public class GameScreen implements Screen, NetworkListener {

    private final Main game;

    private Texture bird;
    private Texture otherBird;
    private float birdY = 300;
    private float otherPlayerY = 300;

    private float velocity = 0;
    private float gravity = 0.5f;

    private boolean gameStarted = false;
    private int estadoJuego = 0; // 0 = esperando, 1 = jugando, 2 = game over

    private GameClient client;
    private BitmapFont font;

    public GameScreen(Main game) {
        this.game = game;

        bird = new Texture("bird1.png");
        otherBird = new Texture("bird1.png");

        font = new BitmapFont();

        // 🔌 Conexión al servidor
        client = new GameClient("localhost", 9999, this);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        // 🔵 PANTALLA DE ESPERA
        if (!gameStarted) {
            font.draw(game.batch, "Esperando jugadores...", 200, 300);
            game.batch.end();
            return;
        }

        // 🔵 GAME OVER
        if (estadoJuego == 2) {
            font.draw(game.batch, "GAME OVER", 250, 300);
            game.batch.end();
            return;
        }

        // 🔵 JUEGO ACTIVO
        if (estadoJuego == 1) {

            // Detectar salto
            if (Gdx.input.justTouched()) {
                velocity = -10;
                client.send("JUMP"); // 🔥 ENVÍA EVENTO
            }

            // Aplicar física
            velocity += gravity;
            birdY -= velocity;

            // Enviar posición cada frame
            client.send("PLAYER:" + birdY); // 🔥 SINCRONIZA POSICIÓN
        }

        // Dibujar jugador local
        game.batch.draw(bird, 100, birdY);

        // Dibujar jugador remoto
        game.batch.draw(otherBird, 400, otherPlayerY);

        game.batch.end();
    }

    // 🔵 MÉTODO QUE RECIBE MENSAJES DEL SERVIDOR
    @Override
    public void onMessageReceived(String message) {

        if (MessageParser.isStart(message)) {
            gameStarted = true;
            estadoJuego = 1;
        }

        if (MessageParser.isGameOver(message)) {
            estadoJuego = 2;
        }

        if (MessageParser.isPlayerPosition(message)) {
            otherPlayerY = MessageParser.getPlayerY(message);
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}

    @Override
    public void dispose() {
        bird.dispose();
        otherBird.dispose();
        font.dispose();
    }
}
