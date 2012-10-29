package com.github.astah.cacoo2astah.view;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

public class ImageSelection extends TransferHandler implements Transferable {
	private static final long serialVersionUID = 1L;

	private static final DataFlavor flavors[] = { DataFlavor.imageFlavor };

	private Image image;
	
	public ImageSelection(JComponent comp) {
		if (comp instanceof JLabel) {
			JLabel label = (JLabel) comp;
			Icon icon = label.getIcon();
			if (icon instanceof ImageIcon) {
				this.image = ((ImageIcon) icon).getImage();
			}
		} else if (comp instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) comp;
			Icon icon = button.getIcon();
			if (icon instanceof ImageIcon) {
				this.image = ((ImageIcon) icon).getImage();
			}
		}
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	@Override
	public Transferable createTransferable(JComponent comp) {
		if (comp instanceof JLabel) {
			JLabel label = (JLabel) comp;
			Icon icon = label.getIcon();
			if (icon instanceof ImageIcon) {
				return this;
			}
		} else if (comp instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) comp;
			Icon icon = button.getIcon();
			if (icon instanceof ImageIcon) {
				return this;
			}
		}
		return null;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor)) {
			return image;
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavors[0].equals(flavor);
	}
}