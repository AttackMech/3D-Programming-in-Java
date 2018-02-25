/**
*   Title: LoadModel.java
*   Description: Loads the specified model and returns a node for use in a scene graph.
*   Date: January 27, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Notes: The code to load the model has been borrowed and adapted from WrapLoaderInfo3D by Andrew Davison in Killer Game
*   Programming.
*
*   Discussion:  When created, this class simply loads the specified model without making any changes.  It has one method to return
*   the BranchGroup for the loaded model.
*
*/ 

import ncsa.j3d.loaders.*;
import com.sun.j3d.loaders.Scene;
import java.io.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.*;

public class LoadModel {
    private Scene loadedScene = null;
    private BranchGroup loadedBG = null;
        
    // loads the specified model as child of a BranchGroup
    LoadModel(String fileName) {
        
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
    }
    
    // return the model BranchGroup node
    public BranchGroup getModel() { return loadedBG; }
  
} // end of class
