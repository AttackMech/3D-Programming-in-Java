/**
*   Title: Ball.java
*   Description: Creates a small textured sphere to look like a soccer ball, with the ability to move in the x/z plane.
*   Date: February 20, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 3.0
*
*   Notes: Ball movement behavior and collision detection are within this class.  The ball now experiences friction such that it will
*   decelerate on its own until it comes to a stop.  Also, the ball will move in the opposite direction when coming into contact with
*   the obstacle or walls of the room.  It continues to stop if coming into contact with the character object.
*
*/


import java.awt.Component;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import sun.audio.*;


public class Ball {

    // instance variables
    private static final float radius = 0.1f;
    private static final float moveAmt = 0.1f;
    private static final float friction = 0.9f;
    private static final double rotAmt =  Math.PI / 2;
    private static final int posX = 1;
    private static final int negX = 2;
    private static final int posZ = 3;
    private static final int negZ = 4;

    private BranchGroup objRoot;
    private TransformGroup position, rotation;
    private Component observer;
    private Texture2D texImage;
    private Sphere ball;
    private Bounds obstacleBounds, charBounds;
    private Point3d upper, lower;
    private int direction;
    private double rotValue, rotFriction;
    private float moveFriction;
    private boolean stop, bounce;
    
    // constructor
    public Ball(Component observer) {
        this.observer = observer;
        stop = false;
        bounce = false;
        rotValue = 0;
        rotFriction = rotAmt / friction;
        moveFriction = moveAmt / friction;
        objRoot = new BranchGroup();
        objRoot.addChild(createChar());
    }  // end of constructor
    
