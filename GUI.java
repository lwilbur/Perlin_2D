import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Scanner;

/**
 * Using Swing, creates a window to view the output of Perlin Noise. The
 * number of pixels per square in the Perlin grid, and the seed value for the
 * hash table used, are chosen by the user.
 */
public class GUI {
    public static void main(String[] args) {
        ///////////////////////////////////////////////////////////////
        // SETUP WINDOW, SELECT PERLIN GRID SIZE AND HASH TABLE SEED //
        //////////////////////////////////////////////////////////////
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int SCR_WIDTH   = screen.width;
        final int SCR_HEIGHT  = screen.height - 100;  // offset by 100 to accommodate home bar

        JFrame window = setupWindow(SCR_WIDTH, SCR_HEIGHT);
        BufferedImage image = new BufferedImage(SCR_WIDTH,
                                                SCR_HEIGHT,
                                                BufferedImage.TYPE_INT_RGB);

        // Getting command-line input
        Scanner s = new Scanner(System.in);
        final int PX_PER_GRID   = selectGridSize(s);       // Pixels per unit square in Perlin grid
        final boolean SHOW_GRID = selectIfGridVisible(s);  // Select grid visibility
        final long SEED         = selectSeed(s);           // Seed to shuffle the hash table
        s.close();

        ////////////////////////////////////////
        // CREATE IMAGE USING ON PERLIN NOISE //
        ////////////////////////////////////////
        Perlin2D.setSeed(SEED);
        for (int x = 0; x < SCR_WIDTH; x++) {
            for (int y = 0; y < SCR_HEIGHT; y++) {
                // Calculate where the current point falls in the grid of
                // PX_PER_GRID-wide squares
                double xInGrid = (double)x / PX_PER_GRID;
                double yInGrid = (double)y / PX_PER_GRID;

                // Get Perlin Noise value (leave as 0 if drawing grid at this (x,y))
                double val = 0;
                if (!SHOW_GRID || (x % PX_PER_GRID != 0 && y % PX_PER_GRID != 0))
                    val = Perlin2D.perlinCalc(xInGrid, yInGrid);

                // Use noise value to determine brightness of each px
                int val256 = (int)(255 * val);   // map range [0, 1] to [0, 255]
                Color color = new Color(val256, val256, val256);
                image.setRGB(x, y, color.getRGB());
            }
        }

        /////////////////////////////////////
        // PRESENT GENERATED IMAGE TO USER //
        /////////////////////////////////////
        JLabel imgLabel = new JLabel(new ImageIcon(image));
        window.add(imgLabel);
        window.setVisible(true);
    }


    /**
     * Create a simple, standard Swing window
     * @param width desired width of window
     * @param height desired height of window
     * @return created JFrame with preset traits
     */
    private static JFrame setupWindow(int width, int height) {
        JFrame window = new JFrame("Perlin");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(width, height));
        window.setResizable(false);

        return window;
    }


    /**
     * Prompts the user on the command line for a valid integer. The integer is
     * used as the number of pixels in each square of the Perlin grid.
     * @param s A Scanner pre-created to read off the command line.
     * @return The user's input long if valid, 1 if invalid.
     */
    private static int selectGridSize(Scanner s) {
        // Get input and test its validity
        System.out.print("Number of pixels per square of Perlin grid (20-1000 recommended): ");
        try {
            int input = Integer.parseInt(s.nextLine()); // Catch non-int inputs
            if (input <= 0)                             // Catch non-pos-int inputs
                throw(new NumberFormatException());
            return input;
        }
        // Return default if invalid
        catch (NumberFormatException e) {
            System.out.println("\tValid integer not entered, defaulting to 100.");
            return 100;
        }
    }


    /**
     * Prompts the user on the command line for 'y' or 'n' to choose whether
     * they want to see the grid lines from the Perlin grid.
     * @param s A Scanner pre-created to read off the command line.
     * @return The user's input long if valid, 1 if invalid.
     */
    private static boolean selectIfGridVisible(Scanner s) {
        // Get input and check what response it matches
        System.out.print("Show Perlin grid lines (y/n): ");
        String input = s.nextLine().toLowerCase();
        if (input.equals("y"))      // y -> show
            return true;
        else if (input.equals("n")) // n -> don't show
            return false;
        else                        // other -> don't show
            System.out.println("\tInput not recognized; defaulting to 'n'");
        return false;
    }


    /**
     * Prompts the user on the command line for a valid long to use as a seed.
     * The seed is later used to order the hash table used in Perlin Noise
     * generation.
     * @param s A Scanner pre-created to read off the command line.
     * @return The user's input long if valid, 1 if invalid.
     */
    private static long selectSeed(Scanner s) {
        // Get input and test its validity
        System.out.print("Long value for seed (leave blank for default): ");
        try {
            return Long.parseLong(s.nextLine());
        }
        // Return default if invalid
        catch (NumberFormatException e) {
            System.out.println("\tLong not entered; defaulting to 1");
            return 1;
        }
    }
}
