package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * Clean up duplicates in the segmentation log file because I'm dumb and had an oversight in the distributed computing process.
 * 
 * Input:
 * 	log/feature.log
 * 
 * Output:
 * 	log/feature.log
 * 
 * @author Kyle Moy
 *
 */
public class SanitizeCentroidLog extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new SanitizeCentroidLog()); }
	public void go(String project) throws Exception {
		//Input
		File centroidLog = new File(root + project + "/log/feature.log");
		
		//Output
		File output = new File(root + project + "/log/feature_clean.log");
		
		//Create file handlers
		Scanner centroidIn = new Scanner(centroidLog);
		centroidIn.useDelimiter(",|\r?\n|\r");
		PrintWriter out = new PrintWriter(output);
		int lf = -1;
		int duplicates = 0;
		int missing = 0;
		while(centroidIn.hasNext()) {
			int f = centroidIn.nextInt();
			double x = centroidIn.nextDouble();
			double y = centroidIn.nextDouble();
			int a = centroidIn.nextInt();
			if (lf == f) {
				duplicates++;
				continue;
			}
			while (f != lf+1) {
				missing ++;
				out.println(++lf + ",-1.0,-1.0,-1");
			}
			lf = f;
			out.println(f + "," + x + "," + y + "," + a);
		}
		out.close();
		centroidIn.close();
		
		centroidLog.delete();
		output.renameTo(centroidLog);
		out("Duplicate Entries: " + duplicates);
		out("Missing Entries: " + missing);
	}
}