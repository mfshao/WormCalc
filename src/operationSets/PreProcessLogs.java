package operationSets;

import java.util.ArrayList;

import operations.ParsePythonTrackerLog;
import operations.ParseTrackerLog;
import operations.SanitizeCentroidLog;
import operations.SanitizeTrackerLog;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class PreProcessLogs extends DataSetOperation {
	public static void main(String[] args) throws Exception {DataSetOperator.operate(new PreProcessLogs());}
	public void go(String project) throws Exception {
		//Operation Set
		ArrayList<DataSetOperation> opSet = new ArrayList<DataSetOperation>();
                opSet.add(new SanitizeTrackerLog());
		opSet.add(new SanitizeCentroidLog());
		opSet.add(new ParsePythonTrackerLog()); //Attempt to parse legacy (Valerie's tracker) log, fail silently
		opSet.add(new ParseTrackerLog());
		
		//Go!
		for (DataSetOperation op : opSet) {
			DataSetOperator.operate(op, project);
		}
	}
}
