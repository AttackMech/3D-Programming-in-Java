/**
*   Title: MyOwn3D.java
*   Description: A 3D environment representing a room with 4 walls and a floor with grid lines, a gun model is loaded in the center
*   Date: December 13, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 2.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  The purpose of this program is to display the room developed in question 1 and load an example external model.
*   The model can be loaded in the default fashion without any changes to positioning or appearance properties, or it can be loaded
*   in a scaled, colored, rotated and translated version.  Switching between the two versions requires commenting in/out the
*   applicable lines of code.
*   
*   Notes: The loaded model without changes is small, coloured black, lying on its side and located at the origin.  The "fixed" 
*   version of the model loads it scaled to the size of the room structure, colored pink, and located in the -y direction from
*   the origin.  The pink colour displayed is the colour when no light hits the object because no lighting is present in this
*   program.
*   
*   TEST PLAN
*
*   Normal case: The grid room structure is displayed with the loaded model with or without changes depending on the version used.
*   
*   There is no user input data to test against, but there are other issues that can be discussed:
*   
*   Bad Data:  
*   
*   1) The stitching issues from question 1 remain the same.
*   
*   2) Incorrect File Name:  The program will catch the error and display the message "File XXX could not be found."  The room
*   structure will be loaded as an empty room without the model. 
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

public class MyOwn3D extends Applet {
    
    private BoundingSphere bounds;
    private SimpleUniverse su;
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
        
        BranchGroup objRoot = new BranchGroup();  // root of visual objects in scene graph
        
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

        // load the gun model into the scene (use comments to see each version)
        LoadModel lm = new LoadModel("GUN1.3DS");
        objRoot.addChild(lm.getModel());
        //         LoadModelFixed lmf = new LoadModelFixed("GUN1.3DS");
        //         objRoot.addChild(lmf.getModel());

        
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
        
        return new Shape3D(grid);
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
        t3d.lookAt( new Point3d(0,5,20), new Point3d(0,0,0), new Vector3d(0,1,0));
        t3d.invert();

        steerTG.setTransform(t3d);
    } // end of initUserPosition()
  
    public static void main(String args[]) {
        Frame frame = new MainFrame(new MyOwn3D(), 256, 256);
    } // end of main
} // end of class