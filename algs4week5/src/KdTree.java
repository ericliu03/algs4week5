
public class KdTree {

	
	private Node root;
	private int pointNum;
	private static class Node implements Comparable<Node>{
		private Point2D p;      // the point
		private RectHV rect;    // the axis-aligned rectangle corresponding to this node
		private Node lb;        // the left/bottom subtree
		private Node rt;	  	// the right/top subtree
		private boolean evenLayer;
		public Node(Point2D p) {
			this.p = p;
		}

		public int compareTo(Node that) {
			return this.p.compareTo(that.p);
		}
	}
	public KdTree(){
		root = null;
		pointNum = 0;
	}
	
	// is the set empty? 
	public boolean isEmpty() {
		return root == null;
	}           
	// number of points in the set 
	public int size() {
		return pointNum;
	}            
	
	private boolean Helper(Node newNode, Node compareNode, boolean even, boolean isInsert) {
		Node nextNode = null;
		boolean isLeft = true;
		//decide which direction should be chosen if not find the same point
		if (newNode.compareTo(compareNode) == 0) return true;
		else if ((even && newNode.p.x() < compareNode.p.x()) || (!even && newNode.p.y() < compareNode.p.y())) 
			nextNode = compareNode.lb;
		else {
			isLeft = false;
			nextNode = compareNode.rt;
		}
		
		if (nextNode == null) {
			//if not find the point, and we need to insert
			if (isInsert) {
				RectHV parentRect = compareNode.rect;
				if (isLeft) {
					compareNode.lb = newNode;
					if (even) newNode.rect = new RectHV(parentRect.xmin(), parentRect.ymin(), compareNode.p.x(), parentRect.ymax());
					else newNode.rect = new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), compareNode.p.y());
				}
				else {
					compareNode.rt = newNode;
					if (even) newNode.rect = new RectHV(compareNode.p.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
					else newNode.rect = new RectHV(parentRect.xmin(), compareNode.p.y(), parentRect.xmax(), parentRect.ymax());
				}
				//is the new point in a even or odd level, reverse of its parent
				newNode.evenLayer = !even;
				return false;
			} else {
				return false;
			}
		} else {
			return Helper(newNode, nextNode, !even, isInsert);
		}
	}
	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		Node newNode = new Node(p);
		if (root == null) {
			root = newNode;
			root.rect = new RectHV(0,0,1,1);
			root.evenLayer = true;
			pointNum ++;
		}
		else if (!Helper(newNode, root, true, true)) pointNum ++;
	}            
	// does the set contain point p? 
	public boolean contains(Point2D p)   {
		if (isEmpty()) return false;
		Node newNode = new Node(p);
		return Helper(newNode, root, true, false);
	}      
	
	// draw all points and sections to standard draw 
	public void draw() {
		Queue<Node> queue = new Queue<Node>();
		queue.enqueue(root);
		while(!queue.isEmpty()) {
			Node currentNode = queue.dequeue();
			
			//draw this point
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(.01);
			currentNode.p.draw();
			
			//draw the section intersected by this point
			StdDraw.setPenRadius();
			if (currentNode.evenLayer) {
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.line(currentNode.p.x(), currentNode.rect.ymin(), currentNode.p.x(), currentNode.rect.ymax());
			} else {
				StdDraw.setPenColor(StdDraw.BLUE);
				StdDraw.line(currentNode.rect.xmin(), currentNode.p.y(), currentNode.rect.xmax(), currentNode.p.y());
			}
			// put 
			if (currentNode.lb != null) queue.enqueue(currentNode.lb);
			if (currentNode.rt != null) queue.enqueue(currentNode.rt);
		}
	}          
	// all points that are inside the rectangle 
	public Iterable<Point2D> range(RectHV rect) {
		Stack<Point2D> result = new Stack<Point2D>();
		if (isEmpty()) return result;
		Queue<Node> queue = new Queue<Node>();
		queue.enqueue(root);
		while(!queue.isEmpty()) {
			Node currentNode = queue.dequeue();
			if (rect.contains(currentNode.p)) result.push(currentNode.p);
			
			if (currentNode.lb != null && currentNode.lb.rect.intersects(rect)) queue.enqueue(currentNode.lb);
			if (currentNode.rt != null && currentNode.rt.rect.intersects(rect)) queue.enqueue(currentNode.rt);
		}
		return result;
	}   
	
	// a nearest neighbor in the set to point p; null if the set is empty 
	public Point2D nearest(Point2D p) {
		if (isEmpty()) return null;
		Point2D resultP = null;
		double minDis = 100;
		Queue<Node> queue = new Queue<Node>();
		queue.enqueue(root);
		while(!queue.isEmpty()) {
			Node currentNode = queue.dequeue();
			double curDis = currentNode.p.distanceSquaredTo(p);
			if (curDis < minDis) {
				minDis = curDis;
				resultP = currentNode.p;
			}
			Node right = currentNode.rt;
			Node left = currentNode.lb;
			
			if (right != null && left != null) {
				if (right.rect.contains(p)) {
					queue.enqueue(right);
					if (left.rect.distanceSquaredTo(p) < minDis) queue.enqueue(left);
				} else {
					queue.enqueue(left);
					if (right.rect.distanceSquaredTo(p) < minDis) queue.enqueue(right);
				}
			} else if (right == null && left != null) {
				if (left.rect.contains(p)) queue.enqueue(left);
				else if (left.rect.distanceSquaredTo(p) < minDis) queue.enqueue(left);
			} else if (left == null && right != null) {
				if (right.rect.contains(p)) queue.enqueue(right);
				else if (right.rect.distanceSquaredTo(p) < minDis) queue.enqueue(right);
			}
//			if (right != null) {
//				if (right.rect.contains(p)) queue.enqueue(right);
//				else if (right.rect.distanceSquaredTo(p) < minDis) {
//					queue.enqueue(right);	
//					rightHaveP = true;
//				}
//			}
//			if (left != null) {
//				if (right != null) {
//					if (!rightHaveP) queue.enqueue(left);
//					else if (left.rect.distanceSquaredTo(p) < minDis) queue.enqueue(left);
//				}
//				else {
//					if (left.rect.contains(p)) queue.enqueue(left);
//					else if (left.rect.distanceSquaredTo(p) < minDis) queue.enqueue(left);
//				}
//			}
		}
		return resultP;
	}          
	
	public static void main(String[] args)   {

		
		KdTree tree = new KdTree();
		System.out.println(tree.nearest(new Point2D(0.3,0.3)));

		tree.insert(new Point2D(1,0));
//		tree.insert(new Point2D(0.3,0.3));
//		tree.insert(new Point2D(0.6,0.9));
//		tree.insert(new Point2D(0.9,0.2));
//		tree.insert(new Point2D(0.1,0.1));
//		System.out.println(tree.isEmpty());
		System.out.println(tree.nearest(new Point2D(0,1)));
		System.out.println(tree.contains(new Point2D(0.3,0.3)));
//		StdDraw.setXscale(0, 1);
//		StdDraw.setYscale(0, 1);

//		RectHV rect = new RectHV(0.03,0.05,0.43,0.43);
//		rect.draw();
//		System.out.print(tree.contains(new Point2D(0.1,0.1)));
//		tree.draw();
//		StdDraw.setPenRadius(.01);
//		for(Point2D point:tree.range(rect)) {
//			System.out.print(point.x());
//			StdDraw.setPenColor(StdDraw.RED);
//			point.draw();
//			StdDraw.show();
//		}
		
		
	} 
}
