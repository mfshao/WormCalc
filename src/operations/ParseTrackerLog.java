package operations;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * STEP 1
 * Parse frame time and camera movement information from Kyle's tracker log output.
 * 
 * Input:
 * 	log/log.dat
 * 
 * Output:
 * 	data/tracker.csv
 * 
 * @author Kyle Moy
 *
 */
public class ParseTrackerLog extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new ParseTrackerLog()); }

	@Override
	public void go(String project) throws Exception {
		//Input
		File trackerLog = new File(root + project + "/log/log.dat");

		if (!trackerLog.exists()) {
			out("No log found.");
			return;
		} else {
			out("Log found.");
		}
		
		//Output
		File output = new File(root + project + "/data/tracker.csv");
		
		
		//Create "data" Folder
		File dir = new File(root + project + "/data/");
		if (!dir.exists())
			dir.mkdir();

		//Create file handlers
		DataInputStream data = new DataInputStream( new FileInputStream((trackerLog)));
		PrintWriter out = new PrintWriter(output);
		
		//Process!
		long epoch = -1;
		long lastTime = -1;
		int xoff = 0;
		int yoff = 0;
		int xl = -1;
		int yl = -1;
		
		while(data.available() > 0) {
			int frame = data.readInt();
			long timeElapsed = data.readLong();
			int x = data.readInt();
			int y = data.readInt();
			
			if (xl == -1) {
				xl = x;
				yl = y;
				
				xoff = x;
				yoff = y;
			}
			
			if (x != xl) {
				int diff = x - xl;
				xoff -= diff;
			}
			if (y != yl) {
				int diff = y - yl;
				yoff += diff;
			}
			
			xl = x;
			yl = y;
			int isMoving = data.readInt();
			if (epoch == -1) epoch = timeElapsed;
			
			timeElapsed -= epoch; //Relative to beginning of recording
			
			if (lastTime == -1) lastTime = timeElapsed;
			long timeDelta = timeElapsed - lastTime;
			lastTime = timeElapsed;
			
			out.println(frame + "," + timeElapsed + "," + timeDelta + "," + 0 + "," + 0 + "," + (isMoving > 0));
		}
		out.close();
		data.close();
	}
}
