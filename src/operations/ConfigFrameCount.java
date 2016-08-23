package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class ConfigFrameCount extends DataSetOperation {
	public static void main(String[] args) throws Exception {DataSetOperator.operate(new ConfigFrameCount());}
	@Override
	public void go(String project) throws Exception {
		setRoot(DataSetOperator.remote_root);
		
		//Input
		File input = new File(root + project + "/config.yml");
		
		//Output
		File output = new File(root + project + "/config.tmp");
		
		Scanner data = new Scanner(input);
		PrintWriter out = new PrintWriter(output);
		boolean fuck = false;
		while (data.hasNextLine()) {
			String line = data.nextLine();
			if (line.contains("frame-end:")) {
				if (fuck) continue;
				fuck = true;
				int count = Integer.parseInt(line.replace("frame-end: ", ""));
				if (true) {
					count = fileCount(project) - 1;
					out.println("frame-end: " + count);
					System.out.println(count);
					//System.out.println(project + ": " + count);
				} else {
					out.println(line);
				}
			} else {
				out.println(line);
			}
		}
		out.close();
		data.close();
		
		input.delete();
		output.renameTo(input);
	}
	public int fileCount(String project) {
		File dir = new File(root + project + "/input/");
		if (!dir.exists()) {
			return 0;
			//throw new Error(project + " input directory does not exist!");
		}
		return dir.list().length;
	}
}
