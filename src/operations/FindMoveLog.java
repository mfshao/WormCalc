package operations;

import java.io.File;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

/**
 * Lol.
 * 
 * @author Kyle Moy
 *
 */
public class FindMoveLog extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new FindMoveLog()); }
	public void go(String project) throws Exception {
		out(root + project + "/input/log.dat");
		File file = new File(root + project + "/input/log.dat");
		if (file.exists()) {
			out("Log in wrong place... moving.");
			file.renameTo(new File(root + project + "/log/log.dat"));
		}
	}
}