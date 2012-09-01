package mikera.ai;

import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;
import mikera.persistent.PersistentMap;

/**
 * Generic generator functions for tasks
 * 
 * Tasks should be persistent and immutable
 * to allow concurrent sharing by actors
 * 
 * @author Mike Anderson
 *
 */
public class Tasks {
	@SuppressWarnings("rawtypes")
	private static final Task<?,?,?> NULL_TASK = new Task() {
		@Override
		public Object run(Object actor, Object param) {
			return null;
		}
	};

	public static <T,P,R> Task<T,P,R>  select(
			final PersistentList<Task<T,P,R>> ts) 
	{
		return new Task<T,P,R>() {

			@Override
			public R run(T actor, P param) {
				int n=ts.size();
				for (int i=0; i<n; i++) {
					R result=ts.get(i).run(actor, param);
					if (result!=null) return result;
				}
				return null;
			}
			
		};
	}
	
	public static <T,P,R> Task<T,P,R>  lookup(
			final PersistentMap<P,Task<T,P,R>> ts) 
	{
		return new Task<T,P,R>() {
			@Override
			public R run(T actor, P param) {
				Task<T,P,R> task=ts.get(param);
				return task.run(actor, param);
			}
		};
	}
	
	public static <T,P,R> Task<T,P,R>  prioritise(
			final PersistentList<PriorityFunction<T,P>> ps,
			final PersistentList<Task<T,P,R>> ts) 
	{
		if (ps.size()!=ts.size()) throw new Error("Wrong size: priorities="+ps.size()+" tasks="+ts.size());
		return new Task<T,P,R>() {
			private final int n=ts.size();

			@Override
			public R run(T actor, P param) {
				Task<T,P,R> bestTask=null;
				double bestPriority=-Double.MAX_VALUE;
				for (int i=0; i<n; i++) {
					double p=ps.get(i).getPriority(actor, param);
					if (p>bestPriority) {
						bestTask=ts.get(i);
						bestPriority=p;
					}
				}
				if (bestTask==null) return null;
				return bestTask.run(actor, param);
			}
			
		};
	}
	
	@SuppressWarnings("unchecked")
	public static <T,P,R> Task<T,P,R> nullTask() {
		return (Task<T,P,R>) NULL_TASK;
	}
	
	public static <T,P,R> Task<T,P,R>  select(
			final Task<T,P,R> a, Task<T,P,R> b) {
		PersistentList<Task<T,P,R>> ts=ListFactory.create(a,b);
		return select(ts);
	}
	
	public static <T,P,R> Task<T,P,R>  sequence(
			final PersistentList<Task<T,P,R>> ts) 
	{
		return new Task<T,P,R>() {
			@Override
			public R run(T actor, P param) {
				int n=ts.size();
				R result=null;
				for (int i=0; i<n; i++) {
					result=ts.get(i).run(actor, param);
					if (result==null) return null;
				}
				return result;
			}
		};
	}
	
	public static <T,P,R> Task<T,P,R>  sequence(
			final Task<T,P,R> a, Task<T,P,R> b) {
		PersistentList<Task<T,P,R>> ts=ListFactory.create(a,b);
		return sequence(ts);
	}
	
	public static <T,PR> Task<T,PR,PR> chain(
			final PersistentList<Task<T,PR,PR>> ts) 
	{
		return new Task<T,PR,PR>() {

			@Override
			public PR run(T actor, PR param) {
				int n=ts.size();
				for (int i=0; i<n; i++) {
					param=ts.get(i).run(actor, param);
				}
				return param;
			}
		};
	}
	
	public static <T,P,P2,R> Task<T,P,R> chain(
			final Task<T,P,P2> t1,
			final Task<T,P2,R> t2) 
	{
		return new Task<T,P,R>() {

			@Override
			public R run(T actor, P param) {
				P2 p2=t1.run(actor,param);
				return t2.run(actor, p2);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	public static <T,P,R1,R2> Task<T,P,R2> test(
			final Task<T,P,R1> cond,
			Task<T,P,R2> ta,
			Task<T,P,R2> tb) 
	{
		// allow for null parameters
		final Task<T,P,R2> a=(ta==null)?(Task<T,P,R2>)NULL_TASK:ta;
		final Task<T,P,R2> b=(tb==null)?(Task<T,P,R2>)NULL_TASK:tb;
		
		return new Task<T,P,R2>() {
			@Override
			public R2 run(T actor, P param) {
				R1 test=cond.run(actor, param);
				if (test!=null) {
					return a.run(actor, param);
				} else {
					return b.run(actor, param);
				}
			}
		};
	}
	
	

}
