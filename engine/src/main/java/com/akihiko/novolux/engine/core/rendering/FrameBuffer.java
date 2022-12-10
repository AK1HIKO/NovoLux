package com.akihiko.novolux.engine.core.rendering;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

import java.util.Arrays;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public class FrameBuffer {

    protected final int width;
    protected final int height;
    protected final int size;
    protected final byte data[];

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.size = this.width * this.height;
        // 4 bytes per pixel: Alpha,Blue,Green,Red channels:
        this.data = new byte[this.size * 4];
    }

    public void fill(byte grayscale) {
        Arrays.fill(this.data, grayscale);
    }

    public void setPixel(int x, int y, byte a, byte r, byte g, byte b) {
        int i = (x + y * this.width) * 4;
        this.data[i + 0] = a;

        this.data[i + 3] = r;
        this.data[i + 2] = g;
        this.data[i + 1] = b;
    }

    public void setPixel(int index, byte a, byte r, byte g, byte b) {
        int i = index * 4;
        this.data[i + 0] = a;

        this.data[i + 3] = r;
        this.data[i + 2] = g;
        this.data[i + 1] = b;
    }

    public Vector4 getPixel(int x, int y) {
        int i = (x + y * this.width) * 4;
        return new Vector4(
                this.data[i + 0],
                this.data[i + 3],
                this.data[i + 2],
                this.data[i + 1]
        );
    }

    public byte getByte(int i) {
        return this.data[i];
    }

    public void renderToBuffer(byte[] destBuffer) {
        for (int i = 0; i < this.size; i++) {
            int destIndex = i * 3;
            int srcIndex = i * 4;
            // BufferedImage is BGR, so discard Alpha channel:
            destBuffer[destIndex + 0] = this.data[srcIndex + 1];
            destBuffer[destIndex + 1] = this.data[srcIndex + 2];
            destBuffer[destIndex + 2] = this.data[srcIndex + 3];
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }
}
