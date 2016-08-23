package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * 
 * Input:
 * 	data/movementFeatures.csv
 * 
 * Output:
 *
 * @author Kyle Moy
 *
 */
public class AngleEntropy extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new AngleEntropy()); }
	public void go(String project) throws Exception {
		//Input
		File input = new File(root + project + "/data/movementFeatures.csv");
		
		//Output
		File output = new File(root + project + "/data/entropy.csv");
		
		//Handle IO
		Scanner data = new Scanner(input);
		data.useDelimiter(",|\r?\n|\r");
		PrintWriter out = new PrintWriter(output);
		double lastAng = -1;
		int [] classes = new int[3];
		int time = 0;
		int lastMin = 0;
		while (data.hasNext()) {
			int f = data.nextInt();
			double tE = data.nextDouble();
			double tD = data.nextDouble();
			double x = data.nextDouble();
			double y = data.nextDouble();
			double vel = data.nextDouble();
			double acc = data.nextDouble();
			double ang = data.nextDouble() / 180 * Math.PI;
			double aV = data.nextDouble();
			if (lastAng == -1) lastAng = ang;
			
			if ((int)(tE) > time) { //if 1 second has passed
				time = (int)(tE);
				double diff = Math.abs(Math.atan2(Math.sin(ang - lastAng), Math.cos(ang - lastAng)));
				lastAng = ang;
				if (diff < ((1/6.0) * Math.PI)) {
					//classes[0] ++;
				} else if (diff < ((1/3.0) * Math.PI)) {
					classes[0] ++;
				} else if (diff < ((11/18.0) * Math.PI)) {
					classes[1] ++;
				} else {
					classes[2] ++;
				}
			}
			if ((int)(tE / 60.0) > lastMin) { // if one minute has passed
				lastMin = (int)(tE / 60.0);
				double entropy = entropy(classes);
				System.out.println(entropy);
				out.println(entropy);
				classes = new int[3];
			}
		}
	}
	public static double entropy(int[] classes) {
		int numClasses = classes.length;
		double total = 0.0;
		for (int i = 0; i < numClasses; i++) {
			//System.out.print(classes[i] + "\t");
			total += classes[i];
		}
		//System.out.println();
		double entropy = 0;
		for (int i = 0; i < numClasses; i++) {
			double probability = classes[i] / total;
			double ent = probability * Math.log(probability) / Math.log(2);
			if (!Double.isNaN(ent)) entropy += ent;
		}
		return -entropy;
	}
}
