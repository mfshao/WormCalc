package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * STEP 3 Calculates the movement features from the centroid positions. Excuse
 * the messy code- this was like, the second thing I coded for this project. If
 * it ain't broke...
 *
 * Input: data/centroid.csv
 *
 * Output: data/movementfeatures.csv
 *
 * @author Kyle Moy
 *
 */
public class MovementFeatures extends DataSetOperation {
    //static int WINSIZE = 30;

    public static int WIN_SIZE = 7;
    static int PXPERMM = 70;
    static int gamma = 1;
    static double deltaTime = 1.0;
    static int dSpeed = 30;
    static int dAcceleration = 1;
    static int dAngle = 30;
    static int dAngularVelocity = 1;
    static boolean smooth = true;

    public static void main(String[] args) throws Exception {
        DataSetOperator.operate(new MovementFeatures());
    }

    public void go(String project) throws Exception {
        //Input
        File input = new File(root + project + "/data/centroid.csv");

        //Output
        File output = new File(root + project + "/data/movementFeatures.csv");

        if (project.indexOf("_") != project.lastIndexOf("_")) {
            if (project.substring(project.indexOf("_") + 1, project.lastIndexOf("_")).equalsIgnoreCase("HR")) {
                PXPERMM *= 2;
            }
        }
        out("PXPERMM: " + PXPERMM);

        Scanner data = new Scanner(input);
        data.useDelimiter(",|\r?\n|\r");
        ArrayList<Entry> entries = new ArrayList<Entry>();

        while (data.hasNext()) {
            entries.add(new Entry(data));
        }

        //Interpolate missing X and Y
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (Double.isNaN(e.x)) {
                double c = 0.0;
                double sum = 0.0;
                for (int j = (int) Math.max(0, (i - 15)); j < (int) Math.min(i + 15, entries.size()); j++) {
                    Entry a = entries.get(j);
                    if (!Double.isNaN(a.x)) {
                        sum += a.x;
                        c++;
                    }
                }
                e.x = sum / c;
            }
            if (Double.isNaN(e.y)) {
                double c = 0.0;
                double sum = 0.0;
                for (int j = (int) Math.max(0, (i - 15)); j < (int) Math.min(i + 15, entries.size()); j++) {
                    Entry a = entries.get(j);
                    if (!Double.isNaN(a.y)) {
                        sum += a.y;
                        c++;
                    }
                }
                e.y = sum / c;
            }
        }

