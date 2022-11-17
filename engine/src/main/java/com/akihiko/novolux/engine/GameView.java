package com.akihiko.novolux.engine;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class GameView extends Canvas {


    //private BufferedImage renderedFrame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

    public GameView() {

    }

    public void render(){
        BufferStrategy bufStrat = getBufferStrategy();
        if(bufStrat == null){
            // If we do not have a bufferStrategy, create a triple-buffering system.
            // Inspired by OpenGLs double-buffering system: https://learnopengl.com/Advanced-OpenGL/Framebuffers
            createBufferStrategy(3);
            // Skip the tick.
            return;
        }

        Graphics g = bufStrat.getDrawGraphics();
        // Do all the drawing
        clear(g);


        g.dispose();

        // Swap buffer:
        bufStrat.show();
    }

    private void clear(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
