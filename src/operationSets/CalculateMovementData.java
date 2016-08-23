package operationSets;

import java.util.ArrayList;

import operations.MovementFeatures;
import operations.MovementFeaturesBinned;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class CalculateMovementData extends DataSetOperation {
	public static void main(String[] args) throws Exception {DataSetOperator.operate(new CalculateMovementData());}
	public void go(String project) throws Exception {
		//Operation Set
		ArrayList<DataSetOperation> opSet = new ArrayList<DataSetOperation>();
		opSet.add(new MovementFeatures());
		opSet.add(new MovementFeaturesBinned());
		
		//Go!
		for (DataSetOperation op : opSet) {
			DataSetOperator.operate(op, project);
		}
	}
}
