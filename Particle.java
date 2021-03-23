import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Definiert ein Partikel-Objekt
 * 
 * Die Eigenschaften bestimmen das physikalische Verhalten sowie die Art der
 * Darstellung beim Rendern
 */
public class Particle {

	/*
	 * Statische Grundwerte
	 */

	/**
	 * Bestimmt um wie viel der Geschwindigkeits-Vektor sich maximal bei Kollision
	 * mit einem der Ränder ändert
	 */
	private static final double COLLISION_SPREAD_FACTOR = 1.5d;

	/**
	 * Bestimmt die Default-Sichtbarkeit der Tracer (z.B. 0.2 = 20% der Helligkeit
	 * der Partikelfarbe)
	 */
	private static final double DEFAULT_TRACER_STRENGTH = 0.2d;

	/**
	 * Bestimmt die Stärke der Gravitation als Vektor<br>
	 * (X sollte immer 0 sein, außer man will Gravitation zur Seite hin)<br>
	 * Positive Y-Werte bedeuten Anziehung nach unten, negative würden quasi die
	 * Gravitation umkehren
	 */
	private static final Vector GRAVITY = new Vector(0.0, 0.122);

	private Color color;
	private Vector position;
	private Vector velocity;

	/**
	 * "Lebenszeit" des Partikels in Frames
	 */
	private long lifetime;
	private long birthtime; // Wird mit lifetime abgeglichen um zu bestimmen ob das Partikel "tot" ist
	private boolean alive = true;

	/**
	 * Farbe der "Spuren" die das Partikel ggf. hinterlässt
	 */
	private Color tracerColor = null;
	private boolean tracer = true;
	private double tracerStrength = DEFAULT_TRACER_STRENGTH;

	/**
	 * Radius in Px für kreisförmiges Leuchten um das Partikel
	 */
	private int glowRadius = 0;
	private boolean glow = false;

	/**
	 * "Flimmer"-Rate des Partikels in Frames (0 = kein Flimmern, gute Werte liegen
	 * zwischen 1 und 20)
	 */
	private int flickerRate = 0;
	private Color flickerColor = null;
	private boolean flicker = false;
	private boolean currentFlicker = flicker;

	private boolean gravityOn = true; // Standardmäßig an, kann aber deaktiviert werden für einzelne Partikel

	private Display parentDisplay = null; // Display auf dem das Partikel "lebt"

	/**
	 * Position im letzten Frame (wird genutzt um die Spuren zu erzeigen)
	 */
	private Point lastPosition = new Point();

	/**
	 * Wenn {@code true} "wobbelt" das Partikel bei jedem Update etwas zufällig
	 * umher
	 */
	private boolean jitter = true;

	private ArrayList<BufferedImage> glowBuffer = new ArrayList<>();
	private ArrayList<BufferedImage> glowBufferFlicker = new ArrayList<>();

	/**
	 * Erstellt ein leeres neues Partikel
	 * 
	 * @param parentDisplay
	 */
	public Particle(Display parentDisplay) {
		this.parentDisplay = parentDisplay;
	}

	/**
	 * Erstellt ein Partikel mit allen nötigen Parametern zum Darstellen
	 * 
	 * @param x             - Initiale X-Position
	 * @param y             - Initiale Y-Position
	 * @param color         - Grundfarbe
	 * @param velocity      - Geschwindigkeits-Vektor (initial)
	 * @param lifetime      - Lebenszeit in Frames
	 * @param parentDisplay
	 */
	public Particle(int x, int y, Color color, Vector velocity, long lifetime, Display parentDisplay) {
		position = new Vector(x, y);
		this.velocity = velocity;
		this.color = color;
		this.lifetime = lifetime;
		this.parentDisplay = parentDisplay;
		lastPosition = new Point((int) position.getX(), (int) position.getY());
		birthtime = parentDisplay.getCurrentFrame();
	}

