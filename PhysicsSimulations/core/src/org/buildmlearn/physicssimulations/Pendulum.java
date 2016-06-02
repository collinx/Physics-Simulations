package org.buildmlearn.physicssimulations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class Pendulum extends SimulationType {

    //NOU
    /** the camera **/
    private com.badlogic.gdx.graphics.OrthographicCamera camera;
    /** the immediate mode renderer to output our debug drawings **/
    private ShapeRenderer renderer;
    /** box2d debug renderer **/
    private Box2DDebugRenderer debugRenderer;


    LineActor line3Actor;

    private Skin skin;
    private TextureAtlas atlas;
    private Stage stage;
    private Table table;
    private BallActor ballActor;
    World b2world;

    Label lengthLabel;
    Label angleLabel;
    Slider lengthSlider;
    private Texture ballTexture;

    Button button;

    float W;
    float H;

    @Override
    public void create() {

        //NOU
        //camera = new OrthographicCamera(48, 32);
        //camera.position.set(0, 0, 0);
        // next we setup the immediate mode renderer
        renderer = new ShapeRenderer();
        // next we create the box2d debug renderer
        debugRenderer = new Box2DDebugRenderer();


        atlas = new TextureAtlas(Gdx.files.internal("data/ui-blue.atlas"));

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        skin.addRegions(atlas);

        stage = new Stage(new ScreenViewport());
        camera = (OrthographicCamera) stage.getCamera();


        W = Gdx.graphics.getWidth();
        H = Gdx.graphics.getHeight();


        button = new Button(skin);
        button.setPosition(W/4, H-50);

        ballTexture = new Texture(Gdx.files.internal("ball.png"), true);
        ballTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        final TextureRegion ballRegion = new TextureRegion(ballTexture);
        ballActor = new BallActor(ballRegion);
        ballActor.addListener(new DragListener() {
            public void drag(InputEvent event, float x, float y, int pointer) {
                ballActor.moveBy(x - ballActor.getWidth() / 2, y - ballActor.getHeight() / 2);
            }
        });
        this.world();
        ballActor.scaleBy(4 / 18f);

        angleLabel = new Label("Angle: 30°", skin);
        Label energyLabel = new Label("Energy", skin);

        lengthLabel = new Label("Length: 0.5 m", skin);
        lengthLabel.setColor(Color.WHITE);

        final Label massLabel = new Label("Mass: 1.0 kg", skin);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.knob = skin.getDrawable("knob_03");
        sliderStyle.background = skin.getDrawable("slider_back_hor");

        lengthSlider = new Slider(0, 15, 1, false, sliderStyle);
        lengthSlider.setAnimateDuration(0);
        lengthSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                //Gdx.app.log("UITest", "slider: " + lengthSlider.getValue());
                lengthLabel.setText("Length: "+ (0.5f + lengthSlider.getValue() / 10f) + " m");
            }
        });

        final Slider massSlider = new Slider(2, 18, 1, false, sliderStyle);
        massSlider.setAnimateDuration(0);
        massSlider.setValue(4);
        massSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                //Gdx.app.log("UITest", "slider: " + massSlider.getValue());
                massLabel.setText("Mass: " + massSlider.getValue() / 2f + " kg");
                ballActor.scaleBy(massSlider.getValue() / 18f);
            }
        });
        ballActor.debug();

        LineActor line1Actor = new LineActor();
        line1Actor.setColor(Color.RED);
        LineActor line2Actor = new LineActor();
        line2Actor.setColor(Color.BLUE);
        line3Actor = new LineActor();
        line3Actor.setColor(Color.GREEN);
        LineActor line4Actor = new LineActor();
        line4Actor.setColor(Color.BLACK);

        table = new Table();
        table.setDebug(false);
        table.right().bottom().padRight(H/10).padBottom(H/10);
        table.setFillParent(true);
        //table.row().expandX();

        table.add(lengthLabel).padRight(10).align(Align.left);
        table.add(lengthSlider).width(W / 4).colspan(3);

        table.row().padTop(30);
        table.add(massLabel).padRight(10).align(Align.left);
        table.add(massSlider).width(W / 4).colspan(3);

        table.row().padTop(30);
        table.add(angleLabel).padRight(10).align(Align.left);

        table.row().padTop(30);
        table.add(energyLabel);
        table.add(line1Actor).height(H/4).width(20).align(Align.center|Align.bottom);
        table.add(line2Actor).height(H/4).width(20).align(Align.center|Align.bottom);
        table.add(line3Actor).width(20).align(Align.center|Align.bottom);

        table.row();
        table.add();
        table.add(line4Actor).height(10).colspan(3).fillX();

        table.row().padTop(10);
        table.add();
        table.add(new Label("KE", skin)).align(Align.center);
        table.add(new Label("PE", skin)).align(Align.center);
        table.add(new Label("TME", skin)).align(Align.center);

        stage.addActor(table);
        stage.addActor(ballActor);
        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);

    }

    void world() {
        b2world = new World(new Vector2(0, -9.81f), true);

        //BALL
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(ballActor.getX() + ballActor.getWidth()/2f,
                ballActor.getY() + ballActor.getHeight()/2f));
        Body body = b2world.createBody(bodyDef);
        body.setType(BodyDef.BodyType.DynamicBody);
        ballActor.body = body;

        PolygonShape polygonShape = new PolygonShape();
        float halfWidth = ballActor.getWidth() / 100f;
        float halfHeight = ballActor.getHeight() / 100f;
        polygonShape.setAsBox(halfWidth, halfHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 50;
        fixtureDef.restitution = 0.5f;
        //fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        //PIN POINT
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.position.set(new Vector2(W/4, H-50));
        //bodyDef2.position.set(new Vector2(button.getX(), button.getY()));
        //bodyDef2.position.add(button.getX(), button.getY());
        Body body2 = b2world.createBody(bodyDef2);
        body2.setType(BodyDef.BodyType.KinematicBody);

        PolygonShape polygonShape2 = new PolygonShape();
        float halfWidth2 = button.getWidth();
        float halfHeight2 = button.getHeight();
        polygonShape2.setAsBox(halfWidth2, halfHeight2);

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = polygonShape2;
//        fixtureDef2.density = 50;
//        fixtureDef2.restitution = 0.5f;
//        fixtureDef2.friction = 0.5f;
        body2.createFixture(fixtureDef2);
        polygonShape2.dispose();


        RopeJointDef ropeJointDef = new RopeJointDef();
        ropeJointDef.bodyA = body;
        ropeJointDef.bodyB = body2;
        ropeJointDef.localAnchorA.set(1, 1);
        ropeJointDef.localAnchorB.set(0, 0);
        ropeJointDef.maxLength = 300f;

        b2world.createJoint(ropeJointDef);

    }

    @Override
    public void resize(int width, int height) {
        camera.update();
//        camera.viewportHeight = H / 160;
//        camera.viewportWidth = W / 160;
//        camera.position.set(camera.viewportWidth / 2,
//                camera.viewportHeight / 2, 0);
//        camera.update();


        stage.getViewport().update(width, height, true);
        this.table.setFillParent(true);
        this.table.invalidate();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f);

        camera = (OrthographicCamera) stage.getCamera();
        camera.update();
        debugRenderer.render(b2world, camera.combined);
        //stage.getBatch().setProjectionMatrix(camera.combined);

        line3Actor.setHeight(lengthSlider.getValue()*10);
        //update angle
        double angle;
        float op = ballActor.body.getPosition().x - button.getX();
        float ad = button.getY() - ballActor.body.getPosition().y;
        angle = Math.toDegrees(Math.atan(op/ad));
        angleLabel.setText(String.format("Angle: %.2f°", angle));

        b2world.step(delta, 8, 3);

        DragListener dragListener = (DragListener) ballActor.getListeners().get(0);
        if (!dragListener.isDragging())
            ballActor.updateImage();
        else
            ballActor.updateBody();

        stage.act(delta);
        stage.draw();


    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
