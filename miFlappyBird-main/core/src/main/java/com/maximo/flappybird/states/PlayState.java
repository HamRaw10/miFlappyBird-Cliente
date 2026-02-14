package com.maximo.flappybird.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.maximo.flappybird.sprites.Bird;
import com.maximo.flappybird.sprites.Tube;
import com.maximo.flappybird.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;

public class PlayState {

    private Bird bird;
    private Texture background;
    private Texture ground;

    private ArrayList<Tube> tubes;
    private HashSet<Tube> scoredTubes;

    private int score;
    private BitmapFont font;
    private boolean gameOver;


    public PlayState() {

        // crear pájaro
        bird = new Bird(100, 400);

        // cargar texturas
        background = new Texture("bg.png");
        ground = new Texture("ground.png");

        // lista de tubos
        tubes = new ArrayList<>();

        // control de puntaje por tubo
        scoredTubes = new HashSet<>();
        gameOver = false;


        // crear tubos iniciales
        for (int i = 0; i < Constants.TUBE_COUNT; i++) {

            tubes.add(new Tube(400 + i * 300));
        }

        // inicializar puntaje
        score = 0;

        // crear fuente
        font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.WHITE);
    }

    public void update(float dt) {

        // input salto
        if (Gdx.input.justTouched()) {

            bird.jump();
        }

        // actualizar pájaro
        bird.update(dt);

        // actualizar tubos
        for (Tube tube : tubes) {

            tube.getPosTopTube().x -= Constants.TUBE_SPEED * dt;
            tube.getPosBottomTube().x -= Constants.TUBE_SPEED * dt;

            tube.getBoundsTop().setPosition(
                tube.getPosTopTube().x,
                tube.getPosTopTube().y
            );

            tube.getBoundsBottom().setPosition(
                tube.getPosBottomTube().x,
                tube.getPosBottomTube().y
            );

            // sumar punto
            if (tube.getPosTopTube().x + tube.getTexture().getWidth()
                < bird.getPosition().x
                && !scoredTubes.contains(tube)) {

                score++;
                scoredTubes.add(tube);
            }

            // reposicionar tubo
            if (tube.getPosTopTube().x < -tube.getTexture().getWidth()) {

                tube.reposition(
                    tube.getPosTopTube().x + Constants.TUBE_COUNT * 300
                );

                scoredTubes.remove(tube);
            }

            // colisión con tubos
            if (tube.getBoundsTop().overlaps(bird.getBounds())
                || tube.getBoundsBottom().overlaps(bird.getBounds())) {

                resetGame();
                return;
            }
        }

        // colisión con suelo
        if (bird.getPosition().y <= ground.getHeight()) {

            resetGame();
        }
    }

    private void resetGame() {

        gameOver = true;
    }
    public boolean isGameOver() {

        return gameOver;
    }

    public int getScore() {

        return score;
    }


    public void render(SpriteBatch batch) {

        // dibujar fondo
        batch.draw(background, 0, 0);

        // dibujar pájaro
        batch.draw(
            bird.getCurrentFrame(),
            bird.getPosition().x,
            bird.getPosition().y
        );


        // dibujar tubos
        for (Tube tube : tubes) {

            batch.draw(
                tube.getTexture(),
                tube.getPosTopTube().x,
                tube.getPosTopTube().y
            );

            batch.draw(
                tube.getTexture(),
                tube.getPosBottomTube().x,
                tube.getPosBottomTube().y
            );
        }

        // dibujar suelo
        batch.draw(ground, 0, 0);

        // dibujar puntaje
        GlyphLayout layout = new GlyphLayout(font, "Score: " + score);
        font.draw(batch,
            layout,
            (Constants.WORLD_WIDTH - layout.width) / 2,
            Constants.WORLD_HEIGHT - 20);


    }

    public void dispose() {


        background.dispose();
        ground.dispose();
        font.dispose();

        for (Tube tube : tubes) {

            tube.dispose();
        }
    }
}
