/*
 * Created on 16.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.view.mindmapview;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

class PathBBox {
	public static Rectangle2D getBBox(Shape s) {
		boolean first = true;
		double bounds[] = new double[4];
		double coords[] = new double[6];
		double curx = 0;
		double cury = 0;
		double movx = 0;
		double movy = 0;
		double cpx0, cpy0, cpx1, cpy1, endx, endy;
		for (PathIterator pi = s.getPathIterator(null);
			 !pi.isDone();
			 pi.next())
		{
			int type = pi.currentSegment(coords);
			switch (pi.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				movx = curx = coords[0];
				movy = cury = coords[1];
				if (first) {
					bounds[0] = bounds[2] = curx;
					bounds[1] = bounds[3] = cury;
					first = false;
				} else {
					accum(bounds, curx, cury);
				}
				break;
			case PathIterator.SEG_LINETO:
				curx = coords[0];
				cury = coords[1];
				accum(bounds, curx, cury);
				break;
			case PathIterator.SEG_QUADTO:
				cpx0 = coords[0];
				cpy0 = coords[1];
				endx = coords[2];
				endy = coords[3];
				double t = findQuadZero(curx, cpx0, endx);
				if (t > 0 && t < 1) {
					accumQuad(bounds, t, curx, cury, cpx0, cpy0, endx, endy);
				}
				t = findQuadZero(cury, cpy0, endy);
				if (t > 0 && t < 1) {
					accumQuad(bounds, t, curx, cury, cpx0, cpy0, endx, endy);
				}
				curx = endx;
				cury = endy;
				accum(bounds, curx, cury);
				break;
			case PathIterator.SEG_CUBICTO:
				cpx0 = coords[0];
				cpy0 = coords[1];
				cpx1 = coords[2];
				cpy1 = coords[3];
				endx = coords[4];
				endy = coords[5];
				int num = findCubicZeros(coords, curx, cpx0, cpx1, endx);
				for (int i = 0; i < num; i++) {
					accumCubic(bounds, coords[i],
							   curx, cury, cpx0, cpy0, cpx1, cpy1, endx, endy);
				}
				num = findCubicZeros(coords, cury, cpy0, cpy1, endy);
				for (int i = 0; i < num; i++) {
					accumCubic(bounds, coords[i],
							   curx, cury, cpx0, cpy0, cpx1, cpy1, endx, endy);
				}
				curx = endx;
				cury = endy;
				accum(bounds, curx, cury);
				break;
			case PathIterator.SEG_CLOSE:
				// Original starting point already included
				curx = movx;
				cury = movy;
				break;
			}
		}
		return new Rectangle2D.Double(bounds[0], bounds[1],
									  bounds[2] - bounds[0],
									  bounds[3] - bounds[1]);
	}

	private static void accum(double[] bounds, double x, double y) {
		bounds[0] = Math.min(bounds[0], x);
		bounds[1] = Math.min(bounds[1], y);
		bounds[2] = Math.max(bounds[2], x);
		bounds[3] = Math.max(bounds[3], y);
	}

	private static double findQuadZero(double cur, double cp, double end) {
		// The polynomial form of the Quadratic is:
		// eqn[0] = cur;
		// eqn[1] = cp + cp - cur - cur;
		// eqn[2] = cur - cp - cp + end;
		// Since we want the derivative, we can calculate it in one step:
		// deriv[0] = cp + cp - cur - cur;
		// deriv[1] = 2 * (cur - cp - cp + end);
		// Since we really want the zero, we can calculate that in one step:
		// zero = -deriv[0] / deriv[1]
		return - (cp + cp - cur - cur) / (2.0 * (cur - cp - cp + end));
	}

	private static void accumQuad(double bounds[], double t,
								  double curx, double cury,
								  double cpx0, double cpy0,
								  double endx, double endy)
	{
		double u = (1-t);
		double x = curx*u*u + 2.0*cpx0*t*u + endx*t*t;
		double y = cury*u*u + 2.0*cpy0*t*u + endy*t*t;
		accum(bounds, x, y);
	}

	private static int findCubicZeros(double zeros[],
									  double cur, double cp0,
									  double cp1, double end)
	{
		// The polynomial form of the Cubic is:
		// eqn[0] = cur;
		// eqn[1] = (cp0 - cur) * 3.0;
		// eqn[2] = (cp1 - cp0 - cp0 + cur) * 3.0;
		// eqn[3] = end + (cp0 - cp1) * 3.0 - cur;
		// Since we want the derivative, we can calculate it in one step:
		zeros[0] = (cp0 - cur) * 3.0;
		zeros[1] = (cp1 - cp0 - cp0 + cur) * 6.0;
		zeros[2] = (end + (cp0 - cp1) * 3.0 - cur) * 3.0;
		int num = QuadCurve2D.solveQuadratic(zeros);
		int ret = 0;
		for (int i = 0; i < num; i++) {
			double t = zeros[i];
			if (t > 0 && t < 1) {
				zeros[ret] = t;
				ret++;
			}
		}
		return ret;
	}

	private static void accumCubic(double bounds[], double t,
								   double curx, double cury,
								   double cpx0, double cpy0,
								   double cpx1, double cpy1,
								   double endx, double endy)
	{
		double u = (1-t);
		double x = curx*u*u*u + 3.0*cpx0*t*u*u + 3.0*cpx1*t*t*u + endx*t*t*t;
		double y = cury*u*u*u + 3.0*cpy0*t*u*u + 3.0*cpy1*t*t*u + endy*t*t*t;
		accum(bounds, x, y);
	}
}