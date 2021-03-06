package org.academiadecodigo.hackathon.archer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.academiadecodigo.hackathon.archer.ArcherGame;
import org.academiadecodigo.hackathon.archer.BodyWorldCreator;
import org.academiadecodigo.hackathon.archer.scenes.Hud;
import org.academiadecodigo.hackathon.archer.sprites.Animatable;
import org.academiadecodigo.hackathon.archer.sprites.archer.Archer;
import org.academiadecodigo.hackathon.archer.sprites.enemies.Skeleton;
import org.academiadecodigo.hackathon.archer.sprites.projectile.Projectile;
import org.academiadecodigo.hackathon.archer.tools.ArcherInputProcessor;

import java.util.ArrayList;

import static org.academiadecodigo.hackathon.archer.ArcherGame.manager;

public class GameScreen implements Screen {

    public static final float PROJECTILE_VELOCITY = 6f;
    public ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>();
    private Music music;
    //Map and camera related stuff
    private BodyWorldCreator creator;
    private OrthographicCamera gamecam;
    private Viewport viewPort;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //World, game and player stuff
    private ArcherGame game;
    private World world;
    private Hud hud;
    private Archer archer;
    private ArcherInputProcessor inputProcessor;


    public GameScreen(ArcherGame archerGame) {

        Box2D.init();

        this.game = archerGame;
        initGameElements();

    }

    private void initGameElements() {

        world = new World(new Vector2(0, 0), true);
        archer = new Archer(this);
        hud = new Hud(game.batch);

        gamecam = new OrthographicCamera();
        viewPort = new FitViewport(game.V_WIDTH / game.PPM, game.V_HEIGHT / game.PPM, gamecam);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/level1.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, 1 / game.PPM);
        gamecam.position.set(viewPort.getWorldWidth() / 2, viewPort.getWorldHeight() / 2, 0);

        creator = new BodyWorldCreator(this);

        inputProcessor = new ArcherInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        music = Gdx.audio.newMusic(Gdx.files.internal("audio/music/in_light_of_darkness.mp3"));
        music.setLooping(true);
        music.play();
    }

    @Override
    public void show() {
    }

    public void update(float dt) {

        handleInput();

        world.step(1 / 60f, 6, 2);

        archer.update(dt);
        setActiveEnemies();
        hud.update(dt);

        gamecam.position.x = archer.body.getPosition().x;
        gamecam.position.y = archer.body.getPosition().y;
        gamecam.update();
        renderer.setView(gamecam);

        checkEnemyCollisions(dt);
        checkIfArcherDead(dt);
        checkIfTimeUp();

    }

    private void checkIfTimeUp() {
        if (hud.isTimeUp()) {
            endGame();
        }
    }


    private void checkIfArcherDead(float dt) {

        for (Skeleton skeleton : skeletons) {

            if (!skeleton.isDead()) {

                skeleton.update(dt);

                CircleShape projectileShape = (CircleShape) archer.body.getFixtureList().get(0).getShape();
                CircleShape skeletonShape = (CircleShape) skeleton.enemyBody.getFixtureList().get(0).getShape();

                float xD = archer.body.getPosition().x - skeleton.enemyBody.getPosition().x;      // delta x
                float yD = archer.body.getPosition().y - skeleton.enemyBody.getPosition().y;      // delta y
                float sqDist = xD * xD + yD * yD;  // square distance
                boolean collision = sqDist <= (projectileShape.getRadius() + skeletonShape.getRadius())
                        * (projectileShape.getRadius() + skeletonShape.getRadius());

                if (collision) {
//                        manager.get("audio/sounds/zombie-hit.wav", Sound.class).play();
                    archer.body.setActive(false);
                    endGame();
                    break;
                }
            }
        }
    }

    public void endGame() {
        music.stop();
        game.setScreen(new EndScreen(game));
    }


    private void checkEnemyCollisions(float dt) {

        boolean allDead = true;

        for (Skeleton skeleton : skeletons) {

            if (!skeleton.isDead()) {

                allDead = false;

                skeleton.update(dt);

                for (Projectile projectile : archer.projectiles) {

                    CircleShape projectileShape = (CircleShape) projectile.body.getFixtureList().get(0).getShape();
                    CircleShape skeletonShape = (CircleShape) skeleton.enemyBody.getFixtureList().get(0).getShape();

                    float xD = projectile.body.getPosition().x - skeleton.enemyBody.getPosition().x;      // delta x
                    float yD = projectile.body.getPosition().y - skeleton.enemyBody.getPosition().y;      // delta y
                    float sqDist = xD * xD + yD * yD;  // square distance
                    boolean collision = sqDist <= (projectileShape.getRadius() + skeletonShape.getRadius())
                            * (projectileShape.getRadius() + skeletonShape.getRadius());

                    if (collision) {
                        manager.get("audio/sounds/zombie-hit.wav", Sound.class).play();
                        archer.projectiles.removeValue(projectile, true);
                        skeleton.setDead(true);
                        skeleton.setRegion(402, 52, 48, 48); // MEGA WORKAROUND DE DEATHS LOL
                        projectile.body.setTransform(1000000f, 1000000f, projectile.body.getAngle());
                        skeleton.enemyBody.setTransform(1000000f, 1000000f, projectile.body.getAngle());
                        Hud.addScore(skeleton.getPoints());
                        break;
                    }
                }
            }
        }

        if (allDead) {
            endGame();
        }

    }

    private void setActiveEnemies() {

        for (Skeleton skeleton : skeletons) {
            float enemyPosX = skeleton.getBody().getPosition().x;
            float enemyPosY = skeleton.getBody().getPosition().y;
            float archerPosX = archer.body.getPosition().x;
            float archerPosY = archer.body.getPosition().y;
            double distanceDiff = ((Math.pow(archerPosX - enemyPosX, 2) + Math.pow(archerPosY - enemyPosY, 2)));

            if (distanceDiff < 35) {
                skeleton.getBody().setActive(true);
            }
        }

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

        drawMovables();
        drawHud();
    }


    private void drawHud() {
        game.getBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void drawMovables() {

        game.getBatch().setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        for (Skeleton skeleton : skeletons) {
            skeleton.draw(game.batch);
        }

        archer.draw(game.batch);

        for (Projectile p : archer.projectiles) {
            p.draw(game.batch);
        }

        game.batch.end();
    }

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void resize(int width, int height) {

        viewPort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {


    }

    public void handleInput() {

        if (inputProcessor.aKey && !inputProcessor.dKey) {
            archer.velocityVector.x = -archer.speed;
        } else if (inputProcessor.dKey && !inputProcessor.aKey) {
            archer.velocityVector.x = archer.speed;
        } else {
            archer.velocityVector.x = 0;
        }

        if (inputProcessor.wKey) {
            archer.velocityVector.y = archer.speed;
        } else if (inputProcessor.sKey) {
            archer.velocityVector.y = -archer.speed;
        } else {
            archer.velocityVector.y = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            archer.fire(new Vector2(PROJECTILE_VELOCITY, 0), true, Animatable.Orientation.EAST);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            archer.fire(new Vector2(-PROJECTILE_VELOCITY, 0), false, Animatable.Orientation.WEST);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            archer.fire(new Vector2(0, PROJECTILE_VELOCITY), true, Animatable.Orientation.NORTH);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            archer.fire(new Vector2(0, -PROJECTILE_VELOCITY), true, Animatable.Orientation.SOUTH);
        }

        archer.body.setLinearVelocity(archer.velocityVector);
    }

    public Archer getArcher() {
        return archer;
    }

    public World getWorld() {
        return world;
    }


}