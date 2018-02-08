package comp557lw.demo;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.ControlFrame;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.FancyAxis;
import mintools.viewer.FlatMatrix4f;
import mintools.viewer.GLSolidCube;
import mintools.viewer.GLSphere;
import mintools.viewer.TrackBallCamera;


public class ShaderDemoApp {

	public static void main( String[] args ) {
		new ShaderDemoApp();
	}

	private Dimension size = new Dimension(512,512);

	private Dimension controlSize = new Dimension(500, 500);

	private int pflProgramID;
		
	/**
	 *  The window handle
	 */
	private long window;

	private TrackBallCamera tbc = new TrackBallCamera();
	
	public ShaderDemoApp() {
		ControlFrame controlFrame = new ControlFrame("Controls");
		controlFrame.add("Scene", getControls());
		controlFrame.add("Trackball", tbc.getControls() );
		controlFrame.setSelectedTab("Scene");	                                
		controlFrame.setSize(controlSize.width, controlSize.height);
		controlFrame.setLocation(size.width + 20, 0);
		controlFrame.setVisible(true);    
		
		Thread displayLoop = new Thread() {
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
		};
	    displayLoop.start(); // start this thread
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
		window = glfwCreateWindow(size.width, size.height, "Shader Demo", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			} else if ( key == GLFW_KEY_SPACE && action == GLFW_RELEASE ) {
				useGLSLProgram.setValue( ! useGLSLProgram.getValue() );
			}
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
		        glViewport( 0, 0, width, height );
			}
		});
		glfwSetMouseButtonCallback( window, new GLFWMouseButtonCallbackI() {			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				tbc.mouseButtonCallback( window, button, action, mods );
			}
		});
		glfwSetCursorPosCallback( window, new GLFWCursorPosCallbackI() {			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				tbc.cursorPosCallback( window, xpos, ypos );				
			}});	
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



	/**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader directory 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private int createProgram( String name ) {
		List<ShaderInfo> shaders = new LinkedList<ShaderInfo>();
		shaders.add( new ShaderInfo( GL_VERTEX_SHADER, name+".vp" ) );
		shaders.add( new ShaderInfo( GL_FRAGMENT_SHADER, name+".fp" ) );
		return LoadShader.loadShaders(shaders);		
	}

	public void init() {
    	GL.createCapabilities();

		// don't need this if we're doing it in the per fragment lighting program
		// but we'll include it for when PFL is not enabled
		glEnable( GL_NORMALIZE );

		glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
		glClearDepth(1.0f);                      // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glEnable( GL_BLEND );
		glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
		glEnable( GL_LINE_SMOOTH );
		glEnable( GL_POINT_SMOOTH );

		// no extra ambient light by default !
		glLightModelfv( GL_LIGHT_MODEL_AMBIENT, new float[] {0,0,0,1} );

		// Set some default material parameters
		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, new float[] {1,1,1,1} );
		glMaterialfv( GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {1,1,1,1} );
		glMaterialf( GL_FRONT_AND_BACK, GL_SHININESS, 50 );


		// CREATE THE FRAGMENT PROGRAM FOR PER FRAGMENT LIGHTING
		pflProgramID = createProgram( "shaderdemo/red" );

	}

	private DoubleParameter lightPosx = new DoubleParameter( "light pos x", 0, -10, 10 );	
	private DoubleParameter lightPosy = new DoubleParameter( "light pos y", 10, -10, 20 );
	private DoubleParameter lightPosz = new DoubleParameter( "light pos z", 3, -10, 10 );

	public void setupLightsInWorld( ) {
		float[] position = { lightPosx.getFloatValue(), lightPosy.getFloatValue(), lightPosz.getFloatValue(), 1 };
		float[] colour = { 0.8f, 0.8f, 0.8f, 1 };
		float[] acolour = { 0, 0, 0, 1 };
		glLightfv( GL_LIGHT0, GL_SPECULAR, colour );
		glLightfv( GL_LIGHT0, GL_DIFFUSE, colour );
		glLightfv( GL_LIGHT0, GL_AMBIENT, acolour );
		glLightfv( GL_LIGHT0, GL_POSITION, position );
		glEnable( GL_LIGHT0 );
	}

	/**
	 * The light projection must be provided to the per fragment lighting program 
	 * so that we can look up the depth of the closest surface in the light depth map.  
	 * NOTE: FlatMatrix4f is a convenient wrapper that combines a vecmath Matrix4f, 
	 * as well as methods asArray() and reconstitute(), which are useful for using 
	 * the matrix with Open 
	 */
	public FlatMatrix4f lightProjectionMatrix = new FlatMatrix4f();        
	
	/**
	 * Inverse of light viewing transformation
	 */
	FlatMatrix4f VLinv = new FlatMatrix4f(); 
	
	/** 
	 * Inverse of light viewing and projection 
	 */
	FlatMatrix4f VLinvPLinv = new FlatMatrix4f();        

	public void display() {

		glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
				
		tbc.prepareForDisplay();
		
		setupLightsInWorld();

		if ( useGLSLProgram.getValue() ) {
			glUseProgram( pflProgramID ); 
			// TODO: try adding a uniform to change what the program does, for instance,
			// change the red fragment colour to be something else
			int sigmaID = glGetUniformLocation(	pflProgramID, "sigma" );	
			glUniform1f( sigmaID, sigma.getFloatValue() );
			drawScene();
		} else {
			glUseProgram( 0 ); 
			drawScene();
		}
		
	}

	public void drawScene( ) {
		final float[] orange = new float[] {1,.5f,0,1};
		final float[] red    = new float[] {1,0,0,1};
		final float[] green  = new float[] {0,1,0,1};
		final float[] blue   = new float[] {0,0,1,1};

		FancyAxis.DEFAULT.draw();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, red );
		glDisable(GL_CULL_FACE);       
		glPushMatrix();
		glTranslated(-2,0.8,0);
		glRotated(45, 0, 1, 0);
		//EasyViewer.glut.glutSolidTeapot(1); // sadly no easy teapot in LWJGL
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, green );
		glPushMatrix();
		glTranslated( 2, 1, 0 );
		GLSphere.DEFAULT.draw();
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, blue );
		glPushMatrix();
		glTranslated(0,.5,2);
		GLSolidCube.DEFAULT.draw();
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, orange );
		glPushMatrix();
		glScaled(15,0.1,15);
		glTranslated(0,-.5,0);
		GLSolidCube.DEFAULT.draw();
		glPopMatrix();
	}

	private BooleanParameter useGLSLProgram = new BooleanParameter("Use Program", false);
	private DoubleParameter sigma = new DoubleParameter( "sigma", 0, 0, 0.5 );
	
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.add( lightPosx.getSliderControls(false) );
		vfp.add( lightPosy.getSliderControls(false) );
		vfp.add( lightPosz.getSliderControls(false) );
		vfp.add( useGLSLProgram.getControls() );		
		vfp.add( sigma.getSliderControls(false));
		return vfp.getPanel();
	}
}
