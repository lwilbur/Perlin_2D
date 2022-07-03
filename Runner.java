import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Runner {
    public static void main(String[] args) {
        boolean DEBUG = false;

        ////////////////////////////////////////////////
        // CALCULATE WINDOW SIZE AND PERLIN GRID SIZE //
        ////////////////////////////////////////////////
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int SCR_WIDTH  = screen.width;
        final int SCR_HEIGHT = screen.height;

        // calculate window size to be an exact number of squares of the grid
        final int PX_PER_GRID = 100;  // Pixels per square in Perlin grid

        int GRID_WIDTH  = SCR_WIDTH / PX_PER_GRID;
        int GRID_HEIGHT = (SCR_HEIGHT - 100) / PX_PER_GRID; // offset by 100 to accommodate home bar
        int WIN_WIDTH   = GRID_WIDTH  * PX_PER_GRID;
        int WIN_HEIGHT  = GRID_HEIGHT * PX_PER_GRID;

        if (DEBUG) System.out.println(String.format("SCR_WIDTH=%d, SCR_HEIGHT=%d\nWIN_WIDTH=%d, WIN_HEIGHT=%d", SCR_WIDTH, SCR_HEIGHT, WIN_WIDTH, WIN_HEIGHT));

        ///////////////////
        // CREATE WINDOW //
        ///////////////////
        JFrame window = setupWindow(SCR_WIDTH, WIN_HEIGHT);
        BufferedImage image = new BufferedImage(SCR_WIDTH, WIN_HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < SCR_WIDTH; x++) {  // If adding
            for (int y = 0; y < WIN_HEIGHT; y++) {
                double xInGrid = (double)x / PX_PER_GRID;
                double yInGrid = (double)y / PX_PER_GRID;
                double val = Perlin2D.perlinCalc(xInGrid, yInGrid);

                int val256 = (int)(255 * val);
                if (val256 == 0)
                    System.out.println("\tval=" + val + "\tval256=" + val256);

                Color color = new Color(val256, val256, val256);

                image.setRGB(x, y, color.getRGB());

                if (DEBUG) {
                    System.out.println(color.getRGB());
                }
            }
        }

        JLabel imgLabel = new JLabel(new ImageIcon(image));
        window.add(imgLabel);
        window.setVisible(true);
        System.out.println("DONE!");
    }

    /**
     * Create a simple, standard Swing window
     * @param width desired width of window
     * @param height desired height of window
     * @return simple functional JFrame
     */
    public static JFrame setupWindow(int width, int height) {
        JFrame window = new JFrame("Perlin");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(width, height));
        window.setResizable(false);

        return window;
    }
}
