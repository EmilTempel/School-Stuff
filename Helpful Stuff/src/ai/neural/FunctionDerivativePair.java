package ai.neural;

import java.io.Serializable;
import java.util.function.Function;

public class FunctionDerivativePair<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Function<T, Double> funct, derivative;
	
	public FunctionDerivativePair(Function<T, Double> funct, Function<T, Double> derivative){
		this.funct = funct;
		this.derivative = derivative;
	}
	
	public double apply(T input) {
		return funct.apply(input);
	}
	
	public double derivative(T input) {
		return derivative.apply(input);
	}
}
