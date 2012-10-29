package com.github.astah.cacoo2astah.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.border.EtchedBorder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.astah.cacoo2astah.CacooClient;
import com.github.astah.cacoo2astah.util.AstahAPIUtils;

public class SheetsViewPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int COMPONENT_WIDTH = 180;
	private static final int COMPONENT_HEIGHT = 150;
	private static final int THUMBNAIL_WIDTH = 160;
	private static final int THUMBNAIL_HEIGHT = 120;
	private static AstahAPIUtils utils = new AstahAPIUtils();
	
	private CacooClient client;
	private static String cacheDir;
	static {
		String userHome = System.getProperty("user.home");
		cacheDir = userHome + File.separator + ".astah" + File.separator + utils.getEdition() + File.separator + "cacoo2astah";
	}

	public SheetsViewPane() {
		initComponents();
	}

	private void initComponents() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
//		setBackground(Color.WHITE);
	}
	
	public void setCacooClient(CacooClient client) {
		this.client = client;
	}
	
	public void updateContents(final JSONObject diagram) {
		removeAll();
		new SwingWorker<Void, Void>() {
			int count = 0;
			@Override
			protected Void doInBackground() throws Exception {
				JSONArray sheets = client.getSheets(diagram.getString("diagramId"));
				count = sheets.length();
				for (int i = 0; i < count; i++) {
					JSONObject sheet = sheets.getJSONObject(i);
					String imageUrlWithAPIKey = sheet.getString("imageUrlForApi") + "?apiKey=" + client.getAPIKey();
					String name = sheet.getString("name");
					
					File cache = ImageDownloader.getImageCache(imageUrlWithAPIKey);
					if (cache.exists()) {
						add(createImageComponent(name, cache.toURI().toURL()));
						if (Calendar.getInstance().getTime().getTime() - cache.lastModified() > 24 * 60 * 60 * 1000) {
							ImageDownloader.download(imageUrlWithAPIKey);
						}
					} else {
						add(createImageComponent(name, new URL(imageUrlWithAPIKey)));
						ImageDownloader.download(imageUrlWithAPIKey);
					}
				}
				return null;
			}
			
			protected void done() {
				int width = (COMPONENT_WIDTH + 12) * 2;
				int height = (int) ((COMPONENT_HEIGHT + 12) * Math.ceil((double) count / (double) 2));
				SheetsViewPane.this.setPreferredSize(new Dimension(width, height));
				SheetsViewPane.this.updateUI();
			}
		}.execute();
	}

	static class ImageDownloader {
		private static final int THREADS = 10;
		private static final Executor exec = Executors.newFixedThreadPool(THREADS);
		
		public static void download(final String imageUrlWithAPIKey) {
			Runnable task = new Runnable() {
				public void run() {
					writeCache(imageUrlWithAPIKey);
				}
			};
			exec.execute(task);
		}
		
		public static String extractImageFileName(String imageUrlWithAPIKey) {
			return imageUrlWithAPIKey.substring(
					imageUrlWithAPIKey.lastIndexOf("/") + 1, 
					imageUrlWithAPIKey.indexOf("?"));
		}
		
		public static File getImageCache(String imageUrlWithAPIKey) {
			return new File(cacheDir, extractImageFileName(imageUrlWithAPIKey));
		}
		
		private static void writeCache(String imageUrlWithAPIKey) {
			InputStream in = null;
			FileOutputStream out = null;
			try {
				URL url = new URL(imageUrlWithAPIKey);
				URLConnection conn = url.openConnection();
				in = conn.getInputStream();

				if (!new File(cacheDir).isDirectory()) {
					new File(cacheDir).mkdirs();
				}

				File cache = getImageCache(imageUrlWithAPIKey);
				cache.deleteOnExit();
				out = new FileOutputStream(cache, false);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = in.read(bytes)) > 0) {
					out.write(bytes, 0, len);
				}
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try { out.close(); } catch (IOException e) {}
				}
				if (in != null) {
					try { in.close(); } catch (IOException e) {}
				}
			}
		}
	}

	private JPanel createImageComponent(String title, final URL imageUrl) {
		final JPanel comp = new JPanel(new BorderLayout(0, 4));
		final JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
		
		new SwingWorker<Void, Void>() {
			JLabel image;
			JLabel nonScaledImage;
			@Override
			protected Void doInBackground() throws Exception {
				ImageIcon icon = new ImageIcon(imageUrl);
				nonScaledImage = new JLabel(new ImageIcon(icon.getImage()));
				
				int imageWidth = THUMBNAIL_WIDTH;
				int imageHegight = THUMBNAIL_HEIGHT;
				if (icon.getIconWidth() > icon.getIconHeight()) {
					imageHegight = -1;
				} else {
					imageWidth = -1;
				}
				
				Image scaledImage = icon.getImage().getScaledInstance(imageWidth, imageHegight, Image.SCALE_AREA_AVERAGING | Image.SCALE_SMOOTH);
				icon.setImage(scaledImage);
				image = new JLabel(icon);
				return null;
			}
			
			protected void done() {
				draggable(image, nonScaledImage);
				comp.remove(loading);
				comp.add(image, BorderLayout.CENTER);
				image.updateUI();
			}
		}.execute();
		
		JLabel label = new JLabel(title, JLabel.CENTER);
		
		comp.add(loading, BorderLayout.CENTER);
		comp.add(label, BorderLayout.SOUTH);
		comp.setPreferredSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
		comp.setBackground(Color.WHITE);
		comp.setBorder(new EtchedBorder());
		
		return comp;
	}

	private void draggable(JComponent virtualComp, JComponent realComp) {
		virtualComp.setTransferHandler(new ImageSelection(realComp));
		MouseListener listener = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JComponent comp = (JComponent) me.getSource();
				TransferHandler handler = comp.getTransferHandler();
				handler.exportAsDrag(comp, me, TransferHandler.COPY);
			}
		};
		virtualComp.addMouseListener(listener);
	}
}