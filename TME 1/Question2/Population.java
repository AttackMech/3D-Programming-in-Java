/**
*   Title: Population.java
*   Description: A collection of various objects for display in a 3D envirionment
*   Date: January 26, 2015
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: 10 objects are placed at various positions throught the 3D environment created in MyOwn3D.  There are 3 inclined planes
*   2 spheres, 2 cubes, 1 rectangular column, and 2 cylindrical columns.  Colors are set to default or specified in the comments for
*   each object.  I basically placed them at random positions and sizes so that they are spread throughout the environment and
*   adjusted them so the do not overlap.
*
*/ 

import com.sun.j3d.utils.geometry.*;
import javax.vecmath.*;
import javax.media.j3d.*;

public class Population
{
    private BranchGroup bg = new BranchGroup();  // basis of the scene graph for all geometry to be added
    
    Population () {

        // create various inclined planes, all with default white color
        // small square just above the middle of the floor
        QuadArray qa = new QuadArray(12, QuadArray.COORDINATES);
        qa.setCoordinate(0, new Point3f(-0.5f, -14.8f, 0.5f));
        qa.setCoordinate(1, new Point3f(0.5f, -14.9f, 0.5f));
        qa.setCoordinate(2, new Point3f(0.5f, -14.9f, -0.5f));
        qa.setCoordinate(3, new Point3f(-0.5f, -14.8f, -0.5f));
        
        // large rectangle leaning against back wall
        qa.setCoordinate(4, new Point3f(8f, -15f, -13f));
        qa.setCoordinate(5, new Point3f(13f, -15f, -13f));
        qa.setCoordinate(6, new Point3f(13f, 3f, -15f));
        qa.setCoordinate(7, new Point3f(8f, 3f, -15f));
        
        // floating kite shape close to inital viewing position
        qa.setCoordinate(8, new Point3f(-3, 4f, 12f));
        qa.setCoordinate(9, new Point3f(-6f, 3f, 9f));
        qa.setCoordinate(10, new Point3f(-3f, 1f, 6f));
        qa.setCoordinate(11, new Point3f(9f, 0.5f, 9f));
        
        // remove the culling of the plane objects so they are visble from all angles
        PolygonAttributes polyAppear = new PolygonAttributes();
        polyAppear.setCullFace(PolygonAttributes.CULL_NONE);
        Appearance planeAppear = new Appearance();
        planeAppear.setPolygonAttributes(polyAppear);
        Shape3D planes = new Shape3D(qa, planeAppear);
        
        
        // create sphere objects
        // a black sphere along the left wall about 1/3 of the way up from the floor
        Sphere sph1 = new Sphere();  // defalut sphere radius 1
        TransformGroup tgSphere1 = new TransformGroup();
        Transform3D t3dSphere1 = new Transform3D();
        t3dSphere1.setTranslation(new Vector3f(-14f, -5f, 5f));
        tgSphere1.setTransform(t3dSphere1);
        tgSphere1.addChild(sph1);
        
        // a red sphere close to front wall near the top
        Appearance appsph2 = new Appearance();
        appsph2.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 0f, 0f), 1));
        Sphere sph2 = new Sphere(1.5f, appsph2);  // sphere radius 1.5, colour red
        TransformGroup tgSphere2 = new TransformGroup();
        Transform3D t3dSphere2 = new Transform3D();
        t3dSphere2.setTranslation(new Vector3f(5f, 5f, 10f));
        tgSphere2.setTransform(t3dSphere2);
        tgSphere2.addChild(sph2);
        
        
        // create cube objects
        // a black cube in the bottom right corner, close to the front wall
        Box cube1 = new Box();  // default box with side length of 2
        TransformGroup tgc1 = new TransformGroup();
        Transform3D t3dc1 = new Transform3D();
        t3dc1.setTranslation(new Vector3f(10f, -12f, 10f));
        tgc1.setTransform(t3dc1);
        tgc1.addChild(cube1);
        
        // a green cube high and to the left along the back wall
        Appearance appc2 = new Appearance();
        appc2.setColoringAttributes(new ColoringAttributes(new Color3f(0f, 1f, 0f), 1));
        Box cube2 = new Box(3f, 3f, 3f, appc2);  // box with side length 3, color green
        TransformGroup tgc2 = new TransformGroup();
        Transform3D t3dc2 = new Transform3D();
        t3dc2.setTranslation(new Vector3f(-7f, 10f, -12f));
        tgc2.setTransform(t3dc2);
        tgc2.addChild(cube2);
        
        
        // create column objects
        // a cyan rectangular column in the lower right, near the front wall
        Appearance appcol = new Appearance();
        appcol.setColoringAttributes(new ColoringAttributes(new Color3f(0f, 1f, 1f), 1));
        Box column = new Box(1f, 4f, 1f, appcol);  // rectangular column with specified dimensions, color cyan
        TransformGroup tgcol = new TransformGroup();
        Transform3D t3dcol = new Transform3D();
        t3dcol.setTranslation(new Vector3f(-7f, -10f, 12f));
        tgcol.setTransform(t3dcol);
        t3dcol.rotX(Math.PI/4);
        t3dcol.rotY(Math.PI/6);        
        tgcol.addChild(column);
        
        
        // create pole objects
        // a short black cylinder on the floor to the right
        Cylinder cyl1 = new Cylinder();  // default cylinder radius 1, height 2
        TransformGroup tgcyl1 = new TransformGroup();
        Transform3D t3dcyl1 = new Transform3D();
        t3dcyl1.setTranslation(new Vector3f(9f, -13f, 0f));
        tgcyl1.setTransform(t3dcyl1);
        tgcyl1.addChild(cyl1);
        
        // a tall magenta cylinder floating slightly to the right and front
        Appearance appcyl2 = new Appearance();
        appcyl2.setColoringAttributes(new ColoringAttributes(new Color3f(1f, 0f, 1f), 1));
        Cylinder cyl2 = new Cylinder(1.5f, 7f, appcyl2);  // cylinder with specified dimensions, color magenta
        TransformGroup tgcyl2 = new TransformGroup();
        Transform3D t3dcyl2 = new Transform3D();
        t3dcyl2.setTranslation(new Vector3f(6f, -6f, -6f));
        tgcyl2.setTransform(t3dcyl2);
        t3dcyl2.rotZ(Math.PI/2);        
        tgcyl2.addChild(cyl2);
        
        
        // add objects to the branch group
        bg.addChild(planes);
        bg.addChild(tgSphere1);
        bg.addChild(tgSphere2);
        bg.addChild(tgc1);
        bg.addChild(tgc2);
        bg.addChild(tgcol);
        bg.addChild(tgcyl1);
        bg.addChild(tgcyl2);
    
    }
    
    // returns the branch group for use in the content branch of the scene graph
    public BranchGroup getShapes () {        
        return bg;
    }

}
