/*
 * Created on Feb 25, 2005
 */
package mintools.viewer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glMultMatrixd;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;


/**
 * Implementation of a simple trackball and camera
 * 
 * @author kry
 */
public class TrackBallCamera {
    
	private boolean enable = true;
	
    private Component trackingSource;
    
    private DoubleParameter dollyRate;
    
    /**
     * This is actually a horizontal or vertical dolly, rather than a pan, which would
     * be a rotation at the camera.  
     */
    public DoubleParameter panRate;
    
    /**
     * Advancing is the same as dollying in and out
     */
    public DoubleParameter advanceRate;
    
    /**
     * The computed angle of rotation will be multipled by this value before
     * applying the rotation.  Values larger than one are useful for getting
     * useful amounts of rotation without requiring lots of mouse movement.
     */
    private DoubleParameter trackballGain;
    
    /**
     * The fit parameter describes how big the ball is relative to the smallest 
     * screen dimension.  With a square window and fit of 2, the ball will fit
     * just touch the edges of the screen.  Values less than 2 will give a ball
     * larger than the window while smaller values will give a ball contained 
     * entirely inside. 
     */
    private DoubleParameter trackballFit;
        
    /** 
     * previous trackball vector 
     */
    private Vector3f trackball_v0 = new Vector3f();

    /** 
     * current trackball vector 
     */
    private Vector3f trackball_v1 = new Vector3f();

    /**
     * previous trackball vector, Not Projected onto the sphere
     */
    private Vector3f trackball_v0np = new Vector3f();

    /**
     * current trackball vector, Not Projected onto the sphere
     */
    private Vector3f trackball_v1np = new Vector3f();
        
    /**
     * Flags of what actions are currently happening.
     */
    private int currentAction;
    
    /**
     * Our current transformation (always baked, it's smackball after all)
     */
    public Matrix4d bakedTransformation = new Matrix4d();
    
    /**
     * A flat matrix for passing to opengl, backed by our transformation matrix
     */
    public FlatMatrix4d transformation = new FlatMatrix4d(bakedTransformation);
            
    /**
     * Create a new trackball with the default settings
     */
    public TrackBallCamera() {
        bakedTransformation.setIdentity();
        createTrackBallParameters();
        createCameraParameters();
    }
    
    protected void createTrackBallParameters() {
        dollyRate = new DoubleParameter("Dolly rate", 0.005, 0.0001, 0.01);
        panRate = new DoubleParameter("Pan rate", 0.05, 0.001, 1);
        advanceRate = new DoubleParameter("Advance rate", 0.05, 0.001, 1);
        trackballGain = new DoubleParameter("trackball gain", 1.2, 0.1, 5);
        trackballFit = new DoubleParameter("trackball fit", 2.0, 0.1, 5);
    }

//    /**
//     * Attach this trackball to the given component.<p>
//     * This also removes us from any previous component to which we were 
//     * attached.
//     * @param component 
//     */
//    public void attach(Component component) {
//        if ( trackingSource != null ) {
//            trackingSource.removeMouseListener(this);
//            trackingSource.removeMouseMotionListener(this);
//        }
//        trackingSource = component;
//        trackingSource.addMouseListener(this);
//        trackingSource.addMouseMotionListener(this);
//    }

    private static final int ROTATE_BIT = 1;
    
    private static final int TWIST_BIT = 2;
    
    private static final int DOLLY_BIT = 4;
    
    private static final int PAN_BIT = 8;
    
    private static final int ADVANCE_BIT = 16;

    private Vector3f tmpVector3f = new Vector3f();

    private Vector3d tmpVector3d = new Vector3d();
    
    private Matrix4d tmpMatrix4d = new Matrix4d();
        
    private static final Vector3d Z_AXIS = new Vector3d(0, 0, 1);
      
    private AxisAngle4d tumble = new AxisAngle4d();
    
    /**
     * Compute the current_rotation matrix given two trackball vectors and 
     * a scale factor for the angle.
     * @param v0
     * @param v1
     * @param factor
     */
    private void applyRotation( Vector3f v0, Vector3f v1, float factor ) {
        tmpVector3f.cross( v0, v1 );        
        float alpha = factor * v0.angle( v1 );
        if ( v0.dot( v1 ) < 0 ) {
            alpha += Math.PI / 2.0f;
        }
        alpha *= trackballGain.getValue();
        tumble.set( tmpVector3f.x, tmpVector3f.y, tmpVector3f.z, alpha );
        tmpMatrix4d.set( tumble );     
        tmpMatrix4d.mul( bakedTransformation );
        bakedTransformation.set( tmpMatrix4d );
    }
    
