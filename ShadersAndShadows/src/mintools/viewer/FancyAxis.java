/*
 * Created on 11-Sep-2003
 */
package mintools.viewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Class for drawing reference frames with arrow geometry.
 * The axis is a grey ball, with red greed and blue arrows pointing 
 * in the x y and z directions.
 * @author kry
 */
public class FancyAxis {
    
	static public FancyAxis DEFAULT = new FancyAxis();
	
    public double size;
    
    /**
     * Creates a new axis of size 1
     */
    public FancyAxis() {
        this( 1 );
    }
    
    /**
     * Creates a new axis of the desired size
     * @param size
     */
    public FancyAxis( double size ) {
        this.size = size;
    }
    
    /**
     * Sets the size of the axis 
     * @param size
     */
    public void setSize( double size ) {
        this.size = size;
    }
        
    /**
     * Draws the axis 
     * @param gl
     */
    public void draw() {
    	
    	glPushMatrix();
    	glScaled( size, size, size );
    	
            float [] ballCol = { 0.5f, 0.5f, 0.5f, 1 };            
            
            glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, ballCol );
            glColor4fv(ballCol);
            glEnable( GL_LIGHTING );
            glPushMatrix();
            glScaled(0.15,0.15,0.15);
            GLSphere.DEFAULT.draw();
            glPopMatrix();
            //EasyViewer.glut.glutSolidSphere( 0.15, 20, 20 );
            
            glPushMatrix();
            float [] xCol = { 1, 0, 0, 1 };
            glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xCol );
            glColor4fv(xCol);
            glRotated( 90, 0, 1, 0 );
            drawArrow();
            glPopMatrix();
            
            glPushMatrix();
            float [] yCol = { 0, 1, 0, 1 };
            glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yCol );
            glColor4fv(yCol);
            glRotated( -90, 1, 0, 0 );
            drawArrow();
            glPopMatrix();
            
            glPushMatrix();
            float [] zCol = { 0, 0, 1, 1 };
            glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zCol );
            glColor4fv(zCol);
            drawArrow();
            glPopMatrix();
           
        glPopMatrix();
    }
    
    /**
     * Draws an arrow (i.e., one axis)
     * @param gl
     */
    public void drawArrow() {
    	double r = 0.07;
    	double h = 0.8;
    	
    	// draw the shaft        
        glPushMatrix();
        glScaled( r, r, h );
        GLCylinder.DEFAULT.draw();
        glPopMatrix();
        
        // draw the point
        glPushMatrix();
        glTranslated( 0, 0, h );
        glScaled(r*2,r*2,1-h);
        GLCone.DEFAULT.draw();
        glPopMatrix();
    }
    
}
