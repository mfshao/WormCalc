package operations;

import java.io.File;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

import deprecated.CombineParsedLogAndFeatures;
import deprecated.LogCleanBad;
import deprecated.LogFillCases;

public class BuildDataFile extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new BuildDataFile()); }
	public void go (String project) throws Exception {
		File file = new File(root + project + "/data/data.csv");
		if (file.exists()) {
			System.out.println(project + " data file exists, skipping...");
			return;
		}
		LogFillCases.go(project);
    	CombineParsedLogAndFeatures.go(project);
    	LogCleanBad.go(project);
	}
}
