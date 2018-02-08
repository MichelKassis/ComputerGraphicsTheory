package mintools.viewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL15;

public class GLWireCube {
	
	private FloatBuffer vertexBuffer;
    
    private ShortBuffer indexBuffer;

    private int numIndices;
    
    /** buffer object IDs for opengl */
    private int ebo, vbo;
    
    public static GLWireCube DEFAULT = new GLWireCube( 1 );
    
    /** 
     * Creates a cube with edge length two times radius.
     */
	public GLWireCube( float radius ) {
		int numVerts = 8 * 3;
        ByteBuffer vbb = ByteBuffer.allocateDirect( numVerts * 4 ); // size of float
        vbb.order( ByteOrder.nativeOrder() );
        vertexBuffer = vbb.asFloatBuffer();
        
        vertexBuffer.put( -radius ); vertexBuffer.put( -radius ); vertexBuffer.put( -radius );
        vertexBuffer.put(  radius ); vertexBuffer.put( -radius ); vertexBuffer.put( -radius );
        vertexBuffer.put(  radius ); vertexBuffer.put(  radius ); vertexBuffer.put( -radius );
        vertexBuffer.put( -radius ); vertexBuffer.put(  radius ); vertexBuffer.put( -radius );
        vertexBuffer.put( -radius ); vertexBuffer.put( -radius ); vertexBuffer.put(  radius );
        vertexBuffer.put(  radius ); vertexBuffer.put( -radius ); vertexBuffer.put(  radius );
        vertexBuffer.put(  radius ); vertexBuffer.put(  radius ); vertexBuffer.put(  radius );
        vertexBuffer.put( -radius ); vertexBuffer.put(  radius ); vertexBuffer.put(  radius );

        numIndices= 12*2;
		ByteBuffer ibb = ByteBuffer.allocateDirect( numIndices * 2 ); // size of short		
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();

        indexBuffer.put( (short) 0 ); indexBuffer.put( (short) 1 ); 
        indexBuffer.put( (short) 1 ); indexBuffer.put( (short) 2 );
        indexBuffer.put( (short) 2 ); indexBuffer.put( (short) 3 );
        indexBuffer.put( (short) 3 ); indexBuffer.put( (short) 0 );
        indexBuffer.put( (short) 4 ); indexBuffer.put( (short) 5 ); 
        indexBuffer.put( (short) 5 ); indexBuffer.put( (short) 6 );
        indexBuffer.put( (short) 6 ); indexBuffer.put( (short) 7 );
        indexBuffer.put( (short) 7 ); indexBuffer.put( (short) 4 );
        indexBuffer.put( (short) 0 ); indexBuffer.put( (short) 4 ); 
        indexBuffer.put( (short) 1 ); indexBuffer.put( (short) 5 );
        indexBuffer.put( (short) 2 ); indexBuffer.put( (short) 6 );
        indexBuffer.put( (short) 3 ); indexBuffer.put( (short) 7 );
        
        vertexBuffer.position(0);                
        indexBuffer.position(0);
        
        // Set up the buffers
	    ebo = GL15.glGenBuffers();
	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
	    GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW );
	    vbo = GL15.glGenBuffers();
	    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo);
	    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW );
	}
	
	@SuppressWarnings("static-access")
	public void draw() {
		glEnableClientState( GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo );
		glVertexPointer( 3, GL_FLOAT, 0, 0 ); // would use glVertexAttrib Pointer otherwise?   
	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        glDrawElements( GL_LINES, numIndices, GL_UNSIGNED_SHORT, 0 );
        glDisableClientState( GL_VERTEX_ARRAY );
	}
}
