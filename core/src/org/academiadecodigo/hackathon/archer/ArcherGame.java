package org.academiadecodigo.hackathon.archer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.academiadecodigo.hackathon.archer.screens.GameScreen;

public class ArcherGame extends Game {

	public SpriteBatch batch;
    public static AssetManager manager;

    //Virtual Screen size and Box2D Scale(Pixels Per Meter)
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 240;
    public static final float PPM = 32;

//	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/");
		setScreen(new GameScreen((this)));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}
}
