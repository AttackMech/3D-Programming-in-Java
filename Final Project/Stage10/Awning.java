/**
*   Title: Room.java
*   Description: Creates 6 textured surfaces as a 10x10x10 cube.
*   Date: February 16, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes:  All surfaces are created such that the are visible from the interior of the room.  No culling has been adjusted so if the
*   user moves outside the room the will not be able to see the back surfaces.  But since the user can't move it is unecessary.
*   
*   Each surface has a slight offset so that there is no interference with the gridlines created.
*   
*   Default colours are set for each surface in case the texture fails to load.  The floor is green, the sky is blue, and the walls are
*   a brownish colour.
*
*/

import java.awt.Component;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;  
import javax.vecmath.*; 


public class Awning {
    
    private BranchGroup awningRoot;
    private Component observer;
    private Texture2D texImage;

    // constructor
    public Awning(Component observer) {
        
        // initialise instance variables
        this.observer = observer;

        // add all surfaces to the environment
        awningRoot = new BranchGroup();
        
        awningRoot.addChild(makeAwning());
    }  // end of constructor
    
    // creates the floor of the room
    private Shape3D makeAwning() {

        // create 2x3 awning
        QuadArray awningQuad = new QuadArray(16, QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2);
        
        // create top
        awningQuad.setCoordinate(0, new Point3d(-1.5, 5, -5));
        awningQuad.setCoordinate(1, new Point3d(-1.5, 5, -3));
        awningQuad.setCoordinate(2, new Point3d(1.5, 5, -3));
        awningQuad.setCoordinate(3, new Point3d(1.5, 5, -5));
        
        // create left side
        awningQuad.setCoordinate(4, new Point3d(-1.5, 5, -5));
        awningQuad.setCoordinate(5, new Point3d(-1.5, 4.5, -5));
        awningQuad.setCoordinate(6, new Point3d(-1.5, 4.5, -3));
        awningQuad.setCoordinate(7, new Point3d(-1.5, 5, -3));
        
        // create right side
        awningQuad.setCoordinate(8, new Point3d(1.5, 4.5, -5));
        awningQuad.setCoordinate(9, new Point3d(1.5, 5, -5));
        awningQuad.setCoordinate(10, new Point3d(1.5, 5, -3));
        awningQuad.setCoordinate(11, new Point3d(1.5, 4.5, -3));
        
        // create front
        awningQuad.setCoordinate(12, new Point3d(-1.5, 5, -3));
        awningQuad.setCoordinate(13, new Point3d(-1.5, 4.5, -3));
        awningQuad.setCoordinate(14, new Point3d(1.5, 4.5, -3));
        awningQuad.setCoordinate(15, new Point3d(1.5, 5, -3));
        
        // create and set texture coords
        TexCoord2f[] texCoords = new TexCoord2f[16];
        for (int i = 0; i < texCoords.length; i += 4) {
            texCoords[i] = new TexCoord2f(0.0f, 0.0f);
            texCoords[i + 1] = new TexCoord2f(0.0f, 2.0f);
            texCoords[i + 2] = new TexCoord2f(3.0f, 2.0f);
            texCoords[i + 3] = new TexCoord2f(3.0f, 0.0f);
        }
        
        awningQuad.setTextureCoordinates(0,0,texCoords);
        
        // create and set appearance of floor
        Appearance awningAppear = new Appearance();
        // load texture file or set color
        if (getTexImage("stripes.png")) {
            awningAppear.setTexture(texImage);
        } else {
            ColoringAttributes ca = new ColoringAttributes(new Color3f(0.0f, 1.0f, 0.0f), 1);
            awningAppear.setColoringAttributes(ca);
        }        
        
        PolygonAttributes awningPA = new PolygonAttributes();
        awningPA.setCullFace(awningPA.CULL_NONE);
        awningAppear.setPolygonAttributes(awningPA);
        
        return new Shape3D(awningQuad, awningAppear);
    }  // end of method makeFloor()
    
    
    
    // retrieves the specified image file and sets it as a texture
    // returns true/false depending on the success of the loading process
    private boolean getTexImage(String filename) {

        try {
            // load texture file
            TextureLoader loader = new TextureLoader("images\\" + filename, observer);
            ImageComponent2D image = loader.getImage();
            texImage = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGB, image.getWidth(), image.getHeight());
            texImage.setImage(0, image);
            texImage.setEnable(true);
            return true;
        } catch (ImageException e) {
                
            // check that texture is loaded
            System.out.println("load failed for texture: " + filename); 
            return false;
        }

    } // end of method getTexImage(String)
    
    // returns BranchGroup for use in the scene graph
    public BranchGroup getBG() {
        return awningRoot;
    }  // end of method getBG()
}  // end of class Room
