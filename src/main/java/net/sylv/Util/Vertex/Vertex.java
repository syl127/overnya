package net.sylv.Util.Vertex;

import java.util.Objects;

public class Vertex {
	public static final int SIZE = 3;

	public static final int BYTES = SIZE*4;

	public final float x;

	public final float y;

	public final float z;

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float[] rawRepresentation() {
		return new float[]{this.x, this.y, this.z};
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		Vertex vertex = (Vertex) o;
		return Float.compare(x, vertex.x) == 0 &&
			  Float.compare(y, vertex.y) == 0 &&
			  Float.compare(z, vertex.z) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString() {
		return "V(" + x + ", " + y + ", " + z + ")";
	}
}
