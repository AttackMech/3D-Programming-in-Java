/**
*   Title: Character.java
*   Description: Creates a small sphere at the center of the room on the floor, textured to look like an eyeball.
*   Date: February 20, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*   Notes: The eyeball texture was hand adjusted by me to appear more realistic when mapped to the sphere.
*
*/


import java.awt.Component;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class Character
{
    // instance variables - replace the example below with your own
    private static final float radius = 0.25f;

    private BranchGroup objRoot;
    TransformGroup position, rotation;
    private Component observer;
    private Texture2D texImage;
    private Sphere character;
    
    public Character(Component observer) {
        this.observer = observer;
        objRoot = new BranchGroup();
        objRoot.addChild(createChar());
    }  // end of constructor
    
    // creates the character appearance and transformations
    private TransformGroup createChar() {
        
        // create appearance for character
        Appearance charAppear = new Appearance();
         // load texture file or set color
        if (getTexImage("blue_eye.jpg")) {
            charAppear.setTexture(texImage);
        } else {
            ColoringAttributes ca = new ColoringAttributes(new Color3f(1f,1f,0f),1);
            charAppear.setColoringAttributes(ca);
        }        
        
        character = new Sphere(radius, Sphere.GENERATE_TEXTURE_COORDS, charAppear);
        character.setCollisionBounds((new BoundingSphere(new Point3d(), radius)));
        // set the initial position of the character
        Transform3D initPosition = new Transform3D();
        initPosition.setTranslation(new Vector3f(0.0f, (0.0f + radius), 0.0f));
        position = new TransformGroup();
        position.setCapability(position.ALLOW_TRANSFORM_WRITE);
        position.setTransform(initPosition);
        
        // set capability for ball rotation
        rotation = new TransformGroup();
        rotation.setCapability(position.ALLOW_TRANSFORM_WRITE);
        
        rotation.addChild(character);
        position.addChild(rotation);
        
        return position;
    }  // end of method createChar()

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
        } // end of try/catch
    } // end of method getTexImage(String)
    
    // return the BranchGroup for this character object
    public BranchGroup getBG() {
        return objRoot;
    }  // end of method getBG()
    
    // return the TransformGroup associated with the position of the object
    public TransformGroup getPosnTG() {
        return position;
    } // end of method getPosnTG()
    
    // return the TransformGroup associated with the rotation of the object
    public TransformGroup getRotnTG() {
        return rotation;
    }  // end of method getRotnTG()
    
    // return the radius value of the object
    public float getRadius() {
        return radius;
    } // end of method getRadius()
    
    public Point3d getPosn() {
        Transform3D t3d = new Transform3D();
        position.getTransform(t3d);
        
        Vector3d v3d = new Vector3d();
        t3d.get(v3d);
        
        return new Point3d(v3d.x, v3d.y, v3d.z);
    }
}  // end of class Character
