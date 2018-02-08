package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class HingeJoint extends DAGNode {
	
	DoubleParameter rotationAngle;
	
	double xAxis;
	double yAxis;
	double zAxis;
	
	double transformationX;
	double transformationY;
	double transformationZ;
	
	
	
	public HingeJoint(String name,
			double transformationX, double transformationY, double transformationZ,
			double xAxis, double yAxis, double zAxis,
			double minRotationAngle, double maxRotationAngle
			) {
		super(name);
		
		this.transformationX = transformationX;
		this.transformationY = transformationY;
		this.transformationZ = transformationZ;
		
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.zAxis = zAxis;
		
		dofs.add( rotationAngle = new DoubleParameter( name+" RotationAngle", 0, minRotationAngle, maxRotationAngle ) );
		
		
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glPushMatrix();
		
		gl.glTranslated(transformationX, transformationY, transformationZ);
	
		gl.glRotated(rotationAngle.getValue(), xAxis, yAxis, zAxis);
		
		super.display(drawable);
		
		gl.glPopMatrix();
	
	}
	
}
