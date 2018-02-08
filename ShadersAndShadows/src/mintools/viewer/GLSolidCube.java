package mintools.viewer;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * Somewhat inefficient cube drawing code.  Should use
 * @author kry
 */
public class GLSolidCube {
	
	public static GLSolidCube DEFAULT = new GLSolidCube( GLPlane.DEFAULT );
	
	private GLPlane plane;
	
	public GLSolidCube() {
		plane = new GLPlane( 1 );
	}
	
	public GLSolidCube( int stacks ) {
		plane = new GLPlane( stacks );
	}
	
	public GLSolidCube( GLPlane plane ) {
		this.plane = plane;
	}
	
	public void draw() {
		// front
		glPushMatrix();
		glTranslatef(0,0, (float)-.5);		
		glRotatef( 180, 0, 1, 0 );
		plane.draw();
		glPopMatrix();
		
		// back
		glPushMatrix();
		glTranslatef(0,0, (float).5);				
		plane.draw();		
		glPopMatrix();
		
		//left
		glPushMatrix();
		glTranslatef( (float) -0.5, 0, 0 );
		glRotatef( -90, 0, 1, 0 );
		plane.draw();
		glPopMatrix();
		
		//right
		glPushMatrix();
		glTranslatef( (float) 0.5, 0, 0 );
		glRotatef( -90, 0, -1, 0 );
		plane.draw();
		glPopMatrix();
	
		//top
		glPushMatrix();
		glTranslatef( 0, (float) 0.5, 0 );
		glRotatef( -90, 1, 0, 0 );
		plane.draw();
		glPopMatrix();
		
		//bottom
		glPushMatrix();
		glTranslatef( 0, (float) -0.5, 0 );
		glRotatef( -90, -1, 0, 0 );
		plane.draw();
		glPopMatrix();
	}
}
	
