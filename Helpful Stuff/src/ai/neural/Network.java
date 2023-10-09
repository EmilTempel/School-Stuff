package ai.neural;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import geometry.Vector;
import math.Functions;

public class Network implements Serializable {

	private static final long serialVersionUID = 1L;

	//@formatter:off
	public static final Function<Double, Double> 
			ReLUFunction = x -> Math.max(x, 0),
			ReLUDerivative = x -> x < 0 ? 0.0 : 1.0, 
			leakyReLUFunction = x -> x >= 0 ? x : x * 0.05,
			leakyReLUDerivative = x -> x >= 0 ? 1 : 0.05, 
			SigmoidFunction = x -> 1 / (Math.exp(-x) + 1),
			SigmoidDerivative = x -> {
				double sigma = SigmoidFunction.apply(x);
				return sigma * (1 - sigma);
			};

	public static final FunctionDerivativePair<Double> 
			ReLU = new FunctionDerivativePair<>(ReLUFunction,ReLUDerivative), 
			leakyReLU = new FunctionDerivativePair<>(leakyReLUFunction, leakyReLUDerivative),
			Sigmoid = new FunctionDerivativePair<>(SigmoidFunction, SigmoidDerivative);

	//@formatter:on

	public static void main(String[] args) {
//		Network network = new Network(leakyReLU, 0.001, new int[] { 4, 8, 2 });
//		network.init(0, 1);
//
//		double[][] inputs = { { 1, 0, 1, 0 }, { 1, 0, 0, 1 }, { 1, 0, 0, 1 }, { 0, 1, 0, 1 } };
//		double[][] outputs = { { 1, 0 }, { 0, 1 }, { 0, 1 }, { 1, 0 } };
//
//		network.checkGradient(inputs[0], outputs[0]);
//		network.train(inputs, outputs, 10000, true);
//
//		for (int i = 0; i < inputs.length; i++) {
//			System.out.println(Arrays.toString(network.calc(inputs[i])));
//		}

//		System.out.println(Arrays.toString(network.calc(new double[] { 0.9, 0 })));

		int bitCount = 16;

//		Network network = new Network(leakyReLU, 0.0001, new int[] { bitCount, 64, 64, bitCount });
//		network.init(-0.3, 0.3);
		FileInputStream fileInputStream;
		Network network = null;
		try {
			fileInputStream = new FileInputStream("test");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			network = (Network) objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		network.setActivationFunction(leakyReLU);

		Function<Double, Double> funct = a -> Math.pow(2, a);
		double[] input = { 3 };

		network.train(funct, 100000, 1, 0, 10, bitCount);

		for (int i = 0; i < 10; i++) {
			double[] in = Functions.toDouble(Functions.toBIN(i, bitCount));
			System.out.println("2^" + i + ": ");
			System.out.println(
					Functions.toDEX(Arrays.stream(network.calc(in)).mapToInt(e -> (int) Math.round(e)).toArray()));
		}
		FileOutputStream fstream;
		try {
			fstream = new FileOutputStream("test");
			ObjectOutputStream stream = new ObjectOutputStream(fstream);
			stream.writeObject(network);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private transient FunctionDerivativePair<Double> activationFunction;
	private double learningFactor;
	private int[] layer;
	private double[][] z, node, bias, biasGradient, derivative;
	private double[][][] weight, weightGradient;
	private int trainingSteps;

	public Network(FunctionDerivativePair<Double> activationFunction, double learningFactor, int... layer) {
		this.activationFunction = activationFunction;
		this.learningFactor = -learningFactor;
		this.layer = layer;
		z = new double[layer.length][];
		node = new double[layer.length][];
		bias = new double[layer.length][];
		biasGradient = new double[layer.length][];
		derivative = new double[layer.length][];
		weight = new double[layer.length][][];
		weightGradient = new double[layer.length][][];

		for (int i = 1; i < layer.length; i++) {
			z[i] = new double[layer[i]];
			node[i] = new double[layer[i]];
			bias[i] = new double[layer[i]];
			biasGradient[i] = new double[layer[i]];
			derivative[i] = new double[layer[i]];
			weight[i] = new double[layer[i]][layer[i - 1]];
			weightGradient[i] = new double[layer[i]][layer[i - 1]];
		}
		derivative[0] = new double[layer[0]];
	}

	public void init(Function<Integer, Double> initFunct) {
		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				bias[i][j] = initFunct.apply(layer[i - 1]);
				for (int k = 0; k < layer[i - 1]; k++) {
					weight[i][j][k] = initFunct.apply(layer[i]);
				}
			}
		}
	}

	public void init(double min, double max) {
		Random random = new Random();
		init(n -> random.nextDouble(min, max));
	}

	public void HeInit() {
		Random random = new Random();
		init(n -> {
			double sqrt = Math.sqrt(2.0 / n);

			return random.nextDouble(-sqrt, sqrt);
		});
	}

	public double[] calc(double[] input) {
		if (input.length != layer[0]) {
			throw new IllegalArgumentException("input is not the right size!");
		}
		node[0] = input;
		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				z[i][j] = bias[i][j] + Vector.dot(new Vector(weight[i][j]), new Vector(node[i - 1]));
				node[i][j] = activationFunction.apply(z[i][j]);
			}
		}
		return node[layer.length - 1];
	}

	private void backProp(double[] output) {
		for (int i = 0; i < layer[layer.length - 1]; i++) {
			derivative[layer.length - 1][i] = 2 * (node[layer.length - 1][i] - output[i]);
		}
		for (int i = layer.length - 2; i >= 0; i--) {
			for (int j = 0; j < layer[i]; j++) {
				derivative[i][j] = 0;
				for (int k = 0; k < layer[i + 1]; k++) {
					derivative[i][j] += derivative[i + 1][k] * activationFunction.derivative(z[i + 1][k])
							* weight[i + 1][k][j];
				}

			}
		}
	}

	private void clearGradient() {
		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				biasGradient[i][j] = 0;
				for (int k = 0; k < layer[i - 1]; k++) {
					weightGradient[i][j][k] = 0;
				}
			}
		}
		trainingSteps = 0;
	}

	private void computeGradient() {
		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				double temp = derivative[i][j] * activationFunction.derivative(z[i][j]);
				biasGradient[i][j] += temp;
				weightGradient[i][j] = Vector
						.add(new Vector(weightGradient[i][j]), Vector.mult(new Vector(node[i - 1]), temp)).getX();
			}
		}

		trainingSteps++;
	}

	private void applyGradient() {
		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				biasGradient[i][j] *= learningFactor / trainingSteps;
				bias[i][j] += biasGradient[i][j];
				biasGradient[i][j] = 0;
				for (int k = 0; k < layer[i - 1]; k++) {
					weightGradient[i][j][k] *= learningFactor / trainingSteps;
					weight[i][j][k] += weightGradient[i][j][k];
					weightGradient[i][j][k] = 0;
				}
			}
		}
		trainingSteps = 0;
	}

	public void train(double[] input, double[] expectedOutput) {
		if (expectedOutput.length != layer[layer.length - 1]) {
			throw new IllegalArgumentException("expectedOutput is not the right size!");
		}

		calc(input);
		backProp(expectedOutput);
		computeGradient();
	}

	public void train(double[][] inputs, double[][] labels, int repetitions, boolean shuffle) {
		if (inputs.length != labels.length) {
			throw new IllegalArgumentException("inputs and labels are different sizes!");
		}

		for (int i = 0; i < repetitions; i++) {
			List<Integer> indices = IntStream.range(0, inputs.length).boxed().toList();

			for (int j : indices) {
				train(inputs[j], labels[j]);
			}

			applyGradient();
		}
	}

	public void train(Function<Double, Double> f, int epochs, int repetitions, int min, int max, int size) {
		Random random = new Random();
		for (int i = 0; i < epochs; i++) {
			for (int k = 0; k < repetitions; k++) {
				int r = random.nextInt(min, max);
				double[] in = Functions.toDouble(Functions.toBIN(r, size));
				train(in, Functions.toDouble(Functions.toBIN((int) Math.round(f.apply((double) r)), size)));
			}
			applyGradient();
		}
	}

	private void checkGradient(double[] input, double[] output) {
		double epsilon = 1E-7;
		calc(input);
		backProp(output);
		clearGradient();
		computeGradient();

		for (int i = 1; i < layer.length; i++) {
			for (int j = 0; j < layer[i]; j++) {
				for (int k = 0; k < layer[i - 1]; k++) {
					double temp = weight[i][j][k];
					weight[i][j][k] = temp + epsilon;
					double costPlus = cost(calc(input), output);

					weight[i][j][k] = temp - epsilon;
					double costMinus = cost(calc(input), output);

					double derivative = (costPlus - costMinus) / (2 * epsilon);

					System.out.println(
							"network: " + weightGradient[i][j][k] + "  gradient checker: " + derivative + " \n");
				}
			}
		}
	}

	public double cost(double[] a, double[] b) {
		Vector c = Vector.sub(new Vector(a), new Vector(b));
		return Vector.dot(c, c);
	}

	public double[] add(double[] a, double[] b) {
		assert a.length == b.length;
		double[] erg = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			erg[i] = a[i] + b[i];
		}
		return erg;
	}

	// Getter und Setter

	public void setActivationFunction(FunctionDerivativePair<Double> activationFuction) {
		this.activationFunction = activationFuction;
	}

}