    // creates the ball appearance and transformations
    private TransformGroup createChar() {
        
        // create appearance for ball
        Appearance ballAppear = new Appearance();
         // load texture file or set color
        if (getTexImage("adidas_soccer.jpg")) {
            ballAppear.setTexture(texImage);
        } else {
            ColoringAttributes ca = new ColoringAttributes(new Color3f(0f, 0f, 0f),1);
            ballAppear.setColoringAttributes(ca);
        }        
        
        ball = new Sphere(radius, Sphere.GENERATE_TEXTURE_COORDS, ballAppear);
        ball.setCollisionBounds((new BoundingSphere(new Point3d(), radius)));
        
        // set the initial position of the ball
        Transform3D initPosition = new Transform3D();
        initPosition.setTranslation(new Vector3f(1.0f, (0.0f + radius), 1.8f));
        position = new TransformGroup();
        position.setCapability(position.ALLOW_TRANSFORM_WRITE);
        position.setTransform(initPosition);
        
        // set capability for ball rotation
        rotation = new TransformGroup();
        rotation.setCapability(position.ALLOW_TRANSFORM_WRITE);
        
        rotation.addChild(ball);
        position.addChild(rotation);
        
        return position;
    }  // end of method createChar()

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
            return false;
        } // end of try/catch
    } // end of method getTexImage(String)
    
    // move ball based in xz plane based on passed criterion
    public void move(int dir, Point3d character, float charRadius) {
        
        direction = dir;
        charBounds = new BoundingSphere(character, (double) charRadius);
        
        if (!stop) {
            // checks the integer value to determine the direction of movement
            moveFriction *= friction;
            
            if (bounce) {
                switch (direction) {
                    case posX: doMove(-moveFriction, 0);
                        break;
                    case negX: doMove(moveFriction, 0);
                        break;
                    case posZ: doMove(0, -moveFriction);
                        break;
                    case negZ: doMove(0, moveFriction);
                        break;
                }
            }else {
                switch (direction) {
                    case posX: doMove(moveFriction, 0);
                        break;
                    case negX: doMove(-moveFriction, 0);
                        break;
                    case posZ: doMove(0, moveFriction);
                        break;
                    case negZ: doMove(0, -moveFriction);
                        break;
                }  // end of switch
            }  // end of if
        }  // end of if
    }  // end of method move(int)
    
    // moves the ball based on the passed values
    private void doMove(float x, float z) {
        
        // create movement vector
        Vector3d move = new Vector3d(x, 0, z);
        
        // get current position vector of ball
        Transform3D current = new Transform3D();
        position.getTransform(current);
        Vector3d currentPosn = new Vector3d();
        current.get(currentPosn);
        
        // create new move position and test for validity
        move.add(currentPosn);
        testPosn(move);
        
        // check if the ball has stopped moving, if not, assign the move and rotate the ball
        if (!stop) {
            
            // set rotation amount
            rotFriction *= friction;
            rotValue += rotFriction;
            Transform3D rotate = new Transform3D();
            
            // determine rotation direction
            if (bounce) {
                switch (direction) {
                    case posX: rotate.rotZ(rotValue);
                        break;
                    case negX: rotate.rotZ(-rotValue);
                        break;
                    case posZ: rotate.rotX(-rotValue);
                        break;
                    case negZ: rotate.rotX(rotValue);
                        break;
                }
            }else {
                
                switch (direction) {
                    case posX: rotate.rotZ(-rotValue);
                        break;
                    case negX: rotate.rotZ(rotValue);
                        break;
                    case posZ: rotate.rotX(rotValue);
                        break;
                    case negZ: rotate.rotX(-rotValue);
                        break;
                }
            }
            
            // set values for rotation and movement
            
            rotation.setTransform(rotate);
            current.set(move);
            position.setTransform(current);
        
        }
    }  // end of method doMove(float, float)
    
    // test potential movement for collision with obstacle or walls
    private void testPosn(Vector3d move) {
        
        // get current collision radius of ball
        BoundingSphere ballBS = new BoundingSphere(new Point3d(move.x, move.y, move.z), radius);
        
        // test if hit obstacle
        if (obstacleBounds.intersect(ballBS)) {
            bounce = !bounce;
            switch (direction) {
                case posX: move.x -= moveFriction;
                    break;
                case negX: move.x += moveFriction;
                    break;
                case posZ: move.z -= moveFriction;
                    break;
                case negZ: move.z += moveFriction;
                    break;
            }
            playSound();
        } else if (charBounds.intersect(ballBS)) {
            stop = true;
        }
        
        // if movement is too small, end animation
        if (moveFriction < 0.01f) {
            stop = true; 
        }
        
        // test if hit walls
        if (move.x > 5 - radius) {
            move.x = 5 - radius;
            bounce = !bounce;
            playSound();
        }else if (move.x < -5 + radius) {
            move.x = -5 + radius;
            bounce = !bounce;
            playSound();
        }
        
        if (move.z > 5 - radius) {
            move.z = 5 -  radius;
            bounce = !bounce;
            playSound();
        }else if (move.z < -5 + radius) {
            move.z = -5 + radius;
            bounce = !bounce;
            playSound();
        }  // end of if        
    }  // end of method testMove(Vector3d)
    
    // return the BranchGroup for this ball object
    public BranchGroup getBG() {
        return objRoot;
    }  // end of method getBG()
    
    // return the TransformGroup associated with the position of the object
    public TransformGroup getPosnTG() {
        return position;
    } // end of method getPosnTG()
    
    // return the TransformGroup associated with the rotation of the object
    public TransformGroup getRotnTG() {
        return rotation;
    }  // end of method getRotnTG()
    
    // return the radius value of the object
    public float getRadius() {
        return radius;
    } // end of method getRadius()
    
    // returns current position of ball
    public Point3d getPosn() {
        Transform3D t3d = new Transform3D();
        position.getTransform(t3d);
        
        Vector3d v3d = new Vector3d();
        t3d.get(v3d);
        
        return new Point3d(v3d.x, v3d.y, v3d.z);
    }  // end of getPosn()
    
    // returns a value to determine if the ball has stopped motion
    public boolean getStop() {
        return stop;
    }
    
    // allows the stop value to be reset for a new movement
    public void reset() {
        stop = false;
        bounce = false;
        rotValue = 0;
        rotFriction = rotAmt / friction;
        moveFriction = moveAmt / friction;
    }
    
    // adds bounds of the obstacle object for collision detection
    public void addObstacleBounds(BoundingBox bounds) {
        obstacleBounds = bounds;
        BoundingBox b = (BoundingBox) bounds;
        upper = new Point3d();
        b.getUpper(upper);
        lower = new Point3d();
        b.getLower(lower);
    }  // end of method addObstacleBounds(Bounds)
    
    // plays a sound from file
    public void playSound() {
        // open the sound file as a Java input stream
        String soundFile = "sounds\\ball_bounce.wav";
        try {
            InputStream in = new FileInputStream(soundFile);
            // create an audiostream from the inputstream
            AudioStream audioStream = new AudioStream(in);
         
            // play the audio clip with the audioplayer class
            AudioPlayer.player.start(audioStream);
        } catch (Exception e) {
            System.out.println("Could not find audio file.");
        }  // end of try/catch
    }  // end of method playSound()
}  // end of class Ball
