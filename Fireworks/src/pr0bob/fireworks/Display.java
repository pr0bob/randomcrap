package pr0bob.fireworks;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Display extends JPanel implements ActionListener {

	/**
	 * Wenn {@code true} wird oben links die aktuelle Anzahl der Partikel angezeigt
	 */
	private static final boolean SHOW_PARTICLE_COUNT = false;

	/**
	 * Haupt-Timer fürs Repaint, 15-30ms sind normalerweise als "flüssig" zu sehen
	 * Kann je nach Performance angepasst werden, ändert aber auch die
	 * Geschwindigkeit der Partikel TODO Sinnvoll wäre hier eher, alle Abläufe zu
	 * synchronisieren mit der Zeit die jeder Frame zum Rendern braucht
	 */
	Timer frameTimer = new Timer(30, this);

	/*
	 * Buffer für die Darstellungsebenen (Background > Tracer > Partikel)
	 */
	private BufferedImage particleBuffer;
	private BufferedImage tracerBuffer;
	private BufferedImage background;

	/*
	 * Listen für Partikel und Raketen, werden automatisch geleert andhand der
	 * "alive" Parameter der Objekte
	 */
	private ArrayList<Particle> particles = new ArrayList<>();
	private ArrayList<Rocket> rockets = new ArrayList<>();

	/**
	 * Der aktuelle Frame, wird kontinuierlich hochgezählt und dient dazu die<br>
	 * verbleibende Lebenszeit der Raketen und Partikel zu bestimmen
	 */
	private long currentFrame = 0;

	private Dimension indsideBounds;

	public Display(int width, int height) {
		setSize(width, height);
		init();
	}

	/**
	 * Innere Maße des Panels für Kollisionsabfragen mit den Rändern
	 */
	public Dimension getInsideBounds() {
		return indsideBounds;
	}

	public void addParticle(Particle p) {
		particles.add(p);
	}

	public void addRocket() {
		rockets.add(
				new Rocket(MathUtils.randInt((int) (getWidth() * 0.1), (int) (getWidth() * 0.9)), getHeight(), this));
	}

	/**
	 * Initialisierung, wird beim Start einmalig aufgerufen um die Buffer zu
	 * erstellen und den Hintergrund zu füllen<br>
	 * Startet den FrameTimer im Anschluss
	 */
	private void init() {
		background = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		particleBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		tracerBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);

		Graphics2D g2d = (Graphics2D) background.getGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		frameTimer.start();
	}

	@Override
	public void paint(Graphics g) {
		// Buffer initialisieren vor dem ersten Frame
		if (particleBuffer == null || particleBuffer.getWidth() != this.getWidth()
				|| particleBuffer.getHeight() != this.getHeight()) {
			init();
			return;
		}

		// Basis Graphics-Komponente des Panels
		Graphics2D g2d = (Graphics2D) g;

		// Partikel-Buffer leeren
		particleBuffer = new BufferedImage(getWidth(), getHeight(), 3);

		Graphics2D particleG = (Graphics2D) particleBuffer.getGraphics();
		Graphics2D tracerG = (Graphics2D) tracerBuffer.getGraphics();

		// TODO hier kann vermutlich die meiste Performance-Optimierung stattfinden,
		// z.B. mehrere Threads die jeweils einen Teil der Partikel abhandeln und
		// parallel laufen
		updateParticles(particleG, tracerG);

		/*
		 * Alle Buffer auf das Panel zeichnen
		 */
		g2d.drawImage(background, 0, 0, null);
		g2d.drawImage(tracerBuffer, 0, 0, null);
		g2d.drawImage(particleBuffer, 0, 0, null);

		// Tracer werden mit jedem Schritt teilweise weg-gefaded
		tracerBuffer = tracerFade(tracerBuffer);

		// Anzahl der Partikel anzeigen, falls Flag gesetzt
		if (SHOW_PARTICLE_COUNT) {
			showParticleCount(g2d);
		}
	}

	private void showParticleCount(Graphics2D bufferG) {
		bufferG.setFont(new Font("Arial", Font.BOLD, 15));
		bufferG.setColor(Color.white);
		bufferG.drawString("P: " + particles.size(), 20, 20);
	}

	private void updateParticles(Graphics2D particleG, Graphics2D tracerG) {
		ArrayList<Particle> deadParticles = new ArrayList<>();

		try {
			for (Particle p : particles) {
				p.updatePosition();
				if (p.setAlive(currentFrame)) {
					p.draw(particleG);
					p.drawTracer(tracerG);
				} else {
					deadParticles.add(p);
				}
			}
		} catch (ConcurrentModificationException ex) {
			// Nichts tun
		}

		particles.removeAll(deadParticles);
	}

	public BufferedImage tracerFade(BufferedImage tracerBuffer) {
		float fadeAmount = MathUtils.randFloat(0.85f, 0.95f);

		BufferedImage fadedImage = new BufferedImage(tracerBuffer.getWidth(), tracerBuffer.getHeight(), 3);
		Graphics2D g2d = fadedImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAmount));
		g2d.drawImage(tracerBuffer, 0, 0, tracerBuffer.getWidth(), tracerBuffer.getHeight(), null);
		g2d.dispose();

		return fadedImage;
	}

	public long getCurrentFrame() {
		return currentFrame;
	}

	public ArrayList<Particle> getParticles() {
		return particles;
	}

	public ArrayList<Rocket> getRockets() {
		return rockets;
	}

	public void setBounds(Dimension bounds) {
		indsideBounds = bounds;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(frameTimer)) {
			setBounds(this.getSize());

			ArrayList<Rocket> deadRockets = new ArrayList<>();
			for (Rocket r : rockets) {
				if (r.isAlive()) {
					r.update();
				} else {
					deadRockets.add(r);
				}
			}
			rockets.removeAll(deadRockets);

			repaint();
			currentFrame++;
		}
	}
}
