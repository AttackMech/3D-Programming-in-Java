/**
*   Title: LoadModelFixed.java
*   Description: A 3D environment representing a room with 4 walls and a floor with grid lines
*   Date: January 27, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Notes: The methods have been copied and adapted from WrapLoaderInfo3D by Andrew Davidson in Killer Game Programming
*   
*   Discussion: This class loads the specified model and places its nodes as children of a BranchGroup.  The loaded model is then
*   adjusted in various ways: rotation, translation, scale, and colour.  The model can be loaded into a scene graph by passing
*   the TransformGroup parent of the entire model.
*
*/ 

import ncsa.j3d.loaders.*;
import com.sun.j3d.loaders.Scene;
import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;

public class LoadModelFixed {
    private Scene loadedScene = null;
    private BranchGroup loadedBG = null;
    private TransformGroup tg = null;
    
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f pink = new Color3f(0.97f, 0.6f, 0.98f);
    
    LoadModelFixed(String fileName) {

        // load the model        
        try {
            ModelLoader loader = new ModelLoader();
            loadedScene = loader.load(fileName);
            if(loadedScene != null) {
                // add model to BranchGroup
                loadedBG = loadedScene.getSceneGroup();
            }
        }
        catch( IOException ioe )
        { System.err.println("Could not find file."); }
        
        // make adjustments to the loaded model
        adjustModel(loadedBG);  // adjust the color of the model
        Transform3D t3d = new Transform3D();
        t3d.rotX( -Math.PI/2.0 );    // models are often on their face; fix that
        Vector3d scaleVec = calcScaleFactor(loadedBG);   // scale the model
        t3d.setScale( scaleVec );
        t3d.setTranslation(new Vector3f(0f, -10f, 0f));  // move the model to a new position

        // add changes and model to TransformGroup
        tg = new TransformGroup(t3d);
        tg.addChild(loadedBG);
        
    }

    // Scale the model based on its original bounding box size
    private Vector3d calcScaleFactor(BranchGroup loadedBG) {
        BoundingBox boundbox = new BoundingBox( loadedBG.getBounds() );

        // obtain the upper and lower coordinates of the box
        Point3d lower = new Point3d();
        boundbox.getLower( lower );
        Point3d upper = new Point3d();
        boundbox.getUpper( upper );

        // store the largest X, Y, or Z dimension and calculate a scale factor
        double max = 0.0;     
        if( (upper.x - lower.x ) > max ) { max = (upper.x - lower.x ); }

        if( (upper.y - lower.y ) > max ) { max = (upper.y - lower.y ); }

        if( (upper.z - lower.z ) > max ) { max = (upper.z - lower.z ); }

        double scaleFactor = 15.0/max;    // 10 is half the width of the floor
        
        // limit the scaling so that a big model isn't scaled too much
        if( scaleFactor < 0.0005 )
            scaleFactor = 0.0005;

        return new Vector3d(scaleFactor, scaleFactor, scaleFactor);
    }   // end of calcScaleFactor()

    // steps through each node in the model to determine if it is an instance of Shape3D
    private void adjustModel(Node node) {
        if(node instanceof Group) {
            Group g = (Group) node;
            Enumeration enumKids = g.getAllChildren();
            while(enumKids.hasMoreElements()) {    // visit children
                SceneGraphObject obj = (SceneGraphObject) enumKids.nextElement();
                if (obj instanceof Node) { adjustModel((Node) obj); }
            }
        }
        // adjust node if instance of Shape3D
        else if (node instanceof Shape3D) { makePink((Shape3D) node); }
    }  // end of visitNode()
    
    // change the shape's colour to pink by changing the material property of the referenced Appearance
    private void makePink(Shape3D shape) {
        Appearance app = shape.getAppearance();
        Material adjustMat = new Material(black, pink, pink, white, 20.0f);
        app.setMaterial( adjustMat );
        shape.setAppearance(app);
    } // end of makePink()
    
    // return the TransformGroup parent of the adjusted model
    public TransformGroup getModel() { return tg; }
  
} // end of class
