package mikera.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import mikera.util.emptyobjects.NullArrays;
import mikera.util.mathz.FloatMaths;

/**
 * Miscelleneous array utulities
 * @author Mike
 */
public class Arrays {
	public static final float[] NULL_FLOATS=NullArrays.NULL_FLOATS;
	public static final int[] NULL_INTS=NullArrays.NULL_INTS;
	public static final byte[] NULL_BYTES=NullArrays.NULL_BYTES;
	public static final double[] NULL_DOUBLES=NullArrays.NULL_DOUBLES;
	
	/**
	 * Returns true if an array is sorted.
	 */
	public static <T extends Comparable<? super T>> boolean isSorted(T[] a, int start, int end) {
		while (start<end) {
			if (a[start].compareTo(a[start+1])>0) return false;
			start++;
		}
		return true;
	}
	
	/**
	 * Returns true if an array is sorted.
	 */
	public static <T extends Comparable<? super T>> boolean isSorted(List<T> a) {
		int length=a.size();
		if (length<=1) return true;
		
		int i=1;
		T previous=a.get(0);
		while (i<length) {
			T current=a.get(i++);
			if (previous.compareTo(current)>0) return false;
			previous=current;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] insertElement(T[] array, T value, int i) {
		int count=array.length;
		T[] newarray=(T[]) Array.newInstance(array.getClass().getComponentType(), count+1);
		
		System.arraycopy(array, 0, newarray, 0, i);
		newarray[i]=value;
		System.arraycopy(array, i, newarray, i+1, count-i);

		return newarray;
	}
	

	public static float[] insertElement(float[] array, float value, int i) {
		int count=array.length;
		float[] newarray=new float[count+1];
		
		System.arraycopy(array, 0, newarray, 0, i);
		newarray[i]=value;
		System.arraycopy(array, i, newarray, i+1, count-i);

		return newarray;
	}
	
	public static <T> T[] deleteElement(T[] array, int i) {
		int count=array.length;
		@SuppressWarnings("unchecked")
		T[] newarray=(T[]) Array.newInstance(array.getClass().getComponentType(), count-1);
		
		System.arraycopy(array, 0, newarray, 0, i);
		System.arraycopy(array, i+1, newarray, i, count-i-1);

		return newarray;
	}
	
	public static float[] deleteElement(float[] array, int i) {
		int count=array.length;
		float[] newarray=new float[count-1];
		
		System.arraycopy(array, 0, newarray, 0, i);
		System.arraycopy(array, i+1, newarray, i, count-i-1);

		return newarray;
	}
	
	public static void swap(int[] data, int a, int b) {
		int t=data[a];
		data[a]=data[b];
		data[b]=t;
	}
	
	/**
	 * Removes duplicate values from a sorted array of ints
	 * 
	 * Returns an array containing the deduplicated values
	 * 
	 * Destroys the original array whenever duplicates are found
	 * 
	 * @param sortedData
	 * @return
	 */
	public static int[] deduplicate(int[] sortedData) {
		int di=0;
		int si=1;
		while (si<sortedData.length) {
			int v=sortedData[si];
			if (sortedData[di]==v) {
				si++;
			} else {
				sortedData[di+1]=v;
				di++;
				si++;
			}
		}
		di++;
		if (di<sortedData.length) {
			int[] ndata=new int[di];
			System.arraycopy(sortedData, 0, ndata, 0, di);
			return ndata;
		}
		return sortedData;
	}
	
	public static void boundToRange(double[] data, double min, double max) {
		for (int i=0; i<data.length; i++) {
			data[i]=Maths.bound(min, data[i], max);
		}
	}
	
	public static float squareDistance(float[] a, float[] b) {
		float eSquared=0;
		for (int i=0; (i<a.length); i++) {
			float d=a[i]-b[i];
			eSquared+=d*d;
		}
		return eSquared;
	}
	
	public static void fillRandom(float[] a) {
		fillRandom(a,0,a.length);
	}
	
	public static void fillRandom(float[] a, int start, int length) {
		for (int i=0; i<length; i++) {
			a[start+i]=Rand.nextFloat();
		}
	}
	
	public static void fillRandom(double[] a) {
		fillRandom(a,0,a.length);
	}
	
	public static void fillRandom(double[] a, int start, int length) {
		for (int i=0; i<length; i++) {
			a[start+i]=Rand.nextDouble();
		}
	}
	
	public static void mergeCopy(double[] src, int srcOffset, double[] dest, int destOffset, int length,double proportion) {
		double keep=1.0-proportion;
		for (int i=0; i<length; i++) {
			double d=dest[destOffset+i]*keep+src[srcOffset+i]*proportion;
			dest[destOffset+i]=d;
		}
	}
	
	public static void mergeLinear(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			double dv=dst[i];
			double sv=src[i];			
			dst[i]=( sv*srcProportion) + (dv*dstProportion);	
		}
	}
	
