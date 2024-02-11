package main.draw;


import java.io.Serializable;

/**
 * <h1>Point</h1> 
 * Holds x and y position on frame
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		Point p = (Point) o;
		return (x == p.x() && y == p.y());
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}

}
