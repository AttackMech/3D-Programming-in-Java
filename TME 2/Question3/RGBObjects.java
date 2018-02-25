/**
*   Title: RGBObjects.java
*   Description: A scene with a red, a green, and a blue object, lit by a directional mageneta light.
*   Date: February 8, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  The purpose of this program is to show how matierals interact with lights.  Each object is a different color, so
*   that each will respond differently to the light source.
*   
*   Notes: This program is based on LitSphereApp.java and MaterialApp.java, both by Sun Microsystems and found in "Getting Started with
*   the Java3D API" by Dennis J. Bouvier.  The program creates a red sphere, a green cube, and a blue cylinder on a black background.
*   Each object is slightly different in size so that they appear similar when viewed by the user.  The need for different sizes is 
*   because the cube and column object are both rotated so that they appear on an angle toward the viewer.  The light object in the
*   scene is a directional light pointing toward the objects.  The colour of the light is set to magenta by default.  Different light
*   colours can be set with command line arguments when running the program.  Different coloured lights will produce different results
*   based on the material values of each object.
*   
*   Command line values for light colour:
*   0 = red, 1 = green, 2 = blue, 3 = magenta, 4 = cyan, 5 = yellow, 6 = white
*   Any other values/default = magenta
*   
*   
*   TEST PLAN
*
*   Normal case: The scene is of a red sphere, green cube and blue column lit by a magenta light.  The colour of the light changes based
*   on the command line argument provided.  For a discussion of interactions with different coloured light sources, please see the
*   TME2.docx file.
*   
*   Bad Data:  
*   
*   1) Invalid command line data:
*       The program checks the validity of the supplied command line argument.  The user can input any value or any number of values,
*       but the program only accepts a single integer in the range of 0-7 inclusive.
*
*/ 

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.lang.NumberFormatException;

public class RGBObjects extends Applet {
   
    // set colours for use in program 
    private static final Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
    private static final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
    private static final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f magenta = new Color3f(1.0f, 0.0f, 1.0f);
    private static final Color3f cyan = new Color3f(0.0f, 1.0f, 1.0f);
    private static final Color3f yellow = new Color3f(1.0f, 1.0f, 0.0f);

    public RGBObjects(String[] args) {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
        SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);
        
        // create base of scene graph
        BranchGroup scene = new BranchGroup();
        
        // create red sphere
        TransformGroup tgRed = createTG(-0.8f, 0.0f, -0.5f);
        scene.addChild(tgRed);
        tgRed.addChild(new Sphere(0.3f, Sphere.GENERATE_NORMALS, 60,
                    createMatAppear(red, white, 3.0f)));
        
        // create green cube
        TransformGroup tgGreen = createTG(0.0f, 0.0f, -0.5f);
        scene.addChild(tgGreen);
        tgGreen.addChild(new Box(0.2121f, 0.2121f, 0.2121f, Box.GENERATE_NORMALS,
                    createMatAppear(green, white, 3.0f)));
                    
        // create blue cylinder
        TransformGroup tgBlue = createTG(0.8f, 0.0f, -0.5f);
        scene.addChild(tgBlue);
        tgBlue.addChild(new Cylinder(0.3f, 0.3f, Cylinder.GENERATE_NORMALS, 60, 60,
                    createMatAppear(blue, white, 3.0f)));
        
        // create ambient light source
        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(new BoundingSphere());
        scene.addChild(lightA);
    
        // create directional light source
        DirectionalLight lightD1 = new DirectionalLight();
        lightD1.setInfluencingBounds(new BoundingSphere());
        Vector3f direction = new Vector3f(-1.0f, -1.0f, -1.0f);
        direction.normalize();
        lightD1.setDirection(direction);
        
        // check command line arguments for integer value
        int colour;
        if (args.length == 0) {
            colour = 3;
            System.out.println("No command line argument available, using default color for light.");
        } else if (args.length == 1) {
            
            if (args[0].length() > 1) {
                colour = 3;
                System.out.println("Invalid command line argument, using default colour for light.");
            } else {
                colour = isInteger(args[0]);
            } // end of if
            
        } else {
            colour = 3;
            System.out.println("Invalid command line argument, using default colour for light.");
        } // end of if
        
        // set colour of directional light based on command line arguments
        switch (colour) {
            case 0:
                lightD1.setColor(red);
                break;
            case 1:
                lightD1.setColor(green);
                break;
            case 2:
                lightD1.setColor(blue);
                break;
            case 3:
                lightD1.setColor(magenta);
                break;
            case 4:
                lightD1.setColor(cyan);
                break;
            case 5:
                lightD1.setColor(yellow);
                break;
            case 6:
                lightD1.setColor(white);
                break;
            default:
                lightD1.setColor(magenta);
                break;
        }
        
        // add light to scene
        scene.addChild(lightD1);
        
        // create base of scene graph
        SimpleUniverse u = new SimpleUniverse(canvas3D);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
    
        // setLocalEyeViewing
        u.getViewer().getView().setLocalEyeLightingEnable(true);
    
        // add content branch graph to scene
        u.addBranchGraph(scene);
    }
    
    // create TransformGroup to position objects in scene
    // object will be rotated to show its 3D structure and positioned according to input values
    private TransformGroup createTG(float x, float y, float z) {

        // set position
        Vector3f position = new Vector3f(x, y, z);
        Transform3D move = new Transform3D();
        
        // set rotation
        Transform3D Yrot = new Transform3D();
        Transform3D Xrot = new Transform3D();
        Yrot.rotY(Math.PI/4.0d);
        move.rotX(Math.PI/5.0d);
        move.mul(Yrot);
        move.setTranslation(position);
        
        // create TransformGroup
        TransformGroup tg = new TransformGroup(move);
        return tg;
    } // end of method creatTG(float, float, float)
    
    // create the appearance object for an object based on the passed values
    private Appearance createMatAppear(Color3f dColor, Color3f sColor, float shine) {
        
        // create material object
        Material material = new Material();
        material.setDiffuseColor(dColor);
        material.setSpecularColor(sColor);
        material.setShininess(shine);
        
        // create appearance object
        Appearance appear = new Appearance();
        appear.setMaterial(material);
        return appear;
    } // end of method creatMatAppear(Color3f, Color3f, float)

    // check if passed string value is an integer, returns int or default value of 3
    private int isInteger(String s) {
        int i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid command line argument, using default colour for light.");
            return 3;
        }
        return i;
    } // end of method isInteger(String)
    
    
    public static void main (String [] args) {
        new MainFrame(new RGBObjects(args), 256, 256);
    } // end of main
} // end of class RGBObjects