        //Clear First Entries
        //...why?
        /*
		for (int i = 0; i < gamma; i++) {
			Entry entryA = entries.get(i);
			entryA.speed = Double.NaN;
			entryA.angle = Double.NaN;
		}
         */
        //Smoothing
        /*
		if (smooth) {
			double[] temp = new double[entries.size()];
			for (int i = 0; i < entries.size(); i++) {
				Entry e = entries.get(i);
				temp[i] = e.time;
			}
			
			double[] kernel = makeKernel(WIN_SIZE);
			int HWIN_SIZE = WIN_SIZE / 2;
			for (int i = HWIN_SIZE; i < entries.size() - HWIN_SIZE; i++) {
				double feat = 0;
				int k = 0;
				for (int j = i - HWIN_SIZE; j < i + HWIN_SIZE; j++) {
					feat += temp[j] * kernel[k];
					k++;
				}
				Entry e = entries.get(i);
				e.time = feat;
			}
		}*/
        //Calculate Speed and Angle
        /*
		for (int i = 0; i < entries.size(); i+= gamma) {
			if (i < dSpeed) continue;
			Entry entryA = entries.get(i);
			
			Entry entryB = entries.get(i - dSpeed);
			entryA.speed = calcSpeed(entryA, entryB);
		}
		for (int i = 0; i < entries.size(); i+= gamma) {
			if (i < dAngle) continue;
			Entry entryA = entries.get(i);
			Entry entryB = entries.get(i - dAngle);
			double angle = calcAngle(entryA, entryB);
			//if (angle < 0) angle = (angle + 360) % 360;
			entryA.angle = angle;
		}*/
        int offset = 0;
        for (int i = 0; i < entries.size(); i += gamma) {
            Entry entryA = entries.get(i);
            Entry entryB = null;

            //Find 1 second ago!
            offset /= 2;
            while (true) {
                if (i - offset < 0) {
                    break;
                }
                entryB = entries.get(i - offset);
                if (entryA.time - entryB.time >= deltaTime) {
                    break;
                } else {
                    offset++;
                }
            }
            if (entryB != null) {
                entryA.speed = calcSpeed(entryA, entryB);
                entryA.angle = calcAngle(entryA, entryB);
            }
        }
        //Interpolate missing Speed and Angle
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            if (Double.isNaN(e.speed)) {
                double c = 0.0;
                double sum = 0.0;
                for (int j = (int) Math.max(0, (i - 15)); j < (int) Math.min(i + 15, entries.size()); j++) {
                    Entry a = entries.get(j);
                    if (!Double.isNaN(a.speed)) {
                        sum += a.speed;
                        c++;
                    }
                }
                if (c != 0.0) {
                    e.speed = sum / c;
                }
            }
            if (Double.isNaN(e.angle)) {
                double c = 0.0;
                double sum = 0.0;
                for (int j = (int) Math.max(0, (i - 15)); j < (int) Math.min(i + 15, entries.size()); j++) {
                    Entry a = entries.get(j);
                    if (!Double.isNaN(a.angle)) {
                        sum += a.angle;
                        c++;
                    }
                }
                if (c != 0.0) {
                    e.angle = sum / c;
                }
            }
        }

        //Smoothing
        if (smooth) {
            double[][] temp = new double[entries.size()][4];
            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                temp[i][0] = e.speed;
                temp[i][2] = e.angle;
            }

            double[] kernel = makeKernel(WIN_SIZE);
            int HWIN_SIZE = WIN_SIZE / 2;
            for (int i = HWIN_SIZE; i < entries.size() - HWIN_SIZE; i++) {
                double feat[] = new double[4];
                int k = 0;
                for (int j = i - HWIN_SIZE; j < i + HWIN_SIZE; j++) {
                    for (int f = 0; f < 4; f++) {
                        feat[f] += temp[j][f] * kernel[k];
                    }
                    k++;
                }
                Entry e = entries.get(i);
                e.speed = feat[0];
                e.angle = feat[2];
            }
        }

        //Calculate Derivatives
        for (int i = 0; i < entries.size(); i += gamma) {
            if (i < dAcceleration) {
                continue; //In case gamma != 1, still start at index 0
            }
            Entry entryA = entries.get(i);
            Entry entryB = entries.get(i - dAcceleration);
            entryA.acceleration = calcAcceleration(entryA, entryB);
        }

        offset = 0;
        for (int i = 0; i < entries.size(); i += gamma) {
            //if (i < dAngularVelocity) continue;
            Entry entryA = entries.get(i);
            Entry entryB = null;

            //Find 1 second ago!
            offset /= 2;
            while (true) {
                if (i - offset < 0) {
                    break;
                }
                entryB = entries.get(i - offset);
                if (entryA.time - entryB.time >= deltaTime) {
                    break;
                } else {
                    offset++;
                }
            }
            if (entryB != null) {
                entryA.angularVelocity = calcAngularVelocity(entryA, entryB);
            }
        }

        if (gamma > 1) {
            ArrayList<Entry> subset = new ArrayList<Entry>();
            for (int i = gamma; i < entries.size(); i += gamma) {
                subset.add(entries.get(i));
            }
            entries = subset;
        }

        //Smoothing
        if (smooth) {
            double[][] temp = new double[entries.size()][4];
            for (int i = 0; i < entries.size(); i++) {
                Entry e = entries.get(i);
                temp[i][1] = e.acceleration;
                temp[i][3] = e.angularVelocity;
            }

            double[] kernel = makeKernel(WIN_SIZE);
            int HWIN_SIZE = WIN_SIZE / 2;
            for (int i = HWIN_SIZE; i < entries.size() - HWIN_SIZE; i++) {
                double feat[] = new double[4];
                int k = 0;
                for (int j = i - HWIN_SIZE; j < i + HWIN_SIZE; j++) {
                    for (int f = 0; f < 4; f++) {
                        feat[f] += temp[j][f] * kernel[k];
                    }
                    k++;
                }
                Entry e = entries.get(i);
                e.acceleration = feat[1];
                e.angularVelocity = feat[3];
            }
        }

        int halfDS = dSpeed / 2;
        for (int i = 0; i < entries.size() - halfDS; i++) {
            Entry a = entries.get(i);
            Entry b = entries.get(i + halfDS);
            a.speed = b.speed;
            a.acceleration = b.acceleration;
            a.angle = b.angle;
            a.angularVelocity = b.angularVelocity;
        }
        //Output
        PrintWriter pw = new PrintWriter(output);
        for (Entry e : entries) {
            e.print(pw);
        }
        data.close();
        pw.close();
    }

    private static double calcAngularVelocity(Entry a, Entry b) {
        if (Double.isNaN(a.angle) || Double.isNaN(b.angle)) {
            return Double.NaN;
        }
        //double angleDiff = a.angle - b.angle; //Not good.
        double angleDiff = Math.abs(Math.atan2(Math.sin(a.angle - b.angle), Math.cos(a.angle - b.angle))); //Takes care of 359-1 problem, I think
        //if (angleDiff < 2) return 0;
        double timeDiff = a.time - b.time;
        if (timeDiff < 0) {
            throw new Error("Time negative?!");
        }
        double value = angleDiff / timeDiff;
        return value;
    }

    private static double calcAngle(Entry a, Entry b) {
        return Math.atan2((a.y - b.y), (a.x - b.x)) * 180 / Math.PI;
    }

    private static double calcAcceleration(Entry a, Entry b) {
        if (Double.isNaN(a.speed) || Double.isNaN(b.speed)) {
            return Double.NaN;
        }
        double speedDiff = a.speed - b.speed;
        double timeDiff = a.time - b.time;
        return speedDiff / timeDiff;
    }

    private static double calcSpeed(Entry a, Entry b) {
        double distance = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        double timeDiff = a.time - b.time;
        double speed = ((distance / PXPERMM) / timeDiff) * 1000;
        if (speed < 1000.0) {
            return speed; //((dist in pixels) / pixels per millimeter) / difference in time * micrometers in a millimeter
        } else {
            return Double.NaN;
        }
    }

    private static double[] makeKernel(int width) {
        double radius = width / 2;
        int r = (int) Math.ceil(radius);
        int rows = r * 2 + 1;
        double[] matrix = new double[rows];
        double sigma = radius / 3;
        double sigma22 = 2 * sigma * sigma;
        double sigmaPi2 = (2 * Math.PI * sigma);
        double sqrtSigmaPi2 = Math.sqrt(sigmaPi2);
        double radius2 = radius * radius;
        double total = 0;
        int index = 0;
        for (int row = -r; row <= r; row++) {
            float distance = row * row;
            if (distance > radius2) {
                matrix[index] = 0;
            } else {
                matrix[index] = Math.exp(-(distance) / sigma22)
                        / sqrtSigmaPi2;
            }
            total += matrix[index];
            index++;
        }
        for (int i = 0; i < rows; i++) {
            matrix[i] /= total;
        }
        return matrix;
    }

    private static class Entry {

        final int frame;
        double x, y, timeDelta;
        double time; //This is NOT a long! We have to divide to get a fractional second.
        double speed, acceleration, angle, angularVelocity;

        public Entry(Scanner in) {
            frame = in.nextInt();
            time = in.nextLong() / 1000.0;
            timeDelta = in.nextDouble() / 1000;
            double _x = in.nextDouble();
            double _y = in.nextDouble();
            if (_x == 0 && _y == 0) {
                x = Double.NaN;
                y = Double.NaN;
            } else {
                x = _x;
                y = _y;
            }
        }

        public void print(PrintWriter pw) {
            pw.println(frame + "," + time + "," + timeDelta + "," + x + "," + y + "," + speed + "," + acceleration + "," + angle + "," + angularVelocity);
        }
    }
}
