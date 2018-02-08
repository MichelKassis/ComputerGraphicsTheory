package mintools.examples;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import mintools.parameters.BooleanParameter;
import mintools.parameters.Vec3Parameter;
import mintools.swing.CollapsiblePanel;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.BoxRoom;
import mintools.viewer.EasyViewer;
import mintools.viewer.FancyArrow;
import mintools.viewer.FancyAxis;
import mintools.viewer.GLCylinder;
import mintools.viewer.GLPlane;
import mintools.viewer.GLSphere;
import mintools.viewer.GLWireCube;
import mintools.viewer.SceneGraphNode;

/**
 * A simple example that draws a teapot in a box room
 * @author kry
 */
public class ExampleApp implements SceneGraphNode {

    /**
     * main entry point
     * @param args
     */
    public static void main( String args[] ) {
        new EasyViewer("Example 1", new ExampleApp() );
    }
    
    private FancyArrow fa = new FancyArrow();
    
    @Override
    public void display() {        
    	glPushMatrix();
        
        boxRoom.display();
        if ( cull.getValue() ) {
            glEnable( GL_CULL_FACE );
        } else {
            glDisable( GL_CULL_FACE );
        }   
        glFrontFace( GL_CCW );
        
        //GLPlane.DEFAULT.draw();
        
        FancyAxis.DEFAULT.draw();
        
        glPushMatrix();
        glTranslated(-1,1,1);
        glColor3f( 1, 0, 0 );
        GLCylinder.DEFAULT.draw();
        glPopMatrix();
        
        glDisable( GL_LIGHTING );
        glColor3f(1, 1, 1);
        GLWireCube.DEFAULT.draw();
        glEnable( GL_LIGHTING );
        
        glPushMatrix();
        glTranslated(1,1,1);
        glColor3f( 0, 1, 0 );
        GLSphere.DEFAULT.draw();
        glPopMatrix();
        
        fa.setFrom( from.x, from.y, from.z );
        fa.setTo( to.x, to.y, to.z );
        
        fa.draw();
        
        // No more teapot... unless we add one :(
        // EasyViewer.glut.glutSolidTeapot(1);
        
        glPopMatrix();
    }
    

    BoxRoom boxRoom = new BoxRoom();
    BooleanParameter cull = new BooleanParameter("cull face", true );
    BooleanParameter drawBoxRoom = new BooleanParameter( "draw box room", true );
    Vec3Parameter from = new Vec3Parameter("From", 0, 0, 0);
    Vec3Parameter to = new Vec3Parameter("From", 3, 3, 3);
    
    @Override
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder( new TitledBorder("controls") );
        vfp.add( cull.getControls() );
        vfp.add( drawBoxRoom.getControls() );
        vfp.add( from );
        vfp.add( to );
        CollapsiblePanel cp = new CollapsiblePanel( vfp.getPanel() );
        cp.collapse();
        return cp;        
    }

    @Override
    public void init() {
    	glEnable( GL_COLOR_MATERIAL );
    }

}
