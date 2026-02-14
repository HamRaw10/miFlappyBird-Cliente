package com.maximo.flappybird.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.maximo.flappybird.utils.Constants;

public class MenuState {

    private Texture background;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private BitmapFont font;


    private boolean startGame;

    public MenuState() {

        background = new Texture("bg.png");

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4);
        titleFont.setColor(Color.WHITE);

        textFont = new BitmapFont();
        textFont.getData().setScale(2);
        textFont.setColor(Color.WHITE);

        font = new BitmapFont();

        startGame = false;
    }

    public void update() {

        if(Gdx.input.justTouched()) {

            startGame = true;
        }
    }

    public void render(SpriteBatch batch) {

        batch.draw(background, 0, 0);

        titleFont.draw(batch, "FLAPPY BIRD", 100, 600);


        GlyphLayout layout = new GlyphLayout(font, "Click para iniciar");
        font.draw(batch,
            layout,
            (Constants.WORLD_WIDTH - layout.width) / 2,
            Constants.WORLD_HEIGHT / 2);

    }

    public boolean shouldStartGame() {

        return startGame;
    }

    public void dispose() {

        background.dispose();
        titleFont.dispose();
        textFont.dispose();
    }
}
