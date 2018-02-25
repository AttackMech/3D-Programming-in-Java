/**
*   Title: CharacterMove.java
*   Description: A behaviour which allows the user to control the movement of the character on the x/z plane.
*   Date: February 20, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 6.0
*
*   Notes: This class extends the standard Behavior class to respond to key press values.  When the user presses one of the predefined
*   keys, the target object is moved by a set amount in the corresponding direction.  Each time the object is moved, the x and z
*   values are checked to make sure the object stays within the virtual room environment and does not collide with other objects in the
*   room.  If collision is detected with the ball object, it will post and id that corresponds to the direction of movement.
*   Additionally, the character rotates to face the direction of input, even if no move is made.
*   
*   Next, the character can jump when the spacebar is pressed.  The jump rate is 1.5 to the height of the jump, when it then falls at
*   a rate of 0.98.  After the character returns to the ground, the character bounces again to 90% of the height of the previous jump.
*   This process continues until the jump height is less than 10% of the initial jump.  During this time, the character can not move in
*   response to key presses, but can move again once the jumping process is completed.
*   
*   Finally, a sound will play to match the bouncing behaviour of the jump animation.  Each time the character bounces, the sound is
*   played.  It does not play on the initial jump or the final landing.
*
*/

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.io.*;
import sun.audio.*;


public class CharacterMove extends Behavior{
    
    // available key values for movement
    private static final int downKey = KeyEvent.VK_DOWN;
    private static final int upKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;
    
    private static final int num2 = KeyEvent.VK_NUMPAD2;
    private static final int num8 = KeyEvent.VK_NUMPAD8;
    private static final int num4 = KeyEvent.VK_NUMPAD4;
    private static final int num6 = KeyEvent.VK_NUMPAD6;
    
    private static final int zKey = KeyEvent.VK_Z;
    private static final int iKey = KeyEvent.VK_I;
    private static final int jKey = KeyEvent.VK_J;
    private static final int lKey = KeyEvent.VK_L;
    
    private static final int mKey = KeyEvent.VK_M;
    private static final int wKey = KeyEvent.VK_W;
    private static final int aKey = KeyEvent.VK_A;
    private static final int dKey = KeyEvent.VK_D;
    
    private static final int space = KeyEvent.VK_SPACE;
    
    private static final float moveAmt = 0.1f;
    private static final float jumpRate = 1.5f;
    private static final float bounceHigh = 0.9f;
    private static final float g = 0.98f;
    
    private static final double faceUp = Math.PI;
    private static final double faceDown = 0;
    private static final double faceRight = Math.PI / 2;
    private static final double faceLeft = -(Math.PI / 2);
    
    private TransformGroup targetTG, rotation;
    private Transform3D here, move;
    private Bounds obstacleBounds;
    private Ball ball;
    private float radius, JumpVelocity, FallVelocity, h, time, jump, bounceLow, maxH, maxJumpT, maxFallT;
    private boolean jumpFinish, bounceFinish;

    // constructor
    CharacterMove (TransformGroup targetTG, TransformGroup rotTG, float radius) {
        // initialaize variables
        this.targetTG = targetTG;
        here = new Transform3D();
        targetTG.getTransform(here);
        rotation = rotTG;
        move = new Transform3D();
        this.radius = radius;
        
        jump = 2.0f * (2.0f * radius);
        bounceLow = jump * 0.1f;
        jumpFinish = false;
        bounceFinish = false;
    }  // end of constructor

    // initialize behavior to respond to key press
    public void initialize() {
        wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
    }  // end of method initialize()
    
