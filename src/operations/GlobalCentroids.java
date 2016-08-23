package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * STEP 2
 * Combine the camera position from the parsed tracker logs and the centroids from the segmentation process.
 * 
 * Input:
 * data/tracker.dat
 * log/feature.log
 * 
 * Output:
 * data/centroid.csv
 * 
 * @author Kyle Moy
 *
 */
public class GlobalCentroids extends DataSetOperation {
	public static int WIN_SIZE = 15;
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new GlobalCentroids()); }
	@Override
	public void go(String project) throws Exception {
		//Input
		File trackerData = new File(root + project + "/data/tracker.csv");
		File centroidLog = new File(root + project + "/log/feature.log");
		
		//Output
		File output = new File(root + project + "/data/centroid.csv");
		
		//Create file handlers
		Scanner trackerIn = new Scanner(trackerData);
		trackerIn.useDelimiter(",|\r?\n|\r");
		Scanner centroidIn = new Scanner(centroidLog);
		centroidIn.useDelimiter(",|\r?\n|\r");
		PrintWriter out = new PrintWriter(output);
		
		//Process!!
		double lastKnownX = -1;
		double lastKnownY = -1;
		int distanceQueue = 0;
		ArrayList<DataEntry> queue = new ArrayList<DataEntry>();
		ArrayList<DataEntry> centroids = new ArrayList<DataEntry>();
		int moving = 0;
		double xoff = 0;
		double yoff = 0;
		while (centroidIn.hasNext()) {
			DataEntry entry = new DataEntry(centroidIn, trackerIn);
			/*
			if (entry.area == -1 || entry.moving) {
				if (entry.moving) {
					moving = 30;
				}
				queue.add(entry);
				continue;
			}
			
			if (moving > 0) {
				moving --;
				queue.add(entry);
				continue;
			}
			*/
			if (lastKnownX == -1 && lastKnownY == -1) {
				lastKnownX = entry.x;
				lastKnownY = entry.y;
			}
			if (lastKnownX != -1 && lastKnownY != -1) { //If there is a known last location, check that this point is within a reasonable range.
				double distance = Math.sqrt(Math.pow(lastKnownX - (entry.x + xoff),2) + Math.pow(lastKnownY - (entry.y + yoff),2));
				if (distance > 10) {
					//If the worm moved too far away, add the opposite distance to the camera offset, thus centering the worm
					xoff += lastKnownX - (entry.x + xoff);
					yoff += lastKnownY - (entry.y + yoff);
					//distanceQueue++;
					//queue.add(entry);
					//continue;
				}
			}
			/*
			if (distanceQueue > 100) {
				throw new Error("...we lost the worm!");
			}
			*/
			
			//Is not a seg. failure, but failures exist on stack
			/*
			if (!queue.isEmpty()) {
				double xDiff = lastKnownX - entry.x;
				double yDiff = lastKnownY - entry.y;
				double xDelta = xDiff / (double)queue.size();
				double yDelta = yDiff / (double)queue.size();
				
				while(!queue.isEmpty()) {
					lastKnownX -= xDelta;
					lastKnownY -= yDelta;
					DataEntry e = queue.remove(0);
					e.x = lastKnownX;
					e.y = lastKnownY;
					//out.println(e.toString());
					centroids.add(e);
				}
				distanceQueue = 0;
			}
			 */
			//out.println(entry.toString());
			entry.x += xoff;
			entry.y += yoff;
			centroids.add(entry);
			lastKnownX = entry.x;
			lastKnownY = entry.y;
		}
		
		trackerIn.close();
		centroidIn.close();
		
		//Smoothing
		double[][] temp = new double[centroids.size()][2];
		for (int i = 0; i < centroids.size(); i++) {
			DataEntry e = centroids.get(i);
			temp[i][0] = e.x;
			temp[i][1] = e.y;
		}
		
		double[] kernel = makeKernel(WIN_SIZE);
		int HWIN_SIZE = WIN_SIZE / 2;
		for (int i = HWIN_SIZE; i < centroids.size() - HWIN_SIZE; i++) {
			double feat[] = new double[2];
			int k = 0;
			for (int j = i - HWIN_SIZE; j < i + HWIN_SIZE; j++) {
				for (int f = 0; f < 2; f++ ){
					feat[f] += temp[j][f] * kernel[k];
				}
				k++;
			}
			DataEntry e = centroids.get(i);
			e.x = feat[0];
			e.y = feat[1];
			out.println(e.toString());
		}
		out.close();
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
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = Math.exp(-(distance) / sigma22)
						/ sqrtSigmaPi2;
			total += matrix[index];
			index++;
		}
		for (int i = 0; i < rows; i++)
			matrix[i] /= total;
		return matrix;
	}
	
	private static class DataEntry {
		public int frameC, area, frameT, timeDelta, xoff, yoff;
		public long timeElapsed;
		public double x, y;
		public boolean moving;
		public DataEntry (Scanner centroidIn, Scanner trackerIn) {
			frameC = centroidIn.nextInt();
			x = centroidIn.nextDouble();
			y = centroidIn.nextDouble();
			area = centroidIn.nextInt();
			frameT = trackerIn.nextInt();
			timeElapsed = trackerIn.nextLong();
			timeDelta = trackerIn.nextInt();
			xoff = trackerIn.nextInt(); //Camera x offset
			yoff = trackerIn.nextInt(); //Camera y offset
			
			x -= xoff;
			y -= yoff;
			moving = trackerIn.nextBoolean();
			if (frameC != frameT) {
				throw new Error("Frame mismatch! " + frameC + " != " + frameT);
			}
		}
		public String toString() {
			return frameC + "," + timeElapsed + "," + timeDelta + "," + x + "," + y;
		}
	}
}
