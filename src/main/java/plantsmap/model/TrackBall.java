/*
 *   Nuclear Plants Map
 *   http://casmi.github.com/
 *   Copyright (C) 2011, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package plantsmap.model;

/**
 * class for Trackball manipulation
 * 
 * @author Y. Ban
 *
 */
public class TrackBall {
	
	private double cx, cy;
	private double sx, sy;
	
	private double cq[] = {1.0, 0.0, 0.0, 0.0};
    private double tq[] = new double[4];
    private double rt[] = new double[16];
    
	public TrackBall(double width, double height) {
		this.sx = 1.0 / width;
		this.sy = 1.0 / height;
		this.rt[ 0] = this.rt[ 5] = this.rt[ 10] = this.rt[ 15] = 1.0;
	}
	
	public double[] getMatrix() {
		return rt;
	}
	
	public void update(double x, double y, double px, double py, 
					boolean isDragged, boolean isPressed, boolean isReleased) {

		if (isDragged) {
			double dx = (x - cx) * sx;
			double dy = - (y - cy) * sy;
			double a = Math.sqrt(dx * dx + dy * dy);
			if (a > 0.0) {
				double ar = a * 2.0 * Math.PI * 0.5;
				double as = Math.sin(ar) / a;
				double dq[] = { Math.cos(ar), dy * as, dx * as, 0.0 };
				qmul(tq, dq, cq);
				qrot(rt, tq);
			}
		}

		if (isPressed) {
			cx = x;
			cy = y;
		}

		if (isReleased) {
			cq[0] = tq[0];
			cq[1] = tq[1];
			cq[2] = tq[2];
			cq[3] = tq[3];
		}
	}

	private static void qmul(double r[], double p[], double q[]) {
		r[0] = p[0] * q[0] - p[1] * q[1] - p[2] * q[2] - p[3] * q[3];
		r[1] = p[0] * q[1] + p[1] * q[0] + p[2] * q[3] - p[3] * q[2];
		r[2] = p[0] * q[2] - p[1] * q[3] + p[2] * q[0] + p[3] * q[1];
		r[3] = p[0] * q[3] + p[1] * q[2] - p[2] * q[1] + p[3] * q[0];
	}

	private static void qrot(double r[], double q[]) {
		double x2 = q[1] * q[1] * 2.0;
		double y2 = q[2] * q[2] * 2.0;
		double z2 = q[3] * q[3] * 2.0;
		double xy = q[1] * q[2] * 2.0;
		double yz = q[2] * q[3] * 2.0;
		double zx = q[3] * q[1] * 2.0;
		double xw = q[1] * q[0] * 2.0;
		double yw = q[2] * q[0] * 2.0;
		double zw = q[3] * q[0] * 2.0;

		r[0] = 1.0 - y2 - z2;
		r[1] = xy + zw;
		r[2] = zx - yw;
		r[4] = xy - zw;
		r[5] = 1.0 - z2 - x2;
		r[6] = yz + xw;
		r[8] = zx + yw;
		r[9] = yz - xw;
		r[10] = 1.0 - x2 - y2;
		r[3] = r[7] = r[11] = r[12] = r[13] = r[14] = 0.0;
		r[15] = 1.0;
	}
}
