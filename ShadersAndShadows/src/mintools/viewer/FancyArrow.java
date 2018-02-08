/*
 * Created on 10-Sep-2003
 */
package mintools.viewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

/**
 * @author kry
 */
public class FancyArrow {

    private Point3d from;
    private Point3d to;
    private Vector3d dir;
    private Color3f color;
    double size;
    double length;

    /**
     * transparency of the arrow, no transparency by default
     */
    public float transparency = 1;
    
    /**
     * Creates an ORANGE arrow from 0,0,0 to 0,0,1 with a girth of 0.1
     */
    public FancyArrow() {
        this( new Point3d(), new Point3d(0,0,1), Colour.orange, 0.1 );
    }

    /**
     * Create an arrow with the given parameters
     * @param from the starting point of the arrow
     * @param to   the end point (i.e., the tip of the arrow)
     * @param color the desired material colour
     * @param size the girth of the arrow
     */
    public FancyArrow( Tuple3d from, Tuple3d to, Color3f color, double size ) {
        this.from = new Point3d(from);
        this.to = new Point3d(to);
        this.size = size;
        this.color = color;
        dir = new Vector3d();
        computeArrowInfo();
    }
    
    /**
     * Draws the arrow
     * @param gl
     */
    public void draw() {
        float [] col = { color.x, color.y, color.z, transparency };        
        glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, col );
        glColor4f( color.x, color.y, color.z, transparency );
        
        glPushMatrix();
        
        // translate from to origin
        glTranslated( from.x, from.y, from.z );
        // and rotate the direction axis onto the z axis
        final Vector3d temp1 = new Vector3d();
        final Vector3d temp2 = new Vector3d();
        temp1.set( 0, 0, 1 );
        temp2.cross( temp1, dir );
        double angle = Math.acos( temp1.dot( dir ) / length ) * 180 / Math.PI;
        glRotated( angle, temp2.x, temp2.y, temp2.z );
        
        // draw the shaft        
        glPushMatrix();
        glScaled( size, size, length-size*3 );
        GLCylinder.DEFAULT.draw();
        glPopMatrix();
        
        // draw the point
        glTranslated( 0, 0, length-size*3 );
        glScaled(size*2,size*2,size*3);
        GLCone.DEFAULT.draw();
        
        glPopMatrix();
    }
    
    private void computeArrowInfo() {
        dir.sub( to, from );
        length = dir.length();
    }
            
    /**
     * @param color3f
     */
    public void setColor(Color3f color3f) {
        color = color3f;
    }
    
    /**
     * Get the current arrow colour
     * @return the current colour
     */
    public Color3f getColor() {
        return color;
    }
    
    /**
     * Sets the colour of the arrow 
     * @param r
     * @param g
     * @param b
     */
    public void setColor(float r, float g, float b) {
        // was overwriting the old colour!
        color = new Color3f(r,g,b);        
    }

    /**
     * @param point3d
     */
    public void setFrom(Tuple3d point3d) {
        setFrom(point3d.x, point3d.y, point3d.z);
    }
    
    /**
     * Sets the position of the arrow's tail.
     * @param x
     * @param y
     * @param z
     */
    public void setFrom(double x, double y, double z) {
        from.set(x, y, z);
        computeArrowInfo();
    }

    /**
     * Sets the position of the arrow's tail.
     * @param p
     */
    public void setFrom( Tuple3f p ) {
    	setFrom(p.x, p.y, p.z);
    }
    
    /**
     * Get the point from which the arrow is pointing
     * @return the from point
     */
    public Point3d getFrom() {
        return from;
    }

    /**
     * @param d
     */
    public void setSize(double d) {
        size = d;
    }

    /**
     * @param point3d
     */
    public void setTo(Point3d point3d) {
        setTo(point3d.x, point3d.y, point3d.z);
    }
    
    /** 
     * Sets the position of the arrow's head
     * @param p
     */
    public void setTo( Point3f p ) {
    	setTo( p.x, p.y, p.z );
    }
    
    /**
     * Sets the position of the arrow's head
     * @param p
     */
    public void setTo( Tuple3d p ) {
    	setTo( p.x, p.y, p.z );
    }
    
    /**
     * Sets the position of the arrow's head
     * @param p
     */
    public void setTo( double []p ) {
    	setTo( p[0], p[1], p[2] );
    }
    
    /**
     * Sets the position of the arrow's head
     * @param p
     */
    public void setTo( float []p ) {
    	setTo( p[0], p[1], p[2] );
    }
    
    /**
     * Sets the position of the arrow's head
     * @param x
     * @param y
     * @param z
     */
    public void setTo(double x, double y, double z) {
        to.set(x, y, z);
        computeArrowInfo();
    }
    
    /**
     * Gets the position of the arrow's head
     * @return the position of the arrow's head
     */
    public Point3d getTo() {
        return to;
    }
    
    /**
     * Gets the length of the arrow
     * @return the length of the arrow
     */
    public double getLength() {
        return length;
    }

}
