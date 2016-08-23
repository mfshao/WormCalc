package deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Run this second
 * @author Administrator
 *
 */
public class CombineParsedLogAndFeatures {
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
			    	System.out.println(name);
			    	go(name);
			    }
		} else {
			go(project);
		}
	}
	public static void go(String project) throws FileNotFoundException {
		Scanner feat = new Scanner(new File(root + project + "/data/feature.csv"));
		feat.useDelimiter(",|\r?\n|\r");
		Scanner sc = new Scanner(new File(root + project + "/data/tracker.csv"));
		//sc.nextLine();
		sc.useDelimiter(",|\r?\n|\r");
		PrintWriter pw = new PrintWriter(root + project + "/data/globalCoordinates.csv");
		for (int i = 0; i < 7; i++)
			sc.next();
		//for (int i = 0; i < 14; i++)
			//System.out.println(sc.next());
		double xoff = 0;
		double yoff = 0;
		while (feat.hasNext()) {
			int f1 = feat.nextInt();
			//System.out.println(f1);
			double dx = feat.nextDouble();
			double dy = feat.nextDouble();
			int x,y;
			if (Double.isNaN(dx)) {
				x = Integer.MIN_VALUE;
				y = Integer.MIN_VALUE;
			} else {
				x = (int)dx;
				y = (int)dy;
			}
			int f = Integer.parseInt(sc.next());
			//System.out.println(f);
			if (f1 != f) {
				throw new Error("Frame number mismatch! " + f1 + "," + f);
			}
			double timeElapsed = Double.parseDouble(sc.next());
			double timeDelta = Double.parseDouble(sc.next());
			double xmove = Double.parseDouble(sc.next());
			double ymove = Double.parseDouble(sc.next());
			double pxPerMM = Double.parseDouble(sc.next());
			double MMPerStep = Double.parseDouble(sc.next());
			if (xmove != 0 || ymove != 0) {
				double vel = pxPerMM * MMPerStep;
				xoff += xmove * vel;
				yoff += ymove * vel;
			}
			int gx = (int)(x - xoff);
			int gy = (int)(y - yoff);
			if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE) {
				pw.println(f + "," + timeElapsed + "," + timeDelta + ",NaN,NaN");
			}
			else pw.println(f + "," + timeElapsed + "," + timeDelta + "," + gx + "," + gy);
		}
		pw.close();
		System.out.println("Combining Complete: " + project);
	}
}
