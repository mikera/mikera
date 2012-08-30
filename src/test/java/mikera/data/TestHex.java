package mikera.data;

import static org.junit.Assert.assertEquals;
import mikera.engine.Hex;
import mikera.util.Rand;

import org.junit.Test;

public class TestHex {
	@Test public void testDirections() {
		for (int i=0; i<6 ; i++) {
			int dx=Hex.HEX_DX[i];
			int dy=Hex.HEX_DY[i];
			
			assertEquals(dx,Hex.dx(i+6*(Rand.r(21)-10)));
			assertEquals(dy,Hex.dy(i+6*(Rand.r(21)-10)));
			
			assertEquals(i,Hex.direction(dx, dy));
		}
	}

	@Test public void testDistance() {
		for (int j=1; j<=10; j++) {
			for (int i=0; i<6 ; i++) {
				int x=Rand.r(21)-10;
				int y=Rand.r(21)-10;
				int dx=Hex.HEX_DX[i];
				int dy=Hex.HEX_DY[i];
				assertEquals(j,Hex.distance(x,y,x+j*dx, y+j*dy));
			}
		}
	}	
	
}
