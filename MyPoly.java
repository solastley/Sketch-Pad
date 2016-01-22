//Solomon Astley, #3938540
//CS 0401 Ramirez, Lab Section Thursday 10:00 am
//Assignment 5, MyPoly Class
//This class represents a single shape for assignment 5

import java.util.*;
import java.awt.*;
import java.awt.geom.*;

public class MyPoly extends Polygon
{
	 // This ArrayList is how we will "display" the points in the MyPoly.  The idea is
	 // that a circle will be created for every (x,y) point in the MyPoly.  To give you
	 // a good start on this, I have implemented the constructors below.
	 private ArrayList<Ellipse2D.Double> thePoints;
	 private Color myColor;
	 private boolean highlighted, inCircle, alSelected;
	 private int currentPosition, myIndex;
	 
	 // Constructors.
	MyPoly()
	{
		super();
		myColor = Color.BLACK;
		thePoints = new ArrayList<Ellipse2D.Double>();
		alSelected = false;
		myIndex = -1;
		inCircle = false;
	}

	MyPoly(int [] xpts, int [] ypts, int npts, Color col)
	{
		super(xpts, ypts, npts);
		myColor = col;
		thePoints = new ArrayList<Ellipse2D.Double>();
		for (int i = 0; i < npts; i++)
		{
			int x = xpts[i];
			int y = ypts[i];
			addCircle(x, y);
		}
		alSelected = false;
		myIndex = -1;
		inCircle = false;
	}
	
	// The setFrameFromCenter() method in Ellipse2D.Double allows the circles to be
	// centered on the points in the MyPoly
	public void addCircle(int x, int y)
	{
		Ellipse2D.Double temp = new Ellipse2D.Double(x, y, 8, 8);
		temp.setFrameFromCenter(x, y, x+4, y+4);
		thePoints.add(temp);
	}
     
    //method to move shape across the canvas
	public void translate(int x, int y)
	{
		//calls the superclass method to move the shape across the screen
		super.translate(x, y);
		thePoints.clear();
		//moves all the points
		for (int i = 0; i < xpoints.length; i++)
		{
			addCircle(xpoints[i], ypoints[i]);
		}
	}
     
	public void setHighlight(boolean b)
	{
		highlighted = b;	
	}
     
	public void addPoint(int x, int y)
	{
		//this simply calls the super class method and then adds a new circle
		super.addPoint(x, y);
		addCircle(x, y);
	}
     
    //this method calls the getClosest() method and then adds a point between that point and the next in line
	public MyPoly insertPoint(int x, int y)
	{
		int index = getClosest(x, y);
		MyPoly newShape = new MyPoly();

		for (int i = 0; i < npoints; i++)
		{
			newShape.addPoint(xpoints[i], ypoints[i]);
			if (i == index)
				newShape.addPoint(x, y);
		}
		newShape.setColor(myColor);
		newShape.setHighlight(true);
		return newShape;
	}
	
	// This method will return the index of the first point of the line segment that is
	// closest to the argument (x, y) point.  It uses some methods in the Line2D.Double
	// class.
	public int getClosest(int x, int y)
	{
		if (npoints == 1)
			return 0;
		else
		{
			Line2D currSeg = new Line2D.Double(xpoints[0], ypoints[0], xpoints[1], ypoints[1]);
			double currDist = currSeg.ptSegDist(x, y);
			double minDist = currDist;
			int minInd = 0;
			for (int ind = 1; ind < npoints; ind++)
			{
				currSeg = new Line2D.Double(xpoints[ind], ypoints[ind],
								xpoints[(ind+1)%npoints], ypoints[(ind+1)%npoints]);
				currDist = currSeg.ptSegDist(x, y);
				if (currDist < minDist)
				{
					minDist = currDist;
					minInd = ind;
				}
			}
			return minInd;
		}
	}

	//method to remove a point from the shape
	public MyPoly removePoint(int x, int y)
	{
		boolean contains = false;
		int index = 0;
		for (int i = 0; i < npoints; i++)
		{
			//if the given point is within a circle on the vertices of the shape, confirm
			if ((thePoints.get(i).x - 8) <= x && x <= (thePoints.get(i).x + 8) && (thePoints.get(i).y - 8) <= y && y <= (thePoints.get(i).y + 8))
			{
				index = i;
				contains = true;
			}
		}

		if (contains == true) //if the right click was made inside a circle
		{
			MyPoly newShape = new MyPoly();

			//redraws all of the points, but skips over the circle that was clicked
			for (int i = 0; i < npoints; i++)
			{
				if (i == index)
					continue;
				else
					newShape.addPoint(xpoints[i], ypoints[i]);
			}
			newShape.setColor(myColor);
			newShape.setHighlight(true);
			if (npoints == 1)
				return null;
			else
				return newShape;
		}
		else
			return this;
	}

