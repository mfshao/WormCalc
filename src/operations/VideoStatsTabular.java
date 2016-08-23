package operations;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import misc.Entry;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class VideoStatsTabular extends DataSetOperation {
	public static ArrayList<ProjectEntry> pe = new ArrayList<ProjectEntry>();
	static long totalTime = 0;
	static long totalFrames = 0;
	public static void main(String[] args) throws Exception {
		DataSetOperator.operate(new VideoStatsTabular());
		//Collections.sort(pe);
		for (ProjectEntry p : pe) {
			p.print();
		}
		System.out.println("Sorted");
		Collections.sort(pe);
		for (ProjectEntry p : pe) {
			p.print();
		}
		System.out.println(getTimeStamp(totalTime));
		System.out.println(totalFrames);
	}
	public void go(String project) throws IOException {
		/*
		String head = "Machine & FPS & TPF";
		System.out.println("\\section{" + project + "}");
		System.out.println(head);
		Scanner in = new Scanner (new File(root + project + "/log/performance.log"));
		while (in.hasNext()) {
			System.out.println(in.next() + " & " + String.format("%1$,.2f", in.nextDouble()) + " & " + String.format("%1$,.2f", in.nextDouble()) + "\\\\");
		}
		in.close();
		*/
		
		int frames = 0;
		String type = "N2";
		boolean food = true;
		int bad = 0;
		int good = 0;
		double percent = 0;
		
		if (project.contains("tph1")) type = "tph1";
		if (project.contains("nf")) food = false;
		
		Scanner config = new Scanner (new File(root + project + "/config.yml"));
		while (config.hasNextLine()) {
			String line = config.nextLine();
			//System.out.println(line);
			if (line.contains("frame-end: ")) {
				frames = Integer.parseInt(line.replace("frame-end: ", ""));
				break;
			}
		}
		//System.out.println(frames);
		config.close();
		File errorLog = new File(root + project + "/log/error.log");
		if (errorLog.exists()) {
			Scanner error = new Scanner (errorLog);
			while (error.hasNext()) {
				error.next();
				bad++;
			}
		}
		
		good = frames - bad;
		percent = ((double)good / (double)frames) * 100.0;
		
		long starttime = -1;
		long endtime = 0;
		Scanner in = new Scanner(new File(root + project + "/data/tracker.csv"));
		in.useDelimiter(",|\r?\n|\r");
		long time = 0;
		for (int i = 0; i < frames; i++) {
			if(!in.hasNext()) {
				System.out.println("Not enough tracker log! " + i);
				break;
			}
			in.nextInt();
			time = in.nextInt();
			in.nextInt();
			in.nextInt();
			in.nextInt();
			in.nextBoolean();
		}
		in.close();
		totalTime += time;
		totalFrames += frames;
		endtime = time;
		
		// " & " + type + " & " + (food ? "yes" : "no") + 
		//String report = project.replace("_","\\_") + " & " + getTimeStamp(endtime) + "\\\\";// + " & " + frames + " & " + good + " (" + String.format("%1$,.2f", percent) + "\\%) & " + bad;
		String report = project + "\t" + getTimeStamp(endtime);
		pe.add(new ProjectEntry(endtime, report));
	}
	private static class ProjectEntry implements Comparable {
		final String report;
		final double length;
		public ProjectEntry (double l, String r) {
			report = r;
			length = l;
		}
		public void print() {
			System.out.println(report);
		}
		@Override
		public int compareTo(Object o) {
			return (int)(((ProjectEntry)o).length - this.length);
		}
	}
	private static String getTimeStamp(long millis) {
		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
	}
}
