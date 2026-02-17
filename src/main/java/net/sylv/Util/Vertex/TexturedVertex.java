package net.sylv.Util.Vertex;

import java.util.Objects;

public class TexturedVertex extends ColoredVertex {
	//@Override
	public static final int SIZE = 8;

	public static final int BYTES = SIZE*4;

	public final float u;

	public final float v;

	public TexturedVertex(float x, float y, float z, float r, float g, float b, float u, float v) {
		super(x, y, z, r, g, b);

		this.u = u;
		this.v = v;
	}

	@Override
	public float[] rawRepresentation() {
		return new float[]{x, y, z, r, g, b, u, v};
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		TexturedVertex vertex = (TexturedVertex) o;
		return super.equals(o) && Float.compare(u, vertex.u) == 0 &&
			  Float.compare(v, vertex.v) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, r, g, b, u, v);
	}

	@Override
	public String toString() {
		return "TV(" + x + ", " + y + ", " + z + " | " + (int)r*255 + ", " + (int)g*255  + ", " + (int)b*255 + " @ " + u + ", " + v + ")";
	}
}
