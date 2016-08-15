package com.mslavik.speedygrader.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import com.mslavik.speedygrader.SpeedyGrader;
import com.mslavik.speedygrader.utils.Utilities;

@SuppressWarnings("serial")
public class AutoUpdater extends JDialog implements ActionListener {

	private static final String version = "3.0";

	private JButton downloadButton;
	private String[] update;

	public AutoUpdater() {
		super(SpeedyGrader.getInstance().getGUI(), "Updater");
		update = checkForUpdate();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		if (update != null) {
			// There is an update
			JLabel label = new JLabel("<html><div style='text-align: center;'>Newer version found!</div></html>", JLabel.CENTER);
			label.setFont(SpeedyGrader.getInstance().getGUI().getTextFont());
			downloadButton = new JButton("Download version " + update[1]);
			downloadButton.addActionListener(this);
			panel.add(label, BorderLayout.NORTH);
			JPanel p = new JPanel();
			p.add(downloadButton);

			panel.add(p, BorderLayout.CENTER);
			this.setSize(300, 100);
		} else {
			// No update found
			JLabel label = new JLabel("<html><div style='text-align: center;'>No newer version found! You are all up-to-date! " + "If you are checking for updates because you found a bug and want to see if it is fixed, then I probably dont know about it. " + "Feel free to email me about the bug at Mitchell.A.Slavik@gmail.com. :)</div></html>", JLabel.CENTER);
			label.setFont(SpeedyGrader.getInstance().getGUI().getTextFont());
			panel.add(label, BorderLayout.CENTER);
			this.setSize(300, 200);
		}
		this.setContentPane(panel);

		this.setResizable(false);
		this.setLocationRelativeTo(SpeedyGrader.getInstance().getGUI());
		this.setVisible(true);
	}

	private String[] checkForUpdate() {
		HttpClientBuilder httpcb = HttpClientBuilder.create();

		HttpGet httpget = new HttpGet("https://api.github.com/repos/MitchellSlavik/SpeedyGrader/contents/dist");

		try {
			HttpResponse response = httpcb.build().execute(httpget);
			HttpEntity ent = response.getEntity();
			if (ent != null) {
				InputStream cont = ent.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(cont));
				String content = "";
				String line;
				while ((line = br.readLine()) != null) {
					content += line + "\n";
				}

				content = content.trim().substring(1, content.length() - 2);
				ArrayList<String> objsStr = new ArrayList<String>();
				while (content.contains("},{")) {
					int index = content.indexOf("},{");
					String obj = content.substring(0, index + 1);
					content = content.substring(index + 2, content.length());
					objsStr.add(obj);
				}
				objsStr.add(content);

				String greatestVersion = version;
				JSONObject greatestObj = null;

				for (String s : objsStr) {
					JSONObject obj = new JSONObject(s);
					String name = (String) obj.get("name");
					String version = getVersion(name);
					if (Utilities.versionCompare(greatestVersion, version) < 0) {
						greatestVersion = version;
						greatestObj = obj;
					}
				}
				if (greatestObj != null) {
					String name = (String) greatestObj.get("name");
					String downloadUrl = (String) greatestObj.get("download_url");
					return new String[] { name, greatestVersion, downloadUrl };
				} else {
					return null;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getVersion(String name) {
		String version = name.split("SpeedyGrader-")[1];
		version = version.substring(0, version.length() - 4);
		return version;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(downloadButton)) {
			downloadButton.setText("Downloading...");
			downloadButton.setEnabled(false);
			HttpClientBuilder httpcb = HttpClientBuilder.create();
			HttpGet httpget = new HttpGet(update[2]);
			HttpResponse response;
			try {
				response = httpcb.build().execute(httpget);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					long length = entity.getContentLength();
					InputStream inputStream = entity.getContent();
					// write the file to whether you want it.
					File outputFile = new File(".", update[0]);
					FileOutputStream fos = new FileOutputStream(outputFile);
					byte[] buffer = new byte[1024];
					long totalLen = 0;
					int len;
					while ((len = inputStream.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						totalLen += len;
					}
					fos.close();

					if (totalLen != length) {
						// We missed part of the file!!! Uh oh!
						outputFile.delete();
						JOptionPane.showMessageDialog(this, "Error downloading file! Please try again!", "Download error", JOptionPane.ERROR_MESSAGE);
						downloadButton.setText("Download version " + update[1]);
						downloadButton.setEnabled(true);
					} else {
						// We got all of it
						JOptionPane.showMessageDialog(this, "Download complete. \nSpeedyGrader will now restart to apply the update.");
						String myJar = new File(AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
						Runtime.getRuntime().exec("java -jar " + update[0] + " -d " + myJar);
						this.dispose();
						SpeedyGrader.getInstance().getGUI().dispose();
						System.exit(0);
					}
				}
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
