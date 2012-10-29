package com.github.astah.cacoo2astah.view;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.json.JSONException;
import org.json.JSONObject;

public class DiagramListCellRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;

	public DiagramListCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JSONObject diagram = (JSONObject) value;
		try {
			setText(diagram.getString("title"));
		} catch (JSONException e) {
			setText("null");
		}
		setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		setPreferredSize(new Dimension(1, 25));
		return this;
	}
}