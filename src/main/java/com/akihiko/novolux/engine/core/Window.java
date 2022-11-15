package com.akihiko.novolux.engine.core;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Window extends JFrame {

    private final JPanel canvas;

    public Window(String title, int width, int height) {
        // Creating JFrame itself.
        super();

        canvas = new JPanel();

        // Force our canvas panel to take the maximum amount of space.
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(canvas, BorderLayout.CENTER);

        this.setTitle(title);

        this.setSize(width, height);
    }

    public void display() {
        this.setVisible(true);
    }

    public void close() {
        this.dispose();
    }
}
