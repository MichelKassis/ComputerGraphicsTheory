package mintools.viewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import com.sun.imageio.plugins.common.ImageUtil;

import mintools.swing.ControlFrame;
import mintools.viewer.GLPlane;
import mintools.viewer.SceneGraphNode;
import mintools.viewer.TrackBallCamera;

import java.awt.Dimension;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.vecmath.Point3f;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class EasyViewer extends Thread  {

    /**
     * The trackball and camera
     */
    public TrackBallCamera trackBall;
    
    /**
     * The scene, needed in the loaded architecture when loading cameras
     */
    private SceneGraphNode scene;
    
    /**
     * The name of this scene
     */
    private String name;
    
    /**
     * The frame containing all controls for this viewing application
     */
    public ControlFrame controlFrame;
    
	/**
	 *  The window handle
	 */
	private long window;

    /**
     * The dimension of the display screen TODO: only one window?  :/
     */
    static public Dimension size;
	
	/**
    * Creates a viewer for the given scene
    * @param name
    * @param scene
    */
   public EasyViewer( String name, SceneGraphNode scene ) {
       this( name, scene, new Dimension(960,550), new Dimension(600, 750) );
   }
       
   /**
    * Creates a new easy viewer with given sizes for display and controls
    * @param name
    * @param scene
    * @param size
    * @param controlSize
    */
   public EasyViewer( String name, SceneGraphNode scene, Dimension size, Dimension controlSize ) {
       this(name,scene,size,controlSize, new Point3f(10,10,10), new Point3f(-10,10,10) );
   }    
   
   /**
    * Creates a new easy viewer with given sizes for display and controls
    * @param name
    * @param scene
    * @param size
    * @param controlSize
    * @param light1Pos
    * @param light2Pos
    */
   public EasyViewer( String name, SceneGraphNode scene, Dimension size, Dimension controlSize, Point3f light1Pos, Point3f light2Pos ) {
       this.scene = scene;
       this.size = size;
       this.name = name;
       
       this.light1Pos.set( light1Pos );
       this.light2Pos.set( light2Pos );
       
       trackBall = new TrackBallCamera();
                                     
       controlFrame = new ControlFrame("Controls");
       // We'll disable the camera tab for now, as nobody should need it until assignment 3
       // well, then again, perhaps assignment 2??
       controlFrame.add("Camera", trackBall.getControls());
       controlFrame.add("Scene", scene.getControls());
       controlFrame.setSelectedTab("Scene");
                               
       controlFrame.setSize(controlSize.width, controlSize.height);
       controlFrame.setLocation(size.width + 20, 0);
       controlFrame.setVisible(true);    

       start(); // start this thread
       
   }
   
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		windowInit();
		
		init();
		
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		
		System.exit(0);
	}

	private void windowInit() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(size.width, size.height, name, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		glfwSetWindowSizeCallback( window, new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long window, int width, int height) {
				size.width = width;
				size.height = height;
				//image = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );            
		        image = new BufferedImage( width, height, BufferedImage.TYPE_3BYTE_BGR );
		        imageBuffer = ByteBuffer.wrap(((DataBufferByte)image.getRaster().getDataBuffer()).getData());
		        glViewport( 0, 0, width, height );
			}
		});

		
		
		glfwSetMouseButtonCallback( window, new GLFWMouseButtonCallbackI() {			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				trackBall.mouseButtonCallback( window, button, action, mods );
			}
		});
		
		glfwSetCursorPosCallback( window, new GLFWCursorPosCallbackI() {			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				trackBall.cursorPosCallback( window, xpos, ypos );				
			}});
	}

	
    public void init() {
    	GL.createCapabilities();
    	glShadeModel(GL_SMOOTH);                 // Enable Smooth Shading
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // Black Background
        glClearDepth(1.0f);                      // Depth Buffer Setup
        glEnable(GL_DEPTH_TEST);                 // Enables Depth Testing
        glDepthFunc(GL_LEQUAL);                  // The Type Of Depth Testing To Do
        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        glEnable( GL_LINE_SMOOTH );
        glEnable( GL_POINT_SMOOTH );
        scene.init();
    }
    
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			display();
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	

    public Point3f light1Pos = new Point3f( 0, 50, 0 );
    public Point3f light2Pos = new Point3f( 0, 2, -2.5f );
    
    public void display() {
        
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
        
        {
            // main light is at the top front of the room.
            int lightNumber = 1;
            float[] position = { light1Pos.x, light1Pos.y, light1Pos.z, 1 };
            float[] colour = { .8f, .8f, .8f, 1 };
            float[] acolour = {0,0,0,1};//{ .05f, .05f, .05f, 1 };
            glLightfv(GL_LIGHT0 + lightNumber, GL_SPECULAR, colour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_DIFFUSE, colour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_AMBIENT, acolour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_POSITION, position);
            glEnable( GL_LIGHT0 + lightNumber );
        }
        
        {
            // put a dim light at the back of the room, in case anyone wants to 
            // look at the back side of objects
            int lightNumber = 0;
            float[] position = { light2Pos.x, light2Pos.y, light2Pos.z, 1 };
            float[] colour = { .2f, .2f, .2f, 1 };
            float[] acolour = { .0f, .0f, .0f, 1 };
            glLightfv(GL_LIGHT0 + lightNumber, GL_SPECULAR, colour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_DIFFUSE, colour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_AMBIENT, acolour);
            glLightfv(GL_LIGHT0 + lightNumber, GL_POSITION, position);
            glEnable( GL_LIGHT0 + lightNumber );
        }
        
        trackBall.prepareForDisplay();

        glEnable( GL_LIGHTING );
        glEnable( GL_NORMALIZE );
        
        if ( scene!= null ) scene.display();
        
    }
    
    /**
     * Saves a snapshot of the current canvas to a file.
     * The image is saved in png format and will be of the same size as the canvas.
     * Note that if you are assembling frames saved in this way into a video, 
     * for instance, using virtualdub, then you'll need to take care that the 
     * canvas size is nice (i.e., a multiple of 16 in each dimension), or add 
     * a filter in virtualdub to resize the image to be a codec friendly size.
     * @param drawable
     * @param file
     * @return true on success
     */
    public boolean snapshot( File file ) {
        //gl.glReadPixels( 0, 0, width, height, GL_ABGR_EXT, GL.GL_UNSIGNED_BYTE, imageBuffer );            
        glReadPixels( 0, 0, size.width, size.height, GL12.GL_BGR, GL_UNSIGNED_BYTE, imageBuffer );
        
        // TODO: this is an AWT tool within jogamp :/
        // ImageUtil.flipImageVertically(image);
        
        try {
            if ( ! ImageIO.write( image, "png", file) ) {
                System.err.println("Error writing file using ImageIO (unsupported file format?)");
                return false;
            }
        } catch (IOException e) {    
            System.err.println("trouble writing " + file );
            e.printStackTrace();
            return false;
        }
        
        // print a message in the display window
        beginOverlay();
        String text =  "RECORDED: "+ file.toString();
        glDisable( GL_LIGHTING );
        glColor4f( 1, 0, 0, 1 );           
        printTextLines( text, 10, size.height-20, 10, 0 );
        glEnable( GL_LIGHTING );
        endOverlay();
        return true;
    }    

    /** Image for sending to the image processor */
    private BufferedImage image;
    
    /** Image Buffer for reading pixels */
    private ByteBuffer imageBuffer;
    
    
    /**
     * Begin drawing overlay (e.g., text, screen pixel coordinate points and 
     * lines)
     * @param drawable
     */
    static public void beginOverlay() {
        glPushAttrib( GL_DEPTH_BUFFER_BIT | GL_ENABLE_BIT | GL_FOG_BIT | GL_LIGHTING_BIT | GL_DEPTH_BUFFER_BIT );
        glPushMatrix();
        glLoadIdentity();
        glMatrixMode( GL_PROJECTION );
        glPushMatrix();
        glLoadIdentity ();
        glOrtho(0, size.width, size.height, 0, -1, 1 );
//        glu.gluOrtho2D( 0, width, height, 0 );
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glMatrixMode( GL_MODELVIEW );
    }
    
    /**
     * Draws multi-line text.
     * @param drawable
     * @param text Text lines to draw, delimited by '\n'.
     */
    static public void printTextLines( String text ) {
        glColor3f(1,1,1);
        //printTextLines( text, 10, 10, 12, GLUT.BITMAP_8_BY_13 );
    }
    
    /**
     * Draws text.
     * @param drawable
     * @param text The String to draw.
     * @param x    The starting x raster position.
     * @param y    The starting y raster position.
     * @param h    The height of each line of text.
     * @param font The font to use (e.g. GLUT.BITMAP_HELVETICA_10).
     */
    static public void printTextLines( String text, double x, double y, double h, int font ) {
        StringTokenizer st = new StringTokenizer( text, "\n" );
        int line = 0;
        while ( st.hasMoreTokens() ) {
            String tok = st.nextToken();
            glRasterPos2d( x, y + line * h );            
            //glut.glutBitmapString(font, tok);
            line++;
        }        
    }
    
    /**
     * End drawing overlay.
     * @param drawable
     */
    static public void endOverlay() {
        glMatrixMode( GL_PROJECTION );
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
        glPopAttrib();        
    }    
    

	public static void main(String[] args) {
		new EasyViewer( "test", null );
	}

}