/**
*   Title: Scene.java
*   Description: A 3D room environment with cental character, obstacle, and ball object.  The ball can be moved by contact with the
*               character object.  No collisions are allowed.
*   Date: February 19, 2014
*   Author: Jason Bishop
*   Student #: 3042012
*   Version: 1.0
*
*
*   DOCUMENTATION
*
*   Program Purpose:  This program creates a virtual environment with textures, gridlines and character as in Stage2.  Additionally,
*   there is a small textured box to act as an obstacle object.  The initial viewpoint is set but can be adjusted with a mouse control
*   behaviour.  Also, the character can be moved by pressing the specified keys but no collision with the object in the scene will be
*   allowed.  A small textured sphere acts as a ball object.  When the character comes in contact with the ball, the character comes to
*   a stop and the ball continues to move in the same direction.  The ball will stop when coming into contact with the wall or obstacle
*   object.  The ball has the ability to jump with a spacebar press.  This will create a bouncing jump animation.
*   
*   The program has beautification elements added to it.  An awning structure has been added to the back wall of the scene.  There is a
*   rain effect of falling spheres and square splashes.  These will fall at random x/z coordinates and continue until hitting the floor,
*   the awning, or the objects in the scene.  Splashes will only appear on the awning or floor.  Music also plays while the program is
*   running in a continuous loop.  Sounds will also play when the character jumps, or the ball hits an obstacle.  Finally, the ball
*   motion has been changed so that it experiences a friction rate of 10% and eventually slows to a stop.  It will also bounce when
*   meeting the obstacle or walls, but stop when hitting the character.
*   
*   Notes: This stage is the same as before with added beatification mentioned above.
*   
*   I noticed that one of the requirements was to add friction/slippage to either or both of the character/ball.  However, the later
*   requirement for ball motion requires adding friction to the ball.  I therefore figured that adding friction to the ball only
*   fulfilled both requirements
*   
*   This program is based on Stage9 of this project.
*   
*   Soundfiles:  char_bounce from SoundBible.com, ball_bounce from freesfx.co.uk, music by The Smashing Pumpkins
*                code to play sounds based on code from http://alvinalexander.com/
*   
*   
*   TEST PLAN
*
*   Normal case: A 10x10x10 3D environment is created with a grassy floor, stone walls, and cloudy blue sky.  Each surface is divided
*   into a 20x20 grid using yellow lines.  The initial viewpoint is set to the front wall, 1/3 of the way up, and looks downward
*   at a 45 degree angle.  The user is unable to move the viewpoint unless the program is re-executed with different starting values.
*   A textured sphere representing the character is in the center of the room on the floor surface.  It is visible in the center of the
*   floor facing the front wall.  The mouse can be used to control the camera in this scene.  The central character can be controlled
*   by pressing the specified keys outlined in the project for stage 5.  An textured cube is placed in the scene and acts as an
*   obstacle.  As the character moves, it will not be allowed to pass through the obstacle.  A small textured sphere is placed in the
*   scene which is moved by making the character come into contact with the ball.  When contact occurs, the ball stops and the ball
*   continues to move in the direction that the character moved to hit it.  The ball itself will stop when coming into contact with
*   the walls of the room or the obstacle object.  If no contact occurs, the ball moves at a constant rate for a set period of time.
*   Both the ball and character will rotate based on the direction of movement.  The character faces the direction of movement and the
*   ball rotates as if rolling in the direction of movement.  With the character jump, the jump goes to an initial height that is 2x
*   the height of the character.  It moves up at a rate of 1.5 and down at a rate of 0.98.  Each successive jump is 90% as high as the
*   previous jump.  Once the height is less than 10% of the original jump, the animation ends.
*   
*   Bad Data:  
*       
*   1) Jumping animation:
*       The jump motion is as specified in the requirements for this stage, however, I do not allow the user to move the character while
*       the animation plays.  
*       
*   2) Audio:
*       The background audio is played when the main() method is called to execute the program.  It is not actually contained in any of
*       the classes for the scene.  Additionally, the audio for jumping/bouncing can cause a delay in the running of the program when
*       playing for the first time.
*       
*   3) Rain:
*       The falling rain animation makes splashes when coming into contact with the awning/floor.  This animation scales the shape until
*       it reaches a certain size and then disappears.  However, at the extreme points of the room/awning, this animation will cause the
*       splash shape to exceed the bounds of the awning/room.  I feel that the splashes animate quickly enough that this effect is not
*       very noticable to the user.
*
*/ 

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior; 
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.*;
import sun.audio.*;

