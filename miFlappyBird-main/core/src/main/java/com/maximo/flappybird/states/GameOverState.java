package com.maximo.flappybird.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.maximo.flappybird.utils.Constants;

public class GameOverState {

    private Texture background;

    private BitmapFont gameOverFont;
    private BitmapFont scoreFont;
    private BitmapFont restartFont;
    private BitmapFont font;


    private int score;

    private boolean restart;

    public GameOverState(int score) {

        this.score = score;

        background = new Texture("bg.png");

        gameOverFont = new BitmapFont();
        gameOverFont.getData().setScale(4);
        gameOverFont.setColor(Color.RED);

        scoreFont = new BitmapFont();
        scoreFont.getData().setScale(3);
        scoreFont.setColor(Color.WHITE);

        restartFont = new BitmapFont();
        restartFont.getData().setScale(2);
        restartFont.setColor(Color.WHITE);

        font = new BitmapFont();

        restart = false;
    }

    public void update() {

        if(Gdx.input.justTouched()) {

            restart = true;
        }
    }

    public void render(SpriteBatch batch) {

        batch.draw(background, 0, 0);

        GlyphLayout layout = new GlyphLayout(font, "Perdiste");
        GlyphLayout layoutLoseTitle = new GlyphLayout(scoreFont, "Score: " + score);
        GlyphLayout layoutRestart = new GlyphLayout(restartFont, "Click Para Reiniciar");
        font.draw(batch,
            layout,
            (Constants.WORLD_WIDTH - layout.width) / 2,
            Constants.WORLD_HEIGHT / 2 + 40);

        font.draw(batch,
            layoutLoseTitle,
            (Constants.WORLD_WIDTH - layoutLoseTitle.width) / 2,
            Constants.WORLD_HEIGHT / 2);



        restartFont.draw(batch,
            layoutRestart,
            (Constants.WORLD_WIDTH - layoutRestart.width) / 2,
            Constants.WORLD_HEIGHT / 2 - 60);


    }

    public boolean shouldRestart() {

        return restart;
    }

    public void dispose() {

        background.dispose();

        gameOverFont.dispose();
        scoreFont.dispose();
        restartFont.dispose();
    }
}
