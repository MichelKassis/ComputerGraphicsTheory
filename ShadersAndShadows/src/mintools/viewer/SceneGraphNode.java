package mintools.viewer;

import javax.swing.JPanel;

/**
 * A simple scene graph node 
 * @author kry
 */
public interface SceneGraphNode {
    
    /**
     * Gives the scene a chance to perform initialization.  
     */
    public void init();

    /**
     * Displays the contents of this scene graph node and its children. 
     */
    public void display();

    /**
     * Gets a control panel associated with this node and its children.
     * @return a control panel
     */
    public JPanel getControls();

}