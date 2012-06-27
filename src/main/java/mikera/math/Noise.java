package mikera.math;

import mikera.util.Maths;
import mikera.util.Rand;

/**
 * Mike Anderson's implementation of value based noise
 * 
 * @author Mike Anderson
 *
 */
public class Noise {
	public static final float FLOAT_FACTOR=1.0f/(-Integer.MIN_VALUE);
	public int SEED=Rand.nextInt();
	
	public static final int CLOUD_OFFSET=101;
	public static final int CLOUD_OCTAVES=8;
	
	public float clouds(float x) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=noise(x*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float tiledClouds(float x, int grid) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=tiledNoise(x*factor, grid*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float clouds(float x, float y) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=noise(x*factor,y*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float tiledClouds(float x, float y, int grid) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=tiledNoise(x*factor,y*factor, grid*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float clouds(float x, float y, float z) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=noise(x*factor,y*factor,z*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			z+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float tiledClouds(float x, float y, float z, int grid) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=tiledNoise(x*factor,y*factor,z*factor, grid*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			z+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float clouds(float x, float y, float z, float t) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=noise(x*factor,y*factor,z*factor,t*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			z+=0.25f/factor;
			t+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	
	public float tiledClouds(float x, float y, float z, float t, int grid) {
		float result=0;
		int factor=1;
		for (int i=0; i<CLOUD_OCTAVES; i++) {
			result+=tiledNoise(x*factor,y*factor,z*factor,t*factor, grid*factor)/factor;
			x+=0.25f/factor+CLOUD_OFFSET;
			y+=0.25f/factor;
			z+=0.25f/factor;
			t+=0.25f/factor;
			factor*=2;
		}
		return 0.5f+result*0.25f;
	}
	

	
	public float noise(float x) {
		int ix=Maths.floor(x);
		return noiseLocal(x-ix,ix);
	}
	
	public float tiledNoise(float x, int grid) {
		int ix=Maths.floor(x);
		return noiseLocal(x-ix,Maths.mod(ix,grid));
	}
		
	private float noiseLocal(float x, int ix) {
		float v0=gridValue(ix);
		float v1=gridValue(ix+1);
		float fx=Maths.smoothFactor(x);
		return Maths.lerp(fx, v0, v1);
	}
	
	public float noise(float x, float y) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		return noiseLocal(x-ix,y-iy,ix,iy);
	}
	
	public float tiledNoise(float x, float y,int grid) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		return noiseLocal(x-ix,y-iy,Maths.mod(ix,grid),Maths.mod(iy,grid));
	}


	private float noiseLocal(float x, float y, int ix, int iy) {
		float v00=gridValue(ix,iy);
		float v01=gridValue(ix+1,iy);
		float v10=gridValue(ix,iy+1);
		float v11=gridValue(ix+1,iy+1);
		float fx=Maths.smoothFactor(x);
		float fy=Maths.smoothFactor(y);
		return Maths.lerp(fy, Maths.lerp(fx, v00,v01), Maths.lerp(fx, v10,v11));
	}
	
	public float noise(float x, float y, float z) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		int iz=Maths.floor(z);
		return noiseLocal(x-ix,y-iy,z-iz,ix,iy,iz);
	}

	public float tiledNoise(float x, float y, float z,int grid) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		int iz=Maths.floor(z);
		return noiseLocal(x-ix,y-iy, z-iz,Maths.mod(ix,grid),Maths.mod(iy,grid),Maths.mod(iz,grid));
	}

	private float noiseLocal(float x, float y, float z, int ix, int iy, int iz) {
		float v000=gridValue(ix,iy,iz);
		float v001=gridValue(ix+1,iy,iz);
		float v010=gridValue(ix,iy+1,iz);
		float v011=gridValue(ix+1,iy+1,iz);
		float v100=gridValue(ix,iy,iz+1);
		float v101=gridValue(ix+1,iy,iz+1);
		float v110=gridValue(ix,iy+1,iz+1);
		float v111=gridValue(ix+1,iy+1,iz+1);
		float fx=Maths.smoothFactor(x);
		float fy=Maths.smoothFactor(y);
		float fz=Maths.smoothFactor(z);
		return Maths.lerp(fz,
				Maths.lerp(fy, Maths.lerp(fx, v000,v001), Maths.lerp(fx, v010,v011)),
				Maths.lerp(fy, Maths.lerp(fx, v100,v101), Maths.lerp(fx, v110,v111)));
	}
	
	public float noise(float x, float y, float z, float t) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		int iz=Maths.floor(z);
		int it=Maths.floor(t);
		return noiseLocal(x-ix,y-iy,z-iz,t-it,ix,iy,iz,it);
	}
	
	public float tiledNoise(float x, float y, float z, float t, int grid) {
		int ix=Maths.floor(x);
		int iy=Maths.floor(y);
		int iz=Maths.floor(z);
		int it=Maths.floor(z);
		return noiseLocal(x-ix,y-iy, z-iz, t-it,Maths.mod(ix,grid),Maths.mod(iy,grid),Maths.mod(iz,grid),Maths.mod(it,grid));
	}

	
	private float noiseLocal(float x, float y, float z, float t, int ix, int iy, int iz, int it) {
		float v0000=gridValue(ix,iy,iz,it);
		float v0001=gridValue(ix+1,iy,iz,it);
		float v0010=gridValue(ix,iy+1,iz,it);
		float v0011=gridValue(ix+1,iy+1,iz,it);
		float v0100=gridValue(ix,iy,iz+1,it);
		float v0101=gridValue(ix+1,iy,iz+1,it);
		float v0110=gridValue(ix,iy+1,iz+1,it);
		float v0111=gridValue(ix+1,iy+1,iz+1,it);
		float v1000=gridValue(ix,iy,iz,it+1);
		float v1001=gridValue(ix+1,iy,iz,it+1);
		float v1010=gridValue(ix,iy+1,iz,it+1);
		float v1011=gridValue(ix+1,iy+1,iz,it+1);
		float v1100=gridValue(ix,iy,iz+1,it+1);
		float v1101=gridValue(ix+1,iy,iz+1,it+1);
		float v1110=gridValue(ix,iy+1,iz+1,it+1);
		float v1111=gridValue(ix+1,iy+1,iz+1,it+1);
		float fx=Maths.smoothFactor(x);
		float fy=Maths.smoothFactor(y);
		float fz=Maths.smoothFactor(z);
		float ft=Maths.smoothFactor(t);
		return Maths.lerp(ft, 
				Maths.lerp(fz,
					Maths.lerp(fy, Maths.lerp(fx, v0000,v0001), Maths.lerp(fx, v0010,v0011)),
					Maths.lerp(fy, Maths.lerp(fx, v0100,v0101), Maths.lerp(fx, v0110,v0111))),
				Maths.lerp(fz,
					Maths.lerp(fy, Maths.lerp(fx, v1000,v1001), Maths.lerp(fx, v1010,v1011)),
					Maths.lerp(fy, Maths.lerp(fx, v1100,v1101), Maths.lerp(fx, v1110,v1111))));
	}
	

	//*******************************************************
	// grid value functions
	//
	// return float between -1 and 1 for each grid coordinate
	
	public float gridValue(int ix) {
		ix*=0x12345678;
		int v=SEED+ix;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValue(int ix, int iy) {
		ix*=0x12345678;
		iy*=0x87654321;
		int v=SEED+ix+iy;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValue(int ix, int iy, int iz) {
		ix*=0x12345678;
		iy*=0x87654321;
		iz*=0x84736251;
		int v=SEED+ix+iy+iz;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValue(int ix, int iy, int iz, int it) {
		ix*=0x12345678;
		iy*=0x87654321;
		iz*=0x84736251;
		it*=0x15263748;
		int v=SEED+ix+iy+iz+it;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValueTiled(int ix, int sx) {
		ix=Maths.mod(ix,sx);
		ix*=0x12345678;
		int v=SEED+ix;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValueTiled(int ix, int iy, int sx, int sy) {
		ix=Maths.mod(ix,sx);
		iy=Maths.mod(iy,sy);
		ix*=0x12345678;
		iy*=0x87654321;
		int v=SEED+ix+iy;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValueTiled(int ix, int iy, int iz, int sx, int sy, int sz) {
		ix=Maths.mod(ix,sx);
		iy=Maths.mod(iy,sy);
		iz=Maths.mod(iz,sz);
		ix*=0x12345678;
		iy*=0x87654321;
		iz*=0x84736251;
		int v=SEED+ix+iy+iz;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
	
	public float gridValueTiled(int ix, int iy, int iz, int it, int sx, int sy, int sz, int st) {
		ix=Maths.mod(ix,sx);
		iy=Maths.mod(iy,sy);
		iz=Maths.mod(iz,sz);
		it=Maths.mod(it,st);
		ix*=0x12345678;
		iy*=0x87654321;
		iz*=0x84736251;
		it*=0x15263748;
		int v=SEED+ix+iy+iz+it;
		v= Rand.xorShift32(v);
		return v*FLOAT_FACTOR;
	}
}
