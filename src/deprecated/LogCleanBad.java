package deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import stdlib.Point2D;

/**
 * Run this third (Optional, cleans up bad points)
 * @author Administrator
 *
 */
public class LogCleanBad {
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
		Scanner sc = new Scanner(new File(root + project + "/data/globalCoordinates.csv"));
		PrintWriter pw = new PrintWriter(root + project + "/data/data.csv");
		sc.useDelimiter(",|\r?\n|\r");
		ArrayList<Point2D> context = new ArrayList<Point2D>();
		ArrayList<String> data = new ArrayList<String>();
		int lf = sc.nextInt();
		double lTE = Double.parseDouble(sc.next());
		double lTD = Double.parseDouble(sc.next());
		double ldx = Double.parseDouble(sc.next());
		double ldy = Double.parseDouble(sc.next());
		int lx,ly;
		if (Double.isNaN(ldx)) {
			lx = Integer.MIN_VALUE;
			ly = Integer.MIN_VALUE;
		} else {
			lx = (int)ldx;
			ly = (int)ldy;
		}
		context.add(new Point2D(lx,ly,lf));
		data.add(lf + "," + lTE + "," + lTD + ",");
		pw.println(lf + "," + lTE + "," + lTD + "," + lx + "," + ly);
		int c = 0;
		while (sc.hasNext()) {
			int f = sc.nextInt();
			double timeElapsed = Double.parseDouble(sc.next());
			double timeDelta = Double.parseDouble(sc.next());
			double dx = sc.nextDouble();
			double dy = sc.nextDouble();
			int x,y;
			if (Double.isNaN(dx)) {
				x = Integer.MIN_VALUE;
				y = Integer.MIN_VALUE;
			} else {
				x = (int)dx;
				y = (int)dy;
			}
			context.add(new Point2D(x,y,f));
			data.add(f + "," + timeElapsed + "," + timeDelta + ",");
			if (context.size() > 3) {
				context.remove(0);
				data.remove(0);
			}
			if (context.size() == 3) {
				Point2D comp = context.get(1);
				boolean fail = false;
				for (Point2D p : context) {
					if (p.distanceTo(comp) > 10) {
						fail = true;
					}
				}
				if (fail || x == Integer.MIN_VALUE) {
					pw.println(data.get(1) + "NaN,NaN");
					c++;
				} else {
					pw.println(data.get(1) + (int)comp.x() + "," + (int)comp.y());
				}
			}
		}
		Point2D comp = context.get(2);
		pw.println(data.get(2) + (int)comp.x() + "," + (int)comp.y());
		pw.close();
		System.out.println("Cleaning Complete: " + project + " Removed: " + c);
	}
}
