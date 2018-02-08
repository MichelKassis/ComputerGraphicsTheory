package comp557.a0;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.JFrame;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Test class for checking JOGL setup
 */
public class A0App implements GLEventListener {

    private static GLU glu = new GLU();    // for gluPerspective
    private static GLUT glut = new GLUT(); // for glutWireCube 

    public static void main(String[] args) {
        new A0App();
    }
    
    public A0App() {
    	GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLCanvas glcanvas = new GLCanvas( glcapabilities );
        glcanvas.addGLEventListener(this);
        FPSAnimator animator; 
        animator = new FPSAnimator(glcanvas, 60);
        animator.start();
        final JFrame jframe = new JFrame( "JOGL OpenGL Setup Test" ); 
        jframe.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                jframe.dispose();
                System.exit( 0 );
            }
        });
        jframe.getContentPane().add( glcanvas, BorderLayout.CENTER );
        jframe.setSize( 640, 480 );
        jframe.setVisible( true );
	}

    private float viewAngle = 45.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 100.0f;

    @Override
    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
        GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
        gl.glClearDepth(1.0f); // Depth Buffer Setup
        gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Draw text
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();                
        glu.gluOrtho2D(0, width, height, 0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glColor3f(0.7f, 1.0f, 1.0f);
        gl.glRasterPos2d(3.0, 15.0);
        glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "Hello Micho" );

        gl.glMatrixMode(GL2.GL_PROJECTION); // Select The Projection Matrix
        gl.glLoadIdentity();
        // Calculate The Aspect Ratio Of The Window
        float aspectRatio = (float) width / (float) height;
        glu.gluPerspective(viewAngle, aspectRatio, nearPlane, farPlane);
        gl.glTranslated(0, 0, -5);
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glRotated( System.nanoTime()*1e-8, 1, 2, 3);
        // Draw something interesting
        glut.glutSolidSphere(1, 50, 50);
    }

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// do nothing
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// do nothing
	}
    
}
