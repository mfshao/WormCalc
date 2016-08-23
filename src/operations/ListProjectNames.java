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
public class ListProjectNames extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new ListProjectNames()); }
	public void go(String project) throws Exception {
		setRoot(DataSetOperator.remote_root);
		DataSetOperator.setVerbose(false);
		File dir = new File(root + project);
		System.out.print(project + ":\t");
		for (File f : dir.listFiles()) {
			if (f.getName().contains(".avi"))
				System.out.println(f.getName());
		}
	}
}