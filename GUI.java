import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GUI {
    public static void main(String[] args) {
        ///////////////////////////////////////////
        // SETUP WINDOW, SELECT PERLIN GRID SIZE //
        ///////////////////////////////////////////
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int SCR_WIDTH  = screen.width;
        final int SCR_HEIGHT = screen.height - 100;  // offset by 100 to accommodate home bar
        final int PX_PER_GRID = 100;    // Pixels per square in Perlin grid

        JFrame window = setupWindow(SCR_WIDTH, SCR_HEIGHT);
        BufferedImage image = new BufferedImage(SCR_WIDTH,
                                                SCR_HEIGHT,
                                                BufferedImage.TYPE_INT_RGB);

        ////////////////////////////////////////
        // CREATE IMAGE BASED ON PERLIN NOISE //
        ////////////////////////////////////////
        for (int x = 0; x < SCR_WIDTH; x++) {
            for (int y = 0; y < SCR_HEIGHT; y++) {
                // Calculate where the current point falls in the grid of
                // PX_PER_GRID-wide squares
                double xInGrid = (double)x / PX_PER_GRID;
                double yInGrid = (double)y / PX_PER_GRID;

                // Get Perlin Noise value, use it to pick brightness of each px
                double val = Perlin2D.perlinCalc(xInGrid, yInGrid);
                int val256 = (int)(255 * val);   // map [0, 1] to [0, 255]
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
    public static JFrame setupWindow(int width, int height) {
        JFrame window = new JFrame("Perlin");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(width, height));
        window.setResizable(false);

        return window;
    }
}
