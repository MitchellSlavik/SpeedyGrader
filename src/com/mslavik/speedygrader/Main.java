package com.mslavik.speedygrader;

import java.io.File;

import com.mslavik.speedygrader.gui.SpeedySplash;

public class Main {

	public static void main(String[] args) {
		if(args.length > 1){
			if(args[0].equals("-d")){
				File f = new File(".", args[1]);
				if(f.exists()){
					int tries = 0;
					while(f.exists() && !f.delete() && tries < 20){
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						tries++;
					}
				}
			}
		}
		SpeedySplash ss = new SpeedySplash();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ss.dispose();
		SpeedyGrader.getInstance();
	}

}
