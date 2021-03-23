import java.awt.Color;
import java.util.ArrayList;

/**
 * "When are they going to get to the fireworks factory?"
 * 
 * Factory Klasse zum Erzeugen von bestimmten Partikeleffekten
 */
public class FireworksFactory {

	private FireworksFactory() {
		// Hidden
	}

	/**
	 * Erzeugt eine zufällige Farbe für die Explosion
	 */
	private static Color getRandomFireworksColor() {
		int colD = MathUtils.randInt(0, 2);
		return new Color(MathUtils.randInt(colD == 0 ? 230 : 0, 255), MathUtils.randInt(colD == 1 ? 230 : 0, 255),
				MathUtils.randInt(colD == 2 ? 230 : 0, 255));
	}

	/**
	 * Erzeugt leicht variierende Orangetöne für die Funken beim Abfeuern/den
	 * Aufstieg
	 */
	private static Color getSparkColor() {
		return new Color(MathUtils.randInt(180, 220), MathUtils.randInt(80, 180), 25);
	}

	/**
	 * Erzeugt Funken-Partikel für den Aufstieg der Rakete
	 * 
	 * @param x       - X-Position
	 * @param y       - Y-Position
	 * @param display - Display-Objekt auf dem die Partikel angezeigt werden sollen
	 * @return ArrayList mit Partikel-Objekten
	 */
	public static ArrayList<Particle> getTrailSparks(int x, int y, Display display) {
		ArrayList<Particle> particles = new ArrayList<>();
		Color col = getSparkColor();
		for (int i = 0; i < MathUtils.randInt(6, 18); i++) {
			Particle p = new Particle(x, y, col, new Vector(MathUtils.randDouble(0, 0.25), 0), MathUtils.randInt(1, 12),
					display);
			p.setFlickerRate(MathUtils.randInt(0, 15));
			p.setFlickerColor(col.darker());
			p.setGlowRadius(0);
			p.setGlow(true);
			p.setTracers(MathUtils.randomChance(0.5));
			p.setTracerColor(col);
			p.setGravity(true);
			p.getVelocity().rotateByDeg(MathUtils.randInt(0, 360));

			if (MathUtils.randomChance(0.05)) {
				particles.add(p.cloneForSplit());
			}
			particles.add(p);
		}
		return particles;
	}

	/**
	 * Erzeugt Funken-Partikel den Abschuss der Rakete
	 * 
	 * @param x       - X-Position
	 * @param y       - Y-Position
	 * @param display - Display-Objekt auf dem die Partikel angezeigt werden sollen
	 * @return ArrayList mit Partikel-Objekten
	 */
	public static ArrayList<Particle> getShotSparks(int x, int y, Display display) {
		ArrayList<Particle> particles = new ArrayList<>();

		for (int i = 0; i < MathUtils.randInt(150, 250); i++) {
			Color sparkColor = getSparkColor();
			Particle p = new Particle(x, y, sparkColor, new Vector(MathUtils.randDouble(0, 6), 0),
					MathUtils.randInt(6, 24), display);
			p.setFlickerRate(MathUtils.randInt(0, 15));
			p.setFlickerColor(sparkColor.darker());
			p.setGlowRadius(0);
			p.setGlow(true);
			p.setTracers(MathUtils.randomChance(0.5));
			p.setTracerColor(sparkColor);
			p.setGravity(false);
			p.getVelocity().rotateByDeg(MathUtils.randInt(180 + 35, 360 - 35));

			if (MathUtils.randomChance(0.05)) {
				particles.add(p.cloneForSplit());
			}
			particles.add(p);
		}
		return particles;
	}

	/**
	 * Erzeugt Partikel für eine 2-farbige Feuerwerksexplosion mit zufälligen
	 * Flacker- und Leuchteffekten
	 * 
	 * @param x       - X-Position
	 * @param y       - Y-Position
	 * @param display - Display-Objekt auf dem die Partikel angezeigt werden sollen
	 * @return ArrayList mit Partikel-Objekten
	 */
	public static ArrayList<Particle> getFireworksExplosion(int x, int y, Display display) {
		ArrayList<Particle> particles = new ArrayList<>();

		// Initiale Explosion der Rakete (Funken-Farbe)
		for (int i = 0; i < MathUtils.randInt(50, 150); i++) {
			Color sparkColor = getSparkColor();
			Particle p = new Particle(x, y, sparkColor,
					new Vector(MathUtils.randDouble(-1, 1), MathUtils.randDouble(-1, -1.6)), MathUtils.randInt(2, 25),
					display);
			p.setFlickerRate(MathUtils.randInt(0, 15));
			p.setFlickerColor(MathUtils.randomChance(0.1) ? Color.white : sparkColor.brighter());
			p.setGlowRadius(2);
			p.setGlow(true);
			p.setTracerColor(sparkColor);
			p.setGravity(false);
			p.getVelocity().rotateByDeg(MathUtils.randInt(0, 360));

			// 10% chance dass Partikel initial aufgespalten werden
			if (MathUtils.randomChance(0.05)) {
				particles.add(p.cloneForSplit());
			}
			particles.add(p);
		}

		// Farbige Explosion
		int r = MathUtils.randInt(0, 45);
		Color primaryColor = getRandomFireworksColor();
		Color secondaryColor = getRandomFireworksColor();

		// 25% Chance auf abweichenden Effekt
		boolean b = MathUtils.randomChance(0.25d);

		for (int i = 0; i < MathUtils.randInt(200, 500); i++) {
			Particle p = new Particle(x, y, (MathUtils.randomChance(0.5) ? primaryColor : secondaryColor),
					new Vector(b ? MathUtils.randDouble(4, 12) : MathUtils.randDouble(1, 8),
							MathUtils.randDouble(0, 0)),
					MathUtils.randInt(20, 55), display);
			p.setTracerColor(primaryColor);
			p.setGlowRadius(MathUtils.randInt(2, 5));
			p.setFlickerColor(
					MathUtils.randomChance(0.25) ? getRandomFireworksColor().brighter() : primaryColor.darker());
			p.setFlickerRate(MathUtils.randInt(3, 22));
			p.setGlow(true);

			// Zufällige Rotation der einzelnen Partikel mit 25% Chance,
			// dass eine nach oben gerichtete Explosion entsteht anstatt kreisförmig
			// (definiert durch den boolean Wert von "b")
			p.getVelocity().rotateByDeg(b ? MathUtils.randInt(180 - r, 360 + r) : MathUtils.randInt(0, 360));

			// 10% chance dass Partikel initial aufgespalten werden
			if (MathUtils.randomChance(0.10)) {
				particles.add(p.cloneForSplit());
			}
			particles.add(p);
		}

		return particles;
	}

}
