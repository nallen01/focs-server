package me.nallen.fox.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class FoxGui extends JFrame implements KeyListener {
	private static final long serialVersionUID = 1L;
	public static final Color chromaColor = new Color(0, 255, 0);
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	
	public FoxGui() {
		super("The Fox");
		
		getContentPane().setBackground(chromaColor);
		setSize(1280, 720);
		
	    setVisible(true);
	    
		addKeyListener(this);
	}
	
	public void toggleFullScreen() {
		if(isFullScreen) {
			dispose();
			
			setExtendedState(JFrame.NORMAL);
			setUndecorated(false);
			setSize(priorDimension);
			setLocation(priorLocation);
		}
		else {
			priorDimension = getSize();
			priorLocation = getLocation();
			
			dispose();
			
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setUndecorated(true);
		}
		
		isFullScreen = !isFullScreen;
		
		setVisible(true);
	}

	public void keyPressed(KeyEvent e) {
		if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
	        toggleFullScreen();
		}
		else if(isFullScreen && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	        toggleFullScreen();
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
}
