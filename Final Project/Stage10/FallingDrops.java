/**
*   Title: BallMove.java
*   Description: A behaviour which controls falling rain animation in the scene.
*   Date: February 22, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: This class extends the standard Behavior class to call the animation behaviour for the rain drops.  It simply calls the
*   method based on elapsed time and passes the current position of the ball and character object for collision detection.
*
*/

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;


public class FallingDrops extends Behavior {
    
    private RainDrops rain;
    private Character character;
    private Ball ball;
    

    // constructor
    public FallingDrops(RainDrops rd, Character c, Ball b) {
        rain = rd;
        character = c;
        ball = b;
    }  // end of constructor

    // initialize behavior based on elapsed tiem
    public void initialize() {
        wakeupOn(new WakeupOnElapsedTime(20));
    }  // end of method initialize()
    
    // process the wakeup event for this behavior
    public void processStimulus(Enumeration criteria) {

        WakeupCriterion wakeup;
        
        // check wakeup event 
        while( criteria.hasMoreElements() ) {
            
            wakeup = (WakeupCriterion) criteria.nextElement();
            
            if (wakeup instanceof WakeupOnElapsedTime) {
                rain.fall(character.getPosn(), ball.getPosn());  // call animation method with current positions of objects
            }  // end of if
        }  // end of while
        
        wakeupOn(new WakeupOnElapsedTime(20));
    }  // end of method processStimulus(Enumeration)
}  // end of class BallMove
