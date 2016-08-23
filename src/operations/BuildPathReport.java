package operations;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class BuildPathReport extends DataSetOperation {
	static String var = "[G1]ds30da30/";
	static int[][] totalTime = new int[2][3];
	

	public static void main(String[] args) throws Exception {
		DataSetOperator.setVerbose(false);
		DataSetOperator.operate(new BuildPathReport());
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
	public void go(String project) throws Exception {
		String template = "\\begin{figure}\n"
				+ "\\caption{" + project.replace("_", "\\_")+ "}"
				+ "\\begin{center}\n"
				+ "\\centerline{\\includegraphics[scale=0.6]{" + project +"/visual/fit_report}}\n"
				+ "\\begin{tabular}{l*{2}{r}}\n"
				+ "Name & Time & Frames\\\\\n"
				+ "\\hline\n"
				+ getStats(project) //"N2\\_f5 & 01:15:31 & 171417 & 165189 (96.37\\%)& 6228\\\\"
				+ "\n\\end{tabular}\n"
				+ "\\end{center}\n"
				+ "\\end{figure}\n"
				+ "\\clearpage\n";
		System.out.println(template);
	}
	private static String getStats(String project) throws FileNotFoundException {
		//System.out.println("MV");
		File file = new File(root + project + "/data/movementFeatures.csv");
		Scanner in = new Scanner(file);
		String line = in.nextLine();
		Scanner ln = new Scanner(line);
		ln.useDelimiter(",|\r?\n|\r");
		ln.nextInt();
		int smillis = (int) (ln.nextDouble() * 1000);
		ln.close();
		while (in.hasNextLine()) {
			line = in.nextLine();
		}
		in.close();
		ln = new Scanner(line);
		ln.useDelimiter(",|\r?\n|\r");
		int frames = ln.nextInt();
		int emillis = (int) (ln.nextDouble() * 1000);
		int millis = emillis - smillis;
		int a = 0;
		if (project.contains("tph1")) a = 1;
		int b = 0;
		if (project.contains("nnf")) b = 2;
		else if (project.contains("nf")) b = 1;
		totalTime[a][b] += millis;
		String time = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));
		ln.close();

		//System.out.println("F");
		/* Doesn't work any more because feature.log now includes null cases
		 * Also irrelevant now because I interpolate through null cases...
		File log = new File(root + project + "/log/feature.log");
		Scanner logS = new Scanner(log);
		int processed = 0;
		while (logS.hasNext()) {
			logS.next();
			processed++;
		}
		logS.close();
		
		File err = new File(root + project + "/log/error.log");
		Scanner errS = new Scanner(err);
		int unprocessed = 0;
		while (errS.hasNext()) {
			errS.next();
			unprocessed++;
		}
		errS.close();
		*/
		String format = project.replace("_", "\\_");
		return format + " & " + time + " & " + frames + "\\\\";
	}
}