	public boolean contains(int x, int y)
	{
		//if there aren't any points, return false
		if (npoints == 0)
			return false;

		//if there is one point, check to see if x and y are that point
		else if (npoints == 1)
		{
			if (x == xpoints[0] && y == ypoints[0])
				return true;
			else
				return false;
		}

		//if there are two points
		else if (npoints == 2)
		{
			//makes three Point2D.Double objects to compare distances for the two points and the arguments
			Point2D.Double a = new Point2D.Double(xpoints[0], ypoints[0]);
			Point2D.Double b = new Point2D.Double(xpoints[1], ypoints[1]);
			Point2D.Double c = new Point2D.Double(x, y);

			//if the distance between one point and the arguments plus the distance between the second point and the arguments
			//is reasonably close to the distance between the two points, then the line contains that argument
			if (a.distance(c) + b.distance(c) <= a.distance(b) + 0.01 && a.distance(c) + b.distance(c) >= a.distance(b) - 0.01)
				return true;
			else
				return false;
		}

		//otherwise just call the super class method
		else
		{
			return super.contains(x, y);
		}
	}
	
	public void draw(Graphics2D g)
	{
		if (npoints == 1)
		{
			g.setColor(Color.BLACK);
			g.drawLine(xpoints[0], ypoints[0], xpoints[0], ypoints[0]);
			if (highlighted == true)
			{
				g.draw(thePoints.get(0));
				if (inCircle)
					g.fill(thePoints.get(currentPosition));
			}
		}
		else if (npoints == 2)
		{
			g.setColor(Color.BLACK);
			g.drawLine(xpoints[0], ypoints[0], xpoints[1], ypoints[1]);
			if (highlighted == true)
			{
				g.draw(thePoints.get(0));
				g.draw(thePoints.get(1));
				if (inCircle)
				{
					g.fill(thePoints.get(currentPosition));
				}
			}
		}
		else
		{
			g.setColor(Color.BLACK);
			g.draw(this);
			if (highlighted == true)
			{
				for (int i = 0; i < npoints; i++)
				{
					g.draw(thePoints.get(i));
				}
				if (inCircle)
					g.fill(thePoints.get(currentPosition));
			}
			else
			{
				g.setColor(myColor);
				g.draw(this);
				g.fill(this);
			}
		}
	}
	  
	public String fileData()
	{
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < npoints; i++)
		{
			if (i < npoints - 1)
				b.append(xpoints[i] + "," + ypoints[i] + ":");
			else
				b.append(xpoints[i] + "," + ypoints[i] + "|");
		}
		b.append(myColor.getRed() + "," + myColor.getGreen() + "," + myColor.getBlue());

		return b.toString();
	}
	
	public void setColor(Color newColor)
	{
		myColor = newColor;
	}	
	
	public Color getColor()
	{
		return myColor;
	}

	//this is a method that was implemented for extra credit. It determines whether or not a given point is within one of the points in the
	//shape, and if it was, it moves that point and changes it
	public void movePoint(int x, int y, int deltaX, int deltaY)
	{
		//if a point is not already selected (so multiple points are not moved at once)
		if (!alSelected)
		{
			//if the given point is within a circle, alSelected changes to true and the index is recorded
			//else, alSelected remains false
			for (int i = 0; i < npoints; i++)
			{
				if ((thePoints.get(i).x - 8) <= x && x <= (thePoints.get(i).x + 8) && (thePoints.get(i).y - 8) <= y && y <= (thePoints.get(i).y + 8))
				{
					alSelected = true;
					myIndex = i;
					break;
				}
				else
				{
					alSelected = false;
					myIndex = -1;
				}
			}
		}

		//if the point was within one of the circles
		if (myIndex > -1)
		{
			//if the mouse is now clicked somewhere outside one of the circles
			if ((thePoints.get(myIndex).x - 8) > x || x > (thePoints.get(myIndex).x + 8) || (thePoints.get(myIndex).y - 8) > y || y > (thePoints.get(myIndex).y + 8))
			{
				//alSelected is changed back to false
				alSelected = false;
				myIndex = -1;
			}
		}

		//if a point is being dragged
		if (myIndex > -1 && alSelected)
		{
			//move the point and change it in the arrays of points
			thePoints.get(myIndex).x = thePoints.get(myIndex).x + deltaX;
			thePoints.get(myIndex).y = thePoints.get(myIndex).y + deltaY;
			xpoints[myIndex] = xpoints[myIndex] + deltaX;
			ypoints[myIndex] = ypoints[myIndex] + deltaY;
		}
	}

	public boolean isHighlighted()
	{
		return highlighted;
	}

	public ArrayList<Ellipse2D.Double> getPoints()
	{
		return thePoints;
	}

	public void setPosition(int i)
	{
		currentPosition = i;
	}

	//this method was necessary to let the draw method know when to fill a point in
	public void setFilled(boolean fillPoint)
	{
		inCircle = fillPoint;
	}
}
