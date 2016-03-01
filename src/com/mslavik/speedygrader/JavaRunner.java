package com.mslavik.speedygrader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.util.concurrent.TimeUnit;

public class JavaRunner implements Runnable {

	private SpeedyGrader sg;
	private int i;
	private String name;
	private String binFolder;
	private Output o;

	public JavaRunner(SpeedyGrader sg, Output o, int i, String name, String binFolder) {
		this.sg = sg;
		this.i = i;
		this.name = name;
		this.o = o;
		this.binFolder = binFolder;
	}

	@Override
	public void run() {
		try {
			Process pro2 = Runtime.getRuntime().exec("java -cp \"" + binFolder + "\" " + name);

			if (!sg.getInput().get(0).equals("")) {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(pro2.getOutputStream()));
				out.write(sg.getInput().get(i));
				out.close();
			}
			System.out.println("Waiting");
			if (pro2.waitFor(10, TimeUnit.SECONDS)) {
				String l = null;
				String s = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(new SequenceInputStream(pro2.getInputStream(), pro2.getErrorStream())));
				while ((l = in.readLine()) != null) {
					s += l + "\n";
				}
				o.setOutput(i, s);
			} else {
				o.setOutput(i, "Process timed out, did you have the correct input?\n");
				pro2.destroyForcibly();
			}
			System.out.println("Stop waiting");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
