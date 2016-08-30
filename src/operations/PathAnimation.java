package operations;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import stdlib.StdDraw;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * Animates the path of the worm from centroid data
 *
 * @author As always, the lovely Kyle Moy
 *
 */
public class PathAnimation extends DataSetOperation {
    //Size options

    final static double SCREEN_WIDTH = 500;
    final static double SCREEN_HEIGHT = 500;

    //Color options
    final static double SPEED_MAX = 300.0;
    final static double VALUE_RANGE = 0.8;// 0.8;
    final static double VALUE_OFFSET = 1 - VALUE_RANGE;// 0.8;

    //Enable UI
    final static boolean UI = false;

    //Drawing options
    final static boolean DRAW_TURNS = false;
    final static boolean DRAW_TEXT = false;
    final static int DRAW_GRID_SIZE = 10;
    final static double DRAW_POINT_SIZE = 0.003;

    //Scaling options
    final static boolean SCALE_FIXED = false;
    final static double SCALE_DIMENSION = 3410;

    //Segment options
    final static boolean SEGMENT_LOCAL = false;
    final static boolean SEGMENT_GLOBAL = false;
    final static boolean SEGMENT_CROP = false;
    final static double SEGMENT_CROP_START = 60 * 0;
    final static double SEGMENT_CROP_END = 24;
    final static double[] SEGMENT_BREAKPOINT = {60 * 25, 60 * 30};
    final static float[] SEGMENT_COLOR = {2f / 3f, 0f, 1f / 3f, 1f / 2f};

    static double minX, maxX, minY, maxY, maxDimension, xoff, yoff;
    static String projectName;

    // Everything is relative!
    static double whole, half, quarter, third, tenth, hundredth;

    public static void main(String[] args) throws Exception {
        DataSetOperator.operate(new PathAnimation());
    }

    public void go(String project) throws Exception {
        projectName = project;
        Scanner featureInput;
        featureInput = new Scanner(new File(root + projectName
                + "/data/movementFeatures.csv"));
        featureInput.useDelimiter(",|\r?\n|\r");

        ArrayList<Point> pointList = new ArrayList<Point>();

        // Read in data
        while (featureInput.hasNext()) {
            // Get speed from 5th column of movement data file
            String n = "";
            int frame = featureInput.nextInt();
            double timeElapsed = featureInput.nextDouble();
            featureInput.next();
            double x = featureInput.nextDouble();
            double y = featureInput.nextDouble();
            n = featureInput.next();
            double acceleration = featureInput.nextDouble();
            double angle = featureInput.nextDouble();
            double angularVelocity = featureInput.nextDouble();

            // If data is missing, skip point
            if (x == Double.NaN) {
                continue;
            }

            // Attempt to parse the speed, handle exceptions (in case of NaN or
            // blank field)
            double speed;
            try {
                speed = Double.parseDouble(n);
            } catch (Exception e) {
                speed = -1;
            }

            // Add to list
            Point p = new Point(frame, x, y, timeElapsed, speed, acceleration, angle, angularVelocity);
            if (!Double.isNaN(x)) {
                pointList.add(p);
            }
        }
        featureInput.close();

        //Draw
        if (SEGMENT_LOCAL) {
            int segmentStart = 0;
            double timeStart = pointList.get(0).timeElapsed;
            for (int i = 0; i < SEGMENT_BREAKPOINT.length; i++) {
                //Find end point index
                int segmentEnd = segmentStart;
                Point p;
                while (true) {
                    if (segmentEnd >= pointList.size()) {
                        break;
                    }
                    p = pointList.get(segmentEnd++);
                    if (p.timeElapsed - timeStart >= SEGMENT_BREAKPOINT[i]) {
                        break;
                    }
                }
                drawPath(pointList, segmentStart, segmentEnd, "SEGMENT_" + i + ".png", i, SEGMENT_COLOR[i]);
                segmentStart = segmentEnd;
            }
        }
        if (SEGMENT_CROP) {
            int segmentStart = 0;
            double timeStart = pointList.get(0).timeElapsed;
            Point p;
            while (true) {
                p = pointList.get(segmentStart++);
                if (p.timeElapsed - timeStart >= SEGMENT_CROP_START) {
                    break;
                }
            }
            int segmentEnd = segmentStart;
            while (true) {
                if (segmentEnd >= pointList.size()) {
                    break;
                }
                p = pointList.get(segmentEnd++);
                if (p.timeElapsed - timeStart >= SEGMENT_CROP_END) {
                    break;
                }
            }
            drawPath(pointList, segmentStart, segmentEnd, projectName + ".png", 0, -1);
        } else {
            //Draw everything
            drawPath(pointList, 0, pointList.size(), "GLOBAL.png", 0, -1);
        }
        if (UI) {
            while (true) {
                Thread.sleep(1000);
            }
        }
    }

