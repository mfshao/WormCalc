package deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Run this first
 * @author Administrator
 *
 */
public class LogFillCases {

	static String root = "//medixsrv/Nematodes/data/";
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.println("Project Name: ");
		String project = in.nextLine();
		if (project.equals("all")) {
			File file = new File(root);
			String[] names = file.list();
			for(String name : names)
			    if (new File(root + name).isDirectory())
			        go(name);
		} else {
			go(project);
		}
	}
	public static void go(String project) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(root + project + "/log/feature.log"));
		File file = new File(root + project + "/data/");
		file.mkdirs();
		PrintWriter pw = new PrintWriter(root + project + "/data/feature.csv");
		sc.useDelimiter(",|\r?\n|\r");
		int lf = sc.nextInt();
		int lx = sc.nextInt();
		int ly = sc.nextInt();
		int ls = sc.nextInt();
		if (lf != 0) {
			for (int i = 0; i < lf; i++) {
				pw.println(i + ",NaN,NaN");
			}
		}
		pw.println(lf + "," + lx + "," + ly);
		int err = 0;
		while (sc.hasNext()) {
			int f = sc.nextInt();
			int x = sc.nextInt();
			int y = sc.nextInt();
			int s = sc.nextInt();
			double dist = Math.sqrt(Math.pow(lx - x, 2) + Math.pow(ly - y, 2));
			//if (dist > 10) {
			//	System.out.println(lf + "->" + f + ": " + dist);
			//	Files.copy((new File("projects/tph1_f3/input/" + String.format("%0" + 6 + "d", f) + ".jpeg")).toPath(), (new File("projects/tph1_f3/verify/" + String.format("%0" + 6 + "d", f) + ".jpeg")).toPath());
			//}
			if (f > lf + 1) {
				while (lf != f - 1) {
					pw.println(++lf + ",NaN,NaN");
					err ++;
					//System.out.println(lf + " > " + f);
				}
				//System.out.println(lf + " is " + f);
			}
			//Duplicate?!
			if (lf != f) pw.println(f + "," + x + "," + y);
			lf = f;
			lx = x;
			ly = y;
		}
		pw.close();
		System.out.println("Fill Cases Complete: " + project + " Errors: " + err);
	}
}
