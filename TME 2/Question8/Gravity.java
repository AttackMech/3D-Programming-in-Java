/**
*   Title: TennisBall.java
*   Description: A behavior that acts like gravity and creates motion and rotation.
*   Date: February 11, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 2.0
*
*   Notes: Initial objects are set in the constructor, but most variables are inititialized in the processStimulus method to allow the
*   user the ability to reset the animation.
*
*/ 

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Gravity extends Behavior{
   
    private static final int enterKey = KeyEvent.VK_ENTER;
    private static final float xAmt = 0.05f;
    private static final float g = 9.81f;
    private static final float cor = 0.728f;  // Coefficient of Restitution, which is basically the "bounciness" of the ball.
    
    private TennisBall tb;
    private TransformGroup position, rotation;
    private Transform3D here, rotT3D;
    
    private float time, height, v, maxHeightTime, fallDist, xDist;
    private double rotValue;
    private boolean initialFall, rightMax, finished;

    Gravity(TennisBall targetTB) {
        
        // initialize variables
        tb = targetTB;
        position = tb.getSphere();
        here = new Transform3D();
        position.getTransform(here);
        rotation = tb.getRot();
        rotT3D = new Transform3D();
        rotValue = 0.0;
    }

    // set initial wakeup behaviour to key press
    public void initialize() {
        wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
    } // end of method initialize()
    
    // processes wakeup event for this behaviour
    public void processStimulus(Enumeration criteria) {

        WakeupCriterion wakeup;
        AWTEvent[] event;

        // get event causing wakeup and take necessary action
        while( criteria.hasMoreElements() ) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if( wakeup instanceof WakeupOnAWTEvent ) {
                event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for( int i = 0; i < event.length; i++ ) {
                    if( event[i].getID() == KeyEvent.KEY_PRESSED )
                    processKeyEvent((KeyEvent)event[i]);  // check which key pressed
                }
            } else if ( wakeup instanceof WakeupOnElapsedTime ) {
                wakeupOn(new WakeupOnElapsedTime(33));
                fall();  // animate object
            }
        }
    }
    
    // determines the key which caused the wakeup event
    // only takes action if 'Enter' key pressed
    private void processKeyEvent(KeyEvent eventKey) {

        int keyCode = eventKey.getKeyCode();
        
        // set or reset values to run the animation
        if (keyCode == enterKey) {
            time = 0f;
            height = 30f - 2f * tb.getRadius();
            initialFall = true;
            xDist = 0.1f;
            rightMax = false;
            finished = false;
            here.setTranslation(new Vector3f(0.0f, height, 0.0f));
            position.setTransform(here);
            
            fall(); // call the animation process
            
            wakeupOn(new WakeupOnElapsedTime(33));  // reset the wakeup event
        } else {
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));  // reset key press event if any other key used
        } // end of if
    } // end of method processKeyEvent(KeyEvent)
    
    // produce the falling animation
    private void fall() {

        rotateBall();
        
        // continue bouncing animation until it is too small to be noticed
        if (!finished) {
          
            if (initialFall) {
                fallDist = height - (0.5f * g * (time * time));  // gravity for an object in free fall
            } else {
                fallDist = -(g / 2f) * (time * time) + v * time;  // projectile motion
                System.out.println("fall dist = " + fallDist);
            }
           
            testMove();  // make sure ball does not pass through floor
        } else {
            finishRoll();  // object continues rolling after bouncing
        }
        
        time += 0.033f;    
    } // end of method fall()
    
    // tests when ball reaches the floor
    private void testMove() {
        
        // initial fall involves no lateral motion, so only use gravity code to adjust position
        // after first bounce begin moving the ball along the x-axis
        if (initialFall) {
            
            // when ball reaches floor, calculate new projectile motion
            if ((fallDist - (15f - tb.getRadius())) < -(15f - tb.getRadius())) {
                here.setTranslation(new Vector3f(0.0f, -(15f - tb.getRadius()), 0.0f));
                initialFall = false;
                getNewHeight();  
                time = 0f;  
            } else {
                here.setTranslation(new Vector3f(0.0f, fallDist - (15f - tb.getRadius()), 0.0f));
            } // end of if
        
        // use projectile motion
        } else {
            here.setTranslation(new Vector3f(getXDist(), fallDist - (15f - tb.getRadius()), 0.0f));
            
            // calculate new bounce once projectile returns to floor
            if (time > (2f * maxHeightTime)) {
                getNewHeight();
                time = 0f; 
            } 
        }
        
        position.setTransform(here);
    } // end of method testMove()
        
    // calculates height of the next bounce based on velocity of falling ball and cor value
    private void getNewHeight() {
        v = (float) Math.sqrt(2f * g * height);
        v *= cor;
        height = (v * v) / (2f * g);
        maxHeightTime = v / g;
        
        // stops bouncing ball when value gets too small to notice
        if (height < 0.001f) {
            finished = true;
        } // end of if
    } // end of method getNewHeight()
    
    // moves ball along x-axis
    // changes direction if ball reaches left or right wall
    private float getXDist() {
        
        // for initial movement or after hitting the left wall
        if (!rightMax) {
            xDist += xAmt;
            
            // check if passing throught the right wall
            if (xDist > (15f - tb.getRadius())) {
                xDist = (15f - tb.getRadius()) - 0.1f;
                rightMax = true;
            }

        // for movement after hitting the right wall
        } else {  
            xDist -= xAmt;
            
            // check if passing through the left wall
            if (xDist < -(15f - tb.getRadius())) {
                xDist = -(15f - tb.getRadius()) + 0.1f;
                rightMax = false;
            } // end of if
        } // end of if
        
        return xDist;
    } // end of method getXDist()
    
    // rotates the ball by a set amount each time
    // rotation is reversed depending on the movement of the ball along the x-axis
    private void rotateBall() {
        
        if (!rightMax) {
            rotValue -= 0.1f;
        } else {
            rotValue += 0.1f;
        }
        rotT3D.rotZ(rotValue);
        rotation.setTransform(rotT3D);
    }  // end of method rotateBall()
    
    // keeps the ball moving along the x-axis after bouncing finished
    // decelerates the ball to a complete stop
    private void finishRoll() {
        
        float slow = 0f;
        
        // will stop motion after 2 seconds
        if (time > 2f) {
            wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));  // allows user to restart the animation
        }
        
        // calculate direction and amount of x-axis movement
        if (rightMax) {
            slow = getXDist() + (time / 40f);
        } else {
            slow = getXDist() - (time / 40f);
        }
            
        here.setTranslation(new Vector3f(slow , fallDist - (15f - tb.getRadius()), 0.0f));  //orig getXdist + slow
        position.setTransform(here);
    
    } // end of method finishRoll()
} // end of class Gravity
