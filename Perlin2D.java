import java.util.*;

/**
 * Methods for 2D Perlin Noise, based on the processes described in:
 * mrl.cs.nyu.edu/~perlin/noise/,
 * https://adrianb.io/2014/08/09/perlinnoise.html,
 * https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/perlin-noise-part-2
 * https://www.youtube.com/watch?v=MJ3bvCkHJtE
 *
 * Designed to be easily portable to applications outside of this project.
 */
public class Perlin2D {
    private static boolean DEBUG = false;  // Set to true for debugging printouts

    // Default hash lookup table as defined by Ken Perlin.  This is a randomly arranged
    // array of all numbers from 0-255 inclusive, repeated twice.
    private static int[] permutation = { 151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180,151,
            160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,  8,
            99,37,240,21,10,23,190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,
            35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168,68,175,74,165,71,
            134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,105,  92,
            41,55,46,245,40,244,102,143,54,65,25,63,161, 1,216,80,73,209,76,132,187,208,
            89,18,169,200,196,135,130,116,188,159, 86,164,100,109,198,173,186,  3,64,52,
            217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,
            58,17,182,189,28,42,223,183,170,213,119,248,152,2,44,154,163,70,221,153,101,
            155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,
            218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,
            249,14,239,107,49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,
            127, 4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,
            61,156,180 };


    /**
     * Changes hash table from Ken Perlin's default to a shuffled form of it,
     * allowing for many different outputs
     * @param seed a long, which decides how the hash lookup table will be
     *             shuffled
     */
    public static void setSeed(long seed) {
        // convert hash table to list (allows for shuffling)
        ArrayList<Integer> permutationList = new ArrayList<Integer>();
        for (int i = 0; i < permutation.length; i++)
            permutationList.add(permutation[i]);

        Collections.shuffle(permutationList, new Random(seed));

        // overwrite hash table
        for (int i = 0; i < permutation.length; i++)
            permutation[i] = permutationList.get(i);
    }


    /**
     * Returns a Perlin noise value for a given (x, y) coordinate.
     * @param x x-coordinate to generate noise value for
     * @param y y-coordinate to generate noise value for
     * @return double value in range [0.0, 1.0] for that point
     */
    public static double perlinCalc(double x, double y) {
        ///////////////////////////////////////////////////////////////
        // CALCULATE DOT PRODUCT BETWEEN GRADIANT & DISTANCE VECTORS //
        ///////////////////////////////////////////////////////////////
        // Find the four corners of the unit square
        int[][] unitSquare = getSquareCoords(x, y);
        double[] dotProds = new double[4];  // stores each gradiant • distance val

        // For each corner of the unit square
        for (int i = 0; i < unitSquare.length; i++) {
            int[] corner = unitSquare[i];

            // Calculate distance vector and grad vector based on given point
            double[] distVec = {x - corner[0], y - corner[1]};
            int[] gradVec    = selectGradVector(corner[0], corner[1]);

            // Take dot product: distance vector • gradient vector
            dotProds[i] = distVec[0] * gradVec[0] + distVec[1] * gradVec[1];

            if (DEBUG) {
                System.out.println(String.format("\tcorner=(%d, %d)",
                        corner[0], corner[1]));
                System.out.println(String.format("\t\tgradVec={%d, %d}",
                        gradVec[0], gradVec[1]));
                System.out.println("\t\tdotProds[i]=" + dotProds[i]);
            }

        }
        ///////////////////////////////////////////////////////////
        // BILINEARLY INTERPOLATE 4 DOT PRODUCTS FOR FINAL VALUE //
        ///////////////////////////////////////////////////////////
        // Fade x and y towards integral vals to improve naturalness of the noise
        double u = fade(x - (int)x);
        double v = fade(y - (int)y);

        // Bilinear interpolation to get the value for the point (using faded vals)
        double top  = linInt(u, dotProds[0], dotProds[1]); // top lt and top rt corners
        double bot  = linInt(u, dotProds[2], dotProds[3]); // bot lt and bot rt corners
        double vert = linInt(v, top, bot);

        if (DEBUG)
            System.out.println("\txFrac="+u+"\n\ttop="+top+"\n\tbot="+bot+"\n\tyFrac="+v+"\n\tvert="+vert);

        return (vert + 1) / 2; // Shift from output range of [-1, 1] to [0, 1]
    }


    /**
     * Determine the coordinates of the 4 corners of a given point's unit square
     * @param x x-coordinate of point to find unit square of
     * @param y y-coordinate of point to find unit square of
     * @return list of coordinate pairs in the format {{x0, y0}, {x1, y0}, {x0, y1}, {x1, y1}}
     *         (that is, {{top lt}, {top rt}, {bot lt}, {bot rt}})
     */
    private static int[][] getSquareCoords(double x, double y) {
        int x0, x1, y0, y1;  // (x0,y0) is up-left corner, (x1,y1) is bot-right
        x0 = (int)x;
        y0 = (int)y;
        x1 = x0 + 1;
        y1 = y0 + 1;

        int[][] coords = { {x0, y0}, {x1, y0}, {x0, y1}, {x1, y1} };
        return coords;
    }


    /**
     * For any given (x, y) coordinate, pseudorandomly select a gradient vector
     * using the hash method.
     * @param x integer x-coordinate of grid to generate a gradient vector for
     * @param y integer y-coordinate of grid to generate a gradient vector for
     * @return The gradient vector for that point, in the format {x, y}, where
     *         the vector is measured with its tail at (0, 0) and its head at
     *         (x, y).
     */
    public static int[] selectGradVector(int x, int y) {
        final int[][] validGradientVecs = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        return validGradientVecs[hash(x, y) & 3]; // same as % 4, but faster
    }


    /***
     * Hashing function based on the function given in
     * https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/perlin-noise-part-2,
     * which is based on Ken Perlin's original.
     * @param x x-coordinate of point to generate hash for
     * @param y y-coordinate of point to generate hash for
     * @return value in range [0, 255] from hash table
     */
    private static int hash(int x, int y) {
        x = x & 255;  // same as % 256, but faster
        y = y & 255;
        return permutation[permutation[x] + y];
    }


    /**
     * Fade function to smooth out each coordinate towards integral values,
     * making it look more natural and appealing. Function originally defined
     * by Ken Perlin.
     * @param val coordinate to be smoothed, represented in the range [0.0, 1.0]
     *            as its location in its unit square (e.g. .25 if you're fading
     *            a point which is 25% of the way across its unit square)
     * @return smoothed value for the coordinate at that point in range [0.0, 1.0]
     */
    private static double fade(double val) {
        return val * val * val * (val * (val * 6 - 15) + 10);
    }


    /**
     * Perform linear interpretation between points a and b, given that a and b
     * are points on a unit square's border, and the target point is frac % of
     * the way across the square from point a to b.
     * @param frac what % of the way across the square the target point is,
     *             represented with a double in the range [0.0, 1.0]
     * @param a a point on a unit square, < b
     * @param b a point on a unit square, > a
     * @return linearly interpolated value between a and b
     */
    private static double linInt(double frac, double a, double b) {
        return a + (frac * (b - a));
    }
}