package com.mslavik.speedygrader.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class AutoUpdater {
	
	private static final String version = "3.0";
	
	public AutoUpdater() {
		checkForUpdate();
	}
	
	private String checkForUpdate(){
		HttpClientBuilder httpcb = HttpClientBuilder.create();

        HttpGet httpget = new HttpGet("https://api.github.com/repos/MitchellSlavik/SpeedyGrader/contents/dist?recursive=1");

        try {
			HttpResponse response = httpcb.build().execute(httpget);
			HttpEntity ent = response.getEntity();
			InputStream cont = ent.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(cont));
			String content = "";
			String line;
			while((line = br.readLine()) != null){
				content+=line+"\n";
			}
			content = content.substring(1, content.length()-1);
			System.out.println(content);
			JSONObject obj = new JSONObject(content);
			String name = (String) obj.get("name");
			String downloadUrl = (String) obj.get("download_url");
			URL website = new URL(downloadUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(new File(".", name));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
