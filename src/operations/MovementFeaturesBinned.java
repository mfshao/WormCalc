package operations;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class MovementFeaturesBinned extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new MovementFeaturesBinned()); }
	public void go(String project) throws Exception {
		Scanner data = new Scanner(new File(root + project + "/data/movementFeatures.csv"));
		data.useDelimiter(",|\r?\n|\r");
		PrintWriter out = new PrintWriter(new File(root + project + "/data/movementFeaturesBinned.csv"));
		int currenttime = 0;
		double totalSpeed = 0;
		double totalAcceleration = 0;
		double totalAngle = 0;
		double totalAngularVelocity = 0;
		int BIN_SIZE = 60;
		int c = 0;
		while (data.hasNext()) {
			data.next();
			double time = data.nextDouble();
			data.next();
			data.next();
			data.next();
			double speed = data.nextDouble();
			double acceleration = data.nextDouble();
			double angle = data.nextDouble();
			double angularVelocity = data.nextDouble();
			if ((int)(time / BIN_SIZE) != currenttime) {
				if (totalAngularVelocity < 0) throw new Error("wut");
				out.println(currenttime + "," + (totalSpeed / c) + "," + (totalAcceleration / c) + "," + (totalAngle / c) + "," + (totalAngularVelocity / c));
				currenttime = (int)(time / BIN_SIZE);
				c = 0;
				totalSpeed = 0;
				totalAngle = 0;
				totalAcceleration = 0;
				totalAngularVelocity = 0;
			}
			c++;
			totalSpeed += speed;
			totalAcceleration += acceleration;
			totalAngle += angle;
			totalAngularVelocity += angularVelocity;
		}
		out.println(currenttime + "," + (totalSpeed / c) + "," + (totalAcceleration / c) + "," + (totalAngle / c) + "," + (totalAngularVelocity / c));
		out.flush();
		out.close();
	}
}
