/**
*   Title: Scene.java
*   Description: A 3D room environment with textured surfaces and cental character.  Initial viewpoint set to specifications.
*   Date: February 16, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  This program creates a virtual environment with textures, gridlines and character as in Stage2.  This time the
*   camera is set to be 1/3 up the front wall and looking down at a 45 degree angle
*   
*   Notes: This stage is the same as before with a new initial camera placement.  Everything else remains the same.
*   
*   This program is based on Stage2 of this project.
*   
*   
*   TEST PLAN
*
*   Normal case: A 10x10x10 3D environment is created with a grassy floor, stone walls, and cloudy blue sky.  Each surface is divided
*   into a 20x20 grid using yellow lines.  The initial viewpoint is set to the front wall, 1/3 of the way up, and looks downward
*   at a 45 degree angle.  The user is unable to move the viewpoint unless the program is re-executed with different starting values.
*   A textured sphere representing the character is in the center of the room on the floor surface.  It is visible in the center of the
*   floor facing the front wall.
*   
*   Bad Data:  
*   
*   1) Other views:
*       If the position of the initial view is changed to another wall, the point where the user needs to look to maintain a 45 degree
*       angle needs to be calculated by hand.  Since the user can't move the view at this point, and future moves are made via mouse, I
*       felt that adding in this functionality was unecessary.
*
*/ 

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior; 
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Scene extends Applet {

    private static final Point3d position = new Point3d(0, (20 / 3), 5);
    private static final Point3d lookAt = new Point3d(0, 0, (5 - (20 / 3)));

    private BoundingSphere bounds;
    private SimpleUniverse su;

    // constructor
    public Scene() {
        // sets standard configurations for the applet basis of the scene graph
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c3d = new Canvas3D(config);
        add("Center", c3d);
        
        // set the viewing properties of the view branch of the scene graph
        bounds = new BoundingSphere(new Point3d(), 1000.0d);
        ViewingPlatform viewingPlatform = new ViewingPlatform();
        viewingPlatform.setCapability(ViewingPlatform.ALLOW_BOUNDS_WRITE);
        viewingPlatform.setCapability(ViewingPlatform.ALLOW_CHILDREN_WRITE);
        viewingPlatform.setBounds(bounds);
        Viewer viewer = new Viewer(c3d);    
        View view = viewer.getView();
        view.setBackClipDistance(100.0d);
        su = new SimpleUniverse(viewingPlatform, viewer);
        
        BranchGroup scene = createSceneGraph(c3d);  // create the content branch of scene graph
        scene.compile();
        
        initUserPosition();  // set user's viewpoint
        
        su.addBranchGraph(scene);  // add the content branch graph to the scene
    } // end of constructor

    // creates the content branch of the scene graph
    private BranchGroup createSceneGraph(Component observer) {
        BranchGroup objRoot = new BranchGroup();
        
        // add the room to the scene
        Room room = new Room(observer);
        objRoot.addChild(room.getBG());
        
        // add the grid lines to the scene
        Grid grid = new Grid();
        objRoot.addChild(grid.getBG());
        
        // add the character to the scene
        Character character = new Character(observer);
        objRoot.addChild(character.getBG());

        return objRoot;
    } // end of method
    
    // sets the user's initial position and viewing direction
    private void initUserPosition()
    {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);

        // args are: viewer posn, where looking, up direction
        t3d.lookAt( position, lookAt, new Vector3d(0,1,0));
        t3d.invert();

        steerTG.setTransform(t3d);
    } // end of initUserPosition()
    
    
    public static void main(String args[]) {
        Frame frame = new MainFrame(new Scene(), 512, 512);
    } // end of main
} // end of class