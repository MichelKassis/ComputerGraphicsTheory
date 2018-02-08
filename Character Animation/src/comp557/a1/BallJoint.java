package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJoint extends DAGNode {
	
	DoubleParameter rotationAngleX, rotationAngleY, rotationAngleZ;

	DoubleParameter rotationX, rotationY, rotationZ;
	
	double minRotationAngleX, maxRotationAngleX;
	double minRotationAngleY, maxRotationAngleY;
	double minRotationAngleZ, maxRotationAngleZ;

	
	double xAxis, yAxis, zAxis;
	
	
	double transformationX, transformationY, transformationZ;
	
	
	
	public BallJoint(String name, double transformationX, double transformationY, double transformationZ,
			double xAxis, double yAxis, double zAxis,
			double minRotationAngleX, double maxRotationAngleX,
			double minRotationAngleY, double maxRotationAngleY,
			double minRotationAngleZ, double maxRotationAngleZ) {
		super(name);
		
		this.transformationX=transformationX;
		this.transformationY=transformationY;
		this.transformationZ=transformationZ;
		
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.zAxis = zAxis;
		
		this.minRotationAngleX = minRotationAngleX;
		this.maxRotationAngleX = maxRotationAngleX;
		
		this.minRotationAngleY = minRotationAngleY;
		this.maxRotationAngleY = maxRotationAngleY;
		
		this.minRotationAngleZ = minRotationAngleZ;
		this.maxRotationAngleZ = maxRotationAngleZ;
		
		
		dofs.add( rotationAngleX = new DoubleParameter( name+" RotationAngle", 0, minRotationAngleX, maxRotationAngleX ) );
		dofs.add( rotationAngleY = new DoubleParameter( name+" RotationAngle", 0, minRotationAngleY, maxRotationAngleY ) );
		dofs.add( rotationAngleZ = new DoubleParameter( name+" RotationAngle", 0, minRotationAngleZ, maxRotationAngleZ ) );


		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glPushMatrix();
		
		gl.glTranslated(transformationX, transformationY, transformationZ);
		
			
		gl.glRotated(rotationAngleX.getValue(), xAxis, 0 , 0);
		gl.glRotated(rotationAngleY.getValue(), 0, yAxis , 0);
		gl.glRotated(rotationAngleZ.getValue(), 0, 0 , zAxis);

		
		super.display(drawable);
		
		gl.glPopMatrix();
	
	}
	
}
