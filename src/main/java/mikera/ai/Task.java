package mikera.ai;

import mikera.annotations.Immutable;

/**
 * Generic AI task representation
 * 
 * Intended to be parameterised by actor and 
 * a task-specific parameter
 * 
 * Generate through Tasks.* static methods
 * 
 * @author Mike Anderson
 *
 * @param <T> Actor type
 * @param <P> Parameter type
 * @param <R> Result type
 */

@Immutable
public abstract class Task<T,P,R> {
	public abstract R run(T actor, P param);
}