public class Scene extends Applet {

    private static final Point3d position = new Point3d(0, (20 / 3), 5);
    private static final Point3d lookAt = new Point3d(0, 0, (5 - (20 / 3)));

    private BoundingSphere bounds;
    private SimpleUniverse su;

    // constructor
    public Scene() {
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
        
        BranchGroup scene = createSceneGraph(c3d);  // create the content branch of scene graph
        scene.compile();
        
        initUserPosition();  // set user's viewpoint
        cameraControls(viewingPlatform, c3d);
        
        su.addBranchGraph(scene);  // add the content branch graph to the scene
    } // end of constructor

    // creates the content branch of the scene graph
    private BranchGroup createSceneGraph(Component observer) {
        BranchGroup objRoot = new BranchGroup();
        objRoot.setCapability(objRoot.ALLOW_CHILDREN_EXTEND); 
        
        // add the room to the scene
        Room room = new Room(observer);
        objRoot.addChild(room.getBG());
        
        // add the grid lines to the scene
        Grid grid = new Grid();
        objRoot.addChild(grid.getBG());
        
        // add the character to the scene
        Character character = new Character(observer);
        objRoot.addChild(character.getBG());

        // place obstacle in the room
        Obstacle obstacle = new Obstacle(0.2f, character.getRadius(), observer);
        obstacle.setPosition(-2.0f, 1.8f);
        objRoot.addChild(obstacle.getBG());
        
        // add ball to the scene
        Ball ball = new Ball(observer);
        ball.addObstacleBounds(obstacle.getBoundingBox());
        objRoot.addChild(ball.getBG());
        
        // add awning to the room
        Awning awning = new Awning(observer);
        objRoot.addChild(awning.getBG());

        // add character movement behaviour
        CharacterMove charMove = new CharacterMove(character.getPosnTG(), character.getRotnTG(), character.getRadius());
        charMove.setSchedulingBounds(bounds);
        charMove.addObstacleBounds(obstacle.getBounds());
        charMove.addBall(ball);
        objRoot.addChild(charMove);
        
        // add ball behaviour
        BallMove ballMove = new BallMove(charMove, ball, character);
        ballMove.setSchedulingBounds(bounds);
        objRoot.addChild(ballMove);
        
        // add rain to scene
        RainDrops rainDrops = new RainDrops(obstacle.getBoundingBox(), character.getRadius(), ball.getRadius());
        FallingDrops fallingDrops = new FallingDrops(rainDrops, character, ball);
        fallingDrops.setSchedulingBounds(bounds);
        objRoot.addChild(rainDrops.getBG());
        objRoot.addChild(fallingDrops);

        return objRoot;
    } // end of method
    
    // sets the user's initial position and viewing direction
    private void initUserPosition()
    {
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup steerTG = vp.getViewPlatformTransform();

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);

        // args are: viewer posn, where looking, up direction
        t3d.lookAt( position, lookAt, new Vector3d(0,1,0));
        t3d.invert();

        steerTG.setTransform(t3d);
    } // end of initUserPosition()
    
    // allows the user to control the camera into the virtual scene using the mouse
    private void cameraControls(ViewingPlatform vp, Canvas3D c) {
        MouseControls mc = 
            new MouseControls(vp, c);
        mc.setSchedulingBounds(bounds);

        //vp = su.getViewingPlatform();
        vp.setViewPlatformBehavior(mc);      
    } // end of orbitControls()
    
    public static void main(String args[]) {
        Frame frame = new MainFrame(new Scene(), 512, 512);
        String music = "sounds\\music.wav";
        try {
            InputStream in = new FileInputStream(music);
            // create an audiostream from the inputstream
            AudioStream audioStream = new AudioStream(in);
            AudioData musicData = audioStream.getData();
            // play the audio clip with the audioplayer class
            
            ContinuousAudioDataStream loop = new ContinuousAudioDataStream(musicData);
            AudioPlayer.player.start(loop);
        } catch (Exception e) {
            System.out.println("Could not load music file.");
        }
    } // end of main
} // end of class