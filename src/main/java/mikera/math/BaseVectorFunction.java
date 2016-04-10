package mikera.math;

public abstract class BaseVectorFunction extends VectorFunction {
	int inputDimensions=0;
	int outputDimensions=1;
	
	public BaseVectorFunction() {
		
	}
	
	public BaseVectorFunction(int inputs, int outputs) {
		inputDimensions=inputs;
		outputDimensions=outputs;
	}
	
	@Override
	public int inputDimensions() {
		return inputDimensions;
	}

	@Override
	public int outputDimensions() {
		return outputDimensions;
	}

}
