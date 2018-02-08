package mintools.viewer;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3d;

/**
 * Some minimal GLU calls
 * @author kry
 */
public class GLU {

	/**
	 * Method gluPerspective from 
	 * https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/glu/Project.java#L196
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	public static void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
		double sine, cotangent, deltaZ;
		double radians = (float) (fovy / 2 * Math.PI/ 180);

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		double[] matrix = new double[] { 1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1 };
		
		matrix[0 * 4 + 0] = cotangent / aspect;
		matrix[1 * 4 + 1] = cotangent;
		matrix[2 * 4 + 2] = - (zFar + zNear) / deltaZ;
		matrix[2 * 4 + 3] = -1;
		matrix[3 * 4 + 2] = -2 * zNear * zFar / deltaZ;
		matrix[3 * 4 + 3] = 0;

		glMultMatrixd(matrix);
	}
	
	/**
	 * Method gluLookAt
	 *
	 * @param eyex
	 * @param eyey
	 * @param eyez
	 * @param centerx
	 * @param centery
	 * @param centerz
	 * @param upx
	 * @param upy
	 * @param upz
	 */
	public static void gluLookAt( double eyex, double eyey, double eyez, double centerx, double centery, double centerz, double upx, double upy, double upz) {
		Vector3d forward = new Vector3d();
		Vector3d side = new Vector3d();
		Vector3d up = new Vector3d();

		forward.set( centerx - eyex, centery - eyey, centerz - eyez );
		up.set( upx, upy, upz );
		forward.normalize();
		side.cross(forward, up);
		side.normalize();
		up.cross(side, forward);

		double[] matrix = new double[16]; // fill in the transpose (for the inverse)
		
		matrix[0 * 4 + 0] = side.x;
		matrix[1 * 4 + 0] = side.y;
		matrix[2 * 4 + 0] = side.z;

		matrix[0 * 4 + 1] = up.x;
		matrix[1 * 4 + 1] = up.y;
		matrix[2 * 4 + 1] = up.z;

		matrix[0 * 4 + 2] = -forward.x;
		matrix[1 * 4 + 2] = -forward.y;
		matrix[2 * 4 + 2] = -forward.z;
		
		matrix[3 * 4 + 3] = 1;
		
		glMultMatrixd(matrix);
		glTranslated( -eyex, -eyey, -eyez );
	}
}
