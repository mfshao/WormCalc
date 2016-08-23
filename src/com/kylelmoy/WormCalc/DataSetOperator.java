package com.kylelmoy.WormCalc;

import java.io.File;
import java.util.Scanner;

public class DataSetOperator {
	public static final String remote_root = "//medixsrv/Nematodes/data/";
	public static final String local_root = "R:/data/";
	static boolean verbose = true;
	static int tabCount = 0;
	public static String root = remote_root;
	public static void operate(DataSetOperation dso) throws Exception {
		DataSetOperation.setRoot(root);
		Scanner in = new Scanner(System.in);
		System.out.println("Root: " + root);
		System.out.println("Project Name: ");
		String line = in.nextLine();
		Scanner input = new Scanner(line);
		while (input.hasNext()) {
			String project = input.next();
			if (project.equals("*")) project = ".*";
			project = "(?iu)" + project;
			File file = new File(root);
			String[] names = file.list();
			for(String name : names) {
				if (name.matches(project)) {
				    if (new File(root + name).isDirectory()) {
				    	operate(dso, name);
				    }
			    }
			}
		}
		input.close();
		System.out.println("====Data Set Operation Complete====");
		//System.exit(0);
	}
	public static void operate(DataSetOperation dso, String name) throws Exception {
		out(dso.getClass().getName() + ": " + name);
		incTab();
		out();
		long performanceClock = System.currentTimeMillis();
    	dso.go(name);
		decTab();
		out("Done! " + (System.currentTimeMillis() - performanceClock) + "ms");
		out();
	}
	public static void out() { out(""); }
	public static void out(String line) {
		if (verbose) {
			System.out.println(getTabs() + line);
		}
	}
	public static void setVerbose(boolean bool) {
		verbose = bool;
	}
	public static String getTabs() {
		String tabs = "";
		for (int i = 0; i < tabCount; i++) tabs += "|\t";
		return tabs;
	}
	public static void incTab() {
		tabCount ++;
	}
	public static void decTab() {
		tabCount --;
	}
	//Oh god what have I done?
	public static abstract class DataSetOperation {
		public static String root;
		public static void setRoot(String r) { root = r; }
		public abstract void go(String project) throws Exception;
		public static void out(String line) { DataSetOperator.out(line); }
	}
}