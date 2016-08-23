package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class ConfigModify extends DataSetOperation {
	public static void main(String[] args) throws Exception {DataSetOperator.operate(new ConfigModify());}
	@Override
	public void go(String project) throws Exception {
		setRoot(DataSetOperator.remote_root);
		
		//Input
		File input = new File(root + project + "/config.yml");
		
		//Output
		File output = new File(root + project + "/config.tmp");
		
		Scanner data = new Scanner(input);
		PrintWriter out = new PrintWriter(output);
		while (data.hasNextLine()) {
			String line = data.nextLine();
			if (line.contains("filename-padding:")) {
				out.println("filename-padding: 7");
			} else {
				out.println(line);
			}
		}
		out.close();
		data.close();
		
		input.delete();
		output.renameTo(input);
	}
}
