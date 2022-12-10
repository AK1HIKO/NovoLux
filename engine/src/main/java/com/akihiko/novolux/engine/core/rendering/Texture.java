package com.akihiko.novolux.engine.core.rendering;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 29/11/22
 */
public class Texture extends FrameBuffer {
    public Texture(int width, int height) {
        super(width, height);
    }

    public static Texture fromImage(File textureFile) throws IOException {
        BufferedImage textureImage = ImageIO.read(textureFile);
        int width = textureImage.getWidth();
        int height = textureImage.getHeight();

        Texture result = new Texture(width, height);

        int size = width * height;
        int[] textureImageBuffer = new int[size];
        textureImage.getRGB(0, 0, width, height, textureImageBuffer, 0, width);

        for (int i = 0; i < size; i++) {
            int pixel = textureImageBuffer[i];

            result.setPixel(i, pixel);
        }
        return result;
    }

    public void copyTexel(int x, int y, FrameBuffer destination, int destX, int destY) {
        int desti = (destX + destY * destination.width) * 4;
        int srci = (x + y * super.width) * 4;

        destination.data[desti + 0] = super.getByte(srci + 0);

        destination.data[desti + 3] = super.getByte(srci + 3);
        destination.data[desti + 2] = super.getByte(srci + 2);
        destination.data[desti + 1] = super.getByte(srci + 1);
    }

    private void setPixel(int index, int colorData) {
        // Masking the required bytes:
        byte a = (byte) ((colorData >> 24) & 0xFF);
        byte r = (byte) ((colorData >> 16) & 0xFF);
        byte g = (byte) ((colorData >> 8) & 0xFF);
        byte b = (byte) ((colorData >> 0) & 0xFF);

        this.setPixel(index, a, r, g, b);
    }
}
