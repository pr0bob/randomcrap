/**
 * This class defines a 2-dimensional vector
 */
public class Vector {
	/**
	 * X-magnitude of the vector
	 */
	private double x;

	/**
	 * Y-magnitude of the vector
	 */
	private double y;

	/*
	 * Relative X & Y are only used for displaying purposes e.g. drawing the vector
	 * at a certain point without altering its base values
	 */
	private double relativeX;
	private double relativeY;

	/**
	 * Global number of vectors (For ID-generation)
	 */
	static long numOfVectors;

	/**
	 * ID of the current vector
	 */
	long vectorID;

	/**
	 * Crates a new vector with x=0.0 and y=0.0
	 */
	public Vector() {
		this.x = 0.0d;
		this.y = 0.0d;
		numOfVectors++;
		this.vectorID = numOfVectors;
	}

	/**
	 * Creates a new vector with the given x/y
	 * 
	 * @param x - X of the vector
	 * @param y - Y of the vector
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
		numOfVectors++;
		this.vectorID = numOfVectors;
	}

	/**
	 * Adds another Vector object to this vector
	 * 
	 * @param v - Vector object to add
	 */
	public void add(Vector v) {
		this.x += v.getX();
		this.y += v.getY();
	}

	/**
	 * Adds x and y to this vector's x and y
	 * 
	 * @param x - X to add
	 * @param y - Y to add
	 */
	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}

	/**
	 * Multiplies this vector by the given amount
	 * 
	 * @param mag - Magnitude to multiply by
	 */
	public Vector magnitude(double mag) {
		this.x *= mag;
		this.y *= mag;
		return new Vector(this.x, this.y);
	}

	/**
	 * Rotates the vector by n degrees
	 * 
	 * @param rad - Angle to rotate by in degree
	 */
	public void rotateByDeg(double deg) {
		deg = Math.toRadians(deg);
		double x1 = this.x * Math.cos(deg) - this.y * Math.sin(deg);
		double y1 = this.x * Math.sin(deg) + this.y * Math.cos(deg);
		this.x = x1;
		this.y = y1;
	}

	/**
	 * Rotates the vector by n rad
	 * 
	 * @param rad - Angle to rotate by in rad
	 */
	public void rotateByRad(double rad) {
		double x1 = this.x * Math.cos(rad) - this.y * Math.sin(rad);
		double y1 = this.x * Math.sin(rad) + this.y * Math.cos(rad);
		this.x = x1;
		this.y = y1;
	}

	/**
	 * @return The angle of the vector in rad
	 */
	public double getAngleRad() {
		return Math.atan2(this.x, this.y);
	}

	/**
	 * @return The angle of the vector in degrees
	 */
	public double getAngleDeg() {
		return Math.toDegrees(Math.atan2(this.x, this.y));
	}

	/**
	 * @return This vector's relative X coordinate
	 */
	public double getRelativeX() {
		return relativeX;
	}

	/**
	 * Sets relative X
	 * 
	 * @param relativeX - Relative X coordinate
	 */
	public void setRelativeX(double relativeX) {
		this.relativeX = relativeX;
	}

	/**
	 * @return This vector's relative Y coordinate
	 */
	public double getRelativeY() {
		return relativeY;
	}

	/**
	 * Sets relative Y
	 * 
	 * @param relativeY - Relative Y coordinate
	 */
	public void setRelativeY(double relativeY) {
		this.relativeY = relativeY;
	}

	/**
	 * Sets x and y coordinates
	 * 
	 * @param x - X coordinate
	 * @param y - Y coordinate
	 */
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return This vector's X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets x
	 * 
	 * @param x - X coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return This vector's Y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets y
	 * 
	 * @param y - Y coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Reverses this vector
	 */
	public void reverse() {
		this.x *= -1.0d;
		this.y *= -1.0d;
	}

	/**
	 * Reverses this vector's X-coordinate
	 */
	public void reverseX() {
		this.x *= -1.0d;
	}

	/**
	 * Reverses this vector's Y-coordinate
	 */
	public void reverseY() {
		this.y *= -1.0d;
	}

	/**
	 * Adds given X value to this vector's X
	 * 
	 * @param x - X to add
	 */
	public void addX(double x) {
		this.x += x;
	}

	/**
	 * Adds given Y value to this vector's Y
	 * 
	 * @param y - Y to add
	 */
	public void addY(double y) {
		this.y += y;
	}
}
