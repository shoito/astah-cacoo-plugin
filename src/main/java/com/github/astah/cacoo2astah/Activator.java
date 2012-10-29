package com.github.astah.cacoo2astah;


import java.io.IOException;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.ICommunicationDiagram;
import com.change_vision.jude.api.inf.model.ICompositeStructureDiagram;
import com.change_vision.jude.api.inf.model.IDataFlowDiagram;
import com.change_vision.jude.api.inf.model.IERDiagram;
import com.change_vision.jude.api.inf.model.IMatrixDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.model.IRequirementDiagram;
import com.change_vision.jude.api.inf.model.IRequirementTable;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.model.IStateMachineDiagram;
import com.change_vision.jude.api.inf.model.IUseCaseDiagram;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.github.astah.cacoo2astah.updater.AutoUpdater;
import com.github.astah.cacoo2astah.util.AstahAPIUtils;
import com.github.astah.cacoo2astah.util.ConfigurationUtils;

public class Activator implements BundleActivator {
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);
	private AstahAPIUtils utils = new AstahAPIUtils();

	@SuppressWarnings("rawtypes")
	public void start(BundleContext context) {
		Class[] diagrams = {
				IActivityDiagram.class,
				IClassDiagram.class,
				ICommunicationDiagram.class,
				ICompositeStructureDiagram.class,
				IDataFlowDiagram.class,
				IERDiagram.class,
				IMatrixDiagram.class,
				IMindMapDiagram.class,
				IRequirementDiagram.class,
				IRequirementTable.class,
				ISequenceDiagram.class,
				IStateMachineDiagram.class,
				IUseCaseDiagram.class
			};
	    IDiagramViewManager diagramViewManager = utils.getDiagramViewManager();
	    
	    for (Class diagram : diagrams) {
	    	diagramViewManager.addDropTargetListener(new DiagramDropExtension(diagram));	
	    }

		Map<String, String> config = ConfigurationUtils.load();
		String updateCheckStr = config.get(ConfigurationUtils.UPDATE_CHECK);
		logger.info("Are there newer versions available? " + updateCheckStr);
		
		if ("false".equalsIgnoreCase(updateCheckStr)) {
			return;
		}
		
		runAutoUpdater();
	}

	public void stop(BundleContext context) {
	}
	
	private void runAutoUpdater() {
		new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(310000);
				} catch (InterruptedException e) {
					logger.warn(e.getMessage(), e);
				}
				
				AutoUpdater autoUpdater = new AutoUpdater();
				try {
					autoUpdater.check();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		};
	}
}