    /** 
     * Compute a twist about the center of the screen given the previous
     * and current np trackball vectors. 
     * @param v0
     * @param v1
     * @param factor
     */
    private void applyTwist( Vector3f v0, Vector3f v1, float factor ) {        
        double delta = Math.atan2( v1.y, v1.x ) - Math.atan2( v0.y, v0.x );
        tumble.set( Z_AXIS, delta * factor );
        tmpMatrix4d.set(tumble);
        // accumulate
        tmpMatrix4d.mul( bakedTransformation );
        bakedTransformation.set( tmpMatrix4d );
    }
    
    private void applyFocalPointTranslation( Vector3d trans ) {
        double focalX = focalPointX.getValue() + trans.x;
        double focalY = focalPointY.getValue() + trans.y;
        double focalZ = focalPointZ.getValue() + trans.z;            
        focalPointX.setValue(focalX);
        focalPointY.setValue(focalY);
        focalPointZ.setValue(focalZ);
    }
    
    private void applyDolly( Vector3f v0, Vector3f v1, float factor ) {
        float deltaY = (v1.y - v0.y) * factor;        
        float fac = (float)Math.pow(2, deltaY * dollyRate.getValue());
        focalDistance.setValue(focalDistance.getValue() * fac);
        near.setValue(near.getValue() * fac );
        far.setValue(far.getValue() * fac );
        panRate.setValue( panRate.getValue() * fac );
    }
    
    private void applyPan( Vector3f v0, Vector3f v1, float factor ) {
        float deltaX = v0.x - v1.x;
        float deltaY = v0.y - v1.y;                 
        tmpVector3d.set(deltaX, deltaY, 0);
        tmpVector3d.scale( factor );
        transformWithTranspose( bakedTransformation, tmpVector3d );
        tmpVector3d.scale(panRate.getValue());            
        applyFocalPointTranslation( tmpVector3d );
    }
    
    private void applyAdvance( Vector3f v0, Vector3f v1, float factor ) {
        float deltaY = v0.y - v1.y;
        tmpVector3d.set(0, 0, deltaY * factor);
        transformWithTranspose( bakedTransformation, tmpVector3d );            
        tmpVector3d.scale(advanceRate.getValue());
        applyFocalPointTranslation( tmpVector3d );
    }
    
    private void doInteraction() {
        if ((currentAction & ROTATE_BIT) != 0) {
            applyRotation( trackball_v0, trackball_v1, 1 );            
        }
        if ((currentAction & TWIST_BIT) != 0) {
            applyTwist( trackball_v0np, trackball_v1np, 1 );
        }
        if ((currentAction & DOLLY_BIT) != 0) {                                    
            applyDolly( trackball_v0np, trackball_v1np, 1 );
        }
        if ((currentAction & PAN_BIT) != 0) {
            applyPan( trackball_v0np, trackball_v1np, 1 );
        }
        if ((currentAction & ADVANCE_BIT) != 0) {            
            applyAdvance( trackball_v0np, trackball_v1np, 1 );
        }        
    }
    
    /**
     * Transform a vector using the transpose of a matrix. <p>
     * t becomes m.transpose() * t <p>
     * Might this eventually want to live in vec math helpers?
     * @param m
     * @param t
     */
    static private void transformWithTranspose( Matrix4d m, Vector3d t ) {
        double x, y, z;
        x = m.m00 * t.x + m.m10 * t.y + m.m20 * t.z;
        y = m.m01 * t.x + m.m11 * t.y + m.m21 * t.z;
        z = m.m02 * t.x + m.m12 * t.y + m.m22 * t.z;
        t.set(x, y, z);
    }
    
//    /**
//     * Set trackball vector v, given the mouse position and the window size.
//     * @param point
//     * @param v
//     * @param vnp
//     */    
//    private void setTrackballVector( Point point, Vector3f v, Vector3f vnp ) {
//    	int width = trackingSource.getWidth();
//        int height = trackingSource.getHeight();
//    	setTrackballVector( point.x, point.y, width, height, v, vnp );
//    }
    
