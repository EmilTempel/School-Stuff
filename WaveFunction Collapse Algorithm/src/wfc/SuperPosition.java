package wfc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SuperPosition {

	ArrayList<BufferedImage> positions;
	BufferedImage value;

	HashMap<BufferedImage, Double> probability;

	public SuperPosition(BufferedImage[] tiles, HashMap<BufferedImage, Double> probability) {
		positions = new ArrayList<>();
		for (BufferedImage i : tiles) {
			positions.add(i);
		}
		value = null;

		this.probability = probability;
	}

	public boolean isCollapsed() {
		return value != null;
	}

	public void collapse(Random seed) {
		double coef = 0;
		for (BufferedImage i : positions) {
			coef += probability.get(i);
		}

		double random = seed.nextDouble(1), c = 0;

		for (BufferedImage i : positions) {
			if (c > random) {
				setValue(i);
				break;
			} else {
				c += probability.get(i) / coef;
			}
		}
	}

	public void setValue(BufferedImage value) {
		this.value = value;
		positions = new ArrayList<>();
	}

	public BufferedImage getValue() {
		return value;
	}

	public double entropy() {
		double entropy = 0;

		for (BufferedImage i : positions) {
			double p = probability.get(i);
			entropy += p * Math.log(p) / Math.log(2);
		}

		return entropy;
	}

	public String toString() {
		return isCollapsed() ? "collapsed" : "not collapsed";
	}
}
