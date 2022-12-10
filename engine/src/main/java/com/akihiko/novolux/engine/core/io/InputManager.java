package com.akihiko.novolux.engine.core.io;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 10/12/22
 */
public class InputManager implements FocusListener, KeyListener, MouseListener, MouseMotionListener {

    // Leaving as enum, for later addition of "pressed"
//    public enum KeyState{
//        DOWN, UP
//    }

    // MouseButtons are negative integers
    private final Set<Integer> downKeys;

    private Vector2 mousePosition;

    @Override
    public void keyPressed(KeyEvent e) {
        downKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        downKeys.remove(e.getKeyCode());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        downKeys.remove(-e.getButton());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        downKeys.add(-e.getButton());
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        this.mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mousePosition.setX(e.getX());
        this.mousePosition.setY(e.getY());
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.downKeys.clear();
        this.mousePosition = Vector2.ZERO();
    }

    public InputManager(Component component) {
        this.mousePosition = Vector2.ZERO();
        this.downKeys = new HashSet<>();

        component.addFocusListener(this);
        component.addKeyListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }

    public boolean isKeyDown(int keycode) {
        return this.downKeys.contains(keycode);
    }


    // Redundant events:
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