    // process the wakeup event for this behavior
    public void processStimulus(Enumeration criteria) {
        
        WakeupCriterion wakeup;
        AWTEvent[] event;
        
        // check what the wakeup event was for this behaviour
        while( criteria.hasMoreElements() ) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if( wakeup instanceof WakeupOnAWTEvent ) {  // check that wakeup event is a key press
                event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for( int i = 0; i < event.length; i++ ) {
                    if( event[i].getID() == KeyEvent.KEY_PRESSED )
                    processKeyEvent((KeyEvent)event[i]);
                }
            } else if (wakeup instanceof WakeupOnElapsedTime) {  // check that wakekup event is a time value
                
                // resets the character for movement if the jump is finished or continues the jump
                if(bounceFinish) {
                    wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
                }else {
                    time += 0.02;
                    jump();
                    wakeupOn(new WakeupOnElapsedTime(20));
                }  // end of if
            }  // end of if
        }  // end of while
    }  // end of method processStimulus(Enumeration)
    
    // checks wakeup key press event and takes corresponding action
    private void processKeyEvent(KeyEvent eventKey) {

        int keyCode = eventKey.getKeyCode();
        
        // call move based on key press
        if (keyCode == downKey || keyCode == num2 || keyCode == zKey || keyCode == mKey) {
            doMove(0.0f, moveAmt);
            rotateCharacter(faceDown);
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }else if (keyCode == upKey || keyCode == num8 || keyCode == iKey || keyCode == wKey) {
            doMove(0.0f, -moveAmt);
            rotateCharacter(faceUp);
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }else if (keyCode == leftKey || keyCode == num4 || keyCode == jKey || keyCode == aKey) {
            doMove(-moveAmt, 0.0f);
            rotateCharacter(faceLeft);
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }else if (keyCode == rightKey || keyCode == num6 || keyCode == lKey || keyCode == dKey) {
            doMove(moveAmt, 0.0f);
            rotateCharacter(faceRight);
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }else if (keyCode == space) {
            
            // reset all values for new jump
            jumpFinish = false;
            bounceFinish = false;
            
            maxH = jump;
            
            JumpVelocity = (float) Math.sqrt(2 * jumpRate * maxH);
            FallVelocity = (float) Math.sqrt(2 * g * maxH);
            
            maxJumpT = JumpVelocity/jumpRate;
            maxFallT = FallVelocity / g;
            time = 0.0f;
            
            jump();
            wakeupOn(new WakeupOnElapsedTime(20));
        }else {
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
        }  // end of if
    }  // end of method processKeyEvent(KeyEvent)
    
    // set translation of object basedd on input parameters
    private void doMove(float x, float z) {
        
        // get current coordinates
        Vector3f currentCoords = new Vector3f();
        here.get(currentCoords);
        
        // test new movement coordinates
        Vector3f moveValue = new Vector3f(x, 0.0f, z);
        moveValue.add(currentCoords);
        if (testMove(moveValue, currentCoords)) {
            // set coordinates for character if checks are ok
            here.setTranslation(moveValue);
            targetTG.setTransform(here);
        }  // end of if
    }  // end of method doMove(float, float)
    
    // checks the value of the next move for collision with the obstacle, ball and walls
    private boolean testMove(Vector3f move, Vector3f current) {
        
        BoundingSphere charBS = new BoundingSphere(new Point3d(move.x, move.y, move.z), radius);
        BoundingSphere ballBS = new BoundingSphere(ball.getPosn(), ball.getRadius());

        if (obstacleBounds.intersect(charBS)) {  // test for collision with object
            return false;
        }else if (ballBS.intersect(charBS)) {  // test for collision with ball and post based on movement direction
            
            if (move.x > current.x) {
                postId(1);
            }else if (move.x < current.x) {
                postId(2);
            } // end of if
            
            if (move.z > current.z) {
                postId(3);
            }else if (move.z < current.z) {
                postId(4);
            } // end of if
            
            return false;
        }else if (move.x > (5 - radius) || move.x < (-5 + radius) || move.z > (5 - radius) || move.z < (-5 + radius)) {  // test within room
            return false;
        }else {  // no collisions detected
            return true;
        }  // end of if
    }  // end of method testMove(Vector3f)
    
    // rotates character to face direction passed
    private void rotateCharacter(double direction) {
        
        Transform3D rotate = new Transform3D();
        rotate.rotY(direction);
        rotation.setTransform(rotate);
    }  // end of rotateCharacter(double)
    
    // moves the character through the jumping animation
    private void jump() {
        
        Vector3f currentCoords = new Vector3f();
        here.get(currentCoords);
        
        if (!jumpFinish) {
            
            // set character height value for upward portion of jump
            h = -(jumpRate / 2.0f) * (time * time) + JumpVelocity * time;
            currentCoords.y = (h + radius);
        } else {
            
            // set character height value for downward portion of jump
            h = -(g / 2.0f) * (time * time) + FallVelocity * time;
            currentCoords.y = (h + radius);
        }
        
        // changes motion when max height or min height reached
        if (jumpFinish && time >= 2 * maxFallT) {
            jumpFinish = false;
            currentCoords.y = radius;
            getNewJump();
        } else if (!jumpFinish && time >= maxJumpT) {
            time = maxFallT;
            jumpFinish = true;
        }
        
        // commit movement to object
        here.setTranslation(currentCoords);
        targetTG.setTransform(here);
    }  // end of method jump()
    
    // calculates values for the next jump in the sequence
    private void getNewJump() {
        
        JumpVelocity = (float) Math.sqrt(2 * jumpRate * maxH);
        JumpVelocity *= bounceHigh;
        
        FallVelocity = (float) Math.sqrt(2 * g * maxH);
        FallVelocity *= bounceHigh;
        
        maxH = (JumpVelocity * JumpVelocity) / (2f * jumpRate);
        
        maxJumpT = JumpVelocity / jumpRate;
        maxFallT = FallVelocity / g;
        time = 0.0f;
        
        // ends jump animation when less than 10% of original jump
        if (maxH < bounceLow) {
            bounceFinish = true;
        }
        
        if (!bounceFinish) {
            playSound();
        }// end of if
    }  // end of method getNewJump()
    
    // plays a sound from file
    public void playSound() {
        // open the sound file as a Java input stream
        String soundFile = "sounds\\char_bounce.wav";
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
    
    // set bounds of obstacle to check for collisions
    public void addObstacleBounds(Bounds bounds) {
        obstacleBounds = bounds;
    }  // end of method addObstacleBounds(Bounds)
    
    // adds ball object to detect for collisions
    public void addBall(Ball b) {
        ball = b;
    }  // end of method addBall(Ball b)
} // end of class CharacterMove
