/**
*   Title: MyOwn3D.java
*   Description: A tennis ball falling in a realistic manner in a 3D room.
*   Date: February 11, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 3.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  The purpose of this program is to use a behaviour to create animation with an object.  Specifcially, a textured
*   object that looks like a tennis ball is created.  Real gravity is simulated through the use of a Behavior object.
*   
*   Notes: The same room structure from MyOwn3D is used again here.  The tennis ball is created as in Question 5 of this assignment.
*   I have placed the object at the top of the room in the middle.  The size of the ball is about 100x larger than and actual
*   tennis ball.  An actual tennis ball is about 6.54 cm, and my sphere object is 654 cm.  This is to make the ball visible in the 3D
*   environment.  The gravity code in the Gravity class is based on real world physics ignoring the effects of air resistance and
*   friction.  The coefficient of restitution (COR) used is essentially a measure of the "bounciness" of the tennis ball - or rather,
*   how much energy it retains after a collision.  There are several sources with varying results for this value in relation to a tennis
*   ball.  I used a value of 0.728 available from axonomist.tripod.com/mindmatters/02-11-18.html.  
*   
*   For further discussion of the gravity code used in this program, please see the file TME2.docx under question 8.
*   
*   Texturing and behaviour code is based on Sprite3D.java and TouristControls.java by Andrew Davison in Killer Game Programming.
*   
*   
*   TEST PLAN
*
*   Normal case: When the enter key is pressed, the tennis ball drops and spins while accelerating toward the floor of the room.  Once
*   the ball hits the floor, the ball bounces and moves in the positive z direction in a parabolic path.  The bounces become smaller
*   and smaller while the ball continues to rotate and move left.  The ball hits the right wall of the room and reveerses its rotation
*   and z-axis movement.  The bounces eventually stop and the ball continues to roll for short time before coming to a stop.  The enter
*   key can be pressed again to restart the process.
*   
*   Bad Data:  
*   
*   1) The user presses a key other than the 'Enter' key:
*       The program does not respond to any other key input.  While the animation is running, it does not respond to any key press at
*       all.  Only at the beginning of the program and once the animation has finished will the program respond to the Enter key by
*       running the animation.
*   
*   2) The size of the ball:
*       Changing the size of the ball makes small changes on the effect of the program.  The height of the ball is always adjusted based
*       on the radius used to create the ball.  This will result in a higher initial drop for smaller radii and lower for larger radii.
*       This will of course have an effect on the future bounces because there will be more or less energy for the bounce based on the
*       initial height.  However, the code for collision with the walls and floor remains unaffected because it is also calculated using
*       the radius of the ball.  Additionally, the texture also scales with the size of the ball.
*       
*    3) Z-axis movement:
*       The z-axis movement is not based on any actual physics calculations.  I created to give the ball a more pleasing motion.  The
*       ball will bounce of of either wall and continue in the oposite direction no matter the size of the ball.  It moves at a constant
*       rate until it comes to a stop.
*       
*    4) Finishing the bounces:
*       The ball finishes bouncing once the bounce size becomes very small.  The ball continues to roll and decelerates until it comes
*       to a complete stop.  The deceleration is not based on any physics, but rather is coded by me to present a pleasing motion.  It
*       will roll for a set period of time no matter the size of the ball because the code is based on the amount of time that has
*       passed.
*       
*    5) Ball rotation:
*       The ball rotates at a constant rate until the animation has finished and the ball comes to a stop.  Again this code is not based
*       on actual physics, but an eye-pleasing motion.  The ball will reverse its rotation once it hits the left or right wall of the
*       room.
*
*/ 

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.keyboard.*;

public class MyOwn3D extends Applet {
    
