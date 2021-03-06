/**
*   Title: Obstacle.java
*   Description: Creates an obstacle box that can be placed on the floor of the room structure.
*   Date: February 17, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: A brick texture is used to create the obstacle, otherwise it defaults to a red colour.  The obstacle size is not allowed to
*   be larger than what can fit in the room with the obstacle present at the center.  Additionally, the positioning of the object will
*   not allow it to be placed such that it is outside the room or overlaps the central character.
*
*/


import java.awt.Component;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class Obstacle {
    // instance variables - replace the example below with your own
    private static final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    
    private Component observer;
    private BranchGroup objRoot;
    private TransformGroup obsPosn;
    private Texture2D texImage;
    private Box obstacle;
    private BoundingBox bx;
    private float side, radius, xPosn, zPosn;
    private boolean positionSet;
    
    // constructor
    public Obstacle(float s, float r, Component o) {
        
        // initialise instance variables
        
        // check if side length is positive and falls within maximum space available
        if (s > (2.5f - (r / 2)) || s <= 0.0f) {
            side = 0.5f;
        } else {
            side = s;
        }
        radius = r;
        observer = o;
        
        objRoot = new BranchGroup();
        obsPosn = new TransformGroup();        
        
        xPosn = 1.0f;
        zPosn = 1.0f;
        positionSet = false;
        
        // set appearance of obstacle
        Appearance obsAppear = new Appearance();
        if (getTexImage("red_brick.jpg")) {
            obsAppear.setTexture(texImage);
        } else {
            ColoringAttributes ca = new ColoringAttributes(new Color3f(1.0f, 0.0f, 0.0f), 1);
            obsAppear.setColoringAttributes(ca);
        }  
        obstacle = new Box(side, side, side, Box.GENERATE_TEXTURE_COORDS, obsAppear);
    }  // end of constructor
    
    // retrieves the specified image file and sets it as a texture
    // returns true/false depending on the success of the loading process
    private boolean getTexImage(String filename) {
        
        try {
            // load texture file
            TextureLoader loader = new TextureLoader("images\\" + filename, observer);
            ImageComponent2D image = loader.getImage();
            texImage = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB, image.getWidth(), image.getHeight());
            texImage.setImage(0, image);
            texImage.setEnable(true);
            return true;
        } catch (ImageException e) {
                
            // check that texture is loaded
            System.out.println("load failed for texture: " + filename); 
            return false;
        } // end of try/catch
    } // end of method getTexImage(String)
    
    // sets position of obstacle after checking values and adds to branch group
    public void setPosition(float x, float z) {
        Transform3D position = new Transform3D();
        position.set(checkPosition(x, z));
        obsPosn.setTransform(position);
        positionSet = true;
        bx = new BoundingBox(new Point3d((xPosn - side), 0, (zPosn - side)), 
                                        new Point3d((xPosn + side), side, (zPosn + side)));                                

        obstacle.setCollisionBounds(bx);
        obsPosn.addChild(obstacle);
        objRoot.addChild(obsPosn);
    }
    
    // check position values
    // make sure object is in bounds and not over character
    // returns vector to use as acceptable position
    private Vector3f checkPosition(float x, float z) {
        
        // check room bounds - if unacceptable, defalut position is (1.0f, size, 1.0f)
        // check x value puts obstacle within room bounds
        if (x < (5.0f - side) && x > (-5.0f + side)) {
            xPosn = x;
        }
        
        // check z value puts obstacle within room bounds
        if (z < (5.0f - side) && z > (-5.0f + side)) {
            zPosn = z;
        }
        
        // check for overlap with central character - if unacceptable, default position is as close as possible to character
        // check x value not overlap character
        if (xPosn < 0.0f && (xPosn + side) > (0.0f - radius)) {
            xPosn = -side - radius;
        } else if (xPosn >= 0.0f && (xPosn - side) < (0.0f + radius)) {
            xPosn = side + radius;
        }
        
        // check z value not overlap character
        if (zPosn < 0.0f && (zPosn + side) > (0.0f - radius)) {
            zPosn = -side - radius;
        } else if (zPosn >= 0.0f && (zPosn - side) < (0.0f + radius)) {
            zPosn = side + radius;
        }

        return new Vector3f(xPosn, side, zPosn);
    }  // end of method checkPosition(float, float)
    
    // return BranchGroup root for object for use in scene graph
    public BranchGroup getBG() {
        if (!positionSet) {
            setPosition(xPosn, zPosn);
        }
        return objRoot;
    }  // end of method getBG()
    
    // return collision bounds for obstacle object
    public Bounds getBounds() {
        return obstacle.getCollisionBounds();
    }  // end of method getBounds()
    
    public BoundingBox getBoundingBox() {
        return bx;
    }  // end of method getBounds()
}  // end of class Obstacle
