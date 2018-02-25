/**
*   Title: KeyBehaviour.java
*   Description: A behavior object to allow the user to move an object in the 3D scene.
*   Date: February 7, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: This class extends the standard Behavior class to respond to key press values.  When the user presses the up/down/left/right
*   arrow keys, the target object is moved by a set amount in the corresponding direction.  Each time the object is moved, the x and z
*   values are checked to make sure the object stays within the virtual room environment.
*
*/ 

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;

public class KeyBehaviour extends Behavior{
    // instance variables - replace the example below with your own
    
    private static final int forwardKey = KeyEvent.VK_DOWN;
    private static final int backKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;
    
    private static final float moveAmt = 0.3f;
    
    private TransformGroup targetTG;
    private Transform3D here, move;
    private float radius;

    KeyBehaviour(TransformGroup targetTG, float radius) {
        
        // initialaize variables
        this.targetTG = targetTG;
        here = new Transform3D();
        targetTG.getTransform(here);
        move = new Transform3D();
        this.radius = radius;
    } // end of constructor

    // initialize behavior to respond to key press
    public void initialize() {
        wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
    } // end of method initialize()
    
    // process the wakeup event for thsi behavior
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;
        
        // check that wakeup event is a key press
        while( criteria.hasMoreElements() ) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if( wakeup instanceof WakeupOnAWTEvent ) {
                event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
                for( int i = 0; i < event.length; i++ ) {
                    if( event[i].getID() == KeyEvent.KEY_PRESSED )
                    processKeyEvent((KeyEvent)event[i]);
                }
            }
        }
        // set new wakeup behavior
        wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
    } // end of method processStimulus(Enumeration)
    
    // checks wakeup key press event and takes corresponding action
    private void processKeyEvent(KeyEvent eventKey) {

        int keyCode = eventKey.getKeyCode();
        
        // call move based on key press
        switch (keyCode) {
            case forwardKey:
                doMove(0.0f, moveAmt);
                break;
            case backKey:
                doMove(0.0f, -moveAmt);
                break;
            case leftKey:
                doMove(-moveAmt, 0.0f);
                break;
            case rightKey:
                doMove(moveAmt, 0.0f);
                break;
            default:
        } // end of switch
    } // end of method processKeyEvent(KeyEvent)
    
    // set translation of object basedd on input parameters
    private void doMove(float x, float z) {
        
        // set values
        move.setTranslation(new Vector3f(x, 0.0f, z));
        here.mul(move);
        
        // call to test values passed
        testMove();
        
    } // end of method doMove(float, float)
    
    // tests values of object to keep within room space
    private void testMove() {
        
        Vector3f translation = new Vector3f();
        here.get(translation);
        
        // test positive and negative x/z values to keep object within room structure
        // will reset values to edge of room if outside the room structure
        if (translation.x > (15 - radius)) { 
            here.setTranslation(new Vector3f((15 - radius), translation.y, translation.z));
        } else if (translation.x < -(15 - radius)) {
            here.setTranslation(new Vector3f(-(15 - radius), translation.y, translation.z));
        } else if (translation.z > (15 - radius)) {
            here.setTranslation(new Vector3f(translation.x, translation.y, (15 - radius)));
        } else if (translation.z < -(15 - radius)) {
            here.setTranslation(new Vector3f(translation.x, translation.y, -(15 - radius)));
        } 

        // commit changes to TransformGroup to move object
        targetTG.setTransform(here);
    } // end of method testMove()
} // end of class KeyBehaviour
