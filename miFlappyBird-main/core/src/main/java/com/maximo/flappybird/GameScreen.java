package com.maximo.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.maximo.flappybird.network.GameClient;
import com.maximo.flappybird.network.NetworkListener;
import com.maximo.flappybird.network.MessageParser;
import com.maximo.flappybird.sprites.Tube;
import com.badlogic.gdx.math.Rectangle;


public class GameScreen implements Screen, NetworkListener {

    private final Main game;

    private Texture bird;
    private Texture otherBird;
    private boolean isPlayerOne = true; // después lo asigna el servidor
    private float birdY = 300;
    private float otherPlayerY = 300;
    private float lastSentY = 0;
    private OrthographicCamera camera;
    private boolean playerAssigned = false;




    private float velocity = 0;
    private float gravity = 0.5f;

    private boolean gameStarted = false;
    private int estadoJuego = 0; // 0 = esperando, 1 = jugando, 2 = game over
    private Texture background;
    private Rectangle birdBounds;


    private GameClient client;
    private BitmapFont font;

    private int score = 0;
    private int otherPlayerScore = 0;

    private Array<Tube> tubes;
    private static final int TUBE_COUNT = 4;
    private static final float TUBE_SPACING = 300;
    private static final float TUBE_SPEED = 200;
    private boolean gane = false;




    public GameScreen(Main game) {
        this.game = game;

        bird = new Texture("bird1.png");
        otherBird = new Texture("red-bird1.png");
        background = new Texture("bg.png");
        tubes = new Array<>();



        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);


        birdBounds = new Rectangle(100, birdY, 50, 50);


        for (int i = 0; i < TUBE_COUNT; i++) {
            tubes.add(new Tube(600 + i * TUBE_SPACING));
        }


        //Aca se conecta el cliente al servidor, donde envia el host, el puerto y el listener
        client = new GameClient("192.168.0.52", 5000, this);

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

            if (gane) {
                font.draw(game.batch, "GANASTE!", 250, 300);
            } else {
                font.draw(game.batch, "PERDISTE!", 250, 300);
            }

            game.batch.end();
            return;
        }


        // Juego activo
        if (estadoJuego == 1) {
            for (int i = 0; i < tubes.size; i++) {

                Tube tube = tubes.get(i);

                // mover tubos
                tube.getPosTopTube().x -= TUBE_SPEED * delta;
                tube.getPosBottomTube().x -= TUBE_SPEED * delta;

                tube.getBoundsTop().setPosition(
                    tube.getPosTopTube().x,
                    tube.getPosTopTube().y
                );

                tube.getBoundsBottom().setPosition(
                    tube.getPosBottomTube().x,
                    tube.getPosBottomTube().y
                );

                // dibujar
                game.batch.draw(
                    tube.getTexture(),
                    tube.getPosTopTube().x,
                    tube.getPosTopTube().y
                );

                game.batch.draw(
                    tube.getTexture(),
                    tube.getPosBottomTube().x,
                    tube.getPosBottomTube().y
                );

                // reposicionar si sale de pantalla
                if (tube.getPosTopTube().x < -tube.getTexture().getWidth()) {
                    tube.reposition(
                        tube.getPosTopTube().x + TUBE_COUNT * TUBE_SPACING
                    );
                    tube.setScored(false);

                }
                if (tube.getBoundsTop().overlaps(birdBounds) ||
                    tube.getBoundsBottom().overlaps(birdBounds)) {

                    estadoJuego = 2;
                }
                if (!tube.isScored() &&
                    tube.getPosTopTube().x + tube.getTexture().getWidth() < 100) {

                    score++;
                    tube.setScored(true);
                }


            }


            if (Gdx.input.justTouched()) {
                velocity = -10;

            }

            velocity += gravity;
            birdY -= velocity;

            birdBounds.setPosition(100, birdY);
            if (birdY <= 0) {
                estadoJuego = 2;
            }



            /*Enviamos el estado completo del jugador en cada frame para que el servidor
            tenga siempre la información actualizada*/
            String alive = (estadoJuego == 2) ? "0" : "1";

            String data =
                (int) birdY + "," +
                    alive + "," +
                    score;

            client.send(data);

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

        Gdx.app.postRunnable(() -> {

            String[] parts = message.split("\\|");

            if (parts[0].startsWith("YOU")) {
                String myColor = parts[0].split(":")[1];
                isPlayerOne = myColor.equals("BLUE");
            }

            if (parts.length > 1 && parts[1].startsWith("START")) {

                boolean started = parts[1].split(":")[1].equals("1");
                gameStarted = started;

                if (started && estadoJuego == 0) {
                    estadoJuego = 1; // 🔥 ACTIVA EL JUEGO
                }
            }

            if (message.startsWith("GAMEOVER")) {

                String winnerColor = message.split(":")[1];

                if ((isPlayerOne && winnerColor.equals("BLUE")) ||
                    (!isPlayerOne && winnerColor.equals("RED"))) {

                    gane = true;

                } else {

                    gane = false;
                }

                estadoJuego = 2;
                gameStarted = false;
                return;
            }

            for (int i = 2; i < parts.length; i++) {

                if (parts[i].startsWith("P")) {

                    String playerData = parts[i].split(":")[1];
                    String[] values = playerData.split(",");

                    int y = Integer.parseInt(values[0]);
                    boolean alive = values[1].equals("1");
                    int scoreReceived = Integer.parseInt(values[2]);
                    String color = values[3];

                    if ((isPlayerOne && color.equals("BLUE")) ||
                        (!isPlayerOne && color.equals("RED"))) {

                        score = scoreReceived;

                        if (!alive && estadoJuego != 2) {
                            gane = false;   // 🔥 si yo muero, pierdo
                            estadoJuego = 2;
                            gameStarted = false;
                        }
                    } else {

                        otherPlayerY = y;
                        otherPlayerScore = scoreReceived;

                        if (!alive && estadoJuego != 2) {
                            gane = true;   // 🔥 si el otro muere, yo gano
                            estadoJuego = 2;
                            gameStarted = false;
                        }




                    }
                }
            }
        });
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
