package mikera.ai;

public interface Sensor {
	/**
	 * Returns the output length of the sensor (number of double values)
	 * @return
	 */
	public int getOutputLength();
	
	/**
	 * Sense a given object
	 * @param a
	 * @return a new double[] array representing the sense data
	 */
	public double[] sense(Object a);
	
	public void sense(Object a, double[] dest);

	public void sense(Object a, double[] dest, int offset);
}
