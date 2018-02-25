/**
*   Title: TennisBall.java
*   Description: Creates a sphere textured to appear as a tennis ball.
*   Date: February 11, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: none
*
*/ 

import com.sun.j3d.utils.geometry.*;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.image.*;

public class TennisBall
{
    private static final float radius = 0.654f;  // radius is 100x normal tennis ball
    
    private BranchGroup bg;  // basis of the scene graph for all geometry to be added
    private TransformGroup ballPosition;
    private TransformGroup ballRotation;
    
    TennisBall (Canvas3D observer) {

        bg = new BranchGroup();
        ballPosition = new TransformGroup();
        ballRotation = new TransformGroup();
        ballPosition.setCapability(ballPosition.ALLOW_TRANSFORM_WRITE);
        ballRotation.setCapability(ballRotation.ALLOW_TRANSFORM_WRITE);
        
        // create and texture a sphere object to look like a tennis ball
        Appearance ballAppear = new Appearance();
        ballAppear.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 0f, 0f), 1));
        
        // get the texture image
        ImageComponent2D tennis = getTexImage("TennisBallz.gif", observer);
        Texture2D texture = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB, tennis.getWidth(), tennis.getHeight());
        texture.setImage(0, tennis);
        texture.setEnable(true);
        ballAppear.setTexture(texture);  // set the texture
        
        // create and adjust the shape
        Sphere ball = new Sphere(radius, Sphere.GENERATE_TEXTURE_COORDS, ballAppear);  
        Transform3D t3dSphere2 = new Transform3D();
        t3dSphere2.setTranslation(new Vector3f(0f, 15f - radius, 0f));
        ballPosition.setTransform(t3dSphere2);
        ballPosition.addChild(ballRotation);
        ballRotation.addChild(ball);
        
        // add to content branch graph
        bg.addChild(ballPosition);

    } // end of constructor
    
    // returns the BranchGroup for use in the content branch of the scene graph
    public BranchGroup getShapes () {        
        return bg;
    } // end of method getShapes()
    
    // returns the TransformGroup assoiciated with the position of the ball
    public TransformGroup getSphere () {
        return ballPosition;
    } // end of method getSphere()    

    // returns the TrandformGroup associated with the rotation of the ball
    public TransformGroup getRot () {
        return ballRotation;
    } // end of method getRot()
    
    // returns the radius of the sphere to use in animation calculations    
    public float getRadius() {
        return radius;
    } // end of method getRadius()
    
    // loads the specified image file for use in texturing the ball
    private ImageComponent2D getTexImage(String filename, Canvas3D observer) {

        // load the image file
        TextureLoader loader = new TextureLoader(filename, observer);
        ImageComponent2D image = loader.getImage();

        // check to see if image is loaded
        if(image == null) { System.out.println("load failed for texture: "+filename); }

        return image;
    }// end of method getTexImage()
} // end of class TennisBall
