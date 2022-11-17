package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Window extends JFrame {

    final GameView gameView;

    public Window(String title, int width, int height) {
        // Creating JFrame itself.
        super();

        this.setTitle(title);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Logger.dumpCore();
            }
        });




        gameView = new GameView();
        gameView.setPreferredSize(new Dimension(width, height));

        this.getContentPane().add(gameView);

        // So that our window size resizes according to the gameView
        this.pack();



        // After calculating the size of the window, center it:
        this.setLocationRelativeTo(null);
    }

    public void display() {
        this.setVisible(true);
    }
}
