package deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CheckErrorsAreMovements {

	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.println("Project Name: ");
		String project = in.nextLine();
		if (project.equals("all")) {
			File file = new File("projects/");
			String[] names = file.list();
			for(String name : names)
				if (!name.contains("broken"))
			    if (new File("projects/" + name).isDirectory()) {
			    	System.out.println(name);
			    	go(name);
			    }
		} else {
			go(project);
		}
	}
	public static void go(String project) throws FileNotFoundException {
		System.out.println(project);
		Scanner tr = new Scanner(new File("projects/" + project + "/data/tracker.csv"));
		tr.useDelimiter(",|\r?\n|\r");
		tr.nextLine();
		Scanner er = new Scanner(new File("projects/" + project + "/log/error.log"));
		int c = 0;
		int positive = 0;
		int negative = 0;
		while (er.hasNext()) {
			int cmp = er.nextInt();
			boolean isMovement = false;
			while (c < cmp) {
				c = tr.nextInt();
				tr.nextDouble();
				tr.nextDouble();
				double xm = tr.nextDouble();
				double ym = tr.nextDouble();
				tr.nextDouble();
				tr.nextDouble();
				isMovement = xm > 0.0 || ym > 0.0;
			}
			if (isMovement) {
				positive++;
			} else {
				negative++;
				//System.out.println(cmp);
			}
		}
		System.out.println("True Positive: " + positive);
		System.out.println("False Positive: " + negative);
	}

}
