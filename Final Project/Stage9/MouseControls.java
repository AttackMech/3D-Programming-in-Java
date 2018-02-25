/**
*   Title: MouseControls.java
*   Description: Controls camera behaviour and keeps camera within bounds of room structure.
*   Date: February 16, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: The code to control the camera actions with the mouse is based of the OrbitBehavior class commonly used.  However, this class
*   uses a central point to rotate the camera around.  To achieve rotation relative to the camera, the center of rotation is reset every
*   time a mouse event occurs.
*   
*   Additionally, every time the camera is moved, the values of the move are checked to keep it within the room bounds.  However, the
*   check value has a slight offset because when set to the exact value the camera tends to get stuck at the edges.  The offset value is
*   quite small and should not be noticable by the user.
*
*/


import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.event.MouseEvent;
import javax.vecmath.*;
import javax.media.j3d.*;


public class MouseControls  extends OrbitBehavior {
    
    // instance variables - replace the example below with your own
    private TransformGroup cameraPosn;
    private ViewingPlatform view;
    Transform3D postion;
    Matrix3d rotation;
    Vector3d postionVector;
    
    // constructor
    public MouseControls(ViewingPlatform vp, Canvas3D c3d) {
        
        super(c3d, OrbitBehavior.REVERSE_ALL);
        // initialise instance variables
        postion = new Transform3D();
        rotation = new Matrix3d();
        postionVector = new Vector3d();
        view = vp;
    }  // end of constructor

    // overrides super class method to set rotational center to current camera position
    protected void processMouseEvent(MouseEvent evt) {

        // get camera position
        cameraPosn = vp.getViewPlatformTransform();
        cameraPosn.getTransform(postion);
        postion.get(postionVector);
        // set rotational center to current camera postion
        super.setRotationCenter(new Point3d(postionVector.x, postionVector.y, postionVector.z));

        // call super class method
        super.processMouseEvent(evt);
    }  // end of method processMouseEvent(MouseEvent)
    
    // overrides super class method to keep camera movement within bounds
    protected void integrateTransforms() {

        // call super class method
        super.integrateTransforms();
        // get camera position and rotation
        cameraPosn = vp.getViewPlatformTransform();
        cameraPosn.getTransform(postion);
        postion.get(postionVector);
        postion.getRotationScale(rotation);
        
        // test and correct positional values
        testPosn();
        
        // set camera position and rotation within bounds
        postion.set(postionVector);        
        postion.setRotationScale(rotation);
        cameraPosn.setTransform(postion);
    }  // end of method integrateTransforms()
    
    // checks x,y,z position values and adjust if out of bounds
    private void testPosn() {
        
        // check and adjust values if necessary
        if (postionVector.x <= -5.01) { postionVector.x = -5;}
        else if (postionVector.x >= 5.01) { postionVector.x = 5; }
        
        if (postionVector.y <= -0.01) { postionVector.y = 0;}
        else if (postionVector.y >= 10.01) { postionVector.y = 10; }
        
        if (postionVector.z <= -5.01) { postionVector.z = -5;}
        else if (postionVector.z >= 5.01) { postionVector.z = 5; }
    }  // end of method testPosn()
}  //  end of class MouseControls
