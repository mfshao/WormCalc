package misc;

import java.io.PrintStream;
import java.util.Scanner;

public class Entry {
	final int frame;
	public double x, y, time, timeDelta;
	public double speed, acceleration, angle, angularVelocity;
	boolean isValid = true;
	public Entry(int frame, double time, double timeDelta, double x, double y) {
		this.frame = frame;
		this.time = time;
		this.timeDelta = timeDelta;
		this.x = x;
		this.y = y;
	}
	public Entry(Scanner in) {
		frame = in.nextInt();
		time = in.nextDouble();
		timeDelta = in.nextDouble();
		double _x = in.nextDouble();
		double _y = in.nextDouble();
		speed = in.nextDouble();
		angle = in.nextDouble();
		acceleration = in.nextDouble();
		angularVelocity = in.nextDouble();
		if (_x == 0.0 && _y == 0.0) {
			x = Double.NaN;
			y = Double.NaN;
			isValid = false;
		} else if (Double.isNaN(x) || Double.isNaN(y)) {
			x = Double.NaN;
			y = Double.NaN;
			isValid = false;
		} else { 
			x = _x;
			y = _y;
		}
	}
	public boolean isValid() {
		return isValid;
	}
	public void print(PrintStream out) {
		out.println(frame + "," + time + "," + timeDelta + "," + (int)x + "," + (int)y + "," + speed + "," + acceleration + "," + angle + "," + angularVelocity);
	}
}
