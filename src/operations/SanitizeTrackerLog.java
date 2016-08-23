/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import com.kylelmoy.WormCalc.DataSetOperator;
import static com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation.root;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.LinkedList;

/**
 *
 * @author MSHAO1
 */
public class SanitizeTrackerLog extends DataSetOperator.DataSetOperation {

    class TrackerLogEntry {

        int frame;
        long timeStamp;
        int x;
        int y;
        int isMoving;
    }

    public static void main(String[] args) throws Exception {
        DataSetOperator.operate(new SanitizeTrackerLog());
    }

    public void go(String project) throws Exception {
        //Input
        File trackerTxtLog = new File(root + project + "/log/log.txt");
        File trackerDatLog = new File(root + project + "/log/log.dat");
//        File trackerTxtLog = new File("D:\\log\\log.txt");
//        File trackerDatLog = new File("D:\\log\\log.dat");
        DataInputStream is = new DataInputStream(new FileInputStream(trackerDatLog));

        //Output
        File outputTxt = new File(root + project + "/log/log_clean.txt");
        File outputDat = new File(root + project + "/log/log_clean.dat");
//        File outputTxt = new File("D:\\log\\log_clean.txt");
//        File outputDat = new File("D:\\log\\log_clean.dat");
        DataOutputStream os = new DataOutputStream(new FileOutputStream(outputDat));
        FileWriter fw = new FileWriter(outputTxt);

        LinkedList<TrackerLogEntry> buffer = new LinkedList<>();
        TrackerLogEntry lasttle = new TrackerLogEntry();
        lasttle.timeStamp = 0;

        while (is.available() > 0) {
            TrackerLogEntry tle = new TrackerLogEntry();
            tle.frame = is.readInt();
            tle.timeStamp = is.readLong();
            tle.x = is.readInt();
            tle.y = is.readInt();
            tle.isMoving = is.readInt();

            if (tle.timeStamp - lasttle.timeStamp == 0) {
                buffer.add(tle);
                continue;
            }

            int initbuflen = buffer.size();

            while (!buffer.isEmpty()) {
                TrackerLogEntry temptle = buffer.pop();
                long timeStamp = (((tle.timeStamp - temptle.timeStamp) / (initbuflen + 1)) * (initbuflen - buffer.size())) + temptle.timeStamp;
                temptle.timeStamp = timeStamp;
                fw.write(String.format("%07d %d %d %d %d%n", temptle.frame, temptle.timeStamp, temptle.x, temptle.y, temptle.isMoving));
                os.writeInt(temptle.frame);
                os.writeLong(temptle.timeStamp);
                os.writeInt(temptle.x);
                os.writeInt(temptle.y);
                os.writeInt(temptle.isMoving);
                os.flush();
                out("Correcting frame #" + temptle.frame);
            }
            fw.write(String.format("%07d %d %d %d %d%n", tle.frame, tle.timeStamp, tle.x, tle.y, tle.isMoving));
            os.writeInt(tle.frame);
            os.writeLong(tle.timeStamp);
            os.writeInt(tle.x);
            os.writeInt(tle.y);
            os.writeInt(tle.isMoving);
            lasttle = tle;
        }
        os.close();
        is.close();
        fw.close();

        trackerTxtLog.delete();
        outputTxt.renameTo(trackerTxtLog);
        trackerDatLog.delete();
        outputDat.renameTo(trackerDatLog);
    }
}