    private static void drawPath(ArrayList<Point> pointList, int startIndex,
            int endIndex, String name, int cropSection, float color) {
        //Error checking
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > pointList.size() - 1) {
            endIndex = pointList.size() - 1;
        }

        //Determine center of mass
        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        Point p;
        for (int i = startIndex; i < endIndex; i++) {
            p = pointList.get(i);
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
            if (p.y < minY) {
                minY = p.y;
            }
        }
        double xRange = Math.abs(maxX - minX);
        double yRange = Math.abs(maxY - minY);
        int xMean = (int) ((maxX + minX) / 2);
        int yMean = (int) ((maxY + minY) / 2);

        //Determine maximum dimension for scaling
        if (SCALE_FIXED) {
            maxDimension = SCALE_DIMENSION;
        } else {
            maxDimension = Math.max(xRange, yRange) / 2;
            maxDimension *= 1.1;
            out("Maximum Dimension: " + maxDimension);
        }

        //Determine centering offset
        xoff = xMean - maxDimension;
        yoff = yMean - maxDimension;

        // Reference Points
        Point start = pointList.get(startIndex);
        Point end = pointList.get(endIndex);
        double timeStart = start.timeElapsed;
        double timeEnd = end.timeElapsed;
        double timeDiff = timeEnd - timeStart;
        double sx = start.x - xoff;
        double sy = start.y - yoff;
        double ex = end.x - xoff;
        double ey = end.y - yoff;
        if (UI) {
            out("Ready! Press enter to Continue.");
            new Scanner(System.in).nextLine();
        }
        // Canvas Setup
        drawInit(color);

        //Start/End Point Dot
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.point(sx, sy);
        StdDraw.setPenColor(Color.RED);
        StdDraw.point(ex, ey);

        //Start/End Point Circle
        StdDraw.setPenRadius(0.001);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.circle(sx, sy, 3 * maxDimension / 100);
        StdDraw.setPenColor(Color.RED);
        StdDraw.circle(ex, ey, 3 * maxDimension / 100);