	public ArrayList<BufferedImage> createGlow() {
		if (glowRadius <= 1) {
			return new ArrayList<>();
		}
		double alphaStep = 200 / glowRadius;
		double alpha = 200;

		ArrayList<BufferedImage> buffer = new ArrayList<>();
		for (int i = 1; i < glowRadius; i++) {
			BufferedImage glowB = new BufferedImage(glowRadius * 2, glowRadius * 2, BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g2d = (Graphics2D) glowB.getGraphics();

			Color baseGlowColor = color;
			if (currentFlicker) {
				baseGlowColor = flickerColor; // Flickern wird auch auf das Leuchten übertragen
			}

			// Alpha-Wert des Leuchtens wird mit steigendem Radius stufenweise herabgesetzt
			Color glowCol = new Color(baseGlowColor.getRed(), baseGlowColor.getGreen(), baseGlowColor.getBlue(),
					(int) alpha);

			g2d.setColor(glowCol);
			g2d.drawOval(glowB.getWidth() / 2 - i, glowB.getHeight() / 2 - i, 2 * i, 2 * i);
			alpha = Math.max(alpha - alphaStep, 0); // Math.max um zu verhindern dass negative Werte vorkommen

			buffer.add(glowB);
		}
		return buffer;
	}

	/**
	 * Zeichnet das Partikel anhand seiner Eigenschaften auf den gegebenen
	 * Graphics2D Context (Aus einem Panel, oder einem BufferedImage z.B.)
	 * 
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		if (!alive) {
			return;
		}

		// Wenn Leuchten an und der Buffer leer ist, Buffer erstellen
		if (!currentFlicker && glow && glowBuffer.isEmpty()) {
			glowBuffer = createGlow();
		}
		if (currentFlicker && glow && glowBufferFlicker.isEmpty()) {
			glowBufferFlicker = createGlow();
		}

		// Bestimmt ob das Partikel flackert (true -> false -> true etc. entsprechend
		// der Flacker-Rate)
		if (flickerRate > 0 && flickerColor != null && (parentDisplay.getCurrentFrame() - birthtime) % flickerRate == 0
				&& !currentFlicker) {
			currentFlicker = MathUtils.randomChance(0.90d); // Minimal zufällig um zu verhindern dass gleichzeitig
															// erstellte
			// Partikel synchron flackern
		} else {
			currentFlicker = false;
		}

		// Leuchten, falls gesetzt
		if (!currentFlicker && glow && glowRadius > 1) {
			for (BufferedImage glowB : glowBuffer) {
				g2d.drawImage(glowB, (int) position.getX() - glowRadius, (int) position.getY() - glowRadius, null);
			}
		} else if (currentFlicker && glow && glowRadius > 1) {
			for (BufferedImage glowB : glowBufferFlicker) {
				g2d.drawImage(glowB, (int) position.getX() - glowRadius, (int) position.getY() - glowRadius, null);
			}
		}

		if (currentFlicker) {
			g2d.setColor(flickerColor);
		} else {
			g2d.setColor(color);
		}
		g2d.drawLine((int) position.getX(), (int) position.getY(), (int) position.getX(), (int) position.getY());
	}

	/**
	 * Zeichnet die Leuchtspuren auf den gegeben Graphics2D-Context (falls tracer =
	 * {@code true})
	 * 
	 * @param g2d
	 */
	public void drawTracer(Graphics2D g2d) {
		if (!tracer) {
			return;
		}

		Color tracerCol = tracerColor == null ? color : tracerColor;
		g2d.setColor(new Color(tracerCol.getRed(), tracerCol.getGreen(), tracerCol.getBlue(),
				(int) (tracerCol.getAlpha() * tracerStrength)));
		g2d.drawLine((int) lastPosition.getX(), (int) lastPosition.getY(), (int) position.getX(),
				(int) position.getY());
	}

	/**
	 * Prüft die Kollision des Partikels mit den Rändern des Panels und lässt es
	 * ggf. abprallen
	 */
	public void collide() {
		Dimension bounds = parentDisplay.getInsideBounds();
		Rectangle north = new Rectangle(0, -500, (int) bounds.getWidth(), 500);
		Rectangle east = new Rectangle((int) bounds.getWidth(), 0, 500, (int) bounds.getHeight());
		Rectangle west = new Rectangle(-500, 0, 500, (int) bounds.getHeight());
		Rectangle south = new Rectangle(0, (int) bounds.getHeight(), (int) bounds.getWidth(), 500);

		lastPosition = new Point((int) position.getX(), (int) position.getY());
		Point futurePosition = new Point((int) (position.getX() + velocity.getX()),
				(int) (position.getY() + velocity.getY()));

		boolean collision = false;

		if (north.contains(futurePosition) || south.contains(futurePosition)) {
			velocity.reverseY();
			velocity.addX(MathUtils.randDouble(-COLLISION_SPREAD_FACTOR, COLLISION_SPREAD_FACTOR));
			collision = true;
		}

		if (east.contains(futurePosition) || west.contains(futurePosition)) {
			velocity.reverseX();
			velocity.addY(MathUtils.randDouble(-COLLISION_SPREAD_FACTOR, COLLISION_SPREAD_FACTOR));
			collision = true;
		}

		// Wenn Kollision, dann leichte Verlangsamung und etwas zufllige Rotation
		if (collision) {
			velocity.magnitude(MathUtils.randDouble(0.7, 0.9));
			velocity.rotateByDeg(MathUtils.randDouble(-15, 15));
		}
	}

	/**
	 * Fügt der Geschwindigkeit in jedem Frame die Gravitation hinzu und falls
	 * jitter = {@code true} wird ein "Wackeln" hinzugefügt
	 */
	public void addGravityAndJitter() {
		if (gravityOn) {
			velocity.add(GRAVITY);
		}

		if (jitter) {
			velocity.add(new Vector(MathUtils.randDouble(-0.2, 0.2), MathUtils.randDouble(-0.02, 0.02)));
		}
	}

	/**
	 * Zusammenfassende Funktion zum Upate der Partikelposition<br>
	 * Prüft Kollision, erneuert die Position, fügt ggf. geltende Kräfte hinzu
	 */
	public void updatePosition() {
		collide();
		position.add(velocity);

		velocity.magnitude(MathUtils.randDouble(0.975, 0.985)); // Simuliert quasi einen leichten Luftwiderstand
		addGravityAndJitter();
	}

	/**
	 * Klon-Funktion für das "Aufspalten" eines Partikels in zwei
	 * 
	 * @return Kopie des Partikels mit verminderter Geschwindigkeit und Lebenszeit
	 */
	public Particle cloneForSplit() {
		Particle p = (Particle) this.clone();
		p.getPosition().add(new Vector(MathUtils.randInt(-1, 1), MathUtils.randInt(-1, 1)));
		p.getVelocity().magnitude(MathUtils.randDouble(0, 1.25));
		p.setLifetime(p.getLifetime() / 2);
		return p;
	}

	/**
	 * Klont ein Partikel 1:1
	 */
	@Override
	public Object clone() {
		Particle p = new Particle((int) position.getX(), (int) position.getY(), color, velocity, lifetime,
				parentDisplay);
		p.setFlicker(hasFlicker());
		p.setFlickerRate(getFlickerRate());
		p.setFlickerColor(getFlickerColor());
		p.setGlow(hasGlow());
		p.setGlowRadius(getGlowRadius());
		p.setGravity(hasGravity());
		p.setJitter(hasJitter());
		p.setLastPosition(getLastPosition());
		p.setTracerColor(getTracerColor());
		p.setTracers(hasTracers());
		p.setTracerStrength(getTracerStrength());
		return p;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public long getLifetime() {
		return lifetime;
	}

	/**
	 * Prüft ob die Lebenszeit abgelaufen ist und setzt ggf. alive auf {@code false}
	 * 
	 * @param currentFrame - Aktuelle Frame-Zahl vom Display
	 * @return - Aktueller Wert von alive
	 */
	public boolean setAlive(long currentFrame) {
		if (currentFrame - birthtime >= lifetime) {
			alive = false;
			return false;
		}
		return true;
	}

	/*
	 * Der ganze mehr oder weniger nötige Getter-Setter Kram...
	 */

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public long getBirthtime() {
		return birthtime;
	}

	public void setBirthtime(long birthtime) {
		this.birthtime = birthtime;
	}

	public Color getTracerColor() {
		return tracerColor;
	}

	public void setTracerColor(Color tracerColor) {
		this.tracerColor = tracerColor;
	}

	public boolean hasGlow() {
		return glow;
	}

	public void setGlow(boolean glow) {
		this.glow = glow;
	}

	public int getGlowRadius() {
		return glowRadius;
	}

	public void setGlowRadius(int glowRadius) {
		this.glowRadius = glowRadius;
	}

	public Color getFlickerColor() {
		return flickerColor;
	}

	public void setFlickerColor(Color flickerColor) {
		this.flickerColor = flickerColor;
	}

	public boolean hasFlicker() {
		return flicker;
	}

	public void setFlicker(boolean flicker) {
		this.flicker = flicker;
	}

	public int getFlickerRate() {
		return flickerRate;
	}

	public void setFlickerRate(int flickerRate) {
		this.flickerRate = flickerRate;
	}

	public boolean hasTracers() {
		return tracer;
	}

	public void setTracers(boolean tracer) {
		this.tracer = tracer;
	}

	public double getTracerStrength() {
		return tracerStrength;
	}

	public void setTracerStrength(double tracerStrength) {
		this.tracerStrength = tracerStrength;
	}

	public boolean hasGravity() {
		return gravityOn;
	}

	public void setGravity(boolean gravity) {
		this.gravityOn = gravity;
	}

	public Display getParentDisplay() {
		return parentDisplay;
	}

	public void setParentDisplay(Display parentDisplay) {
		this.parentDisplay = parentDisplay;
	}

	public Point getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(Point lastPosition) {
		this.lastPosition = lastPosition;
	}

	public boolean hasJitter() {
		return jitter;
	}

	public void setJitter(boolean jitter) {
		this.jitter = jitter;
	}

	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
	}
}
