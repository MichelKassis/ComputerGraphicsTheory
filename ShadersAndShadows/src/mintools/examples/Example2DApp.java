package mintools.examples;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import mintools.parameters.DoubleParameter;
import mintools.swing.CollapsiblePanel;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.EasyViewer;
import mintools.viewer.SceneGraphNode;

public class Example2DApp implements SceneGraphNode {

    @Override
    public void display() {
        
        // Probably better to completely avoid the 3D setup if we 
        // only want to draw in 2D, but this is a quick and easy way 
        // to draw in 2D
        EasyViewer.beginOverlay();
        
        glColor4d( 0,1,0,alpha.getValue() );
        glLineWidth(2);        
        glBegin( GL_LINE_STRIP );
        for ( int i = 0; i < 10; i++) {
            glVertex2d( 200 + i*10, 100 +i*i );
        }
        glEnd();
        
        glColor4d( 1,0,0,alpha.getValue() );
        glPointSize(5);
        glBegin( GL_POINTS );
        for ( int i = 0; i < 10; i++) {
            glVertex2d( 200 + i*10, 100+i*i );
        }        
        glEnd();
        
        
        // lets draw some 2D text
//        glColor4d( 1,1,1,1 );        
//        EasyViewer.printTextLines( "(100,100)", 100, 100, 12, GLUT.BITMAP_HELVETICA_10 );
//        glRasterPos2d( 200, 200 );
//        EasyViewer.printTextLines( "(200,200)\ncan have a second line of text", 200, 200, 12, GLUT.BITMAP_HELVETICA_10 );
        
        EasyViewer.endOverlay();
        
    }

    DoubleParameter alpha = new DoubleParameter( "alpha value" , 0.5, 0, 1 );
    
    @Override
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder( new TitledBorder("Transparency") );
        vfp.add( alpha.getSliderControls(false) );
        CollapsiblePanel cp = new CollapsiblePanel( vfp.getPanel() );
        //cp.collapse();
        return cp;   
    }

    @Override
    public void init() {
        // This will set up nice anti aliased lines and points
        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        glEnable( GL_LINE_SMOOTH );
        glEnable( GL_POINT_SMOOTH );
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        new EasyViewer("Example 1", new Example2DApp(), new Dimension(640,480), new Dimension(320,480) );
    }
    
}
