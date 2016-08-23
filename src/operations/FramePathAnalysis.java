package operations;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.kylelmoy.WormCalc.DataSetOperator;
import com.kylelmoy.WormCalc.DataSetOperator.DataSetOperation;

import stdlib.StdDraw;

public class FramePathAnalysis extends DataSetOperation {
	static ImageFrame frame;
	static String project;
	public static void main(String[] args) throws Exception {
		DataSetOperator.operate(new FramePathAnalysis());
	}
    public void go(String project) throws Exception {
		setRoot(DataSetOperator.remote_root);
    	this.project = project;
    	//sc.close();
        Scanner sc = new Scanner(new File(root + project + "/data/centroid.csv"));
		sc.useDelimiter(",|\r?\n|\r");
		
        StdDraw.show(0);
        EventQueue.invokeLater(new Runnable()
        {
            public void run(){
                FramePathAnalysis.frame = new ImageFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        }
        );
        final KdTree kdtree = new KdTree();
        double minX = (double)Integer.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = (double)Integer.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        out("Loading...");
        int c = 0;
        while (sc.hasNext()) {
        	int f = sc.nextInt();
            double te = sc.nextDouble();
            sc.nextDouble();
            final Double x = sc.nextDouble();
            final Double y = sc.nextDouble();
            final Point2D p = new Point2D(x, y, f);
            c++;
            if (x == -1 && y == -1) continue;
            if (x.isNaN()) continue;
            if (y.isNaN()) continue;
            kdtree.insert(p);
            if (x > maxX) maxX = x;
            if (x < minX) minX = x;
            if (y > maxY) maxY = y;
            if (y < minY) minY = y;
            //if (c > 20000) break;
        }
        out("Done! Loaded " + c + " points");
        out("MinX: " + minX + "\t MaxX: " + maxX);
        out("MinY: " + minY + "\t MaxY: " + maxY);
        StdDraw.setCanvasSize(512,512);
        //StdDraw.setXscale(-1000, 1000);
        //StdDraw.setYscale(-1000, 1000);
        StdDraw.setXscale(minX, maxX);
        StdDraw.setYscale(maxY, minY);
        while (true) {
            final double x = StdDraw.mouseX();
            final double y = StdDraw.mouseY();
            final Point2D query = new Point2D(x, y, 0);
            final Point2D k = kdtree.nearest(query);
            StdDraw.text(minX, minX - 10, "Frame: " + k.ind);
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(.001);
            kdtree.draw();
            query.draw();
            StdDraw.setPenRadius(.02);
            StdDraw.setPenColor(StdDraw.BLUE);
            k.draw();
            frame.show(k);
            StdDraw.show(0);
            StdDraw.show(40);
        }
    }

    static class ImageFrame extends JFrame{
    	ImageComponent component;
        public ImageFrame(){
            setTitle("Frame");
            setSize(640, 480);
            component = new ImageComponent();
            add(component);
        }
        public void show(Point2D p) throws Exception{
        	int x = p.ind;
        	if (component.current == x) return;
        	else component.current = x;
        	//out(x + ", " + p.x() + ", " + p.y());
        	out(root + project + "/input/" + String.format("%06d", x) + ".jpeg");
        	BufferedImage img;
        	try{
        		img = ImageIO.read(new File(root + project + "/input/" + String.format("%06d", x) + ".jpeg"));
        		component.image = img;
        	} catch (Exception e) {}
        	//component.image.getGraphics().drawString("" + x, 0, 20);
        	component.repaint();
        }
    }


    static class ImageComponent extends JComponent{
        private static final long serialVersionUID = 1L;
        Image image;
        int current;
        public ImageComponent(){
        }
        public void paintComponent (Graphics g){
            if(image == null) return;
            g.drawImage(image, 0, 0, this);
        }

    }
    public static class KdTree {
    	/* construct an empty set of points */
    	Node root;
    	int N;
    	private class Node {
    		Point2D point;
    		Node left;
    		Node right;
    		public Node(Point2D p) {
    			point = p;
    		}
    	}
    	public KdTree() {
    	}
    	/* is the set empty? */
    	public boolean isEmpty() {
    		return N == 0;
    	}

    	/* number of points in the set */
    	public int size() {
    		return N;
    	}

    	/* add the point p to the set (if it is not already in the set) */
    	public void insert(Point2D p) {
    		root = insert(root, p, true);
    	}
    	private Node insert(Node root, Point2D p, boolean useX) {
            if (root == null) {
            	N++;
            	return new Node(p);
            }
            double cmp = useX ? p.x() - root.point.x() : p.y() - root.point.y();
            
            if (cmp < 0)
            	root.left  = insert(root.left,  p, !useX);
            else if (cmp >= 0)
            	if (p != root.point)
            		root.right = insert(root.right, p, !useX);
            return root;
    	}
    	/* does the set contain the point p? */
    	public boolean contains(Point2D p) {
    		return contains(root, p, true);
    	}
    	private boolean contains(Node root, Point2D p, boolean useX) {
            if (root == null) return false;
            double cmp = useX ? p.x() - root.point.x() : p.y() - root.point.y();
            
            if (cmp < 0)				return contains(root.left,  p, !useX);
            else if (cmp >= 0)
            	if (p != root.point)	return contains(root.right, p, !useX);
            	else return true;
            return true;
    	}
    	/* draw all of the points to standard draw */
    	public void draw() {
    		ArrayDeque<Node> ad = new ArrayDeque<Node>();
    		ad.add(root);
    		while (!ad.isEmpty()) {
    			Node n = ad.remove();
    			if (n.left != null) ad.add(n.left);
    			if (n.right != null) ad.add(n.right);
    			StdDraw.point(n.point.x(), n.point.y());
    		}
    		//draw(root);
    	}
    	private void draw(Node node) {
    		if (node == null) return;
    		StdDraw.point(node.point.x(), node.point.y());
    		draw(node.left);
    		draw(node.right);
    	}

    	/* a nearest neighbor in the set to p; null if set is empty */
    	  public Point2D nearest(Point2D p) {return nearestv(p, root);}
    	  
    	  private Point2D nearestv(Point2D p, Node n){
    		  if (n==null){return null;}
    		  int cmp =Point2D.X_ORDER.compare(p,n.point);
    		  if (cmp<=0){
    			  Point2D closest=nearesth(p, n.left);
    			  double distance;
    			  if (closest==null || closest.distanceTo(p)>n.point.distanceTo(p)){closest=n.point; distance=n.point.distanceTo(p);}
    			  else {distance= closest.distanceTo(p);}
    			  if(distance>n.point.x()-p.x()){
    				  Point2D right_closest=nearesth(p, n.right);
    				  if (right_closest!=null && right_closest.distanceTo(p)<distance){ return right_closest;} 
    			  }
    			  return closest;
    		  }
    			  Point2D closest=nearesth(p, n.right);
    			  double distance;
    			  if (closest==null || closest.distanceTo(p)>n.point.distanceTo(p)){closest=n.point; distance=n.point.distanceTo(p);}
    			  else {distance= closest.distanceTo(p);}
    			  if(distance>-n.point.x()+p.x()){
    				  Point2D left_closest=nearesth(p, n.left);
    				  if (left_closest!=null && left_closest.distanceTo(p)<distance){ return left_closest;} 
    			  }
    			  return closest;
    	  }
    	  
    	  private Point2D nearesth(Point2D p, Node n){
    		  if (n==null){return null;}
    		  int cmp =Point2D.Y_ORDER.compare(p,n.point);
    		  if (cmp<=0){
    			  Point2D closest=nearestv(p, n.left);
    			  double distance;
    			  if (closest==null || closest.distanceTo(p)>n.point.distanceTo(p)){closest=n.point; distance=n.point.distanceTo(p);}
    			  else {distance= closest.distanceTo(p);}
    			  if(distance>n.point.y()-p.y()){
    				  Point2D right_closest=nearestv(p, n.right);
    				  if (right_closest!=null && right_closest.distanceTo(p)<distance){ return right_closest;} 
    			  }
    			  return closest;
    			  }
    			  Point2D closest=nearestv(p, n.right);
    			  double distance;
    			  if (closest==null || closest.distanceTo(p)>n.point.distanceTo(p)){closest=n.point; distance=n.point.distanceTo(p);}
    			  else {distance= closest.distanceTo(p);}
    			  if(distance>-n.point.y()+p.y()){
    				  Point2D left_closest=nearestv(p, n.left);
    				  if (left_closest!=null && left_closest.distanceTo(p)<distance){ return left_closest;} 
    			  }
    			  return closest;
    	  }

    }
	  public static class Point2D implements Comparable<Point2D> {
		    public static final Comparator<Point2D> X_ORDER = new XOrder();
		    public static final Comparator<Point2D> Y_ORDER = new YOrder();
		    public static final Comparator<Point2D> R_ORDER = new ROrder();

		    public final Comparator<Point2D> POLAR_ORDER = new PolarOrder();
		    public final Comparator<Point2D> ATAN2_ORDER = new Atan2Order();
		    public final Comparator<Point2D> DISTANCE_TO_ORDER = new DistanceToOrder();

		    private final double x;    // x coordinate
		    private final double y;    // y coordinate
		    public final int ind;
		    // create a new point (x, y)
		    public Point2D(double x, double y, int ind) {
		        this.x = x;
		        this.y = y;
		        this.ind = ind;
		    }

		    // return the x-coorindate of this point
		    public double x() { return x; }

		    // return the y-coorindate of this point
		    public double y() { return y; }

		    // return the radius of this point in polar coordinates
		    public double r() { return Math.sqrt(x*x + y*y); }

		    // return the angle of this point in polar coordinates
		    // (between -pi/2 and pi/2)
		    public double theta() { return Math.atan2(y, x); }

		    // return the polar angle between this point and that point (between -pi and pi);
		    // (0 if two points are equal)
		    private double angleTo(Point2D that) {
		        final double dx = that.x - this.x;
		        final double dy = that.y - this.y;
		        return Math.atan2(dy, dx);
		    }

		    // is a->b->c a counter-clockwise turn?
		    // -1 if clockwise, +1 if counter-clockwise, 0 if collinear
		    public static int ccw(Point2D a, Point2D b, Point2D c) {
		        final double area2 = (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
		        if      (area2 < 0) return -1;
		        else if (area2 > 0) return +1;
		        else                return  0;
		    }

		    // twice signed area of a-b-c
		    public static double area2(Point2D a, Point2D b, Point2D c) {
		        return (b.x-a.x)*(c.y-a.y) - (b.y-a.y)*(c.x-a.x);
		    }

		    // return Euclidean distance between this point and that point
		    public double distanceTo(Point2D that) {
		        final double dx = this.x - that.x;
		        final double dy = this.y - that.y;
		        return Math.sqrt(dx*dx + dy*dy);
		    }

		    // return square of Euclidean distance between this point and that point
		    public double distanceSquaredTo(Point2D that) {
		        final double dx = this.x - that.x;
		        final double dy = this.y - that.y;
		        return dx*dx + dy*dy;
		    }

		    // compare by y-coordinate, breaking ties by x-coordinate
		    public int compareTo(Point2D that) {
		        if (this.y < that.y) return -1;
		        if (this.y > that.y) return +1;
		        if (this.x < that.x) return -1;
		        if (this.x > that.x) return +1;
		        return 0;
		    }

		    // compare points according to their x-coordinate
		    private static class XOrder implements Comparator<Point2D> {
		        public int compare(Point2D p, Point2D q) {
		            if (p.x < q.x) return -1;
		            if (p.x > q.x) return +1;
		            return 0;
		        }
		    }

		    // compare points according to their y-coordinate
		    private static class YOrder implements Comparator<Point2D> {
		        public int compare(Point2D p, Point2D q) {
		            if (p.y < q.y) return -1;
		            if (p.y > q.y) return +1;
		            return 0;
		        }
		    }

		    // compare points according to their polar radius
		    private static class ROrder implements Comparator<Point2D> {
		        public int compare(Point2D p, Point2D q) {
		            final double delta = (p.x*p.x + p.y*p.y) - (q.x*q.x + q.y*q.y);
		            if (delta < 0) return -1;
		            if (delta > 0) return +1;
		            return 0;
		        }
		    }

		    // compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
		    private class Atan2Order implements Comparator<Point2D> {
		        public int compare(Point2D q1, Point2D q2) {
		            final double angle1 = angleTo(q1);
		            final double angle2 = angleTo(q2);
		            if      (angle1 < angle2) return -1;
		            else if (angle1 > angle2) return +1;
		            else                      return  0;
		        }
		    }

		    // compare other points relative to polar angle (between 0 and 2pi) they make with this Point
		    private class PolarOrder implements Comparator<Point2D> {
		        public int compare(Point2D q1, Point2D q2) {
		            final double dx1 = q1.x - x;
		            final double dy1 = q1.y - y;
		            final double dx2 = q2.x - x;
		            final double dy2 = q2.y - y;

		            if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 above; q2 below
		            else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 below; q2 above
		            else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
		                if      (dx1 >= 0 && dx2 < 0) return -1;
		                else if (dx2 >= 0 && dx1 < 0) return +1;
		                else                          return  0;
		            }
		            else return -ccw(Point2D.this, q1, q2);     // both above or below

		            // Note: ccw() recomputes dx1, dy1, dx2, and dy2
		        }
		    }

		    // compare points according to their distance to this point
		    private class DistanceToOrder implements Comparator<Point2D> {
		        public int compare(Point2D p, Point2D q) {
		            final double dist1 = distanceSquaredTo(p);
		            final double dist2 = distanceSquaredTo(q);
		            if      (dist1 < dist2) return -1;
		            else if (dist1 > dist2) return +1;
		            else                    return  0;
		        }
		    }


		    // does this point equal y?
		    public boolean equals(Object other) {
		        if (other == this) return true;
		        if (other == null) return false;
		        if (other.getClass() != this.getClass()) return false;
		        final Point2D that = (Point2D) other;
		        // Don't use == here if x or y could be NaN or -0
		        if (Double.compare(this.x,that.x) != 0) return false;
		        if (Double.compare(this.y,that.y) != 0) return false;
		        return true;
		    }

		    // must override hashcode if you override equals
		    // See Item 9 of Effective Java (2e) by Joshua Block
		    private volatile int hashCode;
		    public int hashCode() {
		        int result = hashCode;
		        if (result == 0) {
		            result = 17;
		            result = 31*result + ((Double) x).hashCode();
		            result = 31*result + ((Double) y).hashCode();
		            hashCode = result;
		        }
		        return result;
		    }

		    // convert to string
		    public String toString() {
		        return "(" + x + ", " + y + ")";
		    }

		    // plot using StdDraw
		    public void draw() {
		        StdDraw.point(x, y);
		    }

		    // draw line from this point p to q using StdDraw
		    public void drawTo(Point2D that) {
		        StdDraw.line(this.x, this.y, that.x, that.y);
		    }
		}
}
