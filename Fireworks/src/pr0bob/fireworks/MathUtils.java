package pr0bob.fireworks;
import java.util.Random;

/**
 * Mathematical utilities
 */
public class MathUtils {
	/**
	 * Ranged Integer
	 * 
	 * @param min - Minimum value
	 * @param max - Maximum value
	 * @return Random within the given range
	 */
	public static int randInt(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * Seeded ranged Integer
	 * 
	 * @param min  - Minimum value
	 * @param max  - Maximum value
	 * @param seed - Seed
	 * @return Random within the given range
	 */
	public static int randInt(int min, int max, long seed) {
		Random r = new Random(seed);
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * Ranged Double
	 * 
	 * @param min - Minimum value
	 * @param max - Maximum value
	 * @return Random within the given range
	 */
	public static double randDouble(double min, double max) {
		Random r = new Random();
		return (min + (max - min) * r.nextDouble());
	}

	/**
	 * Seeded ranged Double
	 * 
	 * @param min  - Minimum value
	 * @param max  - Maximum value
	 * @param seed - Seed
	 * @return Random within the given range
	 */
	public static double randDouble(double min, double max, long seed) {
		Random r = new Random(seed);
		return (min + (max - min) * r.nextDouble());
	}

	/**
	 * Ranged Float
	 * 
	 * @param min - Minimum value
	 * @param max - Maximum value
	 * @return Random within the given range
	 */
	public static float randFloat(float min, float max) {
		Random r = new Random();
		return (min + (max - min) * r.nextFloat());
	}

	/**
	 * Returns true at the given chance in percent
	 * 
	 * @param chance - Chance for the returned value to be true in a range of 0.0 to
	 *               1.0 (e.g. 0.5 being 50% chance)
	 * @return Boolean with a {@code chance} chance of being true
	 */
	public static boolean randomChance(double chance) {
		return new Random().nextDouble() < chance;
	}
}
