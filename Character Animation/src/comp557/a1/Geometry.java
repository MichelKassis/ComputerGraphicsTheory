package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class Geometry extends DAGNode {
	
	private Shape shape;
	
	double transX, transY, transZ;
	double rotateX, rotateY, rotateZ;
	double scaleX, scaleY, scaleZ;
	//DoubleParameter scaleX, scaleY, scaleZ;
	float red, green, blue;

	
	public Geometry(String name, Shape shape,
			double transX, double transY,double transZ, 
			double rotateX,double rotateY,double rotateZ,
			double scaleX, double scaleY,double scaleZ,
			float red, float green, float blue){
		super(name);
		
		this.transX = transX ;
		this.transY = transY ;
		this.transZ = transZ ;
		
		this.rotateX = rotateX ;
		this.transY = transY ;
		this.transZ = transZ ;
		
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		
		this.shape = shape;
		
		this.red = red;
		this.green = green;
		this.blue = blue;
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glPushMatrix();
		
		gl.glTranslated(transX, transY, transZ);
		gl.glRotated(rotateX, 1, 0, 0);
		gl.glRotated(rotateY, 0, 1, 0);
		gl.glRotated(rotateZ, 0, 0, 1);
		
		gl.glScaled(scaleX, scaleY, scaleZ);
		
		gl.glColor3f(red, green, blue);
		
		switch(shape) {
		
		case Cube:
			glut.glutSolidCube(1);
			break;
		case Teapot:
			glut.glutSolidTeapot(2);
			break;
		case Sphere:
			glut.glutSolidSphere (1, 50, 50);
			break;
		case Cone:
			glut.glutSolidCone (0.0, 50, 50, 0);
			break;
			
		}
		
		super.display(drawable);
		
		gl.glPopMatrix();
	
	}
	
	public enum Shape {Cube,Sphere,Teapot, Cone}
	
}
