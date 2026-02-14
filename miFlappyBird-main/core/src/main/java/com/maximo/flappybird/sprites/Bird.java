package com.maximo.flappybird.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.maximo.flappybird.utils.Assets;
import com.maximo.flappybird.utils.Constants;

public class Bird {

    private Vector3 position;
    private Vector3 velocity;
    private Rectangle bounds;

    private Animation<TextureRegion> birdAnimation;
    private float stateTime;

    public Bird(int x, int y) {

        position = new Vector3(x, y, 0);
        velocity = new Vector3(0, 0, 0);

        Array<TextureRegion> frames = new Array<>();

        frames.add(new TextureRegion(Assets.manager.get("bird1.png", Texture.class)));
        frames.add(new TextureRegion(Assets.manager.get("bird2.png", Texture.class)));
        frames.add(new TextureRegion(Assets.manager.get("bird3.png", Texture.class)));

        birdAnimation = new Animation<>(0.1f, frames);
        stateTime = 0f;

        // usamos el primer frame para calcular tamaño
        TextureRegion firstFrame = birdAnimation.getKeyFrame(0);

        bounds = new Rectangle(
            position.x,
            position.y,
            firstFrame.getRegionWidth(),
            firstFrame.getRegionHeight()
        );
    }

    public void update(float dt) {

        stateTime += dt;

        velocity.add(0, Constants.GRAVITY, 0);
        position.add(0, velocity.y * dt, 0);

        bounds.setPosition(position.x, position.y);

        if(position.y < 0) {
            position.y = 0;
            velocity.y = 0;
        }
    }

    public void jump() {
        velocity.y = 250;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public TextureRegion getCurrentFrame() {
        return birdAnimation.getKeyFrame(stateTime, true);
    }
}
