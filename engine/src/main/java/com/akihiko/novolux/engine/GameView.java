package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.utils.OBJModelLoader;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.rendering.FrameBuffer;
import com.akihiko.novolux.engine.core.rendering.Renderer;
import com.akihiko.novolux.engine.core.rendering.Texture;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class GameView extends Canvas {


    //private BufferedImage renderedFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    private final Queue<Consumer<Renderer>> renderQueue = new LinkedList<>();
    private final Queue<Consumer<Graphics>> guiQueue = new LinkedList<>();
    //private final float aspectRatio;

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
    public void init(){
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

    public void addRenderCall(Consumer<Renderer> renderCall){
        this.renderQueue.add(renderCall);
    }
    public void addGUICall(Consumer<Graphics> guiCall){
        this.guiQueue.add(guiCall);
    }

    public void render()  {
        this.swapFrameBuffer.fill((byte)0x00);
        this.renderer.clearZBuffer();
//        for(int j = 100; j < 200; j++){
//            renderer.renderBuffer(j, 300-j, 300+j);
//        }
//        Matrix4x4 transform = Matrix4x4.PERSPECTIVE(70.0f, (float) getWidth()/getHeight(), 0.1f, 1000f).multiply(Matrix4x4.TRANSLATION(0.0f, 0.0f, 5f - debugcounter).multiply(Matrix4x4.EULER_ROTATION(debugcounter, debugcounter, 0)));
//        swapFrameBuffer.FillTriangle(a.transform(transform), b.transform(transform), c.transform(transform), debugtexture);
//        renderer.clearZBuffer();
//        try {
//            renderer.drawMesh(
//                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("primitives/monkey.obj").toURI()))),
//                    new TransformComponent(new Vector3(0, 0, 5f)).getTransformationMatrix(),
//                    debugtexture);
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

        for (Consumer<Renderer> renderCall : renderQueue) {
            renderCall.accept(this.renderer);
        }


        /*CameraComponent cameraComponent = new CameraComponent(true, CameraComponent.ProjectionType.PERSPECTIVE, this.aspectRatio, 90, 0.1f, 1000f);
        Vector3[][] tris = new Vector3[][]{
                new Vector3[]{ new Vector3(0,0,0), new Vector3(0,1,0), new Vector3(1,1,0) },
                new Vector3[]{ new Vector3(0,0,0), new Vector3(1,1,0), new Vector3(1,0,0) },

                new Vector3[]{ new Vector3(1,0,0), new Vector3(1,1,0), new Vector3(1,1,1) },
                new Vector3[]{ new Vector3(1,0,0), new Vector3(1,1,1), new Vector3(1,0,1) },

                new Vector3[]{ new Vector3(1,0,1), new Vector3(1,1,1), new Vector3(0,1,1) },
                new Vector3[]{ new Vector3(1,0,1), new Vector3(0,1,1), new Vector3(0,0,1) },

                new Vector3[]{ new Vector3(0,0,1), new Vector3(0,1,1), new Vector3(0,1,0) },
                new Vector3[]{ new Vector3(0,0,1), new Vector3(0,1,0), new Vector3(0,0,0) },
        };
        for(Vector3[] tri : tris){
            Vector3 translatedA = tri[0].setZ(tri[0].getZ() + 3).setX(tri[0].getX() - 3);
            Vector3 translatedB = tri[1].setZ(tri[1].getZ() + 3).setX(tri[1].getX() - 3);
            Vector3 translatedC = tri[2].setZ(tri[2].getZ() + 3).setX(tri[2].getX() - 3);

            Vector3 projectedA = cameraComponent.project(translatedA);
            Vector3 projectedB = cameraComponent.project(translatedB);
            Vector3 projectedC = cameraComponent.project(translatedC);

            projectedA.setX((projectedA.getX() + 1) * 0.5f * getWidth());
            projectedA.setY((projectedA.getY() + 1) * 0.5f * getHeight());
            projectedB.setX((projectedB.getX() + 1) * 0.5f * getWidth());
            projectedB.setY((projectedB.getY() + 1) * 0.5f * getHeight());
            projectedC.setX((projectedC.getX() + 1) * 0.5f * getWidth());
            projectedC.setY((projectedC.getY() + 1) * 0.5f * getHeight());

            g.setColor(Color.CYAN);
            Polygon p = new Polygon();
            p.addPoint((int)projectedA.getX(), (int)projectedA.getY());
            p.addPoint((int)projectedB.getX(), (int)projectedB.getY());
            p.addPoint((int)projectedC.getX(), (int)projectedC.getY());
            g.drawPolygon(p);
            g.setColor(Color.RED);
            g.drawString("FPS: 30???", 0, g.getFontMetrics().getHeight());
//            GraphicsHelper.drawTriangle(g, (int)projectedA.getX(), (int)projectedA.getY(), (int)projectedB.getX(), (int)projectedB.getY(), (int)projectedC.getX(), (int)projectedC.getY());
        }

        */

        // Render to BufferedImage:
        this.swapFrameBuffer.renderToBuffer(renderBuffer);

        // Derive "Graphics2D" for GUI rendering:
        Graphics2D guiRenderer = renderImage.createGraphics();
        for(Consumer<Graphics> guiCall : guiQueue){
            guiCall.accept(guiRenderer);
        }
        // Dispose of derived "Graphics2D"
        guiRenderer.dispose();

        // Draw our final frame:
        this.graphics.drawImage(renderImage, 0, 0, swapFrameBuffer.getWidth(), swapFrameBuffer.getHeight(), null);

        // Display new buffer:
        this.bufferStrategy.show();
    }

    private void clear(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
