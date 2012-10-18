package mikera.ai;

import static org.junit.Assert.assertTrue;
import mikera.ai.Task;
import mikera.ai.Tasks;
import mikera.engine.Dir;
import mikera.engine.TreeGrid;

import org.junit.Test;

public class TestTask {
	protected final TreeGrid<Integer> map=new TreeGrid<Integer>();
	
	protected static class State {
		byte dir=Dir.N;		
		boolean halted=false;
	}
	
	protected static class Actor {
		int x=0;
		int y=0;
		int z=0;
	}
	
	protected Task<Actor,State,Integer> moveTask=new Task<Actor,State,Integer>() {
		@Override
		public Integer run(Actor actor, State state) {
			actor.x+=Dir.dx(state.dir);
			actor.y+=Dir.dy(state.dir);
			actor.z+=Dir.dz(state.dir);
			return null;
		}	
	};
	
	protected Task<Actor,State,Integer> halt=new Task<Actor,State,Integer>() {
		@Override
		public Integer run(Actor actor, State state) {
			state.halted=true;
			return Integer.valueOf(-1);
		}	
	};
	
	protected Task<Actor,State,Integer> sense=new Task<Actor,State,Integer>() {
		@Override
		public Integer run(Actor actor, State state) {
			return map.get(actor.x,actor.y,actor.z);
		}	
	};
	
	protected Task<Actor,Integer,Integer> testNegative=new Task<Actor,Integer,Integer>() {
		@Override
		public Integer run(Actor actor, Integer value) {
			if ((value!=null)&&(value<0)) return value;
			return null;
		}	
	};
	
	@Test public void testTask() {
		map.clear();
		map.set(0,10,0,-1); // end point
		
		State state=new State();
		Actor actor=new Actor();
		Task<Actor,State,Integer> task=Tasks.test(
				Tasks.chain(sense,testNegative),
				halt,
				moveTask
		);
		
		int iterations=0;
		
		while ((!state.halted)&&(iterations++<10000)) {
			task.run(actor, state);
		}
		
		assertTrue(state.halted);
	}
}
