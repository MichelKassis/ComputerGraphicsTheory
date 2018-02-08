package mintools.viewer;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

public class GLPlane {

	// Quad variables
    
//	private int vbo = 0;
//    private int ebo = 0;
    
    private int indicesCount = 0;
	
    private int size;
    
    public static GLPlane DEFAULT = new GLPlane( 2 );
        
    public GLPlane( int size ) {
    	this.size = size;
    	
//		int numVertFloats = size * size * 3;		
//	    FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer( numVertFloats );
//	    for ( int i = 0 ; i < size; i++ ) {
//	    	for ( int j = 0; j < size; j++ ) {
//	    		verticesBuffer.put( (float) ( -0.5 + i/(size - 1.0) ) );
//	    		verticesBuffer.put( (float) ( -0.5 + j/(size - 1.0) ) );
//	    		verticesBuffer.put( 0 );	    		
//	    	}
//	    }
//        verticesBuffer.flip();
//        
//        indicesCount = size * (size-1) * 2;	    
//		ShortBuffer indicesBuffer = BufferUtils.createShortBuffer(indicesCount);
//		for ( int i = 0; i < (size-1); i++ ) {
//			for ( int j = 0; j < size; j++ ) {
//				indicesBuffer.put( (short) ( i*size + j ) );
//				indicesBuffer.put( (short) ( i*size + j + size ) );
//			}
//		}
//		indicesBuffer.flip();
//
//		ebo = GL15.glGenBuffers();
//	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
//	    GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW );
//	    vbo = GL15.glGenBuffers();
//	    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo);
//	    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW );
    }
		
	public void draw() {
//		glEnableClientState( GL_VERTEX_ARRAY );
//		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo );
//		glVertexPointer( 3, GL_FLOAT, 0, 0 ); // would use glVertexAttrib Pointer otherwise?
//			    	    
//	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
//	    
//		glNormal3f(0,0,1);
//		          
//        // Draw the vertices
//        GL11.glDrawElements(GL11.GL_QUAD_STRIP, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
//         
//	    glDisableClientState( GL_VERTEX_ARRAY );

		glNormal3f(0,0,1);
		glBegin( GL_QUADS );
		glVertex3d( -0.5, -0.5, 0 );
		glVertex3d( -0.5,  0.5, 0 );
		glVertex3d(  0.5,  0.5, 0 );
		glVertex3d(  0.5, -0.5, 0 );
		glEnd();
	}
	
	/**
	 * @return the numer of divisions per side in the discretization of this plane
	 */
	public int getSize() {
		return size;
	}
	
}
