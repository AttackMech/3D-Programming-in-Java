/**
*   Title: Scene.java
*   Description: A 3D room environment with textured surfaces.
*   Date: February 16, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  This program creates a virtual environment with ground, sky and walls all created with textured quads.  Each
*   surface is divided into a 20x20 grid using yellow lines
*   
*   Notes: For this program, I hand coded each surface position such that it faces inward.  This removes the need to adjust the culling
*   properties as all faces point inward toward the middle of the room.  I selected textures at random from a Google image search.  I
*   also hand coded all the texture coordinates for a pleasing look for each surface.
*   
*   Since the user is unable to adjust the viewpoint in this program.  I have set the initial position such that 4 of the 6 possible
*   surfaces can be viewed.  I have also declared the values for position and view direction as class variables so that they can be
*   easily adjusted and allow other views with subsequent runs of the program.
*   
*   This program is based on MyOwn3D from TME2.
*   
*   
*   TEST PLAN
*
*   Normal case: A 10x10x10 3D environment is created with a grassy floor, stone walls, and cloudy blue sky.  Each surface is divided
*   into a 20x20 grid using yellow lines.  The initial viewpoint is set to the front right corner, halfway up, and looks straight
*   toward the opposite corner of the environment.  The user is unable to move the viewpoint unless the program is re-executed with
*   different starting values.
*   
*   Bad Data:  
*   
*   1) Surface offset:
*       Each surface of the room is offset by a factor of 0.1.  This is to prevent stitching from occuring with the grid lines in the
*       room.  When viewed up close, the seams are visible.  However, the presense of the lines help mitigate the unpleasant gap effect.
*       This looks much better than the stitching of the grid lines if no offset were used.
*       
*   2) Extra lines:
*       When creating the grid lines, there are double lines produced along the edges where surfaces meet.  This is because each surface
*       is created in pairs.  The overlap is not visible to the user.  Additionally, the extra 8 lines will probably not have much 
*       computational effect as they are quite easy to render.  Therefore, I have let them stand, as I felt it was not worth the extra
*       programming effort to remove them.
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

    private static final Point3d position = new Point3d(5, 5, 5);
    private static final Point3d lookAt = new Point3d(-5, 5, -5);
    
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
        
        initUserPosition();  // set user's viewpoin
        
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