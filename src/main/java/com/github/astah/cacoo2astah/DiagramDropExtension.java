package com.github.astah.cacoo2astah;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Set;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import com.change_vision.jude.api.inf.editor.ClassDiagramEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.view.DiagramDropTargetListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.github.astah.cacoo2astah.util.AstahAPIUtils;

public final class DiagramDropExtension extends DiagramDropTargetListener {
	private AstahAPIUtils utils = new AstahAPIUtils();

	@SuppressWarnings("unchecked")
	public DiagramDropExtension(Class<?> targetDiagram) {
		super((Class<? extends IDiagram>) targetDiagram);
	}

	@Override
	public void dropExternalData(DropTargetDropEvent dtde) {
	}

	@Override
	public void dropModels(DropTargetDropEvent dtde, Set<?> models) {
		if (dtde == null || !dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			return;
		}

		Transferable transferable = dtde.getTransferable();
		try {
			Image image = (Image) transferable
					.getTransferData(DataFlavor.imageFlavor);
			createImage(image, getLocation(dtde));
			dtde.dropComplete(true);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createImage(Image image, Point location) {
		try {
			utils.getProjectAccessor().getTransactionManager().beginTransaction();
			
			ClassDiagramEditor editor = utils.getClassDiagramEditor();
			editor.setDiagram(utils.getDiagramViewManager().getCurrentDiagram());
			editor.createImage(image, location);
			
			utils.getProjectAccessor().getTransactionManager().endTransaction();
		} catch (InvalidEditingException e) {
			e.printStackTrace();
			utils.getProjectAccessor().getTransactionManager().abortTransaction();
		}
	}
	
	private Point getLocation(DropTargetDropEvent dtde) {
		Point location = dtde.getLocation();
		if (canUseToWorldCoordAPI()) {
			IDiagramViewManager viewManager = utils.getDiagramViewManager();
			Point2D worldCoordLocation = viewManager.toWorldCoord(location.x, location.y);
			location = new Point((int) worldCoordLocation.getX(), (int) worldCoordLocation.getY());
		}
		
		return location;
	}
	
	private int compareAstahAPIVersionTo(String target) {
		String astahAPIVersionStr = utils.getProjectAccessor().getAstahAPIVersion();
		DefaultArtifactVersion astahAPIVersion = new DefaultArtifactVersion(astahAPIVersionStr);
		DefaultArtifactVersion targetVersion = new DefaultArtifactVersion(target);
		return astahAPIVersion.compareTo(targetVersion);
	}
	
	private boolean canUseToWorldCoordAPI() {
		return compareAstahAPIVersionTo("6.6.4") >= 0;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}