package operationSets;

import java.util.ArrayList;

import operations.GlobalCentroids;
import operations.PathAnimation;
import operations.CellOccupancy;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class ProcessDataSet extends DataSetOperation {

    public static void main(String[] args) throws Exception {
        DataSetOperator.operate(new ProcessDataSet());
    }

    public void go(String project) throws Exception {
        //Operation Set
        ArrayList<DataSetOperation> opSet = new ArrayList<DataSetOperation>();
        opSet.add(new PreProcessLogs());
        opSet.add(new GlobalCentroids());
        opSet.add(new CalculateMovementData());
        opSet.add(new PathAnimation());
        opSet.add(new CellOccupancy());

        //Go!
        for (DataSetOperation op : opSet) {
            DataSetOperator.operate(op, project);
        }
    }
}
