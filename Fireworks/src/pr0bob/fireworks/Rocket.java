package pr0bob.fireworks;
/**
 * Klasse für eine Feuerwerksrakete die vom gegebenen X/Y-Punkt aus aufsteigt
 */
public class Rocket {

	/**
	 * Aktuelle Position der Rakete
	 */
	private Vector position;

	/**
	 * Geschwindigkeit der Rakete (Rotation dieses Vektors definiert auch die
	 * Flugrichtung)
	 */
	private Vector velocity;

	/**
	 * Display-Objekt auf dem die Rakete "zuhause" ist
	 */
	private Display display;

	/**
	 * Lebenszeit in Frames, wird heruntergezählt bis 0 erreicht ist, dann
	 * explodiert die Rakete und wird für "tot" erklärt um in der Display Klasse aus
	 * der Liste entfernt zu werden
	 */
	private long lifetime = MathUtils.randInt(150, 200);

	/**
	 * {@code true} wenn die Rakete noch im Flug ist<br>
	 * {@code false} wenn ab dem Moment der Explosion
	 */
	private boolean alive = true;

	/**
	 * Gibt an ob der erste Frame gerendert wird (Abschuss der Rakete, danach false)
	 */
	private boolean initial = true;

	/**
	 * Die Chance (z.B. 0.05 = 5%) gibt an wie wahrscheinlich es ist, dass die
	 * Rakete sich "wild" verhält beim Flug Hat Effekt auf die min/max Gradzahl der
	 * Rotation bei jedem update der Position
	 */
	private boolean crazy = MathUtils.randomChance(0.05);

	/**
	 * Erstellt eine neue Rakete
	 * 
	 * @param x       - Initiale X-Position
	 * @param y       - Initiale Y-Position
	 * @param display - Display auf dem die Rakete angezeigt werden soll
	 */
	public Rocket(int x, int y, Display display) {
		this.display = display;
		position = new Vector(x, y);
		velocity = new Vector(0, MathUtils.randInt(-7, -4));
	}

	/**
	 * {@code true} solange die Rakete nicht explodiert ist (Durch Ablauf der
	 * Lebenszeit oder Kollision mit einem der Ränder)
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Update der Position der Rakete auf dem Display. Erzeugt entsprechenden
	 * Partikeleffekt, je nachdem ob die Rakete abgeeuert wurde, gerade aufsteigt
	 * oder explodiert und rotiert den Velocity-Vektor zufällig, damit die Rakete
	 * nicht nur vollkommen gerade fliegt
	 */
	public void update() {
		if (initial) {
			display.getParticles()
					.addAll(FireworksFactory.getShotSparks((int) position.getX(), (int) position.getY(), display));
			initial = false;
		} else if (lifetime > 0 && position.getY() <= display.getHeight()
				&& position.getY() >= display.getHeight() * 0.33 // Wenn näher als 1/3 am oberen Rand, wird Explosion
																	// ausgelöst
				&& position.getX() > 1 && position.getX() < display.getWidth() - 1) {
			position.add(velocity);
			velocity.rotateByDeg(MathUtils.randDouble(crazy ? -25 : -4.5, crazy ? 25 : 4.5));
			display.getParticles()
					.addAll(FireworksFactory.getTrailSparks((int) position.getX(), (int) position.getY(), display));
		} else {
			display.getParticles().addAll(
					FireworksFactory.getFireworksExplosion((int) position.getX(), (int) position.getY(), display));
			alive = false;
		}

		lifetime--; // Lebenszeit herunterzählen
	}

}
