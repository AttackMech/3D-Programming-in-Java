/**
*   Title: BallMove.java
*   Description: A behaviour which controls the movement of the ball in the x/z plane.
*   Date: February 19, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: This class extends the standard Behavior class to respond to the behaviour of the character object.  The direction of 
*   movement is determined by the integer value posted by the CharacterMove behaviour.  It allows the ball to continue in its direction
*   of motion for a set period of time, unless the ball has come to a stop.
*
*/

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;


public class BallMove extends Behavior {
    
    private int postID;
    
    private Behavior charBehave;
    private Character character;
    private Ball targetBall;
    private TransformGroup targetTG;
    private Transform3D here, move;
    private float radius;
    private int time;

    // constructor
    public BallMove(Behavior b, Ball ball, Character c) {
        charBehave = b;
        targetBall = ball;
        character = c;
        time = 0;
    }  // end of constructor

    // initialize behavior to respond to post from character behaviour
    public void initialize() {
        wakeupOn(new WakeupOnBehaviorPost(charBehave, 0));
    }  // end of method initialize()
    
    // process the wakeup event for this behavior
    public void processStimulus(Enumeration criteria) {

        WakeupCriterion wakeup;
        
        // check that wakeup event is a post from character behaviour
        while( criteria.hasMoreElements() ) {
            
            wakeup = (WakeupCriterion) criteria.nextElement();
            
            if( wakeup instanceof WakeupOnBehaviorPost ) {
                
                // get post id of wakeup behaviour
                WakeupOnBehaviorPost wakeupPost = (WakeupOnBehaviorPost) wakeup;
                postID = wakeupPost.getTriggeringPostId();
                
                // move ball and reset wakeup for time
                targetBall.move(postID, character.getPosn(), character.getRadius());
                wakeupOn(new WakeupOnElapsedTime(50));
            } else if (wakeup instanceof WakeupOnElapsedTime) {
                
                // checks time passed and resets the behaviour if the ball has stopped or enought time passes
                if (targetBall.getStop()) {
                    //time = 0;
                    wakeupOn(new WakeupOnBehaviorPost(charBehave, 0));
                    targetBall.reset();
                } else {  // move the ball
                    targetBall.move(postID, character.getPosn(), character.getRadius());
                    //time ++;
                    wakeupOn(new WakeupOnElapsedTime(50));
                }  // end of if
            }  // end of if
        }  // end of while
    }  // end of method processStimulus(Enumeration)
}  // end of class BallMove
