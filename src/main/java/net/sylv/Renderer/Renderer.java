package net.sylv.Renderer;

import net.sylv.Util.EBO;
import net.sylv.Util.VAO;
import net.sylv.Util.VBO;
import net.sylv.Util.Vertex.TexturedVertex;
import net.sylv.Util.Vertex.Vertex;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Renderer {
	public VBO vbo;
	public EBO ebo;
	public VAO vao;

	private float x;
	private float y;
	private float z;

	private float r;
	private float g;
	private float b;

	private float u;
	private float v;

	public boolean rendering;

	public Renderer() {
		vbo = new VBO(new Vertex[0], TexturedVertex.SIZE);
		ebo = new EBO(vbo);

		vao = new VAO(vbo, ebo, () -> {
			vbo.upload(GL_STREAM_DRAW);

			ebo.upload(new short[0], GL_STREAM_DRAW);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, TexturedVertex.BYTES, 0);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, TexturedVertex.BYTES, 12);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, TexturedVertex.BYTES, 24);

			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
		});
	}

	public void upload(TexturedVertex t) {
		vbo.addVertex(t);
	}

	public Renderer upload() {
		upload(new TexturedVertex(x, y, z, r, g, b, u, v));

		x = y = z = r = g = b = u = v = 0;

		return this;
	}

	public Renderer pos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public Renderer col(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;

		return this;
	}

	public Renderer tex(float u, float v) {
		this.u = u;
		this.v = v;

		return this;
	}

	public void startRendering() {
		if (rendering) throw new IllegalStateException("Already rendering");

		rendering = true;
		vao.bind();

		// not sure why unbidning the vao from before just isn't working???
		vbo.bind();
		ebo.bind();

		vbo.clear();
		ebo.clear();
	}

	public void finishRender() {
		if (!rendering) throw new IllegalStateException("Not rendering");

		ebo.upload(ebo.getOrderedIndices(), GL_STREAM_DRAW);
		vbo.upload(GL_STREAM_DRAW);

		rendering = false;
	}

	public void draw() {
		finishRender();

		ebo.draw();

		vao.unbind();
	}
}
