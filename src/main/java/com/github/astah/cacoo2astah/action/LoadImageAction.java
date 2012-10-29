package com.github.astah.cacoo2astah.action;


import java.awt.Frame;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.github.astah.cacoo2astah.view.DiagramViewer;

public class LoadImageAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
	    try {
	        ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
	        projectAccessor.getProject();
	        new DiagramViewer((Frame) window.getParent()).setVisible(true);
	    } catch (ProjectNotFoundException e) {
	        String message = "Project is not opened. Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE); 
	    } catch (Exception e) {
	    	JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE); 
	        throw new UnExpectedException();
	    }
	    return null;
	}


}
