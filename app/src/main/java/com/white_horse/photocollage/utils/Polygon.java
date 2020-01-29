package com.white_horse.photocollage.utils;


import com.white_horse.photocollage.models.Point;

import java.util.List;

public class Polygon {

    // Polygon coodinates.
    private final float[] polyY, polyX;

    // Number of sides in the polygon.
    private final int polySides;

    /**
     * Default constructor.
     * @param px Polygon y coods.
     * @param py Polygon x coods.
     * @param ps Polygon sides count.
     */
    public Polygon(final float[] px, final float[] py, final int ps ) {
        polyX = px;
        polyY = py;
        polySides = ps;
    }

    public Polygon(List<Point> points){
        polySides = points.size();
        polyY = new float[polySides];
        polyX = new float[polySides];

        for(int i = 0; i < polySides; i++) {
            polyX[i] = points.get(i).getRawX();
            polyY[i] = points.get(i).getRawY();
        }

        LogTrace.Companion.d(polyX);
        LogTrace.Companion.d(polyY);
    }

    /**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean contains( final float x, final float y ) {
        boolean oddTransitions = false;
        for( int i = 0, j = polySides -1; i < polySides; j = i++ ) {
            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) ) {
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x ) {
                    oddTransitions = !oddTransitions;
                }
            }
        }
        return oddTransitions;
    }

    public double area() {
        // Initialze area
        double area = 0.0;

        int n = polySides;
        // Calculate value of shoelace formula
        int j = n - 1;
        for (int i = 0; i < n; i++)
        {
            area += (polyX[j] + polyX[i]) * (polyY[j] - polyY[i]);
            // j is previous vertex to i
            j = i;
        }

        // Return absolute value
        return Math.abs(area / 2.0);
    }
}
