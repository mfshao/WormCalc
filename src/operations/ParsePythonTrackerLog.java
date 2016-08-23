package operations;

import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * STEP 1 (Legacy)
 * Parse frame time and camera movement information from Valerie's tracker log output.
 * 
 * Input:
 * 	log/tracker.log
 * 
 * Output:
 * 	data/tracker.csv
 * 
 * @author Kyle Moy
 *
 */
public class ParsePythonTrackerLog extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new ParsePythonTrackerLog()); }

	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	public static double PXPERMM = 70;
	public static double MMPERSTEP = 0.2; 
	public static int PXPERSTEP = (int) (MMPERSTEP * PXPERMM);
	@Override
	public void go(String project) throws Exception {
		//Input
		File trackerLog = new File(root + project + "/log/tracker.log");
		
		if (!trackerLog.exists()) {
			out("No legacy log found.");
			return;
		} else {
			out("Legacy log found.");
		}
		
		//Output
		File output = new File(root + project + "/data/tracker.csv");
		
		
		//Create "data" Folder
		File dir = new File(root + project + "/data/");
		if (!dir.exists())
			dir.mkdir();

		//Create file handlers
		Scanner data = new Scanner(trackerLog);
		PrintWriter out = new PrintWriter(output);
		
		//Process!
		long epoch = 0;
		long lastTime = 0;
		int xoff = 0;
		int yoff = 0;
		int frame = 0;
		int isMoving = 0;
		while(data.hasNext()) {
			String line = data.nextLine();
			if (line.contains("wrote frame")) {
				String[] info = line.split("\t");
				if (epoch == 0) {
					epoch = parseTime(info[0]);
				}
				long timeElapsed = parseTime(info[0]) - epoch;
				long timeDelta = timeElapsed - lastTime;
				lastTime = timeElapsed;
				out.println(frame + "," + timeElapsed + "," + timeDelta + "," + xoff + "," + yoff + "," + (isMoving > 0));
				if (isMoving > 0) isMoving --;
				frame++;
			} else if (line.contains("steps:")) {
				String[] info = line.split("steps: ")[1].replaceAll("[\\(\\)]", "").split(",");
				int xstep = Integer.parseInt(info[0].replace(" ",""));
				int ystep = Integer.parseInt(info[1].replace(" ",""));
				xoff += xstep * PXPERSTEP;
				yoff += ystep * PXPERSTEP;
				isMoving = 10;
			}
		}
		out.close();
		data.close();
	}
	public long parseTime(String time) throws ParseException {
		return format.parse(time).getTime();
	}
}
