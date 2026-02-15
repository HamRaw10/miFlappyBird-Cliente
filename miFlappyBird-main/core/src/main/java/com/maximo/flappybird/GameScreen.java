package com.maximo.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
    private boolean isPlayerOne = true; // después lo asigna el servidor
    private float birdY = 300;
    private float otherPlayerY = 300;
    private float lastSentY = 0;
    private OrthographicCamera camera;



    private float velocity = 0;
    private float gravity = 0.5f;

    private boolean gameStarted = false;
    private int estadoJuego = 0; // 0 = esperando, 1 = jugando, 2 = game over
    private Texture background;


    private GameClient client;
    private BitmapFont font;

    private int score = 0;
    private int otherPlayerScore = 0;


    public GameScreen(Main game) {
        this.game = game;

        bird = new Texture("bird1.png");
        otherBird = new Texture("red-bird1.png");
        background = new Texture("bg.png");


        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);


        // 🔌 Conexión al servidor
        client = new GameClient("localhost", 9999, this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Fondo
        game.batch.draw(background, 0, 0, 800, 480);


        // Espera
        if (!gameStarted) {
            font.draw(game.batch, "Esperando jugadores...", 200, 300);
            game.batch.end();
            return;
        }

        // Game Over
        if (estadoJuego == 2) {
            font.draw(game.batch, "GAME OVER", 250, 300);
            game.batch.end();
            return;
        }

        // Juego activo
        if (estadoJuego == 1) {

            if (Gdx.input.justTouched()) {
                velocity = -10;
                client.send("JUMP");
            }

            velocity += gravity;
            birdY -= velocity;

            /*Enviamos el estado completo del jugador en cada frame para que el servidor
            tenga siempre la información actualizada*/
            if (estadoJuego == 1) {

                String alive = (estadoJuego == 2) ? "0" : "1";

                String data =
                    "Y:" + (int) birdY +
                        "|ALIVE:" + alive +
                        "|SCORE:" + score;

                client.send(data);
            }

        }

        // Dibujar pájaros (UNA SOLA VEZ)
        if (isPlayerOne) {
            game.batch.draw(bird, 100, birdY, 50, 50);
            game.batch.draw(otherBird, 400, otherPlayerY, 50, 50);
        } else {
            game.batch.draw(otherBird, 100, birdY, 50, 50);
            game.batch.draw(bird, 400, otherPlayerY, 50, 50);
        }

        // Score
        font.draw(game.batch, "Score: " + score, 20, 460);
        font.draw(game.batch, "Enemy: " + otherPlayerScore, 600, 460);

        game.batch.end();
    }


    // 🔵 MÉTODO QUE RECIBE MENSAJES DEL SERVIDOR
    /*Separa el mensaje por "|"
Lee si el juego empezó
Recorre cada jugador
Actualiza:
Mi estado
Estado del enemigo
Eso es sincronización centralizada.*/
    @Override
    public void onMessageReceived(String message) {

        String[] parts = message.split("\\|");

        // START
        gameStarted = parts[0].split(":")[1].equals("1");

        for (int i = 1; i < parts.length; i++) {

            if (parts[i].startsWith("P")) {

                String playerData = parts[i].split(":")[1];
                String[] values = playerData.split(",");

                int y = Integer.parseInt(values[0]);
                boolean alive = values[1].equals("1");
                int scoreReceived = Integer.parseInt(values[2]);
                String color = values[3];

                // Si este jugador es el mismo color que yo
                if ((isPlayerOne && color.equals("BLUE")) ||
                    (!isPlayerOne && color.equals("RED"))) {

                    birdY = y;
                    score = scoreReceived;
                    estadoJuego = alive ? 1 : 2;

                } else {
                    otherPlayerY = y;
                    otherPlayerScore = scoreReceived;
                }
            }
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
