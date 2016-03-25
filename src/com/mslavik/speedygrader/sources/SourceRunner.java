package com.mslavik.speedygrader.sources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.util.concurrent.TimeUnit;

import com.mslavik.speedygrader.SpeedyGrader;
import com.mslavik.speedygrader.io.Output;

public class SourceRunner implements Runnable {

	private SpeedyGrader sg;
	private SourceFile sf;
	private int i;
	private Output o;

	public SourceRunner(SpeedyGrader sg, Output o, int i, SourceFile sf) {
		this.sg = sg;
		this.i = i;
		this.o = o;
		this.sf = sf;
	}

	@Override
	public void run() {
		try {
			ProcessBuilder b = null; 
			switch(sf.getSourceType()){
			case CPP:
				b = new ProcessBuilder("\""+SourceFile.getBinFolder().getAbsolutePath()+File.separator+sf+".exe\"");
				break;
			case JAVA:
				b = new ProcessBuilder("java", "-cp", "\""+SourceFile.getBinFolder().getAbsolutePath()+"\"", "\""+sf.toString()+"\"");
				break;
			}
			
			Process p = b.start();

			if (!sg.getInput().get(0).equals("")) {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
				out.write(sg.getInput().get(i));
				out.close();
			}
			
			if (!sg.timeoutPrograms() || (sg.timeoutPrograms() && p.waitFor(10, TimeUnit.SECONDS))) {
				String l = null;
				String s = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(p.getInputStream(), p.getErrorStream())));
				while ((l = in.readLine()) != null) {
					s += l + "\n";
				}
				o.setOutput(i, s);
			} else {
				o.setOutput(i, "Process timed out, did you have the correct input?\n");
				p.destroyForcibly();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		}
	}

}
