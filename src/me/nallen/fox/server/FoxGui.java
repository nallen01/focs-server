package me.nallen.fox.server;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FoxGui extends JFrame implements KeyListener, DataListener {
	private static final long serialVersionUID = 1L;
	private static final Color chromaColor = new Color(0, 255, 0);
	private static final Color redColor = new Color(255, 0, 0);
	private static final Color blueColor = new Color(0, 0, 255);
	private static final Color whiteColor = new Color(255, 255, 255);
	private static final Color grayColor = new Color(63, 63, 63);
	private static final Color blackColor = new Color(0, 0, 0);
	
	private static final double SCORE_BOX_WIDTH = 0.08;
	private static final double SCORE_BOX_HEIGHT = 0.07;
	private static final double SCORE_BOX_CENTER_GAP = 0.1125;
	private static final double SCORE_BOX_BOTTOM_OFFSET = 0.115;
	private static final double SCORE_BOX_X_CURVE = 0.4;
	private static final double SCORE_BOX_Y_CURVE = 0.7;
	private static final double SCORE_BOX_FONT = 0.3;
	
	private static final double TOP_BOX_WIDTH = 0.15;
	private static final double TOP_BOX_HEIGHT = 0.1;
	private static final double TOP_BOX_TOP_OFFSET = 0.047;
	private static final double TOP_BOX_SIDE_OFFSET = 0.1516;
	private static final double TOP_BOX_X_CURVE = 0.10;
	private static final double TOP_BOX_Y_CURVE = 0.20;
	private static final double TOP_BOX_SIDE_GAP = 0.0625;
	private static final double TOP_BOX_BOTTOM_GAP = 0.028;
	
	private static final double MAIN_BOX_WIDTH = 0.7;
	private static final double MAIN_BOX_HEIGHT = 0.55;
	private static final double MAIN_BOX_TOP_OFFSET = 0.2;
	private static final double MAIN_BOX_SIDE_OFFSET = 0.15;
	
	private static final int GRAPH_MAX_Y_VALUE = 400;
	private static final double GRAPH_LINE_WIDTH = 0.006;
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	
	private GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	private JPanel redScorePanel;
	private JLabel redScore;

	private JPanel blueScorePanel;
	private JLabel blueScore;
	
	private JPanel historyPanel;
	private JPanel redHistoryPanel;
	private JPanel blueHistoryPanel;
	
	public FoxGui() {
		super("The Fox");
		
		getContentPane().setBackground(chromaColor);
		getContentPane().setPreferredSize(new Dimension(1280, 720));
		
	    setVisible(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    setLayout(null);
	    
	    redScorePanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           int width = getWidth();
	           int height = getHeight();
	           Graphics2D graphics = (Graphics2D) g;
	           graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	           graphics.setColor(whiteColor);

	           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, SCORE_BOX_X_CURVE * width, SCORE_BOX_Y_CURVE * height));
	           graphics.fill(new Rectangle2D.Double(SCORE_BOX_X_CURVE*width,0,width-SCORE_BOX_X_CURVE*width,height));
	           graphics.fill(new Rectangle2D.Double(0,SCORE_BOX_Y_CURVE*height,width,height-SCORE_BOX_Y_CURVE*height));
	        }
	    };
	    redScorePanel.setOpaque(false);
	    redScorePanel.setLayout(null);
	    
	    redScore = new JLabel("0");
	    redScore.setHorizontalAlignment(SwingConstants.CENTER);
	    redScore.setForeground(redColor);
	    
	    redScorePanel.add(redScore);
	    add(redScorePanel);
	    
	    blueScorePanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           int width = getWidth();
	           int height = getHeight();
	           Graphics2D graphics = (Graphics2D) g;
	           graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	           graphics.setColor(whiteColor);

	           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, SCORE_BOX_X_CURVE * width, SCORE_BOX_Y_CURVE * height));
	           graphics.fill(new Rectangle2D.Double(0,0,width-SCORE_BOX_X_CURVE*width,height));
	           graphics.fill(new Rectangle2D.Double(0,SCORE_BOX_Y_CURVE*height,width,height-SCORE_BOX_Y_CURVE*height));
	        }
	    };
	    blueScorePanel.setOpaque(false);
	    blueScorePanel.setLayout(null);
	    
	    blueScore = new JLabel("0");
	    blueScore.setHorizontalAlignment(SwingConstants.CENTER);
	    blueScore.setForeground(blueColor);
	    
	    blueScorePanel.add(blueScore);
	    add(blueScorePanel);
	    
	    historyPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           
	           if(!FoxServer.foxData.getLargeHistory()) {
	        	   int width = getWidth();
		           int height = getHeight();
		           Graphics2D graphics = (Graphics2D) g;
		           graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		           graphics.setColor(grayColor);

		           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, TOP_BOX_X_CURVE * width, TOP_BOX_Y_CURVE * height));
		           graphics.fill(new Rectangle2D.Double(0,0,width-TOP_BOX_X_CURVE*width,height));
		           
		           graphics.setColor(blackColor);

		           graphics.fill(new RoundRectangle2D.Double(0, 0, width - TOP_BOX_SIDE_GAP * width, height - TOP_BOX_BOTTOM_GAP * height, TOP_BOX_X_CURVE * width, TOP_BOX_Y_CURVE * height));
		           graphics.fill(new Rectangle2D.Double(0, 0, width - TOP_BOX_SIDE_GAP * width, height - TOP_BOX_Y_CURVE * height - TOP_BOX_BOTTOM_GAP * height));
		           graphics.fill(new Rectangle2D.Double(0, 0, width - TOP_BOX_X_CURVE * width - TOP_BOX_SIDE_GAP * width, height - TOP_BOX_BOTTOM_GAP * height));
	           }
	        }
	    };
	    historyPanel.setOpaque(false);
	    historyPanel.setLayout(null);
	    
	    redHistoryPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
	    	protected void paintComponent(Graphics g) {
	    		super.paintComponent(g);
	    		
	    		paintGraph(this, (Graphics2D) g, redColor, FoxServer.foxData.getRedScoreHistory());
	    	}
	    };
	    redHistoryPanel.setOpaque(false);
	    historyPanel.add(redHistoryPanel);
	    
	    blueHistoryPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
	    	protected void paintComponent(Graphics g) {
	    		super.paintComponent(g);
	    		
	    		paintGraph(this, (Graphics2D) g, blueColor, FoxServer.foxData.getBlueScoreHistory());
	    	}
	    };
	    blueHistoryPanel.setOpaque(false);
	    historyPanel.add(blueHistoryPanel);
	    
	    add(historyPanel);
	    
		addKeyListener(this);
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {}

			public void componentMoved(ComponentEvent arg0) {}

			public void componentResized(ComponentEvent arg0) {
				updatePositions();
			}

			public void componentShown(ComponentEvent arg0) {}
		});
		
		
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			try {
				Class<?> c = Class.forName("com.apple.eawt.FullScreenUtilities");
				Method m = c.getMethod("setWindowCanFullScreen", Window.class, boolean.class);
				m.invoke(c, this, true);
			} catch (Exception e) { e.printStackTrace();}
		}
		
		
		pack();
	    updatePositions();
	    updateScores();
	    
	    FoxServer.foxData.addListener(this);
	}
	
	private void paintGraph(JPanel p, Graphics2D g, Color c, int[] points) {
		int width = p.getWidth();
		int height = p.getHeight() - 1;
		
		if(!FoxServer.foxData.getLargeHistory()) {
			width = (int) (width * (1 - TOP_BOX_SIDE_GAP));
			height = (int) (height * (1 - TOP_BOX_BOTTOM_GAP));
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(c);
		float line_width = (float) (GRAPH_LINE_WIDTH * height);
		if(line_width < 1)
			line_width = 1;
		g.setStroke(new BasicStroke(line_width));
		
		double pixels_per_y = ((double) height) / GRAPH_MAX_Y_VALUE;
		double pixels_per_x = ((double) width) / (FoxData.NUM_HISTORY_POINTS - 1);
		
		for(int i=1; i<points.length; i++) {
			if(points[i] >= 0) {
				int start_x = (int) (pixels_per_x * (i-1));
				int start_y = height - (int) (pixels_per_y * points[i-1]);
				int end_x = (int) (pixels_per_x * i);
				int end_y = height - (int) (pixels_per_y * points[i]);
				
				g.drawLine(start_x, start_y, end_x, end_y);
			}
		}
	}
	
	public void updateScores() {
		redScore.setText("" + FoxServer.foxData.getRedScore());
		blueScore.setText("" + FoxServer.foxData.getBlueScore());
	}
	
	public void updateGraphs() {
		redHistoryPanel.repaint();
		blueHistoryPanel.repaint();
	}

	public void update(UpdateType type) {
		if(type == UpdateType.TICK) {
			updateGraphs();
		}
		else if(type == UpdateType.SCORE) {
			updateScores();
		}
		else if(type == UpdateType.SETTING) {
			updatePositions();
		}
	}
	
	private void updatePositions() {
		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();
		
		int panel_width = (int) (SCORE_BOX_WIDTH * width);
		int panel_height =  (int) (SCORE_BOX_HEIGHT * height);
		int panel_y = height - (int) (SCORE_BOX_BOTTOM_OFFSET * height) - panel_height;
		int panel_x_offset = (int) ((SCORE_BOX_CENTER_GAP / 2) * width);
		
	    redScorePanel.setBounds((width / 2) - panel_x_offset - panel_width, panel_y, panel_width, panel_height);
	    redScore.setBounds(0, 0, redScorePanel.getWidth(), redScorePanel.getHeight());
	    redScore.setFont(new Font(redScore.getFont().getFontName(), Font.BOLD, (int) (SCORE_BOX_FONT*redScorePanel.getWidth())));

	    blueScorePanel.setBounds((width / 2) + panel_x_offset, panel_y, panel_width, panel_height);
	    blueScore.setBounds(0, 0, blueScorePanel.getWidth(), blueScorePanel.getHeight());
	    blueScore.setFont(new Font(blueScore.getFont().getFontName(), Font.BOLD, (int) (SCORE_BOX_FONT*blueScorePanel.getWidth())));
 
	    if(FoxServer.foxData.getShowHistory()) {
	    	if(FoxServer.foxData.getLargeHistory()) {
			    int middle_box_width = (int) (MAIN_BOX_WIDTH * width);
			    int middle_box_height = (int) (MAIN_BOX_HEIGHT * height);
			    int middle_box_x = (int) (MAIN_BOX_SIDE_OFFSET * width);
			    int middle_box_y = (int) (MAIN_BOX_TOP_OFFSET * height);
			    
			    historyPanel.setBounds(middle_box_x, middle_box_y, middle_box_width, middle_box_height);
	    	}
	    	else {
			    int top_box_width = (int) (TOP_BOX_WIDTH * width);
			    int top_box_height = (int) (TOP_BOX_HEIGHT * height);
			    int top_box_x = (int) (TOP_BOX_SIDE_OFFSET * width);
			    int top_box_y = (int) (TOP_BOX_TOP_OFFSET * height);
			    
			    historyPanel.setBounds(top_box_x, top_box_y, top_box_width, top_box_height);
	    	}
	    	
		    historyPanel.setVisible(true);
	    }
	    else {
		    historyPanel.setVisible(false);
	    }
	    
	    redHistoryPanel.setBounds(0, 0, historyPanel.getWidth(), historyPanel.getHeight());
	    blueHistoryPanel.setBounds(0, 0, historyPanel.getWidth(), historyPanel.getHeight());
	}
	
	public void toggleFullScreen() {
		if(isFullScreen) {
			dispose();
			
			setExtendedState(JFrame.NORMAL);
			setUndecorated(false);
			setSize(priorDimension);
			setLocation(priorLocation);
			
			isFullScreen = false;
			setVisible(true);
		}
		else {
			if (gd.isFullScreenSupported()) {
				priorDimension = getSize();
				priorLocation = getLocation();
				
				dispose();
				
				setBounds(0, 0, getToolkit().getScreenSize().width,
                        getToolkit().getScreenSize().height);
				setUndecorated(true);

				toFront();
	
		        //gd.setFullScreenWindow(this);
				
				isFullScreen = true;
				setVisible(true);
			}
		}
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
