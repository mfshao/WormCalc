package operations;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import misc.Entry;
import stdlib.StdDraw;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;


public class CellOccupancy extends DataSetOperation {
	static int PXPERMM = 70;
	static double BIN_SIZE = 1; //In micrometers
	static String projectName;
	final static double BIN_WIDTH = 50;
	final static double SCREEN_WIDTH = 400;
	final static double SCREEN_HEIGHT = 400;
	final static double MIN_TIME = 0;//40.0 * 60.0;
	final static double MAX_TIME = Double.MAX_VALUE;//40.0 * 60.0;
	final static double TIME_BIN = 1.0 * 60.0;
	//final static double OCC_MAX = 1000.0;
	final static double COLOR_RANGE = 0.7;
	final static boolean visualize = true;
	final static String filename = "occupancy_variable";
	static double whole, half, quarter, third, tenth, hundredth, maxDimension, timeMax;
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new CellOccupancy()); }
	public void go(String project) throws Exception {
		//System.out.print(project);
		projectName = project;
		timeMax = 0;
		Scanner data = new Scanner(new File(root + project + "/data/movementFeatures.csv"));
		data.useDelimiter(",|\r?\n|\r");
		ArrayList<Entry> entries = new ArrayList<Entry>();
		double starttime = -1;
		double endtime = 0;
		while(data.hasNext()) {
			Entry e = new Entry(data);
			if (starttime == -1) starttime = e.time;
			endtime = e.time - starttime;
			if (e.time < MIN_TIME) continue;
			if (e.time - starttime > MAX_TIME) break;
			if (e.isValid()) entries.add(e);
		}
		if (endtime < MIN_TIME * 0.99) return;
		double minx = Integer.MAX_VALUE;
		double maxx = Integer.MIN_VALUE;
		double miny = Integer.MAX_VALUE;
		double maxy = Integer.MIN_VALUE;
		for (Entry e : entries) {
			if (e.x < minx) minx = e.x;
			if (e.x > maxx) maxx = e.x;
			if (e.y < miny) miny = e.y;
			if (e.y > maxy) maxy = e.y;
		}
		int meanx = (int) ((maxx + minx) / 2);
		int meany = (int) ((maxy + miny) / 2);
		double rangex = maxx - minx;
		double rangey = maxy - miny;
		double mdim = Math.max(rangex, rangey) / 2;
		int binLength = (int)(Math.ceil((Math.max(rangex, rangey) / PXPERMM) / BIN_SIZE));
		binLength *= 1.1;
		//binLength = 113; //full
		//binLength = 90; //40min
		//binLength = 65;
		maxDimension = ((binLength+1) * BIN_WIDTH) / 2;
		//maxDimension = 2825.0; //override
		//System.out.println(maxDimension);
		double[][] occupancy = new double[binLength][binLength];
		double xoff = meanx - mdim;
		double yoff = meany - mdim;
		int startx, starty, endx, endy;
		Entry s = entries.get(10);
		startx = (int) (((s.x - xoff) / PXPERMM) / BIN_SIZE);
		starty = (int) (((s.y - yoff) / PXPERMM) / BIN_SIZE);
		Entry end = entries.get(entries.size()-10);
		endx = (int) (((end.x - xoff) / PXPERMM) / BIN_SIZE);
		endy = (int) (((end.y - yoff) / PXPERMM) / BIN_SIZE);
		PrintWriter occOut = new PrintWriter(new File(root + project + "/data/occupancy.csv"));
		boolean[][] visited = new boolean[binLength][binLength];
		double currentTime = 0;
		int max = 0;
		for (Entry e : entries) {
			if (Double.isNaN(e.x) && Double.isNaN(e.y)) continue; 
			int mx = (int) (((e.x - xoff) / PXPERMM) / BIN_SIZE);
			int my = (int) (((e.y - yoff) / PXPERMM) / BIN_SIZE);
			occupancy[mx][my] += e.timeDelta;
			visited[mx][my] = true;
			if (e.time > currentTime + TIME_BIN) {
				currentTime = e.time;
				int visitCount = 0;
				for (boolean[] a : visited)
					for (boolean b : a)
						if (b) visitCount++;
				if (visitCount > max) max = visitCount;
				visited = new boolean[binLength][binLength];
				occOut.println((int)currentTime + "," + visitCount);
			}
		}
		occOut.close();
		System.out.println(max);
		double totalTime = entries.get(entries.size() -1).time - entries.get(0).time;
		int cellsVisited = 0;
		int minCellX = Integer.MAX_VALUE;
		int maxCellX = Integer.MIN_VALUE;
		int minCellY = Integer.MAX_VALUE;
		int maxCellY = Integer.MIN_VALUE;
		for (int y = 0; y < binLength; y++) {
			for (int x = 0; x < binLength; x++) {
				if(occupancy[x][y] > timeMax) timeMax = occupancy[x][y];
				if (occupancy[x][y] != 0)  {
					cellsVisited ++;
				}
			}	
		}

		DecimalFormat df = new DecimalFormat("#.###");
		String info = project + " & " + cellsVisited + " & " + df.format((cellsVisited/(totalTime/60))) + " & " + getTimeStamp((int)(totalTime * 1000)) + " \\\\";
		//System.out.println(info);
		//report(info);
		//timeMax = 100;
		if (visualize) {
			drawInit();
			
			//Determine centering offset
			int sumx = 0;
			int sumy = 0;
			int c = 0;
			for (int y = 0; y < binLength; y++) {
				for (int x = 0; x < binLength; x++) {
					if (occupancy[x][y] == 0) continue;
					else {
						sumx += x;
						sumy += y;
						c++;
						if (x < minCellX) minCellX = x;
						if (x > maxCellX) maxCellX = x;
						if (y < minCellY) minCellY = y;
						if (y > maxCellY) maxCellY = y;
					}
				}
			}
			meanx = (int) ((maxCellX + minCellX) / 2);
			meany = (int) ((maxCellY + minCellY) / 2);
			int binCenter = binLength / 2;
			int offx = binCenter - (meanx);
			int offy = binCenter - (meany);

			//Mark Start and End
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.filledRectangle((startx + offx) * BIN_WIDTH + (BIN_WIDTH / 2), (starty + offy) * BIN_WIDTH + (BIN_WIDTH / 2), (BIN_WIDTH / 2)+4, (BIN_WIDTH / 2)+4);
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.filledRectangle((endx + offx) * BIN_WIDTH + (BIN_WIDTH / 2), (endy + offy) * BIN_WIDTH + (BIN_WIDTH / 2), (BIN_WIDTH / 2)+4, (BIN_WIDTH / 2)+4);
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.filledRectangle((startx + offx) * BIN_WIDTH + (BIN_WIDTH / 2), (starty + offy) * BIN_WIDTH + (BIN_WIDTH / 2), (BIN_WIDTH / 2)-4, (BIN_WIDTH / 2)-4);
			StdDraw.filledRectangle((endx + offx) * BIN_WIDTH + (BIN_WIDTH / 2), (endy + offy) * BIN_WIDTH + (BIN_WIDTH / 2), (BIN_WIDTH / 2)-4, (BIN_WIDTH / 2)-4);
			
			for (int y = 0; y < binLength; y++) {
				for (int x = 0; x < binLength; x++) {
					if (occupancy[x][y] == 0) continue;
					double occRatio = (occupancy[x][y] / timeMax);
					if (occRatio > 1) occRatio = 1;
					float color = (float) (COLOR_RANGE - (occRatio * COLOR_RANGE));
					StdDraw.setPenColor(Color.getHSBColor(color, 1.0f, 1.0f));
					StdDraw.filledCircle((x + offx) * BIN_WIDTH + (BIN_WIDTH / 2), (y + offy) * BIN_WIDTH + (BIN_WIDTH / 2), (occRatio * BIN_WIDTH / 2) * 0.8 + (BIN_WIDTH / 2) * 0.2);
				}
			}
			StdDraw.show();
			File dir = new File(root + projectName + "/visual");
			dir.mkdirs();
			StdDraw.save(root + projectName + "/visual/" + filename + ".png");
		}//" + c++ + ".png");
	}
	public static void report(String info) {
		info = info.replace("_", "\\_");
		String template = "\\begin{figure}\n"
				+ "\\begin{center}\n"
				+ "\\includegraphics[scale=0.5]{" + projectName + "/visual/" + filename + "}\n"
				+ "\\begin{tabular}{l*{3}{r}}\n"
				+ "Name & Cells Visited & (Cells / Min) & Total Time\\\\\n"
				+ "\\hline\n"
				+ info
				+ "\n\\end{tabular}\n"
				+ "\\end{center}\n"
				+ "\\end{figure}\n"
				+ "\\newpage\n";
		System.out.println(template);
	}
	private static String getTimeStamp(int millis) {
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
	private static void drawInit() {
		//Calculate proportions
		whole = maxDimension * 2;
		half = maxDimension;
		quarter = maxDimension * 0.5;
		third = maxDimension * (2.0 / 3.0);
		tenth = whole * 0.1;
		hundredth = whole * 0.01;
		double borderWidth = tenth * 0.5;
		StdDraw.show(10);
		StdDraw.setCanvasSize((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT);
		StdDraw.clear();
		StdDraw.setXscale(0 - borderWidth , whole + borderWidth);
		StdDraw.setYscale(0 - borderWidth, whole + borderWidth);

		// Color Bar Labels
		DecimalFormat df = new DecimalFormat("#.##");
		StdDraw.text(whole - third, whole + 4 * hundredth, "0");
		StdDraw.text(whole, whole + 4 * hundredth, ""+ df.format(timeMax));
		StdDraw.text(whole - 0.5 * third, whole + 4 * hundredth, "Time (s)");

		// Color Bar
		for (double i = 0; i < third; i+= third / 1000) {
			//float value = (float) (COLOR_RANGE - (i / third * COLOR_RANGE) + COLOR_OFFSET);
			//StdDraw.setPenColor(Color.getHSBColor((0f/3f), 1.0f, value));
			float value = (float) (COLOR_RANGE - (i / third * COLOR_RANGE));
			StdDraw.setPenColor(Color.getHSBColor(value, 1.0f, 1.0f));
			StdDraw.line(whole - third + i, whole + 2.5 * hundredth, whole - third + i, whole + hundredth);
		}

		// Axes Labels
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.text(-3 * hundredth, -3 * hundredth, "mm");
		Color gray = new Color (200, 200, 200);
		int c = 0;
		for (int i = 0; i < (int)(whole / (BIN_SIZE * BIN_WIDTH)) + 1; i++) {
			double m = (i * (BIN_SIZE * BIN_WIDTH));
			StdDraw.setPenColor(Color.BLACK);
			if (c++ % 5 == 0) {
				StdDraw.text(m, -4 * hundredth, "" + (int) (m / BIN_WIDTH));
				StdDraw.text(-4 * hundredth, m, "" + (int) (m / BIN_WIDTH));
			}
			StdDraw.setPenColor(gray);
			StdDraw.line(m, 0, m, whole);
			StdDraw.line(0, m, whole, m);
		}
		
		// Axes
		StdDraw.setPenRadius(0.005);
		StdDraw.setPenColor(Color.BLACK);
		StdDraw.line(0, 0, 0, whole);
		StdDraw.line(0, 0, whole, 0);
		StdDraw.setPenRadius(0.001);
		//StdDraw.show(10);
		
		//Information
		//StdDraw.textLeft(0, whole + 4 * hundredth, "Project: " + projectName);
	}
}
