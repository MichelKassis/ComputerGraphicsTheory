/*
 * Created on May 11, 2006
 */
package mintools.viewer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.opengl.GL11.*;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import mintools.parameters.BooleanParameter;
import mintools.swing.CollapsiblePanel;
import mintools.swing.VerticalFlowPanel;

/**
 * Geometry for a box shaped room to help give our whole
 * simulation some context.
 * 
 * @author Paul Kry
 */
public class BoxRoom implements SceneGraphNode {

    private BooleanParameter drawFloor = new BooleanParameter("draw floor", true);
        
    /** Bounding box of the floor and walls */
    private double xl, xh, yl, yh, zl, zh;

    /**
     * Creates a room in which we can display something.
     */
    public BoxRoom() {
        Point3d ll = new Point3d(-2,-2,-2);
        Point3d ur = new Point3d( 2, 2, 2);
        setBoundingBox( ll, ur );
    }
    
    /**
     * Sets the bounding box of the room from the provided
     * lower left (ll) and upper right (ur) corners.
     * @param ll
     * @param ur
     */
    public void setBoundingBox( Tuple3d ll, Tuple3d ur ) {    
        xl = ll.x;
        xh = ur.x;
        yl = ll.y;
        yh = ur.y;
        zl = ll.z;
        zh = ur.z;
    }
        
    public void display() {
        if ( drawFloor.getValue () ) {
                        
            final float[] shinycolour = new float[] {0,0,0,1};
            final float[] floorcolour = { 0.5f, 0.5f, 0.65f, 1 };
            glEnable( GL_CULL_FACE );
            glColor4fv( floorcolour );
            glMaterialfv( GL_FRONT,GL_AMBIENT_AND_DIFFUSE, floorcolour );
            glMaterialfv( GL_FRONT,GL_SPECULAR, shinycolour );
            glMateriali( GL_FRONT,GL_SHININESS, 0 );
            
            // cheepo per pixel lighting imitation by drawing lots of quads
            
            int N = 10;
            
            // draw floor
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                glBegin(GL_QUAD_STRIP);
                glNormal3d( 0, 1, 0 );                
                    for ( int i = 0; i <= N; i++ ) {
                    double xa = xl + (xh-xl)*i/(N);                    
                    glVertex3d( xa, yl, za );
                    glVertex3d( xa, yl, zb );                    
                }
                glEnd();
            }
             
            // draw the left wall...            
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                glBegin(GL_QUAD_STRIP);
                glNormal3d( 1, 0, 0 );
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    glVertex3d( xl, ya, zb );
                    glVertex3d( xl, ya, za );
                }
                glEnd();
            }
            
                                   
            // draw the right wall...
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                glBegin(GL_QUAD_STRIP);
                glNormal3d( -1, 0, 0 );
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    
                    glVertex3d( xh, ya, za );
                    glVertex3d( xh, ya, zb );
                }
                glEnd();
            }
            
            // draw back wall
            for ( int j = 0; j < N; j++ ) {
                double xa = zl + (zh-zl)*j/(N);
                double xb = zl + (zh-zl)*(j+1)/(N);
                glBegin(GL_QUAD_STRIP);
                glNormal3d( 0, 0, 1);
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    glVertex3d( xa, ya, zl );
                    glVertex3d( xb, ya, zl );
                }
                glEnd();
            }
            
            // draw the front wall...
            for ( int j = 0; j < N; j++ ) {
                double xa = zl + (zh-zl)*j/(N);
                double xb = zl + (zh-zl)*(j+1)/(N);
                glBegin(GL_QUAD_STRIP);
                glNormal3d( 0, 0, -1);
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    glVertex3d( xb, ya, zh );
                    glVertex3d( xa, ya, zh );                    
                }
                glEnd();
            }            
        }    
    }
        
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder( new TitledBorder("floor controls" ) );        
        vfp.add( drawFloor.getControls() );
        CollapsiblePanel cp = new CollapsiblePanel( vfp.getPanel() );
        cp.collapse();
        return cp;
    }
    
    public void init() {
        // do nothing        
    }
 
}
