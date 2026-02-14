package com.maximo.flappybird.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    public static final AssetManager manager = new AssetManager();

    public static void load() {

        manager.load("bg.png", Texture.class);
        manager.load("ground.png", Texture.class);
        manager.load("tube.png", Texture.class);

        manager.load("bird1.png", Texture.class);
        manager.load("bird2.png", Texture.class);
        manager.load("bird3.png", Texture.class);

        manager.finishLoading(); // espera a que cargue todo
    }

    public static void dispose() {
        manager.dispose();
    }
}
