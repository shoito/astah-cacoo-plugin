package com.github.astah.cacoo2astah.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.astah.cacoo2astah.CacooClient;
import com.github.astah.cacoo2astah.util.AstahAPIUtils;

public class DiagramViewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String PROPERTY_FILE_NAME = "cacoo2astah.properties";
	private static AstahAPIUtils utils = new AstahAPIUtils();
	
	private CacooClient client;
	private JList diagramList;
	private SheetsViewPane sheetsViewPane;
	
	private DefaultListModel diagramListModel;
	private static String homeEditionDir;
	static {
		String userHome = System.getProperty("user.home");
		homeEditionDir = userHome + File.separator + ".astah" + File.separator + utils.getEdition();
		File dir = new File(homeEditionDir);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
	}
	
	public static void main(String args[]) throws Exception {
		new DiagramViewer(null).setVisible(true);
	}
	
	public DiagramViewer(Frame parent) {
		String apiKey = getAPIKey();
		client = new CacooClient(apiKey);
		if (StringUtils.isBlank(apiKey)) {
			showAPIKeyInputDialog();
		}
		
		initComponents();
		setLocationRelativeTo(parent);
		
		getDiagrams();
	}
	
	private void getDiagrams() {
		new SwingWorker<Void, Void>() {
			JSONArray diagrams;
			@Override
			protected Void doInBackground() throws Exception {
				diagrams = client.getDiagrams();
				return null;
			}
			
			protected void done() {
				int count = diagrams.length();
				diagramListModel.clear();
				for (int i = 0; i < count; i++) {
					try {
						diagramListModel.addElement(diagrams.getJSONObject(i));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				setDefaultSelection();
			}
		}.execute();
	}

	private String getAPIKey() {
		String apiKey = "";
		Properties prop = new Properties();
		try {
			File propertyFile = new File(homeEditionDir, PROPERTY_FILE_NAME);
			prop.load(new FileInputStream(propertyFile));
			apiKey = prop.getProperty("api_key");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return apiKey;
	}

	private void setDefaultSelection() {
		if (!diagramListModel.isEmpty()) {
			diagramList.setSelectedIndex(0);
		}
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		diagramListModel = new DefaultListModel();
		
		JSplitPane baseSplitPane = new JSplitPane();
		JScrollPane leftScrollPane = new JScrollPane();
		JScrollPane rightScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		diagramList = new JList();
		diagramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		diagramList.addListSelectionListener(new DiagramListSelectionListener());
		
		sheetsViewPane = new SheetsViewPane();
		sheetsViewPane.setCacooClient(client);

		diagramList.setCellRenderer(new DiagramListCellRenderer());
		diagramList.setModel(diagramListModel);

		leftScrollPane.setBorder(new TitledBorder("図の一覧 - Cacoo"));
		leftScrollPane.setViewportView(diagramList);

		rightScrollPane.setBorder(new TitledBorder("シートの一覧"));
		rightScrollPane.setViewportView(sheetsViewPane);

		baseSplitPane.setLeftComponent(leftScrollPane);
		baseSplitPane.setRightComponent(rightScrollPane);
		baseSplitPane.setDividerLocation(180);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(baseSplitPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton settingButton = new JButton("設定");
		settingButton.addMouseListener(new SettingButtonMouseListener());
		JButton closeButton = new JButton("Close");
		closeButton.addMouseListener(new CloseButtonMouseListener());
		buttonPanel.add(new JLabel("astahの図上にシートをドラッグ＆ドロップしてください"));
		buttonPanel.add(settingButton);
		buttonPanel.add(closeButton);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setSize(640, 480);
	}

	private void showAPIKeyInputDialog() {
		String apiKey = JOptionPane.showInputDialog("下記ページで表示されるAPIキーを入力してください.\nhttps://cacoo.com/profile/api");

		if (apiKey == null) {
		} else {
			Properties prop = new Properties();
			try {
				File propertyFile = new File(homeEditionDir, PROPERTY_FILE_NAME);
				if (!propertyFile.exists()) {
					propertyFile.createNewFile();
				}
				prop.load(new FileInputStream(propertyFile));
				prop.setProperty("api_key", apiKey);
				prop.store(new FileOutputStream(propertyFile), null); 
				client.setAPIKey(apiKey);
				getDiagrams();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class DiagramListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}

			int selectedIndex = diagramList.getMinSelectionIndex();
			JSONObject selectedDiagram = (JSONObject) diagramListModel.getElementAt(selectedIndex);
			sheetsViewPane.updateContents(selectedDiagram);
		}
	}

	class SettingButtonMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent mouseevent) {
			showAPIKeyInputDialog();
		}
	}
	
	class CloseButtonMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent mouseevent) {
			DiagramViewer.this.dispose();
		}
	}
}