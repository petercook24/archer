package org.academiadecodigo.hackathon.archer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.academiadecodigo.hackathon.archer.screens.GameScreen;
import org.academiadecodigo.hackathon.archer.screens.StartScreen;

public class ArcherGame extends Game {

	public SpriteBatch batch;

    //Virtual Screen size and Box2D Scale(Pixels Per Meter)
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 240;
    public static final float PPM = 32;

//	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new StartScreen(this));
		/*setScreen(new GameScreen(this));*/
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
