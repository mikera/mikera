package mikera.geom;

import mikera.util.emptyobjects.NullArrays;

public class GeometryData {
	// vertices
	// stored in flat format i.e. (x1 , y1, z1 , x2, y2, z2 ....)
	public int vertexCount=0;
	public float[] vertices=NullArrays.NULL_FLOATS;
	
	// vertex indices
	public int vertexIndexCount=0;
	public int[] vertexIndices=NullArrays.NULL_INTS;

	// colours
	// stored in flat format i.e. (r1 , g1, b1, a1 , r2 , g2 , b2, a2, .....)
	public int colourCount=0;
	public float[] colours=NullArrays.NULL_FLOATS;
	
	// colour indices
	public int colourIndexCount=0;
	public int[] colourIndices=NullArrays.NULL_INTS;

	
	// normals
	// stored in flat format i.e. (x1 , y1, z1 , x2, y2, z2 ....)
	public int normalCount=0;
	public float[] normals=NullArrays.NULL_FLOATS;
	
	// normal indices
	public int normalIndexCount;
	public int[] normalIndices=NullArrays.NULL_INTS;
	
	// texture coordinates
	// stored as (tx1, ty1, tx2, ty2 ....
 	public int textureCoordinateCount=0;
	public float[] textureCoordinates=NullArrays.NULL_FLOATS;

}
