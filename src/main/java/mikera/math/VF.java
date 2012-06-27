package mikera.math;

import mikera.util.Maths;

public class VF {

	public static VectorFunction noiseFunction(int inputDimensions, int outputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction() {
			@Override public int outputDimensions() {return outputDimensions;}
			public void calculate(Vector input, Vector output) {
				int isize=input.size();
				switch (isize) {
					case 1:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=PerlinScalar.noise(input.data[0], i);
						}
						return;
					case 2:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=PerlinScalar.noise(input.data[0],input.data[1], i);
						}
						return;
					case 3:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=PerlinScalar.noise(input.data[0],input.data[1], input.data[2], i);
						}
						return;
					case 4:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=PerlinScalar.noise(input.data[0]+2*i,input.data[1]-3*i, input.data[2]+10*i, input.data[3]-17*i);
						}
						return;

				}
				throw new Error("Unsupported noise input dimension: "+isize);
			}
		};
		vf.outputDimensions=outputDimensions;
		vf.inputDimensions=inputDimensions;
		return vf;
	}
	
	public static VectorFunction cloudFunction(int inputDimensions, int outputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction() {
			public final Noise noise=new Noise();
			public void calculate(Vector input, Vector output) {
				int isize=input.size();
				switch (isize) {
					case 1:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.clouds(input.data[0], i);
						}
						return;
					case 2:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.clouds(input.data[0],input.data[1], i);
						}
						return;
					case 3:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.clouds(input.data[0],input.data[1], input.data[2], i);
						}
						return;
					case 4:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.clouds(input.data[0]+29*i,input.data[1]-3*i, input.data[2]+10*i, input.data[3]-17*i);
						}
						return;

				}
				throw new Error("Unsupported noise input dimension: "+isize);
			}
		};
		vf.outputDimensions=outputDimensions;
		vf.inputDimensions=inputDimensions;
		return vf;
	}
	
	public static VectorFunction tiledCloudFunction(int inputDimensions, int outputDimensions, final int grid) {
		BaseVectorFunction vf=new BaseVectorFunction() {
			public final Noise noise=new Noise();
			public void calculate(Vector input, Vector output) {
				int isize=input.size();
				switch (isize) {
					case 1:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.tiledClouds(input.data[0], i, grid);
						}
						return;
					case 2:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.tiledClouds(input.data[0],input.data[1], i, grid);
						}
						return;
					case 3:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.tiledClouds(input.data[0],input.data[1], input.data[2], i, grid);
						}
						return;
					case 4:
						for (int i=0; i<outputDimensions; i++) {
							output.data[i]=noise.tiledClouds(input.data[0]+29*i,input.data[1]-3*i, input.data[2]+10*i, input.data[3]-17*i, grid);
						}
						return;

				}
				throw new Error("Unsupported noise input dimension: "+isize);
			}
		};
		vf.outputDimensions=outputDimensions;
		vf.inputDimensions=inputDimensions;
		return vf;
	}
	
	public static VectorFunction clamp(final VectorFunction f, final float min, final float max) {
		BaseVectorFunction vf=new BaseVectorFunction() {
			public void calculate(Vector input, Vector output) {
				f.calculate(input, output);
				for (int i=0; i<outputDimensions; i++) {
					output.data[i]=Maths.bound(min, input.data[i], max);
				}
			}
		};
		vf.inputDimensions=f.inputDimensions();
		vf.outputDimensions=f.outputDimensions();
		return vf;
	}
	
	public static VectorFunction takeComponents(final VectorFunction f, final int start, final int count) {
		BaseVectorFunction vf=new BaseVectorFunction() {
			public void calculate(Vector input, Vector output) {
				f.calculate(input, output);
				for (int i=0; i<count; i++) {
					output.data[i]=input.data[start+i];
				}
			}
		};		
		vf.inputDimensions=f.inputDimensions();
		vf.outputDimensions=count;
		return vf;
	}
	
	
	
	public static VectorFunction zeroExtendComponents(final VectorFunction f, int outputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction(f.inputDimensions(),outputDimensions) {
			final int od=f.outputDimensions();
			public void calculate(Vector input, Vector output) {
				f.calculate(input, output);
				for (int i=od; i<outputDimensions; i++) {
					output.data[i]=0;
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction fillComponents(final VectorFunction f, int outputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction(f.inputDimensions(),outputDimensions) {
			final int od=f.outputDimensions();
			public void calculate(Vector input, Vector output) {
				f.calculate(input, output);
				for (int i=0; i<outputDimensions; i++) {
					output.data[i]=input.data[i%od];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction add(final VectorFunction f1, final VectorFunction f2) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f1.outputDimensions());
			public void calculate(Vector input, Vector output) {
				int isize=input.size();
				if (temp.size()!=isize) temp=new Vector(isize);
				f1.calculate(input, temp);
				f2.calculate(input, output);
				for (int i=0; i<isize; i++) {
					output.data[i]+=temp.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction setComponent(final VectorFunction f1, final int component, final VectorFunction scalarFunction) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(1);
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, output);
				scalarFunction.calculate(input, temp);
				output.data[component]=temp.data[0];
			}
		};		
		return vf;
	}
	
	public static VectorFunction getComponent(final VectorFunction f1, final int component) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),1) {
			Vector temp=new Vector(f1.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, temp);
				output.data[0]=temp.data[component];
			}
		};		
		return vf;
	}
	
	public static VectorFunction setComponents(final VectorFunction f1, final int component, int count, final VectorFunction vectorFunction) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),count) {
			Vector temp=new Vector(vectorFunction.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, output);
				vectorFunction.calculate(input, temp);
				for (int i=0; i<outputDimensions; i++) {
					output.data[component+i]=temp.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction select(final VectorFunction f1, final VectorFunction f2, final VectorFunction f3) {
		BaseVectorFunction vf=new BaseVectorFunction(f2.inputDimensions(),f2.outputDimensions()) {
			Vector temp=new Vector(f1.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, temp);
				if (temp.data[0]>0) {
					f2.calculate(input, output);				
				} else {
					f3.calculate(input, output);						
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction min(final VectorFunction f1, double d) {
		Vector v=new Vector(f1.outputDimensions());
		v.fill((float)d);
		return min(f1,v);
	}

	public static VectorFunction min(final VectorFunction f1, Vector v) {
		return min(f1,constant(v));	
	}

	public static VectorFunction min(final VectorFunction f1, final VectorFunction f2) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f2.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f2.calculate(input, temp);
				f1.calculate(input, output);
				for (int i=0; i<outputDimensions; i++) {
					output.data[i]=Maths.min(output.data[i],temp.data[i]);
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction max(final VectorFunction f1, double d) {
		Vector v=new Vector(f1.outputDimensions());
		v.fill((float)d);
		return max(f1,v);
	}

	public static VectorFunction max(final VectorFunction f1, Vector v) {
		return max(f1,constant(v));	
	}

	public static VectorFunction max(final VectorFunction f1, final VectorFunction f2) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f2.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f2.calculate(input, temp);
				f1.calculate(input, output);
				for (int i=0; i<outputDimensions; i++) {
					output.data[i]=Maths.max(output.data[i],temp.data[i]);
				}
			}
		};		
		return vf;
	}

	public static VectorFunction madd(final VectorFunction f1, final VectorFunction f2, final double v) {
		BaseVectorFunction vf=new BaseVectorFunction(f2.inputDimensions(),f2.outputDimensions()) {
			Vector temp=new Vector(f2.outputDimensions());
			float factor=(float)v;
			public void calculate(Vector input, Vector output) {
				f2.calculate(input, temp);
				f1.calculate(input, output);
				for (int i=0; i<outputDimensions; i++) {
					output.data[i]+=factor*temp.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction add(final VectorFunction f1, double d) {
		Vector vector=new Vector(f1.outputDimensions());
		for (int i=0; i<vector.size(); i++) vector.data[i]=(float)d;
		return add(f1, vector);
	}
	
	public static VectorFunction add(final VectorFunction f1, final Vector v) {
		if (v.size()!=f1.outputDimensions()) throw new Error("Wrong vector size ["+v.size()+"] for function ["+f1.outputDimensions()+"]");
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			final Vector value=new Vector(v);
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, output);
				int isize=outputDimensions;
				for (int i=0; i<isize; i++) {
					output.data[i]+=value.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction muliply(final VectorFunction f1, final Vector v) {
		if (v.size()!=f1.outputDimensions()) throw new Error("Wrong vector size ["+v.size()+"] for function ["+f1.outputDimensions()+"]");
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			final Vector value=new Vector(v);
			public void calculate(Vector input, Vector output) {
				int isize=value.size();
				f1.calculate(input, output);
				for (int i=0; i<isize; i++) {
					output.data[i]*=value.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction multiply(final VectorFunction f1, final double v) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			final float value=(float)v;
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, output);
				int osize=output.size();
				for (int i=0; i<osize; i++) {
					output.data[i]*=value;
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction compose(final VectorFunction outer,final VectorFunction inner) {
		BaseVectorFunction vf=new BaseVectorFunction(inner.inputDimensions(),outer.outputDimensions()) {
			Vector temp=new Vector(inner.outputDimensions());
			public void calculate(Vector input, Vector output) {
				inner.calculate(input, temp);
				outer.calculate(temp, output);
			}
		};			
		return vf;
	}
	
	public static VectorFunction constant(final Vector v) {
		BaseVectorFunction vf=new BaseVectorFunction(0,v.size()) {
			final Vector value=new Vector(v);
			public void calculate(Vector input, Vector output) {
				int isize=outputDimensions;
				for (int i=0; i<isize; i++) {
					output.data[i]=value.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction sin(final float freq) {
		BaseVectorFunction vf=new BaseVectorFunction(1,1) {
			public void calculate(Vector input, Vector output) {
				output.data[0]=Maths.sin(input.data[0]*freq);
			}
		};		
		return vf;
	}
	
	public static VectorFunction cos(final float freq) {
		BaseVectorFunction vf=new BaseVectorFunction(1,1) {
			public void calculate(Vector input, Vector output) {
				output.data[0]=Maths.cos(input.data[0]*freq);
			}
		};		
		return vf;
	}
	
	public static VectorFunction dist(final int inputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction(inputDimensions,1) {
			public void calculate(Vector input, Vector output) {
				float result=0;
				for (int i=0; i<inputDimensions; i++) {
					float x=input.data[i];
					result+=x*x;
				}			
				output.data[0]=Maths.sqrt(result);
			}
		};		
		return vf;
	}
	
	public static VectorFunction hump(final int inputDimensions) {
		BaseVectorFunction vf=new BaseVectorFunction(inputDimensions,1) {
			public void calculate(Vector input, Vector output) {
				float result=0;
				for (int i=0; i<inputDimensions; i++) {
					float x=input.data[i];
					result+=x*x;
				}			
				output.data[0]=1.0f/(1.0f+result);
			}
		};		
		return vf;
	}
	
	public static VectorFunction mandelbrot(final float step, final float max) {
		BaseVectorFunction vf=new BaseVectorFunction(2,1) {
			public void calculate(Vector input, Vector output) {
				float result=0;
				float cx=input.data[0];
				float cy=input.data[1];
				float x=cx;
				float y=cy;
				while (result<max) {
					float tx=x;
					x=x*x-y*y+cx;
					y=2*tx*y+cy;
					result+=step;
					if ((x*x+y*y)>4) break;
				}			
				output.data[0]=result;
			}
		};		
		return vf;
	}
	
	public static VectorFunction concat(final VectorFunction f1, final VectorFunction f2) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()+f2.outputDimensions()) {
			final Vector temp=new Vector(f2.outputDimensions());
			public void calculate(Vector input, Vector output) {
				f1.calculate(input, output);
				f1.calculate(input, temp);
				int isize=f2.outputDimensions();
				int ioff=f1.outputDimensions();
				for (int i=0; i<isize; i++) {
					output.data[ioff+i]=temp.data[i];
				}
			}
		};		
		return vf;
	}
	

	
	public static VectorFunction multiply(final VectorFunction f1, final VectorFunction f2) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f1.outputDimensions());
			public void calculate(Vector input, Vector output) {
				int isize=input.size();
				if (temp.size()!=isize) temp=new Vector(isize);
				f1.calculate(input, temp);
				f2.calculate(input, output);
				for (int i=0; i<isize; i++) {
					output.data[i]*=temp.data[i];
				}
			}
		};		
		return vf;
	}
	
	public static VectorFunction perturb(final VectorFunction f1, final VectorFunction f2) {
		return perturb(f1,f2,1);
	}
	

	
	public static VectorFunction perturb(final VectorFunction f1, final VectorFunction f2, final double v) {
		if (f2.outputDimensions()!=f1.inputDimensions()) throw new Error("Wrong dimension ["+f2.outputDimensions()+"] for perturbation");
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			final float factor=(float)v;
			Vector temp=new Vector(f2.outputDimensions());
			public void calculate(Vector input, Vector output) {
				int isize=inputDimensions;
				f2.calculate(input, temp);
				for (int i=0; i<isize; i++) {
					temp.data[i]=temp.data[i]*factor+input.data[i];
				}
				f1.calculate(temp, output);
			}
		};		
		return vf;
	}
	
	public static VectorFunction offset(final VectorFunction f1, final Vector v) {
		if (v.size()!=f1.inputDimensions()) throw new Error("Wrong dimension ["+v.size()+"] for offset, expected "+f1.inputDimensions());
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector vector=new Vector(v);
			Vector temp=new Vector(f1.inputDimensions());
			public void calculate(Vector input, Vector output) {
				int isize=inputDimensions;
				for (int i=0; i<isize; i++) {
					temp.data[i]=input.data[i]+vector.data[i];
				}
				f1.calculate(temp, output);
			}
		};		
		return vf;
	}
	
	public static VectorFunction scale(final VectorFunction f1, final Vector v) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f1.inputDimensions());
			Vector scaleFactor=new Vector(v);
			public void calculate(Vector input, Vector output) {
				int isize=inputDimensions;
				for (int i=0; i<isize; i++) {
					temp.data[i]=input.data[i]*scaleFactor.data[i];
				}
				f1.calculate(temp, output);
			}
		};		
		return vf;
	}
	
	public static VectorFunction scale(final VectorFunction f1, final double v) {
		BaseVectorFunction vf=new BaseVectorFunction(f1.inputDimensions(),f1.outputDimensions()) {
			Vector temp=new Vector(f1.inputDimensions());
			float factor=(float)v;
			public void calculate(Vector input, Vector output) {
				int isize=inputDimensions;
				for (int i=0; i<isize; i++) {
					temp.data[i]=input.data[i]*factor;
				}
				f1.calculate(temp, output);
			}
		};		
		return vf;
	}

}