        StdDraw.setPenRadius(DRAW_POINT_SIZE);
        int seg = 0;
        for (int c = startIndex; c < endIndex; c++) {
            Point last = pointList.get(Math.max(c - 1, 0));
            p = pointList.get(c);

            // Set point color
            if (SEGMENT_LOCAL) {
                double ratio = (p.timeElapsed - timeStart) / timeDiff;
                if (ratio >= 1) {
                    ratio = 0.999999;
                }
                float value = (float) Math.min((ratio * VALUE_RANGE), 1.0);
                value = (float) (VALUE_RANGE - value + VALUE_OFFSET);
                StdDraw.setPenColor(Color.getHSBColor(color, 1.0f, value));
            } else if (SEGMENT_GLOBAL) {
                for (; seg < SEGMENT_BREAKPOINT.length; seg++) {
                    if ((p.timeElapsed - timeStart) < SEGMENT_BREAKPOINT[seg]) {
                        break;
                    }
                }
                StdDraw.setPenColor(Color.getHSBColor(SEGMENT_COLOR[seg], 1.0f, 1.0f));
            } else {
                // Calculate the color depending on speed
                double speedRatio = p.speed / SPEED_MAX;
                if (speedRatio > 1) {
                    speedRatio = 0.999999;
                }
                if (speedRatio <= 0) {
                    speedRatio = 0.000001;
                }
                color = (float) Math.min((speedRatio * VALUE_RANGE), 1.0);
                if (p.speed == -1) {
                    color = 0;
                }
                color = (float) ((VALUE_RANGE - (double) color));
                StdDraw.setPenColor(Color.getHSBColor(color, 1.0f, 1.0f));
            }

            // Draw point
            if (p.speed == -1) {
                StdDraw.setPenColor(Color.getHSBColor(0.0f, 0.0f, 0.0f));
            }
            double mX = (p.x - xoff);
            double mY = (p.y - yoff);
            StdDraw.point(mX, mY);
            double pangleradians = p.angle / 180 * Math.PI;
            double langleradians = last.angle / 180 * Math.PI;
            double angleDiff = Math.abs(Math.atan2(
                    Math.sin(pangleradians - langleradians),
                    Math.cos(pangleradians - langleradians)));
            if (DRAW_TURNS) {
                if (angleDiff > Math.PI / 2) {
                    StdDraw.setPenRadius(0.001);
                    StdDraw.setPenColor(Color.getHSBColor(0.0f, 0.0f, 0.0f));
                    StdDraw.rectangle(p.x - xoff, p.y - yoff, 2 * hundredth,
                            2 * hundredth);
                    StdDraw.setPenRadius(DRAW_POINT_SIZE);
                }
            }

            // Handle Display
            if (UI) {
                //StdDraw.show(0);
            }
        }
        StdDraw.show(0);
        if (!name.equals("")) {
            writeFrame(name);
        }
    }

    private static void writeFrame(String name) {
        StdDraw.show(0);
        File dir = new File(root + projectName + "/visual/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        StdDraw.save(root + projectName + "/visual/" + name);
    }

    /* THIS DOES NOT WORK ANY MORE
	private static void drawText(Point p) {
		int millis = (int) (p.timeElapsed * 1000);
		String time = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
		StdDraw.setPenColor(Color.WHITE);
		
		//Clear
		//TO DO: FIX TEXT CLEARING
		StdDraw.filledPolygon(new double[] { quarter, whole + 4 * hundredth,
				2 * third - hundredth, 2 * third - hundredth }, new double[] {
				whole + 0.5 * hundredth, whole + 5 * hundredth,
				whole + 5 * hundredth, whole + 0.5 * hundredth });
		StdDraw.setPenColor(Color.BLACK);

		//Draw time
		StdDraw.textLeft(quarter, whole + 4 * hundredth, "Time: " + time);
		
		double mX = (p.x - xoff);
		double mY = (p.y - yoff);
		StdDraw.textLeft(hundredth, hundredth,
				"Location: mm(" + String.format("%5s", (int) (mX / 7) / 10.0)
						+ "," + String.format("%5s", (int) (mY / 7) / 10.0)
						+ ")");
	}
     */
    private static void drawInit(float color) {
        // Calculate proportions
        whole = maxDimension * 2;
        half = maxDimension;
        quarter = maxDimension * 0.5;
        third = maxDimension * (2.0 / 3.0);
        tenth = whole * 0.1;
        hundredth = whole * 0.01;
        double borderWidth = tenth * 0.5;
        double PXPERMM = 70;
        if (projectName.indexOf("_") != projectName.lastIndexOf("_")) {
            if ((projectName.substring(projectName.indexOf("_") + 1, projectName.lastIndexOf("_")).equalsIgnoreCase("HR")) && (PXPERMM == 70)) {
                PXPERMM *= 2;
            }
        }
        out("PXPERMM: " + PXPERMM);
        StdDraw.init(UI);
        StdDraw.show(10);
        StdDraw.setCanvasSize((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT);
        StdDraw.clear();
        StdDraw.setXscale(0 - borderWidth, whole + borderWidth);
        StdDraw.setYscale(0 - borderWidth, whole + borderWidth);

        // Color Bar
        if (!SEGMENT_GLOBAL) {
            if (color >= 0) {
                // Gradient intensity for time
                StdDraw.text(whole - third, whole + 4 * hundredth, "start");
                StdDraw.text(whole, whole + 4 * hundredth, "end");
                for (double i = 0; i < third; i += third / 1000) {
                    float value = (float) (VALUE_RANGE - (i / third * VALUE_RANGE) + VALUE_OFFSET);
                    StdDraw.setPenColor(Color.getHSBColor(color, 1.0f, value));
                    StdDraw.line(whole - third + i, whole + 2.5 * hundredth, whole
                            - third + i, whole + hundredth);
                }
            } else {
                // Gradient hue for speed
                StdDraw.text(whole - third, whole + 4 * hundredth, "0");
                StdDraw.text(whole, whole + 4 * hundredth, "0.3+");
                StdDraw.text(whole - 0.5 * third, whole + 4 * hundredth, "mm/s");
                for (double i = 0; i < third; i += third / 1000) {
                    float value = (float) (VALUE_RANGE - (i / third * VALUE_RANGE));
                    StdDraw.setPenColor(Color.getHSBColor(value, 1.0f, 1.0f));
                    StdDraw.line(whole - third + i, whole + 2.5 * hundredth, whole
                            - third + i, whole + hundredth);
                }
            }
        }
        // Axes Labels
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.text(-5 * hundredth, -5 * hundredth, "mm");
        Color gray = new Color(200, 200, 200);
        for (int i = 0; i < (int) (whole / (DRAW_GRID_SIZE * PXPERMM)) + 1; i++) {
            double m = (i * (DRAW_GRID_SIZE * PXPERMM));
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(m, -4 * hundredth, "" + (int) (m / 70));
            StdDraw.text(-4 * hundredth, m, "" + (int) (m / 70));

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

        // Information
        if (DRAW_TEXT) {
            StdDraw.textLeft(0, whole + 4 * hundredth, "Project: "
                    + projectName);
        }
    }

    private static class Point {

        final double frame, x, y, timeElapsed, speed, acceleration, angle, angularVelocity;

        public Point(double _f, double _x, double _y, double _te, double _s,
                double _ac, double _a, double _av) {
            frame = _f;
            x = _x;
            y = _y;
            timeElapsed = _te;
            speed = _s;
            acceleration = _ac;
            angle = _a;
            angularVelocity = _av;
        }
    }
}
