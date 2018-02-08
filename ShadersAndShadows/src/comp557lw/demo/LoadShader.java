package comp557lw.demo;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

public class LoadShader {
	
	private static CharSequence fromFile(String filename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			FileChannel fc = fis.getChannel();
			// Create a read-only CharBuffer on the file
			ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
			CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
			return cbuf;
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println("Problems reading source: " + filename );
		}
		return null;
	}

	public static int loadShaders( List<ShaderInfo> shaders ) {
	    if ( shaders == null || shaders.size() == 0 ) { return 0; }
	    int program = GL20.glCreateProgram();
	    for ( ShaderInfo entry : shaders ) {
	    	if ( entry.type == GL_NONE ) break;
	        int shader = glCreateShader( entry.type );
	        entry.shader = shader;
	        CharSequence source = fromFile ( entry.fileName );
	        if ( source == null ) {
	            for ( ShaderInfo e : shaders ) {
	                if ( e.type != GL_NONE ) {
	                	glDeleteShader( entry.shader );
	                	entry.shader = 0;
	                }
	            }
	            return 0;
	        }
	        glShaderSource( shader, source );
	        glCompileShader( shader );
	        int compiled = GL20.glGetShaderi( shader, GL_COMPILE_STATUS );
	        if ( compiled == 0 ) {
	            System.err.println( "Shader compilation failed: ");
	            System.err.println( glGetShaderInfoLog( shader ) );
	            return 0;
	        }
	        glAttachShader( program, shader );
	    }
	    glLinkProgram( program );
	    int linked = glGetProgrami( program, GL_LINK_STATUS );
	    if ( linked == 0 ) {
	        System.err.println( "Shader linking failed: " );
	        System.err.println( glGetProgramInfoLog( program ) );
	        for ( ShaderInfo e : shaders ) {
	        	if ( e.type != GL_NONE ) {
	        		glDeleteShader( e.shader );
	        		e.shader = 0;
	        	}
	        }
	        return 0;
	    }
	    return program;
	}
	
}
