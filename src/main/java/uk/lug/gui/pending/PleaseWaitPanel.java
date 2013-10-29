package uk.lug.gui.pending;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.lang.Runnable;

import uk.lug.util.SwingHelper;

public class PleaseWaitPanel extends JComponent implements ActionListener {
	private static final int ORBIT_COUNT = 7;
	private float alpha = 0.5f;
	private Dimension lastSize = null;
	private Area staticIamge = null;
	private long t = System.currentTimeMillis();

	private int minuteDegrees = 0;
	private int hourDegrees = 0;
	private int millisToRotate = 4500;
	private long startTime = 0;
	private Timer timer = new Timer((int) (1000 / 25), this);
	private String text = "Please wait...";

	public PleaseWaitPanel() {
		super();
		setName("glasspane.pleasewait");
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private Font font = new Font("sansserif", Font.ITALIC, 18);

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int height = getHeight();
		int width = getWidth();

		int alphaComponent = (int) (255 * alpha);
		g2.setColor(new Color(255, 255, 255, alphaComponent));
		g2.fillRect(0, 0, width, height);

		g2.setColor(new Color(25, 25, 25, alphaComponent));
		drawStaticCircle();
		g2.fill(staticIamge);

		Area arm = drawOuterCircle();

		g2.fill(arm);

		FontMetrics fm = g2.getFontMetrics(font);
		Rectangle2D bounds = fm.getStringBounds(text, g2);
		g2.setColor(Color.BLACK);
		int tx = (int) ((getWidth() / 2) - (bounds.getWidth() / 2));
		int ty = (int) (getHeight() - (bounds.getHeight()) + 10);

		g2.setFont(font);
		g2.drawString(text, tx, ty);
	}

	private Area drawOuterCircle() {
		Area ret = new Area();
		for (int i = 0; i < ORBIT_COUNT; i++) {
			Area circle = getCircle(minuteDegrees + (i * (360/ORBIT_COUNT)) );
			ret.add(circle);

		}
		return ret;

	}

	private Area getCircle(int degrees) {
		int d = degrees;
		if (d > 360) {
			d = d - 360;
		}
		if (d < 0) {
			d = d + 360;
		}
		int baseSize = (getWidth() < getHeight() ? getWidth() : getHeight());
		int hubSize = (int) (baseSize * 0.1f);
		int x = (getWidth() / 2) - (hubSize / 2);
		int y = (getHeight() / 2) - (hubSize / 2);
		Ellipse2D.Float hub = new Ellipse2D.Float(x, y, hubSize, hubSize);
		int outerSize = (int) (baseSize * 0.75f);
		int innerSize = (int) (baseSize * 0.6f);

		Area ret = new Area(hub);
		ret.transform(AffineTransform.getTranslateInstance((double) (baseSize * 0.362f), 0d));
		ret.transform(AffineTransform.getRotateInstance(Math.toRadians(d), getWidth() / 2, getHeight() / 2));
		return ret;
	}

	private void drawStaticCircle() {
		if (lastSize != null && lastSize.equals(getSize())) {
			return;
		}
		lastSize = getSize();
		int baseSize = (getWidth() < getHeight() ? getWidth() : getHeight());
		int outerSize = (int) (baseSize * 0.75f);
		// Outer ring
		int x = (getWidth() / 2) - (outerSize / 2);
		int y = (getHeight() / 2) - (outerSize / 2);
		Ellipse2D.Float outer = new Ellipse2D.Float(x, y, outerSize, outerSize);
		staticIamge = new Area(outer);

		int innerSize = (int) (baseSize * 0.70f);
		x = (getWidth() / 2) - (innerSize / 2);
		y = (getHeight() / 2) - (innerSize / 2);
		Ellipse2D.Float inner = new Ellipse2D.Float(x, y, innerSize, innerSize);
		Area ret = new Area(inner);

		int hubSize = (int) (baseSize * .2f);
		x = (getWidth() / 2) - (hubSize / 2);
		y = (getHeight() / 2) - (hubSize / 2);
		Ellipse2D.Float hub = new Ellipse2D.Float(x, y, hubSize, hubSize);

		staticIamge.subtract(ret);
		staticIamge.add(new Area(hub));
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible == isVisible()) {
			return;
		}
		if (visible) {
			start();
		} else {
			stop();

		}
		minuteDegrees = 0;
		hourDegrees = 0;
		Runnable r = new Runnable() {

			public void run() {
				superVisible(visible);
			}
		};
		SwingHelper.runInEventThread(r);
	}

	protected void superVisible(boolean visible) {
		super.setVisible(visible);
	}

	public void stop() {
		timer.stop();
	}

	public void start() {
		startTime = System.currentTimeMillis();
		timer.start();
	}

	public static void main(String[] args) {
		try {

			JFrame win = new JFrame("Test of wait panel.");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel lowerPanel = new JPanel(new GridLayout(3, 3));
			for (int i = 0; i < 9; i++) {
				lowerPanel.add(new JButton(Integer.toString(i + 1)));
			}
			PleaseWaitPanel glass = new PleaseWaitPanel();
			glass.setVisible(false);
			win.setGlassPane(glass);
			win.getContentPane().add(lowerPanel);
			win.setSize(640, 480);
			win.setVisible(true);

			for (int i = 0; i < 10; i++) {
				Thread.sleep(500);
				glass.setVisible(true);
				Thread.sleep(3250);
				glass.setVisible(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		long timeDiff = System.currentTimeMillis() - startTime;
		float part = ((float) timeDiff) / millisToRotate;
		minuteDegrees = (int) (360 * part);
		hourDegrees = (int) (minuteDegrees / 12);
		repaint();
	}
}
