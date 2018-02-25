/**
*   Title: Scene.java
*   Description: A 3D room environment with cental character and obstacle object.  No collisions are allowed.
*   Date: February 17, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  This program creates a virtual environment with textures, gridlines and character as in Stage2.  Additionally,
*   there is a small textured box to act as an obstacle object.  The initial viewpoint is set but can be adjusted with a mouse control
*   behaviour.  Also, the character can be moved by pressing the specified keys but no collision with the object in the scene will be
*   allowed.
*   
*   Notes: This stage is the same as before with an added obstacle in the scene and the ability to move central character without
*   collision with the obstacle object.
*   
*   This program is based on Stage5 of this project.
*   
*   
*   TEST PLAN
*
*   Normal case: A 10x10x10 3D environment is created with a grassy floor, stone walls, and cloudy blue sky.  Each surface is divided
*   into a 20x20 grid using yellow lines.  The initial viewpoint is set to the front wall, 1/3 of the way up, and looks downward
*   at a 45 degree angle.  The user is unable to move the viewpoint unless the program is re-executed with different starting values.
*   A textured sphere representing the character is in the center of the room on the floor surface.  It is visible in the center of the
*   floor facing the front wall.  The mouse can be used to control the camera in this scene.  The central character can be controlled
*   by pressing the specified keys outlined in the project for stage 5.  An textured cube is placed in the scene and acts as an
*   obstacle.  As the character moves, it will not be allowed to pass through the obstacle.
*
*   The requirements for this stage also state that character stop moving when touching a ball.  However, the ball creation step is 
*   outlined in the next stage.  I will implement this functionality at that point.
*   
*   Bad Data:  
*   
*   1) User tries to move over area covered by the obstacle object:
*       The character is prevented from colliding with the obstacle object through a check of the intersection of the collision bounds.
*       The move will not take place if there is an intersection.  All other moves within the room environment will be allowed as in
*       the previous stage.
*       
*   2) No touching edges:
*       Because the character moves in increments 0.1 units through the scene, it appears that the object stops before coming into
*       contact with the obstacle or walls.  However, if the dimensions of the obstacle or character are changed, this gap will be
*       reduced or disappear entirely.
*       
*   3) Obstacle creation:
*       When the obstacle is created, the maximum size allowed is the space left in the room minus the size of the central character.
*       If the user tries to exceed this amount, the default size of the box is 0.5, which creates a 1x1x1 box.  If the user attempts
*       to add the object without setting the position, the obstacle will default to a position of (1,1) in the xz plane.  Whether the
*       position is set or not, the position values are checked to make sure that the object created will be positioned within the
*       bounds of the room and will not overlap the central character.  If the position is outside the room, the same default value is
*       attempted as above.  If any of the above positions will overlap the central character, the x and z values are adjusted such that
*       the object fits just beside the central character.
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
        cameraControls(viewingPlatform, c3d);
        
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

        // place obstacle in the room
        Obstacle obstacle = new Obstacle(0.2f, character.getRadius(), observer);
        obstacle.setPosition(-2.0f, 1.5f);
        objRoot.addChild(obstacle.getBG());
        
        // add character movement behaviour
        CharacterMove charMove = new CharacterMove(character.getPosnTG(), character.getRadius());
        charMove.setSchedulingBounds(bounds);
        charMove.addObstacleBounds(obstacle.getBounds());
        objRoot.addChild(charMove);
        
        
        
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
    
    // allows the user to control the camera into the virtual scene using the mouse
    private void cameraControls(ViewingPlatform vp, Canvas3D c) {
        MouseControls mc = 
            new MouseControls(vp, c);
        mc.setSchedulingBounds(bounds);

        //vp = su.getViewingPlatform();
        vp.setViewPlatformBehavior(mc);      
    } // end of orbitControls()
    
    public static void main(String args[]) {
        Frame frame = new MainFrame(new Scene(), 512, 512);
    } // end of main
} // end of class