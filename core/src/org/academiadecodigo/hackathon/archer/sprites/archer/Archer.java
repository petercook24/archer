package org.academiadecodigo.hackathon.archer.sprites.archer;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.academiadecodigo.hackathon.archer.ArcherGame;
import org.academiadecodigo.hackathon.archer.screens.GameScreen;
import org.academiadecodigo.hackathon.archer.sprites.projectile.Projectile;

public class Archer extends Sprite {

    public World world;
    public Body body;
    public Vector2 velocityVector;
    private GameScreen gameScreen;
    private Array<Projectile> projectiles;

    public Archer(GameScreen gameScreen) {

        this.gameScreen = gameScreen;
        this.world = gameScreen.getWorld();

        defineArcher();

        projectiles = new Array<Projectile>();

    }


    private void init() {

    }



    private void defineArcher() {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(600 / ArcherGame.PPM, 600 / ArcherGame.PPM);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(5 / ArcherGame.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        velocityVector = new Vector2(0,0);

    }

    public void fire(Vector2 velocityVector, boolean fireRight) {
        projectiles.add(new Projectile(gameScreen, body.getPosition(), velocityVector, fireRight));
    }
}
