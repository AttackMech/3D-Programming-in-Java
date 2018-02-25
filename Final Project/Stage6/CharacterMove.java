/**
*   Title: CharacterMove.java
*   Description: A behaviour which allows the user to control the movement of the character on the x/z plane.
*   Date: February 17, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: This class extends the standard Behavior class to respond to key press values.  When the user presses one of the predefined
*   keys, the target object is moved by a set amount in the corresponding direction.  Each time the object is moved, the x and z
*   values are checked to make sure the object stays within the virtual room environment.
*
*/

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.*;


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
    
    
    private static final float moveAmt = 0.1f;
    
    private TransformGroup targetTG;
    private Transform3D here, move;
    private float radius;
    private Bounds obstacleBounds;

    // constructor
    CharacterMove (TransformGroup targetTG, float radius) {
        
        // initialaize variables
        this.targetTG = targetTG;
        here = new Transform3D();
        targetTG.getTransform(here);
        move = new Transform3D();
        this.radius = radius;
    }  // end of constructor

    // initialize behavior to respond to key press
    public void initialize() {
        wakeupOn(new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED));
    }  // end of method initialize()
    
    // process the wakeup event for this behavior
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
    }  // end of method processStimulus(Enumeration)
    
    // checks wakeup key press event and takes corresponding action
    private void processKeyEvent(KeyEvent eventKey) {

        int keyCode = eventKey.getKeyCode();
        
        // call move based on key press
        if (keyCode == downKey || keyCode == num2 || keyCode == zKey || keyCode == mKey) {
            doMove(0.0f, moveAmt);
        }else if (keyCode == upKey || keyCode == num8 || keyCode == iKey || keyCode == wKey) {
            doMove(0.0f, -moveAmt);
        }else if (keyCode == leftKey || keyCode == num4 || keyCode == jKey || keyCode == aKey) {
            doMove(-moveAmt, 0.0f);
        }else if (keyCode == rightKey || keyCode == num6 || keyCode == lKey || keyCode == dKey) {
            doMove(moveAmt, 0.0f);
        }
    }  // end of method processKeyEvent(KeyEvent)
    
    // set translation of object basedd on input parameters
    private void doMove(float x, float z) {
        
        // get current coordinates
        Vector3f currentCoords = new Vector3f();
        here.get(currentCoords);
        
        // test new movement coordinates
        Vector3f moveValue = new Vector3f(x, 0.0f, z);
        moveValue.add(currentCoords);
        if (testMove(moveValue)) {
            // set coordinates for character if checks are ok
            here.setTranslation(moveValue);
            targetTG.setTransform(here);
        }  // end of if
    }  // end of method doMove(float, float)
    
    private boolean testMove(Vector3f v) {
        
        BoundingSphere bs = new BoundingSphere(new Point3d(v.x, v.y, v.z), radius);

        if (obstacleBounds.intersect(bs)) {  // test for collision with object
            return false;
        }else if (v.x > (5 - radius) || v.x < (-5 + radius) || v.z > (5 - radius) || v.z < (-5 + radius)) {  // test within room
            return false;
        }else {
            return true;
        }
    }  // end of method testMove(Vector3f)
    
    // set bounds of obstacle to check for collisions
    public void addObstacleBounds(Bounds bounds) {
        obstacleBounds = bounds;
    }  // end of method addObstacleBounds(Bounds)
} // end of class CharacterMove
