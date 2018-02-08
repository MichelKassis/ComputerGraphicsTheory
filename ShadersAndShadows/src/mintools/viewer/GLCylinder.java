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

public class GLCylinder {

	static public GLCylinder DEFAULT = new GLCylinder( 24, 8 );
	
	private FloatBuffer vertexBuffer;
	
	private FloatBuffer normalBuffer;
        
    private ShortBuffer indexBuffer;
    
    /** Buffer objects used by OpenGL */
    private int ebo, vbo, nbo;
    
    private int slices;
    
    private int stacks;
        
    private int bottomCapStart;
    
    private int topCapStart;
    
    /**
     * Creates a cylinder of heigth 1 in z with a base at the origin and with radius 1
     * @param slices
     * @param stacks
     */
    public GLCylinder( int slices, int stacks ) {
    	this.slices = slices;
    	this.stacks = stacks;
    	
		int numVertFloats = (slices * stacks + 2) * 3;		
	    ByteBuffer vbb = ByteBuffer.allocateDirect( numVertFloats * 4 ); // size of float is 4
	    vbb.order( ByteOrder.nativeOrder() );
	    vertexBuffer = vbb.asFloatBuffer();
	    	    
	    vbb = ByteBuffer.allocateDirect( numVertFloats * 4 );
	    vbb.order( ByteOrder.nativeOrder() );
	    normalBuffer = vbb.asFloatBuffer();
	    
	    for ( int i = 0 ; i < slices; i++ ) {
	    	float c = (float) Math.cos( Math.PI * 2 * i / slices );
	    	float s = (float) Math.sin( Math.PI * 2 * i / slices );
	    	for ( int j = 0; j < stacks; j++ ) {
	    		vertexBuffer.put( c );
	    		vertexBuffer.put( s );
	    		vertexBuffer.put( (float) j / (float) (stacks-1) );
	    		normalBuffer.put( c );
	    		normalBuffer.put( s );
	    		normalBuffer.put( 0 );
	    	}
	    }
		vertexBuffer.put( 0 );
		vertexBuffer.put( 0 );
		vertexBuffer.put( 0 );
		vertexBuffer.put( 0 );
		vertexBuffer.put( 0 );
		vertexBuffer.put( 1 );
	    
		// first bunch of indices are the strips for the sides, then the second term is for the two caps
		int numIndices = (stacks + 1) * 2 * slices + (slices + 2) * 2;	    
		ByteBuffer ibb = ByteBuffer.allocateDirect( numIndices * 2 ); // size of short is 2		
	    ibb.order(ByteOrder.nativeOrder());
	    indexBuffer = ibb.asShortBuffer();
	    
	    int N = slices * stacks;
		for ( int i = 0; i < slices; i++ ) {
			for ( int j = 0; j < stacks; j++ ) {
				indexBuffer.put( (short) ( i*stacks + j ) );
				indexBuffer.put( (short) ( (i*stacks + j + stacks) % N ) );
			}
		}
		
		bottomCapStart = indexBuffer.position();
		indexBuffer.put( (short) N );
		for ( int i = 0; i < slices; i++ ) {
			indexBuffer.put( (short) ((slices-1-i)*stacks) );
		}		
		indexBuffer.put( (short) ((slices-1)*stacks) );
		
		topCapStart = indexBuffer.position();
		indexBuffer.put( (short) (N+1) );
		for ( int i = slices-1; i >= 0; i-- ) {
			indexBuffer.put( (short) (N-1-i*stacks) );
		}		
		indexBuffer.put( (short) (N-1-(slices-1)*stacks) );

	    vertexBuffer.position(0);        	    
	    normalBuffer.position(0);
	    indexBuffer.position(0);
	    
	    
	    // Set up the buffers
	    ebo = GL15.glGenBuffers();
	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
	    GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW );
	    vbo = GL15.glGenBuffers();
	    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo);
	    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW );
	    nbo = GL15.glGenBuffers();
	    GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, nbo);
	    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, normalBuffer, GL15.GL_STATIC_DRAW );
	    
    }
	
	@SuppressWarnings("static-access")
	public void draw() {
		glEnableClientState( GL_VERTEX_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo );
		glVertexPointer( 3, GL_FLOAT, 0, 0 ); // would use glVertexAttrib Pointer otherwise?
		
		glEnableClientState( GL_NORMAL_ARRAY );
		GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, nbo );
		glNormalPointer( GL_FLOAT, 0, 0 );
	    	    
	    GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);

	    for ( int i = 0; i < slices; i++) {
			//indexBuffer.position( i*(2*stacks) );
			int pos = i*(2*stacks);
			GL11.glDrawElements( GL_TRIANGLE_STRIP, 2*stacks, GL_UNSIGNED_SHORT, pos*2 ); // i.e., size of short is 2
		}
	    
	    glDisableClientState( GL_NORMAL_ARRAY );	    
	    glNormal3f( 0, 0, -1 );
	    //indexBuffer.position( bottomCapStart );
	    glDrawElements( GL_TRIANGLE_FAN, slices+2, GL_UNSIGNED_SHORT, bottomCapStart*2 );	    
	    glNormal3f( 0, 0, 1 );
	    //indexBuffer.position( topCapStart );
	    glDrawElements( GL_TRIANGLE_FAN, slices+2, GL_UNSIGNED_SHORT, topCapStart*2 );	    
	    glDisableClientState( GL_VERTEX_ARRAY );	    
	}
	    
}