	public static void mergeRandomly(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			if (Rand.chance(srcProportion)) dst[i]=src[i];	
		}
	}
	
	public static void mergeInterleave(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<(int)(length*srcProportion); i++) {
			dst[i]=src[i];	
		}
	}
	
	public static void mergeTanhSourceCertainty(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			if (Rand.chance(srcProportion)) {
				double dv=dst[i];
				double sv=src[i];			
				double sourceCertainty=sv*sv;
				dst[i]=sourceCertainty*sv+(1-sourceCertainty)*dv;
			}
		}
	}
	
	public static void mergeTanhCertainty(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			if (Rand.chance(srcProportion)) {
				double dv=dst[i];
				double sv=src[i];			
				double sourceCertainty=sv*sv*srcProportion;
				double destCertainty=sv*sv*dstProportion;
				dst[i]=(sourceCertainty*sv+destCertainty*dv)/(sourceCertainty+destCertainty);
			}
		}
	}
	
	public static void mergeProbabilities(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			double dv=dst[i];
			double sv=src[i];			
			
			double result= Math.sqrt(dv*sv) / ( Math.sqrt( dv*sv ) + Math.sqrt((1-dv)*(1-sv)) );
			dst[i]=result;	
		}
	}
	
	public static void mergeGeometric(double[] src, double[] dst, int length, double srcProportion, double dstProportion) {
		for (int i=0; i<length; i++) {
			double dv=dst[i];
			double sv=src[i];
						
			if (dstProportion<=0.0) {
				dst[i]=Math.pow(sv,srcProportion);
			} else if (srcProportion<=0.0){
				dst[i]=Math.pow(dv,dstProportion);
			} else {
				dst[i]=Math.pow(sv,srcProportion)*Math.pow(dv,dstProportion);
			}		
		}
	}

	public static <T> void swap(List<T> a, int x, int y) {
		T t=a.get(x);
		a.set(x,a.get(y));
		a.set(y,t);
	}
	
	public static <T> void swap(ArrayList<T> a, int x, int y) {
		T t=a.get(x);
		a.set(x,a.get(y));
		a.set(y,t);
	}
	
	public static <T> void swap(T[] a, int x, int y) {
		T t=a[x];
		a[x]=a[y];
		a[y]=t;
	}


	public static <T extends Comparable<? super T>> void mergeInOrder(T[] src, T[] dst, int p1, int p2, int p3, int p4) {
		if (src[p2].compareTo(src[p3])<=0) return; // already sorted!
		
		// cut away ends
		while (src[p1].compareTo(src[p3])<=0) p1++;
		while (src[p2].compareTo(src[p4])<=0) p4--;
		
		int i1=p1;
		int i3=p3;
		int di=p1;
		while(di<p4) {
			if (src[i1].compareTo(src[i3])<=0) {
				dst[di++]=src[i1++];
			} else {
				dst[di++]=src[i3++];
				if (i3>p4) {
					System.arraycopy(src,i1,dst,di,p2-i1+1);
					break;
				}
			}
		}
		
		System.arraycopy(dst, p1, src, p1, (p4-p1)+1);
	}

	public static <T extends Comparable<? super T>> void mergeSort(T[] src, T[] dst, int start, int end) {
		if (start+1>=end) {
			if (start>=end) return;
			if (src[start].compareTo(src[end])>0) {
				swap(src,start,end);
			}
			return;
		}
		
		int middle=(start+end)/2;
		mergeSort(src,dst,start, middle);
		mergeSort(src,dst,middle+1, end);
		mergeInOrder(src,dst,start,middle,middle+1,end);
	}
	
	private static ThreadLocal<Comparable<?>[]> mergeSortTemp=new ThreadLocal<Comparable<?>[]>();
	
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> void mergeSort(T[] src) {
		int length=src.length;
		Comparable<?>[] temp=mergeSortTemp.get();
		if ((temp==null)||(temp.length<length)) {
			temp=new Comparable[length*3/2];
			mergeSortTemp.set(temp);
		}
		mergeSort(src,(T[])temp,0,length-1);
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> al=new ArrayList<Integer>();
		System.out.println(Arrays.isSorted(al));
		al.add(1);
		System.out.println(Arrays.isSorted(al));
		al.add(2);
		System.out.println(Arrays.isSorted(al));
		al.add(10);
		System.out.println(Arrays.isSorted(al));
		al.add(3);
		System.out.println(Arrays.isSorted(al));	
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] resize(T[] array, int newsize) {
		int len=array.length;
		T[] newarray=(T[]) Array.newInstance(array.getClass().getComponentType(), newsize);
		System.arraycopy(array, 0, newarray, 0, Maths.min(newsize,len));
		return newarray;
	}

	public static void zeroFill(float[] array) {
		java.util.Arrays.fill(array, 0);
	}
	
	public static void zeroFill(double[] array) {
		java.util.Arrays.fill(array, 0);
	}

	public static void add(float[] dest, float[] src) {
		for (int i=0; i<src.length; i++) {
			dest[i]+=src[i];
		}
	}
	
	public static void add(double[] dest, double[] src) {
		for (int i=0; i<src.length; i++) {
			dest[i]+=src[i];
		}
	}
	
	public static void sub(double[] dest, double[] src, int length) {
		for (int i=0; i<length; i++) {
			dest[i]-=src[i];
		}
	}

	
	public static void add(float[] src, float[] dest, float factor) {
		for (int i=0; i<src.length; i++) {
			dest[i]+=src[i]*factor;
		}
	}
	
	public static void add(double[] src, double[] dest, double factor) {
		for (int i=0; i<src.length; i++) {
			dest[i]+=src[i]*factor;
		}
	}
	
	public static void addWeighted(double[] src, double srcFactor, double[] dest, double destFactor) {
		addWeighted(src,srcFactor,dest,destFactor,Maths.min(src.length, dest.length));
	}
	
	public static void addWeighted(double[] src, double srcFactor, double[] dest, double destFactor, int length) {
		for (int i=0; i<length; i++) {
			dest[i]= dest[i]*destFactor + src[i]*srcFactor;
		}
	}
	
	public static void addMultiple(float[] src, int srcOffset, float[] dest, int destOffset, int length, float factor) {
		for (int i=0; i<length; i++) {
			dest[i+destOffset]+=src[i+srcOffset]*factor;
		}
	}
	
	public static void addMultiple(double[] src, int srcOffset, double[] dest, int destOffset, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[i+destOffset]+=src[i+srcOffset]*factor;
		}
	}
	
	public static void multiply(float[] array, float factor) {
		for (int i=0; i<array.length; i++) {
			array[i]*=factor;
		}
	}
	
	public static void multiply(double[] array, double factor) {
		for (int i=0; i<array.length; i++) {
			array[i]*=factor;
		}
	}
	
	public static void multiply(double[] array, int length, double factor) {
		for (int i=0; i<length; i++) {
			array[i]*=factor;
		}
	}

	public static void add(float[] array, float value) {
		for (int i=0; i<array.length; i++) {
			array[i]+=value;
		}
	}

	public static boolean checkRange(float[] array, double min, double max) {
		for (int i=0; i<array.length; i++) {
			float v=array[i];
			if ((v<min)||(v>max)) return false;
		}
		return true;
	}
	
	public static boolean checkRange(double[] array, double min, double max) {
		for (int i=0; i<array.length; i++) {
			double v=array[i];
			if ((v<min)||(v>max)) return false;
		}
		return true;
	}

	public static void applySigmoid(float[] data) {
		applySigmoid(data,0,data.length);
	}
	
	public static void applySigmoid(double[] data) {
		applySigmoid(data,0,data.length);
	}
	
	public static void applySigmoid(float[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=(float) Maths.logistic(data[i]);
		}
	}
	
	public static void applySigmoid(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=Maths.logistic(data[i]);
		}
	}
	
	public static void applySoftplus(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=Maths.softplus(data[i]);
		}
	}
	
	public static void applySigmoid(float[] data, int offset, int length, float gain) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=(float) Maths.logistic(data[i]*gain);
		}
	}
	
	public static void applySigmoid(double[] data, int offset, int length, double gain) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=Maths.logistic(data[i]*gain);
		}
	}
	
	public static void applyTanh(float[] data) {
		applyTanh(data,0,data.length);
	}
	
	public static void applyTanh(float[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=FloatMaths.tanh(data[i]);
		}
	}
	
	public static void applyTanh(double[] data) {
		applyTanh(data,0,data.length);
	}
	
	public static void applyTanh(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=Math.tanh(data[i]);
		}
	}
	
	public static void applyTanhScaled(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			data[i]=Maths.tanhScaled(data[i]);
		}
	}
	
	public static void applyStochasticSigmoid(float[] data) {
		applyStochasticSigmoid(data,0,data.length);	
	}


	public static void applyStochasticSigmoid(float[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			float v=data[i];
			if (v<=-30f) {
				data[i]=0.0f;
			} else if (v>=30f) {
				data[i]=1.0f;
			} else {
				data[i]=Rand.nextFloat()<Maths.logistic(v)?1:0;
			}
		}	
	}
	
	public static void applyStochasticSigmoid(double[] data, int offset,
			int length, double gain) {
		for (int i=offset; i<(offset+length); i++) {
			double v=data[i]*gain;
			if (v<=-30f) {
				data[i]=0.0f;
			} else if (v>=30f) {
				data[i]=1.0f;
			} else {
				data[i]=Rand.nextDouble()<Maths.logistic(v)?1:0;
			}
		}	
	}
	
	public static void applyStochasticSigmoid(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			double v=data[i];
			if (v<=-30f) {
				data[i]=0.0;
			} else if (v>=30) {
				data[i]=1.0;
			} else {
				data[i]=(Rand.nextDouble()<Maths.logistic(v))?1:0;
			}
		}	
	}
	
	public static void applyStochasticBinary(float[] data) {
		applyStochasticBinary(data,0,data.length);
	}

	public static void applyStochasticBinary(float[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			float v=data[i];
			if (v<=0.0f) {
				data[i]=0.0f;
			} else if (v>=1.0f) {
				data[i]=1.0f;
			} else {
				data[i]=Rand.nextFloat()<v?1:0;
			}
		}
	}
	
	public static void applyStochasticBinary(double[] data) {
		applyStochasticBinary(data,0,data.length);
	}

	public static void applyStochasticBinary(double[] data, int offset, int length) {
		for (int i=offset; i<(offset+length); i++) {
			double v=data[i];
			if (v<=0.0f) {
				data[i]=0.0;
			} else if (v>=1.0) {
				data[i]=1.0;
			} else {
				data[i]=Rand.nextDouble()<v?1:0;
			}
		}
	}

	public static double squaredError(float[] output, float[] result) {
		double err=0;
		for (int i=0; i<output.length; i++) {
			double d=output[i]-result[i];
			err+=d*d;
		}
		return err;
	}
	
	public static double squaredError(double[] output, double[] result) {
		double err=0;
		for (int i=0; i<output.length; i++) {
			double d=output[i]-result[i];
			err+=d*d;
		}
		return err;
	}

	public static void bitsToFloatArray(long val, float[] data, int length) {
		for (int i=0; i<length; i++) {
			data[i]=((val&1)==0)?0.0f:1.0f;
			val = val>>1;
		}
	}
	
	public static void bitsToDoubleArray(long val, double[] data, int length) {
		for (int i=0; i<length; i++) {
			data[i]=((val&1)==0)?0.0:1.0;
			val = val>>1;
		}
	}
	
	/**
	 * Converts an integer to a float array encoding of the integer class number,
	 * i.e. 0 for all values other then the class number, 1 for the correct class
	 * 
	 * @param val
	 * @param data
	 * @param length
	 */
	public static void intToClassArray(int classValue, float[] data, int length) {
		for (int i=0; i<length; i++) {
			data[i]=(classValue==i)?0.0f:1.0f;
		}
	}

	public static boolean contains(int[] array, int value) {
		for (int i=0; i<array.length; i++) {
			if (array[i]==value) return true;
		}
		return false;
	}

	public static void shuffle(int[] is) {
		for (int i=is.length-1; i>=1; i--) {
			int j=Rand.r(i+1);
			if (i!=j) {
				int t=is[i];		
				is[i]=is[j];
				is[j]=t;
			}
		}
	}

	public static void addConstant(int[] arr, int value) {
		for (int i=0; i<arr.length ; i++) {
			arr[i]+=value;
		}
		
	}

	public static void copy(double[] src, double[] dest) {
		System.arraycopy(src, 0,dest,0,src.length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] subArray(T[] array, int start, int end) {
		int len=end-start;
		T[] newarray=(T[]) Array.newInstance(array.getClass().getComponentType(), len);
		System.arraycopy(array, start, newarray, 0, len);
		return newarray;
	}

	public static void scaleToAverage(double[] data, int offset, int length,double targetAverage) {
		double sum=0.0;
		for (int i=offset; i<(length+offset); i++) {
			sum+=data[i];
		}
		
		if (sum!=0) {
			double factor=targetAverage*(length/sum);
			for (int i=offset; i<(length+offset); i++) {
				data[i]*=factor;
			}
		}
	}
	
	public static double[] calcAverage(double[][] arrays) {
		int len=arrays[0].length;
		double[] result=new double[len];
		for (double [] ds : arrays) {
			Arrays.add(result, ds);
		}
		double factor=1.0/arrays.length;
		for (int i=0; i<len; i++) {
			result[i]*=factor;
		}
		return result;
	}


}
