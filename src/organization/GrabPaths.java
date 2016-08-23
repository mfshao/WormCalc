package organization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GrabPaths {
	static int WINSIZE = 30;
	static int PXPERMM = 70;
	static String root = "//medixsrv/Nematodes/data/";
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.println("Project Name: ");
		String project = in.nextLine();
		if (project.equals("all")) {
			File file = new File(root);
			String[] names = file.list();
			for(String name : names)
			    if (new File(root + name).isDirectory()) {
			    	go(name);
			    }
		} else {
			go(project);
		}
	}
	private static void go(String project) throws Exception {
		System.out.println(project);
		File file = new File(root + project + "/path/full_path.png");
		File dir = new File("path/" + project + "");
		dir.mkdirs();
		copy(file, new File ("path/" + project + "/full_path.png"));
	}
	private static void copy(File source, File dest)
			throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}
}
