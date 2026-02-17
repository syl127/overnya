package net.sylv.Util;

import net.sylv.Util.Vertex.Vertex;
import org.lwjgl.system.NativeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL15.*;

public class VBO {
	public final int id;

	public final int vertexSize;

	public EBO ebo;

	public ArrayList<Vertex> vertices;

	public float[] raw_vertices;

	public VBO(Vertex[] vertices, int vertexSize) {
		this.vertexSize = vertexSize;

		//raw_vertices = new float[Math.max(vertices.length*vertexSize, 4)];
		if (vertices != null) {
			this.vertices = new ArrayList<>(vertices.length);
			Collections.addAll(this.vertices, vertices);
		} else {
			this.vertices = new ArrayList<>(0);
		}

		id = glGenBuffers();
	}

	public void clear() {
		if (ebo != null) ebo.clear();

		vertices.clear();
	}

	public VBO setEBO(EBO ebo) {
		if (this.ebo != null) throw new IllegalStateException("VBO Cannot have more than 1 EBO");

		for (int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			int idx = ebo.uploadVertex(v, i);

			if (idx != i) {
				vertices.remove(i--);
			}
		}

		this.ebo = ebo;
		return this;
	}

	private float[] resizeArray(float[] arr, int minElements) {
		return Arrays.copyOf(arr, (int) Math.pow(2, Math.ceil(Math.log(minElements) / Math.log(2))));
	}

	public int addVertex(Vertex v) {
		if (ebo != null) {
			int i = vertices.size();
			int idx = ebo.uploadVertex(v, i);
			if (idx == i) {
				vertices.add(v);
			}

			return idx;
		}

		vertices.add(v);

		return vertices.size();
	}

	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, id);
	}

	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void upload(@NativeType("GLenum") int usage) {
		// only resize and do stuff with raw_vertices here
		raw_vertices = new float[vertices.size()*vertexSize];

		AtomicInteger rawLoc = new AtomicInteger();
		vertices.forEach(
			  v -> System.arraycopy(v.rawRepresentation(), 0, raw_vertices, rawLoc.getAndAdd(vertexSize), vertexSize)
		);

		upload(raw_vertices, usage);
	}

	// TODO: convert to buffer before upload? i doubt it'd actually be faster
	public void upload(@NativeType("void const *") float[] data, @NativeType("GLenum") int usage) {
		glBufferData(GL_ARRAY_BUFFER, data, usage);
	}
}
