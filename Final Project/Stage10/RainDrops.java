import java.awt.Component;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;  
import javax.vecmath.*; 
import com.sun.j3d.utils.geometry.Sphere;
import java.util.ArrayList;
import java.util.Random;

public class RainDrops {
    
    private static final Color3f cyan = new Color3f(0.0f, 1.0f, 1.0f);
    
    private static final float radius = 0.02f;
    private static final double startPosn = 10 - radius;
    private static final double fallRate = 0.2;
    private static final double splashArea = 4 * Math.PI * (radius * radius);
    private static final double splashSide = Math.sqrt(splashArea);
    
    private TransformGroup[] allDrops, allSplash;
    private boolean[] toSplash;
    private double charRadius, ballRadius;
    private BranchGroup dropRoot;
    private Appearance rainAppear;
    private Random rand;
    private Bounds obsBounds;
    
    // constructor
    public RainDrops(Bounds obs, float charRadius, float ballRadius) {
        
        // initialise instance variables
        obsBounds = obs;
        this.charRadius = charRadius;
        this.ballRadius = ballRadius;
        allDrops = new TransformGroup[50];
        allSplash = new TransformGroup[50];
        toSplash = new boolean [50];            // array used to determine if splash should appear
        java.util.Arrays.fill(toSplash, false);  
        rand = new Random();
        
        dropRoot = new BranchGroup();
        dropRoot.setCapability(dropRoot.ALLOW_CHILDREN_EXTEND); 
        
        rainAppear = new Appearance();
        ColoringAttributes rainColour = new ColoringAttributes(cyan, 1);
        rainAppear.setColoringAttributes(rainColour);
        
        PolygonAttributes rainPA = new PolygonAttributes();
        rainPA.setCullFace(rainPA.CULL_NONE);
        rainAppear.setPolygonAttributes(rainPA);
        
        // create all drops and splashes for use in the scene
        makeDrops();
        makeSplashes();
    }  // end of constructor

    // makes 50 spheres to simulate raindrops in the scene
    // initial positions are set outside the room structure at random x/z coords
    public void makeDrops() {

        // set random position of drop at top or above the room
        for (int i = 0; i < allDrops.length; i++) {
            Sphere drop = new Sphere(radius, rainAppear);
            TransformGroup position = new TransformGroup();
            position.setCapability(position.ALLOW_TRANSFORM_WRITE);
            Transform3D t3d = new Transform3D();
            Vector3d point = new Vector3d(getRandom(), (startPosn + (fallRate * i)), getRandom());
            t3d.set(point);

            //make drops invisible if outside the room
            if (i > 0) {
                t3d.setScale(0);
            }
            
            // add objects to scene and array
            position.setTransform(t3d);
            position.addChild(drop);
            dropRoot.addChild(position);
            allDrops[i] = position;
        }  // end of for
    }  // end of method makeDrops()
    
    // creates 50 quads to represent splashes in the scene
    // initial positions are set to match the corresponding drops created
    public void makeSplashes() {

        // set random position of drop at top of room
        for (int i = 0; i < allSplash.length; i++) {
            // create splash shape
            QuadArray splash = new QuadArray(4, QuadArray.COORDINATES);
                splash.setCoordinate(0, new Point3d(-splashSide, 0, -splashSide));
                splash.setCoordinate(1, new Point3d(-splashSide, 0, splashSide));
                splash.setCoordinate(2, new Point3d(splashSide, 0, splashSide));
                splash.setCoordinate(3, new Point3d(splashSide, 0, -splashSide));
            
            Shape3D splashShape =  new Shape3D(splash, rainAppear);
            
            // get position of drop associated with splash
            Transform3D dropPosn = new Transform3D();
            allDrops[i].getTransform(dropPosn);
            Vector3d dropPoint = new Vector3d();
            dropPosn.get(dropPoint);
            
            // set position of splash based on drop coords
            TransformGroup splashTransform = new TransformGroup();
            splashTransform.setCapability(splashTransform.ALLOW_TRANSFORM_WRITE);
            
            Vector3d splashPoint;
            if (dropPoint.x <= 1.5 && dropPoint.x >= -1.5 && dropPoint.z <= -3) {
                splashPoint = new Vector3d(dropPoint.x, 5.01, dropPoint.z);
            } else {
                splashPoint = new Vector3d(dropPoint.x, 0, dropPoint.z);
            }
            Transform3D splashPosn = new Transform3D();
            splashPosn.set(splashPoint);
            
            splashPosn.setScale(0);
            splashTransform.setTransform(splashPosn);
            
            // add to scene and array
            splashTransform.addChild(splashShape);
            dropRoot.addChild(splashTransform);
            allSplash[i] = splashTransform;
        }  // end of for
    }  // end of method makeSplashes()
    
