package com.maximo.flappybird.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.maximo.flappybird.utils.Constants;

public class Tube {

    private Texture texture;
    private Vector2 posTopTube;
    private Vector2 posBottomTube;

    private Rectangle boundsTop;
    private Rectangle boundsBottom;

    public Tube(float x) {

        texture = new Texture("tube.png");

        float availableHeight = Constants.WORLD_HEIGHT
            - 2 * Constants.TUBE_MIN_HEIGHT
            - Constants.TUBE_GAP;

        float y = Constants.TUBE_MIN_HEIGHT
            + MathUtils.random(availableHeight);


        posTopTube = new Vector2(x, y + Constants.TUBE_GAP);
        posBottomTube = new Vector2(x, y - texture.getHeight());

        boundsTop = new Rectangle(
            posTopTube.x,
            posTopTube.y,
            texture.getWidth(),
            texture.getHeight()
        );

        boundsBottom = new Rectangle(
            posBottomTube.x,
            posBottomTube.y,
            texture.getWidth(),
            texture.getHeight()
        );
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2 getPosTopTube() {
        return posTopTube;
    }

    public Vector2 getPosBottomTube() {
        return posBottomTube;
    }

    public Rectangle getBoundsTop() {
        return boundsTop;
    }

    public Rectangle getBoundsBottom() {
        return boundsBottom;
    }

    public void reposition(float x) {

        float availableHeight = Constants.WORLD_HEIGHT
            - 2 * Constants.TUBE_MIN_HEIGHT
            - Constants.TUBE_GAP;

        float y = Constants.TUBE_MIN_HEIGHT
            + MathUtils.random(availableHeight);


        posTopTube.set(x, y + Constants.TUBE_GAP);
        posBottomTube.set(x, y - texture.getHeight());

        boundsTop.setPosition(posTopTube.x, posTopTube.y);
        boundsBottom.setPosition(posBottomTube.x, posBottomTube.y);
    }

    public void dispose() {
        texture.dispose();
    }
}
