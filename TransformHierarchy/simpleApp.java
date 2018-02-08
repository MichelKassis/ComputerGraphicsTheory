package comp557.examples;

import javax.swing.JPanel;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.gl2.GLUT;

import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.EasyViewer;
import mintools.viewer.FancyAxis;
import mintools.viewer.SceneGraphNode;

public class simpleApp implements SceneGraphNode {

	public static void main( String[] args ) {
		new EasyViewer( "example", new simpleApp() );
	}
	
	public simpleApp() {
		// do nothing
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		FancyAxis.draw( drawable );	
		drawLabel( gl, "Canonical" );
		
		double s = scale.getValue();
		gl.glScaled( s, s, s );
		
		gl.glPushMatrix();
		gl.glTranslated(0, b.getValue(), 0);
		gl.glTranslated(1.5,0, 0);
		gl.glScaled(3, .5, .5);
		EasyViewer.glut.glutSolidCube(1);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		
		gl.glTranslated(a.getValue(), 0, 0);
		
		double rad2deg = 180.0/3.14;
		gl.glRotated( x.getValue() * rad2deg, 1, 0, 0);
		gl.glRotated( y.getValue() * rad2deg, 0, 1, 0);
		gl.glRotated( z.getValue() * rad2deg, 0, 0, 1);
		
		//EasyViewer.glut.glutSolidTeapot(1);
		FancyAxis.draw( drawable );	
		drawLabel( gl, "Object" );

		gl.glTranslated(1.5,0, 0);
		gl.glScaled(3, .5, .5);

		EasyViewer.glut.glutSolidCube(1);

		gl.glPopMatrix();
		
	}
	
	DoubleParameter scale = new DoubleParameter( "scale", 1, 0.1, 10 );
	
	DoubleParameter x = new DoubleParameter( "x", 0, -3.14, 3.14 );
	DoubleParameter y = new DoubleParameter( "y", 0, -3.14, 3.14 );
	DoubleParameter z = new DoubleParameter( "z", 0, -3.14, 3.14 );
	DoubleParameter a = new DoubleParameter( "a", 0, -3.14, 3.14 );
	DoubleParameter b = new DoubleParameter( "b", 0, -3.14, 3.14 );
	DoubleParameter c = new DoubleParameter( "c", 0, -3.14, 3.14 );

	@Override
	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.add( scale.getSliderControls(true) );
		vfp.add( x.getSliderControls(false) );
		vfp.add( y.getSliderControls(false) );
		vfp.add( z.getSliderControls(false) );

		vfp.add( a.getSliderControls(false) );
		vfp.add( b.getSliderControls(false) );
		vfp.add( c.getSliderControls(false) );
		return vfp.getPanel();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		// do nothing
	}
	
    public void drawLabel( GL2 gl, String msg ) {
    	gl.glDisable( GL2.GL_LIGHTING );
    	gl.glColor4f(1,1,1,1);
    	gl.glRasterPos3f( .3f,.3f,.3f );    	
    	EasyViewer.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, msg );
    	gl.glEnable( GL2.GL_LIGHTING );
    }
    
}
