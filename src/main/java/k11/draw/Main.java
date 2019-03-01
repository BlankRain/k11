package k11.draw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws IOException {
        String pic = "pic.jpg";
        BufferedImage image = ImageIO.read(new File(pic));
        int h = image.getHeight();
        int w = image.getWidth();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pix = image.getRGB(i, j);
                Color c = new Color(pix);
                int r = c.getBlue();
                System.out.print(r > 126 ? '-' : ' ');
            }
            System.out.println();
        }
    }
}
