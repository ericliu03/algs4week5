
public class PointSET {
	private SET<Point2D> points;

	// construct an empty set of points
	public PointSET() {
		points = new SET<Point2D>();
	}                           
	// is the set empty? 
	public boolean isEmpty(){
		return points.isEmpty();
		
	}                
	 // number of points in the set
	public int size() {
		return points.size();
	}                    
	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) { 
		points.add(p);
	}              
    // does the set contain point p? 
	public boolean contains(Point2D p)  {
		return points.contains(p);
	}
    // draw all points to standard draw
	public void draw() {

		for (Point2D point:points) {
			point.draw();
		}
//		StdDraw.show(0);
	} 
	// all points that are inside the rectangle
	public Iterable<Point2D> range(RectHV rect) {
		Stack<Point2D> insideNodes = new Stack<Point2D>();
		for(Point2D point:points) {
			if (rect.contains(point)) insideNodes.push(point);
		}
		return insideNodes;
	}      
	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(Point2D p) {
		double minDis = 100;
		Point2D nearestP = null;
		for(Point2D point:points) {
			double tempDis = p.distanceSquaredTo(point);
			if (tempDis < minDis) {
				nearestP = point;
				minDis = tempDis;
			}
		}
		return nearestP;
	}     

	public static void main(String[] args)    {
//		StdDraw.setXscale(0, 1);
//		StdDraw.setYscale(0, 1);
//		StdDraw.setPenColor(StdDraw.BLACK);
		//StdDraw.setPenRadius(.01);
//		PointSET brute = new PointSET();
//		Point2D p = new Point2D(0.50, 0.30);
//
//		brute.insert(p);
//		Point2D p1 = new Point2D(0.3, 0.50);
//
//		brute.insert(p1);
//		brute.draw();
	}              // unit testing of the methods (optional) 
}