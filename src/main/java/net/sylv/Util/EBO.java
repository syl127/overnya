package net.sylv.Util;

import net.sylv.Util.Vertex.Vertex;
import org.lwjgl.system.NativeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL15.*;

// TODO: integrate directly with VBO, idk if this is written well at all!
public class EBO {
	public final int id;

	public final VBO vbo;

	public final HashMap<Vertex, Integer> indices = new HashMap<>();

	public final ArrayList<Short> order = new ArrayList<>();

	public short[] data;

	public EBO(VBO vbo) {
		id = glGenBuffers();

		this.vbo = vbo;
		vbo.setEBO(this);
	}

	public void clear() {
		indices.clear();
		order.clear();
	}

	public int uploadVertex(Vertex v, Integer index) {
		// NOTE: this is commented out cause we don't store normals or something yet probably
		// uncomment this when we do?
//		if (indices.containsKey(v)) {
//			Vertex hit = null;
//			for (Vertex v2 : indices.keySet()) {
//				if (v2.equals(v)) {
//					hit = v2;
//					break;
//				}
//			}
//
//			System.err.printf("Cached vertex %s %s (hit %s at %s)%n", index, v, hit, indices.get(v));
//
//			return indices.get(v);
//		}

		//System.out.println("Added vertex: " + v + " at " + index);
		indices.put(v, index);
		order.add(index.shortValue());
		return index;
	}

	public short[] getOrderedIndices() {
		short[] n = new short[order.size()];
		int i = 0;
		for (short index : order) {
			n[i++] = index;
		}

		return n;
	}

	public void bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
	}

	public void unbind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	// TODO: convert to buffer before upload? i doubt it'd actually be faster
	public void upload(@NativeType("void const *") short[] data, @NativeType("GLenum") int usage) {
		this.data = data;

		glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
	}

	public void draw(int mode, int count, long indices) {
		glDrawElements(mode, count, GL_UNSIGNED_SHORT, indices);
	}

	public void draw(int mode) {
		glDrawElements(mode, data.length, GL_UNSIGNED_SHORT, 0);
	}

	public void draw() {
		draw(GL_TRIANGLES);
	}
}
