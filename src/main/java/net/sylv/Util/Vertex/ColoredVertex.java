package net.sylv.Util.Vertex;

import java.util.Objects;

public class ColoredVertex extends Vertex {
	//@Override
	public static final int SIZE = 6;

	public static final int BYTES = SIZE*4;

	public final float r;

	public final float g;

	public final float b;

	public ColoredVertex(float x, float y, float z, float r, float g, float b) {
		super(x, y, z);

		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public float[] rawRepresentation() {
		return new float[]{this.x, this.y, this.z, this.r, this.g, this.b};
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		ColoredVertex vertex = (ColoredVertex) o;
		return super.equals(o) && Float.compare(r, vertex.r) == 0 &&
			  Float.compare(g, vertex.g) == 0 &&
			  Float.compare(b, vertex.b) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, r, g, b);
	}

	@Override
	public String toString() {
		return "CV(" + x + ", " + y + ", " + z + " | " + (int)r*255 + ", " + (int)g*255  + ", " + (int)b*255 + ")";
	}
}