    /**
     * Set trackball vector v, given the mouse position and the window size.
     * @param point
     * @param v
     * @param vnp
     */
    private void setTrackballVector( double mouseX, double mouseY, int width, int height, Vector3f v, Vector3f vnp ) {
        double trackball_scale = trackballFit.getValue() / Math.min(width, height);
        float xcen = width / 2.0f;
        float ycen = height / 2.0f;
        v.set( (float)(mouseX - xcen), (float)(ycen - mouseY), 0 );
        vnp.set( v );
        v.scale( (float) trackball_scale );
        float x2y2 = v.x * v.x + v.y * v.y;
        if ( x2y2 < 1.0 ) {
            v.z = (float) Math.sqrt(1.0f - x2y2);
        } else {
            v.z = 0;
            v.normalize();
        }
    }
    
   
    /**
     * Decide all the actions that should be taking place based on 
     * the mouse buttons and modifiers
     * @param e
     */
    private void setAction( long window ) {
    	
    	boolean lshift = GLFW.glfwGetKey( window, GLFW.GLFW_KEY_LEFT_SHIFT ) == GLFW.GLFW_PRESS;
    	boolean lctrl = GLFW.glfwGetKey( window, GLFW.GLFW_KEY_LEFT_CONTROL ) == GLFW.GLFW_PRESS;
    	boolean b1 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_1 ) == GLFW.GLFW_PRESS;
    	boolean b2 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_3 ) == GLFW.GLFW_PRESS;
    	boolean b3 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_2 ) == GLFW.GLFW_PRESS;
    	b1 = b1 && !lshift;
    	// disable rotation when holding shift in case someone wants to do a box selection

        currentAction = 0;
        
        if ( lctrl & b1 ) currentAction |= TWIST_BIT;
        if ( lctrl & b3 ) currentAction |= ADVANCE_BIT;
        if ( !lctrl & b1 ) currentAction |= ROTATE_BIT;
        if ( !lctrl & b2 ) currentAction |= PAN_BIT;
        if ( !lctrl & b3 ) currentAction |= DOLLY_BIT;
    }
    
    public void cursorPosCallback( long window, double x, double y ) {
    	boolean b1 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_1 ) == GLFW.GLFW_PRESS;
    	boolean b2 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_3 ) == GLFW.GLFW_PRESS;
    	boolean b3 = GLFW.glfwGetMouseButton( window, GLFW.GLFW_MOUSE_BUTTON_2 ) == GLFW.GLFW_PRESS;
    	if ( b1  || b2 || b3 ) {
    		setAction( window );        
    		final double[] xpos = new double[1];
        	final double[] ypos = new double[1];
        	final int[] width = new int[1];
        	final int[] height = new int[1];
        	GLFW.glfwGetCursorPos(window, xpos, ypos);
        	GLFW.glfwGetWindowSize(window, width, height );
    		setTrackballVector( xpos[0], ypos[0], width[0], height[0], trackball_v1, trackball_v1np );
            
            if ( enable ) doInteraction();  
            
            // normalizing every time is a bit overkill, but why not
            Matrix3d m = new Matrix3d();        
            bakedTransformation.getRotationScale( m );
            m.normalizeCP();
            bakedTransformation.setRotationScale( m );
            
            // copy current vectors to previous vectors          
            trackball_v0.set( trackball_v1 );
            trackball_v0np.set( trackball_v1np );
    	} 
    }
    
    public void mouseButtonCallback( long window, int button, int action, int mods ) {    	
    	if ( action == GLFW.GLFW_PRESS ) {
    		final double[] xpos = new double[1];
        	final double[] ypos = new double[1];
        	final int[] width = new int[1];
        	final int[] height = new int[1];
        	GLFW.glfwGetCursorPos(window, xpos, ypos);
        	GLFW.glfwGetWindowSize(window, width, height );
    		setTrackballVector( xpos[0], ypos[0], width[0], height[0], trackball_v1, trackball_v1np );
            trackball_v0.set( trackball_v1 );
            trackball_v0np.set( trackball_v1np );
    	}
    }
    
    protected JPanel controlPanel;

    /**
     * Gets the controls for the trackball and the camera
     * @return controls
     */
    public JPanel getControls() {
        if ( controlPanel != null ) return controlPanel;
        
        VerticalFlowPanel panel = new VerticalFlowPanel();

        panel.add(near.getControls());
        panel.add(far.getControls());
        panel.add(isOrtho.getControls());
        panel.add(fovy.getSliderControls(false));
        panel.add(orthoWidth.getControls());
        panel.add(orthoHeight.getControls());
        panel.add(magnification.getControls());

        panel.add(focalPointX.getControls());
        panel.add(focalPointY.getControls());
        panel.add(focalPointZ.getControls());
        panel.add(focalDistance.getControls());
        
        panel.add( new JLabel("TrackBall Settings") );
        panel.add(dollyRate.getSliderControls(true));
        panel.add(panRate.getSliderControls(true));
        panel.add(advanceRate.getSliderControls(true));
        panel.add(trackballFit.getSliderControls(false));
        panel.add(trackballGain.getSliderControls(true));
        
        controlPanel = panel.getPanel();
        return controlPanel;        
    }
    
    // Intrinsic parameters
    public DoubleParameter near;
    public DoubleParameter far;
    protected BooleanParameter isOrtho;
    public DoubleParameter fovy;
    protected DoubleParameter orthoWidth;
    protected DoubleParameter orthoHeight;
    protected DoubleParameter magnification;
        
    // Extrinsic parameters
    protected DoubleParameter focalPointX;
    protected DoubleParameter focalPointY;
    protected DoubleParameter focalPointZ;

    protected DoubleParameter focalDistance;
    
    protected void createCameraParameters() {
        near = new DoubleParameter("Near distance", 10, Double.MIN_VALUE, Double.POSITIVE_INFINITY);
        far = new DoubleParameter("Far distance", 100, Double.MIN_VALUE, Double.POSITIVE_INFINITY);

        isOrtho = new BooleanParameter("Ortho", false);

        fovy = new DoubleParameter("FoVY", 45, 0, 180);
        orthoWidth = new DoubleParameter("Width", 15, 0, Double.POSITIVE_INFINITY);
        orthoHeight = new DoubleParameter("Height", 15, 0, Double.POSITIVE_INFINITY);
        magnification = new DoubleParameter("Zoom", 1, Double.MIN_VALUE, Double.POSITIVE_INFINITY);

        focalPointX = new DoubleParameter("Focal point.x", 0f, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        focalPointY = new DoubleParameter("Focal point.y", 0f, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        focalPointZ = new DoubleParameter("Focal point.z", 0f, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        focalDistance = new DoubleParameter("Focal distance", 20, 0, Double.POSITIVE_INFINITY);
    }
    
    private int[] viewportDimensions = new int[4];
    
    public float aspect=-1;
    
    public void applyProjectionTransformation() {
        if (isOrtho.getValue()) {
            double halfWidth = orthoWidth.getValue() / 2;
            double halfHeight = orthoHeight.getValue() / 2;
            glOrtho(-halfWidth, halfWidth, -halfHeight, halfHeight, near.getValue(), far.getValue());            
        } else {
            glGetIntegerv(GL_VIEWPORT, viewportDimensions);
            aspect = (viewportDimensions[2] ) / (float)(viewportDimensions[3]);
            GLU.gluPerspective(fovy.getFloatValue(), aspect, near.getFloatValue(), far.getFloatValue());
        }
    }

    public void applyViewTransformation() {
        // Dolly back
        glTranslated(0, 0, -focalDistance.getValue());
        // Zoom
        double zoom = magnification.getValue(); 
        glScaled( zoom, zoom, zoom );
        // Apply orientation
        glMultMatrixd( transformation.asArray() );        
        // Move to focal point
        glTranslated(-focalPointX.getValue(), -focalPointY.getValue(), -focalPointZ.getValue());
    }
    
    public void applyInverseViewTransformation() {
        // Move to focal point
        glTranslated(focalPointX.getValue(), focalPointY.getValue(), focalPointZ.getValue());

        // Apply orientation
        transformation.getBackingMatrix().transpose();
        glMultMatrixd( transformation.asArray() );        
        transformation.getBackingMatrix().transpose();

        // Zoom        
        double zoom = 1.0/magnification.getValue(); 
        glScaled( zoom, zoom, zoom );
        
        // Dolly back
        glTranslated(0, 0, focalDistance.getValue());
    } 

    /**
     * Sets up projection and modelview matrices with the current camera parameters.
     * Note that the current matrix values have no effect and are simply overwritten.
     * @param drawable
     */
    public void prepareForDisplay() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        applyProjectionTransformation();        
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();        
        applyViewTransformation();
    }
    
    /**
     * Manually set the dolly (zoom).
     * @param factor
     */
    public void zoom(float factor) {
        float fac = (float)Math.pow(2, -factor * dollyRate.getValue());
        focalDistance.setValue(focalDistance.getValue() * fac);
        near.setValue(near.getValue() * fac );
        far.setValue(far.getValue() * fac );
        panRate.setValue( panRate.getValue() * fac );
    }
    
    /** 
     * Manually set the focal distance
     * @param val
     */
    public void setFocalDistance( double d ) {
    	focalDistance.setValue( d );
    }
    
    public void enable( boolean val ) {
    	enable = val;
    }
}