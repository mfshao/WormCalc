package operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.imageio.ImageIO;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

public class ImageCompression extends DataSetOperation {
	public static void main(String[] args) throws Exception { DataSetOperator.operate(new ImageCompression()); }

	@Override
	public void go(String project) throws Exception {
		String path = DataSetOperator.remote_root + project + "/input/";
		if (!new File(path).exists()) return;
		File dir = new File(DataSetOperator.local_root + project + "/package/");
		dir.mkdirs();
		int c = 0;
		int pkg = 0;
		for (int comp = 0; comp < 10; comp++) {
			System.out.println("Package: " + pkg + "\tCompression: " + comp);
			File pack = new File(DataSetOperator.local_root + project + "/package/" + pkg++ + ".pkg");
			DeflaterOutputStream out = new DeflaterOutputStream(new FileOutputStream(pack),new Deflater(comp), 1024 * 4);
			
			long totalTime = 0;
			for (int i = 0; i < 128; i++) {
				long time = System.nanoTime();
				File in = new File(path + String.format("%06d", c++) + ".jpeg");
				BufferedImage img;
				try {
					img = ImageIO.read(in);
				} catch (Exception e) {
					break;
				}
				byte[] src = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
				for (int j = 0; j < src.length / 3; j++) {
					int gray = (int) ((src[j] + src[j+1] + src[j+2]) / 3);
					if (gray > 200) {
						gray = 255;
					}
					out.write(gray);
				}
				totalTime += System.nanoTime() - time;
			}
			out.close();
			totalTime /= 512;
			System.out.println(totalTime);
		}
	}
}
