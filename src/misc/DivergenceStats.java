package misc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * Calculates the Jefferey's Divergence value between two Cells Visited graphs
 * @author KMOY4
 *
 */
public class DivergenceStats {
	static final String remote_root = "//medixsrv/Nematodes/data/";
	static final String local_root = "R:/data/";
	public static final String root = local_root;
	static double TIME_MAX = 10;
	static final int BIN = 1;
	public static void main(String[] args) throws Exception {
		//File file = new File(root);
		//String[] names = file.list();
		ArrayList<String> projects = new ArrayList<String>();
		/*for(String name : names) {
		    if (new File(root + name).isDirectory()) {
		    	projects.add(name);
		    }
		}*/
		
		if (TIME_MAX <= 10) {
			projects.add("N2_f5");
			projects.add("N2_f6");
			projects.add("N2_nf1");
			projects.add("N2_nf2");
			projects.add("N2_nf3");
			projects.add("N2_nf4");
			projects.add("N2_nf5");
			projects.add("N2_nf6");
			projects.add("N2_nf7");
			projects.add("N2_nnf1");
			projects.add("N2_nnf2");
			projects.add("tph1_f1");
			projects.add("tph1_f2");
			projects.add("tph1_f3");
			projects.add("tph1_f4");
			projects.add("tph1_nf1");
			projects.add("tph1_nf2");
			projects.add("tph1_nf3");
			projects.add("tph1_nf4");
			projects.add("tph1_nf5");
		} else if (TIME_MAX <= 36) {
			projects.add("N2_f5");
			projects.add("N2_f6");
			projects.add("N2_nf4");
			projects.add("N2_nf7");
			projects.add("N2_nnf1");
			projects.add("tph1_f1");
			projects.add("tph1_f2");
			projects.add("tph1_f3");
			projects.add("tph1_f4");
			projects.add("tph1_nf1");
			projects.add("tph1_nf3");
		} else if (TIME_MAX <= 60) {
			projects.add("N2_f5");
			projects.add("N2_f6");
			projects.add("tph1_f1");
			projects.add("tph1_f2");
			projects.add("tph1_f3");
			projects.add("tph1_f4");
			projects.add("tph1_nf1");
		}
		int size = projects.size();
		double[][] jefferyScore = new double[size][size];
		for (int y = 0; y < size; y++) {
			for (int x = y; x < size; x++) {
				jefferyScore[x][y] = jefferyDivergence(projects.get(x), projects.get(y));
			}
		}
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (jefferyScore[x][y] != 0) System.out.println(jefferyScore[x][y]);
			}
			//System.out.println();
		}
		
		double min = Double.MAX_VALUE;
		double mean = 0;
		double max = 0;
		for (double[] a : jefferyScore) {
			for (double b : a) {
				mean += b;
				if (b > max) max = b;
				if (b != 0 && b < min) min = b;
			}
		}
		mean /= size * size;
		System.out.println(min);
		System.out.println(mean);
		System.out.println(max);
		
		ImageIO.write(scoreToImage(jefferyScore), "png", new File("reports/Jeffery Divergence/JefferyScoreTable_" + (BIN * 60) + "_" + TIME_MAX + ".png"));
	}
	public static BufferedImage scoreToImage(double[][] score) {
		int size = score.length;
		double mean = 0;
		double max = 0;
		for (double[] a : score) {
			for (double b : a) {
				mean += b;
				if (b > max) max = b;
			}
		}
		mean /= size * size;
		//max = Math.log(max);
		BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				//double ratio = (Math.log(score[x][y]) / max) * 0.7;
				int val = 255 - (int)((score[x][y] / max) * 255.0);
				out.setRGB(x,y,getRGB(val, val, val));
				//out.setRGB(x,y,Color.HSBtoRGB((float)ratio, 1.0f, 1.0f));
			}
		}
		return out;
	}
	public static int getRGB(int r, int g, int b) {
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
	}
	public static double jefferyDivergence(String a, String b) throws FileNotFoundException {
		ArrayList<Integer> oA = readOccupancy(a);
		ArrayList<Integer> oB = readOccupancy(b);
		int size = oA.size();
		if (oB.size() != oA.size()) {
			throw new Error("Mismatching occupancy data length!");
		}
		double score = 0.0;
		for (int i = 0; i < size; i++) {
			int p = oA.get(i);
			int q = oB.get(i);
			double val = (p - q) * (Math.log(p) - Math.log(q));
			if (!Double.isNaN(val) && !Double.isInfinite(val)) {
				score += val;
			}
		}
		return score;
	}
	public static ArrayList<Integer> readOccupancy(String project) throws FileNotFoundException {
		ArrayList<Integer> occ = new ArrayList<Integer>();
		Scanner data = new Scanner(new File(root + project + "/data/occupancy_" + (BIN * 60) + ".csv"));
		data.useDelimiter(",|\r?\n|\r");
		int c = 0;
		while (true) {
			data.nextInt();
			occ.add(data.nextInt());
			c+= BIN;
			if (c >= TIME_MAX) break;
		}
		data.close();
		return occ;
	}
}
