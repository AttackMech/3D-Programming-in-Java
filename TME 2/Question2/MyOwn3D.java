/**
*   Title: MyOwn3D.java
*   Description: A 3D room environment loaded with various geometric structures and one sphere moved with the arrow keys.
*   Date: February 7, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  The purpose of this program is to show how behaviours in Java3D can be used to manipulate objects.  Specifically,
*   how an object can be moved using various key presses as with a sprite.
*   
*   Notes: There are 10 objects created as specified in the requirements for question 2 in TME1.  As specified in this question, one
*   of the spheres has the ability to move around the 3D environment with the up/down/left/right arrow keys.  Each key corresponds to
*   the movement of the sphere as you would expect with each key (ie. up = forward, left = left, etc.).  The behaviour checks the input
*   key value and makes changes to the transform group of the the red sphere in the environment.  Each move is tested to make sure that
*   the sphere does not pass through the boundaries of the room created.  Also, to respond to key presses, the user must make sure that
*   the applet window is active by clicking on it with the mouse.
*   
*   The code for the behaviour in this program is based on TouristControls.java by Andrew Davison in Killer Game Programming.
*   
*   
*   TEST PLAN
*
*   Normal case: The scene is created with blue walls and floor with white grid lines as before.  Also, the same mouse controls affect
*   the view as before.  The sphere can be moved by pressing the arrow keys.  All other inputs are disregarded.
*   
*   Bad Data:  
*   
*   1) The user tries to move the object outside the room environment:
*       In this case, the program checks the move to be made and if it places the sphere outside of the bounds of the room, the move
*       is reset to the edge of the room.  This allows the user to move in any direction without passing through the walls of the room.
*   
*   2) Collisions with other objects in the room:
*       The other objects in the room are placed such that there is no interaction with the sphere object.  In other words, there is no
*       overlap, no matter where the sphere moves in the environment.  However, if the size or initial position of the sphere were to 
*       be changed, there is no code to prevent clipping.  The sphere would pass through any object as if it were not there.
*       
*   3) A key other than an arrow key is pressed:
*       The user is free to press any key, but only the up/down/left/right arrow keys will respond to input.  Any other key press at any
*       other time does not have any effect on the virtual scene.
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
    BranchGroup objRoot = new BranchGroup();  // root of visual objects in scene graph
    private static final Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);  // solid color blue for coloring sides and floor
    
    // constructor
    public MyOwn3D() {
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
        Population shapes = new Population();
        objRoot.addChild(shapes.getShapes());
        
        // add behavior to scene to allow object movement
        KeyBehaviour movement = new KeyBehaviour(shapes.getSphere(), shapes.getRadius());
        movement.setSchedulingBounds(bounds);
        objRoot.addChild(movement);
        
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