    private BoundingSphere bounds;
    private SimpleUniverse su;
    private Canvas3D c3d;
    BranchGroup objRoot;  // root of visual objects in scene graph
    private static final Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);  // solid color blue for coloring sides and floor
    
    // constructor
    public MyOwn3D() {
        
        objRoot = new BranchGroup();
        
        // sets standard configurations for the applet basis of the scene graph
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        c3d = new Canvas3D(config);
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
        
        BranchGroup scene = createSceneGraph();  // create the content branch of scene graph
               
        initUserPosition();        // set user's viewpoint
        orbitControls(c3d);   // controls for moving the viewpoint
        scene.compile();
        su.addBranchGraph(scene);  // add the content branch graph to the scene
    } // end of constructor

    // creates the content branch of the scene graph
    private BranchGroup createSceneGraph() {
        
        // create the sides and floor
        TransformGroup floor = makeFace("floor", blue);
        TransformGroup back = makeFace("back", blue);
        TransformGroup front = makeFace("front", blue);
        TransformGroup left = makeFace("left", blue);
        TransformGroup right = makeFace("right", blue);
        // TransformGroup ceiling = makeFace("ceiling", new Color3f(1.0f, 1.0f, 1.0f));  // white
        
        // create lines to divide surfaces into 3x3 grids
        Shape3D grid = makeGrid();
        
        // add children to root branch graph
        objRoot.addChild(floor);
        objRoot.addChild(back);
        objRoot.addChild(left);
        objRoot.addChild(right);
        objRoot.addChild(front);
        // objRoot.addChild(ceiling);
        objRoot.addChild(grid);
        
        // adds various objects to the scene
        TennisBall tb = new TennisBall(c3d);
        objRoot.addChild(tb.getShapes());
        Gravity gravity = new Gravity(tb);
        gravity.setSchedulingBounds(bounds);
        objRoot.addChild(gravity);
        
        return objRoot;
    } // end of method

    // method to create sides and floor of a room
    private TransformGroup makeFace(String face, Color3f faceColor) {
        
        // create standard QuadArray with coordinates for basic square surface
        QuadArray qa = new QuadArray(4, QuadArray.COORDINATES);
        qa.setCoordinate(0, new Point3f(-15f, 0f, 15f));
        qa.setCoordinate(1, new Point3f(15f, 0f, 15f));
        qa.setCoordinate(2, new Point3f(15f, 0f, -15f));
        qa.setCoordinate(3, new Point3f(-15f, 0f, -15f));
        
        // set appearance for created quad
        ColoringAttributes ca = new ColoringAttributes(faceColor, 1);
        Appearance appear = new Appearance();
        appear.setColoringAttributes(ca);
        Shape3D shape = new Shape3D(qa, appear);
        
        // create ability to transform and translate surface to correct position
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        Transform3D rotate = new Transform3D();
        
        // translate quad based on input paramaters
        // surfaces are off by a factor of 0.005 to allow grid lines to be more visible
        switch (face) {
            case "floor": t3d.setTranslation(new Vector3f(0f, -15.007f, 0f));
                tg.setTransform(t3d);
                tg.addChild(shape);
                break;
            // case "ceiling": t3d.rotX(Math.PI);
            //     t3d.setTranslation(new Vector3f(0f, 15.007f, 0f));                
            //     tg.setTransform(t3d);
            //     tg.addChild(shape);
            //     break;
            case "left": t3d.rotZ(-Math.PI/2);
                t3d.setTranslation(new Vector3f(-15.007f, 0f, 0f));
                tg.setTransform(t3d);
                tg.addChild(shape);
                break;
            case "right": t3d.rotZ(Math.PI/2);
                t3d.setTranslation(new Vector3f(15.007f, 0f, 0f));
                tg.setTransform(t3d);
                tg.addChild(shape);
                break;
            case "front": t3d.rotX(-Math.PI/2);
                t3d.setTranslation(new Vector3f(0f, 0f, 15.007f));                
                tg.setTransform(t3d);
                tg.addChild(shape);
                break;
            case "back": t3d.rotX(Math.PI/2);
                t3d.setTranslation(new Vector3f(0f, 0f, -15.007f));                
                tg.setTransform(t3d);
                tg.addChild(shape);
                break;
            default: return null;
        }
        return tg;
    } // end of makeFace method
    
    // makes lines to divide constructed area into 3x3 grid for each side
    private Shape3D makeGrid() {
        
        // set up initial coordinates for z plane grid lines
        Point3f[] gridCoords = new Point3f[47];
        gridCoords[0] = new Point3f(-15f, 15f, 15f);
        gridCoords[1] = new Point3f(-15f, -15f, 15f);
        gridCoords[2] = new Point3f(15f, -15f, 15f);
        gridCoords[3] = new Point3f(15f, 15f, 15f);
        
        // create remaining grid lines through z plane
        Point3f adjust = new Point3f(0, 0, -10);
        for (int i = 4; i < 16; i++) {
            gridCoords[i] = new Point3f(gridCoords[i - 4]);
            gridCoords[i].add(adjust);
        }
        
        // set up initial coordinates for x plane grid lines
        gridCoords[16] = new Point3f(gridCoords[3]);
        gridCoords[17] = new Point3f(gridCoords[2]);
        gridCoords[18] = new Point3f(gridCoords[14]);
        gridCoords[19] = new Point3f(gridCoords[15]);
        
        // create remaining grid lines through x plane
        adjust = new Point3f(-10, 0, 0);
        for (int i = 20; i < 32; i++) {
            gridCoords[i] = new Point3f(gridCoords[i - 4]);
            gridCoords[i].add(adjust);
        }
        
        // set up initial coordinates for y plane grid lines
        gridCoords[32] = new Point3f(gridCoords[0]);
        gridCoords[33] = new Point3f(gridCoords[3]);
        gridCoords[34] = new Point3f(gridCoords[15]);
        gridCoords[35] = new Point3f(gridCoords[12]);
        gridCoords[36] = new Point3f(gridCoords[0]);
        
        // create remaining grid lines through y plane
        adjust = new Point3f(0, -10, 0);
        for (int i = 37; i < 47; i++) {
            gridCoords[i] = new Point3f(gridCoords[i - 5]);
            gridCoords[i].add(adjust);
        }
        
        // create array for vertex counts in LineStripArray
        int[] lineCounts = new int[11];
        for (int i = 0; i < lineCounts.length; i++) {
            if (i < 8) {lineCounts[i] = 4;}
            else {lineCounts[i] = 5;}
        }
        
        // use LineStripArray to create all lines using points above
        LineStripArray grid = new LineStripArray(47, LineStripArray.COORDINATES, lineCounts);
        for (int i = 0; i < gridCoords.length; i++) {
            grid.setCoordinate(i, gridCoords[i]);
        }
        
        // set appearance of lines to be thicker and smoother for visibility
        LineAttributes la = new LineAttributes();
        la.setLineWidth(3.0f);
        la.setLineAntialiasingEnable(true);
        Appearance lineApp = new Appearance();
        lineApp.setLineAttributes(la);
        
        return new Shape3D(grid, lineApp);
    } // end of makeGrid method
    
    // allows user to use mouse controls to move around the scene
    private void orbitControls(Canvas3D c)
    {
        OrbitBehavior orbit = 
            new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
        orbit.setSchedulingBounds(bounds);

        ViewingPlatform vp = su.getViewingPlatform();
        vp.setViewPlatformBehavior(orbit);      
    } // end of orbitControls()
    
    // sets the user's initial position and viewing direction
    private void initUserPosition()
    {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);

        // args are: viewer posn, where looking, up direction
        t3d.lookAt( new Point3d(0,25,40), new Point3d(), new Vector3d(0,1,0));
        t3d.invert();

        steerTG.setTransform(t3d);
    } // end of initUserPosition()
    
    
    public static void main(String args[]) {
        Frame frame = new MainFrame(new MyOwn3D(), 256, 256);
    } // end of main
} // end of class