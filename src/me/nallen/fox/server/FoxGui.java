package me.nallen.fox.server;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class FoxGui extends JFrame implements KeyListener, DataListener {
	private static final long serialVersionUID = 1L;
	private static final Color chromaColor = new Color(255, 0, 255);
	private static final Color redColor = new Color(218, 38, 46);
	private static final Color blueColor = new Color(0, 118, 190);
	private static final Color whiteColor = new Color(255, 255, 255);
	private static final Color grayColor = new Color(105, 105, 105); 
	
	private static final double SCORE_BOX_WIDTH = 186.0 / 1920;
	private static final double SCORE_BOX_HEIGHT = 105.0 / 1080;
	private static final double RED_SCORE_BOX_X = 226.0 / 1920;
	private static final double BLUE_SCORE_BOX_X = 1508.0 / 1920;
	private static final double SCORE_BOX_BOTTOM_OFFSET = 0.0 / 1080;
	private static final double SCORE_BOX_X_CURVE = 0.15;
	private static final double SCORE_BOX_Y_CURVE = 0.2;
	private static final double SCORE_BOX_FONT = 0.3;
	
	private static final double SCORE_BAR_X = 412.0 / 1920;
	private static final double SCORE_BAR_Y = 1060.0 / 1080;
	private static final double SCORE_BAR_WIDTH = 1096.0 / 1920;
	private static final double SCORE_BAR_HEIGHT = 20.0 / 1080;
	
	private static final double TOP_BOX_WIDTH = 226.0 / 1920;
	private static final double TOP_BOX_HEIGHT = 138.0 / 1080;
	private static final double TOP_BOX_TOP_OFFSET = 37.0 / 1080;
	private static final double TOP_BOX_SIDE_OFFSET = 0.0 / 1920;
	private static final double TOP_BOX_X_CURVE = 0.10;
	private static final double TOP_BOX_Y_CURVE = 0.20;
	
	private static final double MAIN_BOX_WIDTH = 186.0 / 1920;
	private static final double MAIN_BOX_HEIGHT = 714.0 / 1080;
	private static final double MAIN_BOX_X = 30.0 / 1920;
	private static final double MAIN_BOX_Y = 182.0 / 1080;
	private static final double MAIN_BOX_X_CURVE = 0.15;
	private static final double MAIN_BOX_Y_CURVE = 0.03;
	private static final int MAIN_BOX_DIVIDER_SECONDS = 60;
	private static final double MAIN_BOX_DIVIDER_X = 0.05;
	private static final double MAIN_BOX_DIVIDER_WIDTH = 0.9;
	
	private static final int GRAPH_MAX_Y_VALUE = 80;
	private static final double GRAPH_LINE_WIDTH = 0.006;
	private static final double GRAPH_VERTICAL_LINE_WIDTH = 0.02;
	
	public boolean isFullScreen = false;
	public Dimension priorDimension = null;
	public Point priorLocation = null;
	
	private GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	
	private JPanel redScorePanel;
	private JLabel redScore;
	private JPanel redScoreBar;

	private JPanel blueScorePanel;
	private JLabel blueScore;
	private JPanel blueScoreBar;
	
	private JPanel historyPanel;
	private JPanel graphPanel;
	
	public FoxGui() {
		super("The Fox");
		
		setContentPane(new JPanel() {
			private static final long serialVersionUID = 1L;
			private Image img;
			
			public void paintComponent(Graphics g) {
				if(img == null) {
					try {
						img = ImageIO.read(new File("/Users/nathan/Google Drive/Robotics/AudOverlay.png"));
					}
					catch(IOException ex) {}
				}
				g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			}
		});
		
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

	           graphics.setColor(redColor);

	           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, SCORE_BOX_X_CURVE * width, SCORE_BOX_Y_CURVE * height));
	           graphics.fill(new Rectangle2D.Double(0,SCORE_BOX_Y_CURVE*height,width,height-SCORE_BOX_Y_CURVE*height));
	        }
	    };
	    redScorePanel.setOpaque(false);
	    redScorePanel.setLayout(null);
	    
	    redScore = new JLabel("0");
	    redScore.setHorizontalAlignment(SwingConstants.CENTER);
	    redScore.setForeground(whiteColor);
	    // Open Sans should be pretty close, or Roboto?
	    //redScore.setFont(new Font("Open Sans", Font.PLAIN, 12));
	    
	    redScorePanel.add(redScore);
	    add(redScorePanel);
	    
	    redScoreBar = new JPanel();
	    redScoreBar.setBackground(redColor);
	    add(redScoreBar);
	    
	    blueScorePanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	           super.paintComponent(g);
	           int width = getWidth();
	           int height = getHeight();
	           Graphics2D graphics = (Graphics2D) g;
	           graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	           graphics.setColor(blueColor);

	           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, SCORE_BOX_X_CURVE * width, SCORE_BOX_Y_CURVE * height));
	           graphics.fill(new Rectangle2D.Double(0,SCORE_BOX_Y_CURVE*height,width,height-SCORE_BOX_Y_CURVE*height));
	        }
	    };
	    blueScorePanel.setOpaque(false);
	    blueScorePanel.setLayout(null);
	    
	    blueScore = new JLabel("0");
	    blueScore.setHorizontalAlignment(SwingConstants.CENTER);
	    blueScore.setForeground(whiteColor);
	    // Open Sans should be pretty close, or Roboto?
	    //blueScore.setFont(new Font("Open Sans", Font.PLAIN, 12));
	    
	    blueScorePanel.add(blueScore);
	    add(blueScorePanel);
	    
	    blueScoreBar = new JPanel();
	    blueScoreBar.setBackground(blueColor);
	    add(blueScoreBar);
	    
	    historyPanel = new JPanel() {
	    	private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	           
        	    int width = getWidth();
	            int height = getHeight();

	            Graphics2D graphics = (Graphics2D) g;
	            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	           
	            graphics.setColor(whiteColor);
	           
	            if(FoxServer.foxData.getLargeHistory()) {
				    graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, MAIN_BOX_X_CURVE * width, MAIN_BOX_Y_CURVE * height));
				   
				    graphics.setColor(grayColor);
					float line_width = (float) (GRAPH_LINE_WIDTH * width);
					if(line_width < 1)
						line_width = 1;
					graphics.setStroke(new BasicStroke(line_width));
					
					double pixels_per_sec = ((double) height) / FoxData.HISTORY_SECONDS;
					int start_x = (int) (MAIN_BOX_DIVIDER_X * width);
					int end_x = (int) ((MAIN_BOX_DIVIDER_X + MAIN_BOX_DIVIDER_WIDTH) * width);
	
					for(int i=1; i<FoxData.HISTORY_SECONDS / MAIN_BOX_DIVIDER_SECONDS; i++) {
						int y = (int) (pixels_per_sec * i * MAIN_BOX_DIVIDER_SECONDS);
						graphics.drawLine(start_x, y, end_x, y);
					}
	           }
	           else {
		           graphics.fill(new RoundRectangle2D.Double(0, 0, width, height, TOP_BOX_X_CURVE * width, TOP_BOX_Y_CURVE * height));
		           graphics.fill(new Rectangle2D.Double(0, 0, width - TOP_BOX_X_CURVE * width, height));
	           }
	        }
	    };
	    historyPanel.setOpaque(false);
	    historyPanel.setLayout(null);
	    
	    graphPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
	    	protected void paintComponent(Graphics g) {
	    		super.paintComponent(g);

				if(FoxServer.foxData.getLargeHistory()) {
					paintGraphVertical(this, (Graphics2D) g, FoxServer.foxData.getRedScoreHistory(), FoxServer.foxData.getBlueScoreHistory());
				}
				else {
					paintGraph(this, (Graphics2D) g, redColor, FoxServer.foxData.getRedScoreHistory());
					paintGraph(this, (Graphics2D) g, blueColor, FoxServer.foxData.getBlueScoreHistory());
				}
			}
	    };
	    graphPanel.setOpaque(false);
	    historyPanel.add(graphPanel);
	    
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
	
	private void paintGraphVertical(JPanel p, Graphics2D g, int[] redPoints, int[] bluePoints) {
		int width = p.getWidth();
		int height = p.getHeight() - 1;
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		float line_width = (float) (GRAPH_VERTICAL_LINE_WIDTH * width);
		if(line_width < 1)
			line_width = 1;
		g.setStroke(new BasicStroke(line_width));
		
		double pixels_per_y = ((double) height) / (FoxData.NUM_HISTORY_POINTS - 1);
		
		int points = redPoints.length < bluePoints.length ? redPoints.length : bluePoints.length;
		
		double prevFrac = 0;
		if(points > 0) {
			if((redPoints[0] + bluePoints[0]) > 0) {
				prevFrac = ((double) bluePoints[0]) / (redPoints[0] + bluePoints[0]);
			}
		}
		
		int validPoints = points;
		for(int i=0; i<points; i++) {
			if(redPoints[i] < 0 || bluePoints[i] < 0) {
				validPoints = i;
				
				break;
			}
		}
		
		int yOffset = points - validPoints;
		
		for(int i=1; i<validPoints; i++) {
			if(redPoints[i] >= 0 && bluePoints[i] >= 0) {
				double fraction = 0.5;
				if((redPoints[i] + bluePoints[i]) > 0) {
					fraction = ((double) bluePoints[i]) / (redPoints[i] + bluePoints[i]);
				}

				int start_y = (int) (pixels_per_y * (yOffset + i-1));
				int start_x = (int) (width * prevFrac);
				int end_y = (int) (pixels_per_y * (yOffset + i));
				int end_x = (int) (width * fraction);
				
				if(fraction > 0.5 && prevFrac < 0.5) {
					int orig_y = start_y;
					int orig_x = start_x;
					
					double diff = fraction - prevFrac;
					double midPoint = (0.5 - prevFrac) / diff;
					
					start_y = (int) (pixels_per_y * (yOffset+i-1+midPoint));
					start_x = (int) (width * 0.5);
					
					g.setColor(getColorForFraction((prevFrac + 0.5) / 2));
					g.drawLine(orig_x, orig_y, start_x, start_y);
					
					prevFrac = 0.5;
				}
				else if(fraction < 0.5 && prevFrac > 0.5) {
					int orig_y = start_y;
					int orig_x = start_x;
					
					double diff = prevFrac - fraction;
					double midPoint = (prevFrac - 0.5) / diff;
					
					start_y = (int) (pixels_per_y * (yOffset+i-1+midPoint));
					start_x = (int) (width * 0.5);
					
					g.setColor(getColorForFraction((prevFrac + 0.5) / 2));
					g.drawLine(orig_x, orig_y, start_x, start_y);
					
					prevFrac = 0.5;
				}
				
				g.setColor(getColorForFraction((prevFrac + fraction) / 2));
				g.drawLine(start_x, start_y, end_x, end_y);
				
				prevFrac = fraction;
			}
		}
	}
	
	private Color getColorForFraction(double fraction) {
		if(fraction > 0.5) {
			return blueColor;
		}
		else if(fraction < 0.5) {
			return redColor;
		}
		else {
			return grayColor;
		}
	}
	
	private void updateScoreBars() {
		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();
		
		int redScore = FoxServer.foxData.getRedScore();
		int blueScore = FoxServer.foxData.getBlueScore();
		
		double fraction = 0.5;
		if((redScore + blueScore) > 0) {
			fraction = ((double) redScore) / (redScore + blueScore);
		}
		
		setDoubleBounds(redScoreBar, SCORE_BAR_X * width, SCORE_BAR_Y * height, fraction * SCORE_BAR_WIDTH * width, SCORE_BAR_HEIGHT * height);
		setDoubleBounds(blueScoreBar, (SCORE_BAR_X + (fraction * SCORE_BAR_WIDTH)) * width, SCORE_BAR_Y * height, (1 - fraction) * SCORE_BAR_WIDTH * width, SCORE_BAR_HEIGHT * height);
	}
	
	public void updateScores() {
		redScore.setText("" + FoxServer.foxData.getRedScore());
		blueScore.setText("" + FoxServer.foxData.getBlueScore());
	}
	
	public void updateGraphs() {
		graphPanel.repaint();
		
		updateScoreBars();
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
	
	private void setDoubleBounds(JComponent comp, double x, double y, double width, double height) {
		comp.setBounds((int) Math.round(x), (int) Math.round(y), (int) Math.round(width), (int) Math.round(height));
	}
	
	private void updatePositions() {
		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();
		
		double panel_width = SCORE_BOX_WIDTH * width;
		double panel_height =  SCORE_BOX_HEIGHT * height;
		double panel_y = height - SCORE_BOX_BOTTOM_OFFSET * height - panel_height;
		double red_panel_x = RED_SCORE_BOX_X * width;
		double blue_panel_x = BLUE_SCORE_BOX_X * width;
		
		setDoubleBounds(redScorePanel, red_panel_x, panel_y, panel_width, panel_height);
	    redScore.setBounds(0, 0, redScorePanel.getWidth(), redScorePanel.getHeight());
	    redScore.setFont(new Font(redScore.getFont().getFontName(), Font.BOLD, (int) (SCORE_BOX_FONT*redScorePanel.getWidth())));
	    
	    setDoubleBounds(blueScorePanel, blue_panel_x, panel_y, panel_width, panel_height);
	    blueScore.setBounds(0, 0, blueScorePanel.getWidth(), blueScorePanel.getHeight());
	    blueScore.setFont(new Font(blueScore.getFont().getFontName(), Font.BOLD, (int) (SCORE_BOX_FONT*blueScorePanel.getWidth())));

	    if(FoxServer.foxData.getShowHistory()) {
	    	if(FoxServer.foxData.getLargeHistory()) {
			    int middle_box_width = (int) (MAIN_BOX_WIDTH * width);
			    int middle_box_height = (int) (MAIN_BOX_HEIGHT * height);
			    int middle_box_x = (int) (MAIN_BOX_X * width);
			    int middle_box_y = (int) (MAIN_BOX_Y * height);
			    
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
	    
	    graphPanel.setBounds(0, 0, historyPanel.getWidth(), historyPanel.getHeight());
	    
	    updateScoreBars();
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
