package mikera.ai;

public abstract class PriorityFunction<T, P> {

	public abstract double getPriority(T actor, P param);
}
