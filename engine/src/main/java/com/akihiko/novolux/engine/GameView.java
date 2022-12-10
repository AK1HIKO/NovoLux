package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.core.rendering.FrameBuffer;
import com.akihiko.novolux.engine.core.rendering.Renderer;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class GameView extends Canvas {

    private final Queue<Consumer<Renderer>> renderQueue = new LinkedList<>();
    private final Queue<Consumer<Graphics2D>> guiQueue = new LinkedList<>();

    private FrameBuffer swapFrameBuffer;
    private byte[] renderBuffer;
    private BufferedImage renderImage;
    private BufferStrategy bufferStrategy;
    private Graphics graphics;

    private Renderer renderer;

    //    private final Renderer renderer;
    public GameView(int width, int height) {
        super();

        Dimension size = new Dimension(width, height);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setPreferredSize(size);
    }

    /**
     * Initializes our GameView, by creating necessary framebuffers, renders and so on.
     * Must be called after the view was packed. Otherwise, getBufferStrategy will yield null.
     */
    public void init() {
        int width = super.getWidth();
        int height = super.getHeight();
        this.swapFrameBuffer = new FrameBuffer(width, height);
        this.renderImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        this.renderBuffer = ((DataBufferByte) this.renderImage.getRaster().getDataBuffer()).getData();

        // Only 1 buffer, since we do manual double-buffering using the "swapFrameBuffer".
        super.createBufferStrategy(1);
        this.bufferStrategy = super.getBufferStrategy();

        // Since we only have 1 buffer, there is no need to dispose of graphics, hence can be initialized only once.
        this.graphics = bufferStrategy.getDrawGraphics();

        this.renderer = new Renderer(this.swapFrameBuffer);
    }

    public void addRenderCall(Consumer<Renderer> renderCall) {
        this.renderQueue.add(renderCall);
    }

    public void addGUICall(Consumer<Graphics2D> guiCall) {
        this.guiQueue.add(guiCall);
    }

    public void render() {
        this.swapFrameBuffer.fill((byte) 0x00);
        this.renderer.clearZBuffer();

        for (Consumer<Renderer> renderCall : renderQueue) {
            renderCall.accept(this.renderer);
        }

        // Render to BufferedImage:
        this.swapFrameBuffer.renderToBuffer(renderBuffer);

        // Derive "Graphics2D" for GUI rendering:
        Graphics2D guiRenderer = renderImage.createGraphics();
        for (Consumer<Graphics2D> guiCall : guiQueue) {
            guiCall.accept(guiRenderer);
        }
        // Dispose of derived "Graphics2D"
        guiRenderer.dispose();

        // Draw our final frame:
        this.graphics.drawImage(renderImage, 0, 0, swapFrameBuffer.getWidth(), swapFrameBuffer.getHeight(), null);

        // Display new buffer:
        this.bufferStrategy.show();
    }

    private void clear(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
