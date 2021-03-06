package org.buildmlearn.physicssimulations;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * The <code>BallActor</code> class extends Actor class by adding a {@link Body} object
 * @author  Costin Giorgian
 */
public class BallActor extends Actor {
    float unscaledWidth;
    float unscaledHeight;
    Image ballImage;
    Body body;

    BallActor(TextureRegion textureRegion) {
        this.ballImage = new Image(textureRegion);
        this.unscaledWidth = ballImage.getWidth() / 160;
        this.unscaledHeight = ballImage.getHeight()/ 160;
        setWidth(unscaledWidth);
        setHeight(unscaledHeight);
        setRotation(ballImage.getRotation());
    }

    /**
     * Update the {@link  #ballImage} after {@link #body} act
     */
    public void updateImage() {
        setPosition(body.getPosition().x - getWidth()/2f,
                body.getPosition().y - getHeight()/2f);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        setBounds(getX(), getY(), getWidth(), getHeight());

    }

    /**
     * Update the {@link  #body} after {@link #ballImage} was changed
     */
    public void updateBody() {
        body.setTransform(ballImage.getX() + getWidth()/2f,
                ballImage.getY() + getHeight()/2f,
                ballImage.getRotation() * MathUtils.degreesToRadians);
    }

    @Override
    public void scaleBy(float scale) {
        float w = ballImage.getWidth() / 2f;
        float h = ballImage.getHeight() / 2f;
        setSize(unscaledWidth * scale, unscaledHeight * scale);
        float ww = ballImage.getWidth() / 2f;
        float hh = ballImage.getHeight() / 2f;
        moveBy(w - ww, h - hh);
        if (body != null)
            updateBody();
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        ballImage.setSize(width, height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ballImage.draw(batch, parentAlpha);
    }

    @Override
    public void moveBy(float x, float y) {
        super.moveBy(x, y);
        ballImage.moveBy(x, y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        ballImage.setPosition(x, y);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        ballImage.setWidth(width);
        unscaledWidth = width;
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        ballImage.setHeight(height);
        unscaledHeight = height;
    }

    @Override
    public void rotateBy(float amountInDegrees) {
        super.rotateBy(amountInDegrees);
        ballImage.rotateBy(amountInDegrees);
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        ballImage.setOrigin(originX, originY);
    }

    @Override
    public void setRotation(float degrees) {
        super.setRotation(degrees);
        ballImage.setRotation(degrees);
    }
}