    // make rain drops fall in the negative y-axis direction
    public void fall(Point3d charPosition, Point3d ballPosition) {
        
        BoundingSphere charBounds = new BoundingSphere(charPosition, charRadius);
        BoundingSphere ballBounds = new BoundingSphere(ballPosition, ballRadius);
        
        for (int i = 0; i < allDrops.length; i++) {

            Transform3D dropPoint = new Transform3D();
            Vector3d point = new Vector3d();
            allDrops[i].getTransform(dropPoint);
            dropPoint.get(point);
            point.y -= fallRate;
            double scale = dropPoint.getScale();
            BoundingSphere dropBounds = new BoundingSphere(new Point3d(point.x, point.y, point.z), (double) radius);
            
            // check for intersection with objects in scene
            if (charBounds.intersect(dropBounds) || ballBounds.intersect(dropBounds) || obsBounds.intersect(dropBounds)) {
                point = new Vector3d(getRandom(), startPosn, getRandom());
                Transform3D splash = new Transform3D();
                
                if (point.x <= 1.5 && point.x >= -1.5 && point.z <= -3 && point.y <= 5) {  // set y coords on awning
                    splash.set(new Vector3d(point.x, 5, point.z));
                    splash.setScale(0);
                }else {
                    splash.set(new Vector3d(point.x, 5, point.z));
                    splash.setScale(0);
                }
                allSplash[i].setTransform(splash);
                
            }else if (point.x <= 1.5 && point.x >= -1.5 && point.z <= -3 && point.y <= 5) {  // check if intersect with awning
                point = new Vector3d(getRandom(), startPosn, getRandom());
                toSplash[i] = true; 
            }else if (point.y <= 0) {
                point = new Vector3d(getRandom(), startPosn, getRandom());
                toSplash[i] = true;
            }
            
            dropPoint.set(point);
            
            // make sure initial raindrops are not visible above the ceiling of the room
            if (point.y > startPosn) {
                dropPoint.setScale(0);
                // make visible
            }
            allDrops[i].setTransform(dropPoint);
        }
        
        // create necessary splash animations
        splash();
        
    }  // end of method fall()
    
    
    // creates splash animations based on the the boolean array values
    private void splash() {
        
        for (int i = 0; i < toSplash.length; i++) {
            if (toSplash[i]) {
                
                Transform3D t3d = new Transform3D();
                allSplash[i].getTransform(t3d);
                
                double scale = t3d.getScale();
                scale += 0.05;
                
                // when reach  max size, reset splash to be invisible and match corresponding drop
                if (scale > 1) {
                    
                    Transform3D dropTransform = new Transform3D();
                    allDrops[i].getTransform(dropTransform);
                    Vector3d dropPosn = new Vector3d();
                    dropTransform.get(dropPosn);
                    
                    if (dropPosn.x <= 1.5 && dropPosn.x >= -1.5 && dropPosn.z <= -3) {  // check if on awning structure
                        t3d.set(new Vector3d(dropPosn.x, 5.01, dropPosn.z));
                    } else {
                        t3d.set(new Vector3d(dropPosn.x, 0, dropPosn.z));
                    }
                    t3d.setScale(0);
                    toSplash[i] = false;
                    
                } else {
                    
                    t3d.setScale(scale);
                }
                
                allSplash[i].setTransform(t3d);
            }  // end of if
        }  // end of for
    }  // end of method splash()
    
    // creates a random double between -5 and 5
    private double getRandom() {
        
        boolean positive = rand.nextBoolean();
        if (positive) {
            return 5 * rand.nextDouble();
        }else {
            return -5 * rand.nextDouble();
        }
    }  // end of getRandom()
    
    // returns BranchGroup for use in the scene graph
    public BranchGroup getBG() {
        return dropRoot;
    }  // end of method getBG()
}  // end of class RainDrops
