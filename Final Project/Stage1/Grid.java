/**
*   Title: Grid.java
*   Description: Creates a 10x10x10 cube of yellow gridlines with each surface divided into a 20x20 grid.
*   Date: February 16, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes:  Lines at the edges of the cube will overlap, but this should have an insignificant effect on rendering performance.
*   
*   As all lines have the same appearance property, one appearance is shared among all created objects.
*
*/

import javax.media.j3d.*;
import javax.vecmath.*;


public class Grid {
   
    private BranchGroup objectRoot;
    Color3f yellow = new Color3f(1f,1f,0f);

    // constructor
    public Grid() {
        
        objectRoot = new BranchGroup();
        
         // set line attributes/appearance
        Appearance lineApp = new Appearance();
        
        LineAttributes la = new LineAttributes();
        la.setLineWidth(2f);
        la.setLineAntialiasingEnable(true);
        //la.setLinePattern(la.PATTERN_DOT);
        lineApp.setLineAttributes(la);
        
        ColoringAttributes ca = new ColoringAttributes(yellow, 1);
        lineApp.setColoringAttributes(ca);
        
        // add pairs of surfaces to the root BranchGroup
        objectRoot.addChild(makeTopBottom(lineApp));
        objectRoot.addChild(makeLeftRight(lineApp));
        objectRoot.addChild(makeFrontBack(lineApp));
    }  // end of constructor

    // create the top and bottom surface grid lines
    private Shape3D makeTopBottom(Appearance a) {

        double d = -5;  // value used to set line points
        
        // create intersecting lines in array
        LineArray grid = new LineArray(168, LineArray.COORDINATES);
        for (int i = 0; i < grid.getVertexCount() / 2 ; i += 2) {
            if (i < grid.getVertexCount() / 4) {
                grid.setCoordinate(i, new Point3d(d, 0, 5));
                grid.setCoordinate(i + 1, new Point3d(d, 0, -5));
                grid.setCoordinate(i + 84, new Point3d(d, 10, 5));
                grid.setCoordinate(i + 85, new Point3d(d, 10, -5));
                d += 0.5;
            } else {
                d -= 0.5;
                grid.setCoordinate(i, new Point3d(5, 0, d));
                grid.setCoordinate(i + 1, new Point3d(-5, 0, d));
                grid.setCoordinate(i + 84, new Point3d(5, 10, d));
                grid.setCoordinate(i + 85, new Point3d(-5, 10, d));
                
            }  // end of if
        }  // end of for
        
        return new Shape3D(grid, a);
    }  // end of method makeTopBottom(Appearance)

    // create the left and right surface grid lines
    private Shape3D makeLeftRight(Appearance a) {

        double d = -5;  // value used to set line points
        
        // create intersecting lines in array
        LineArray grid = new LineArray(168, LineArray.COORDINATES);
        for (int i = 0; i < grid.getVertexCount() / 2 ; i += 2) {
            if (i < grid.getVertexCount() / 4) {
                grid.setCoordinate(i, new Point3d(-5, d + 5, 5));  
                grid.setCoordinate(i + 1, new Point3d(-5, d + 5, -5));
                grid.setCoordinate(i + 84, new Point3d(5, d + 5, 5));
                grid.setCoordinate(i + 85, new Point3d(5, d + 5, -5));
                d += 0.5;
            } else {
                d -= 0.5;
                grid.setCoordinate(i, new Point3d(-5, 0, d));
                grid.setCoordinate(i + 1, new Point3d(-5, 10, d));
                grid.setCoordinate(i + 84, new Point3d(5, 0, d));
                grid.setCoordinate(i + 85, new Point3d(5, 10, d));
                
            }  // end of if
        }  // end of for
        
        return new Shape3D(grid, a);

    }  // end of method makeLeftRight(Appearance) 
    
    // create the front and back surface grid lines
    private Shape3D makeFrontBack(Appearance a) {

        double d = -5;  // value used to set line points
        
        // create intersecting lines in array
        LineArray grid = new LineArray(168, LineArray.COORDINATES);
        for (int i = 0; i < grid.getVertexCount() / 2 ; i += 2) {
            if (i < grid.getVertexCount() / 4) {
                grid.setCoordinate(i, new Point3d(-5, d + 5, 5));  
                grid.setCoordinate(i + 1, new Point3d(5, d + 5, 5));
                grid.setCoordinate(i + 84, new Point3d(-5, d + 5, -5));
                grid.setCoordinate(i + 85, new Point3d(5, d + 5, -5));
                d += 0.5;
            } else {
                d -= 0.5;
                grid.setCoordinate(i, new Point3d(d, 0, 5));
                grid.setCoordinate(i + 1, new Point3d(d, 10, 5));
                grid.setCoordinate(i + 84, new Point3d(d, 0, -5));
                grid.setCoordinate(i + 85, new Point3d(d, 10, -5));
                
            }  // end of if
        }  // end of for
        
        return new Shape3D(grid, a);

    }  // end of method makeFrontBack(Appearance)
    
    // return root BranchGraph for use in the scene graph
    public BranchGroup getBG() {
        return objectRoot;
    }  // end of method getBG()
}  // end of class Grid
