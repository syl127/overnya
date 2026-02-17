package net.sylv.Util;

import static org.lwjgl.opengl.GL30.*;

public class VAO {
	public final int id;

	public final VBO vbo;

	public final EBO ebo;

	public VAO(VBO vbo, EBO ebo, Runnable run) {
		id = glGenVertexArrays();

		this.vbo = vbo;
		this.ebo = ebo;

		bind();
		vbo.bind();
		ebo.bind();

		run.run();

		unbind();
//		vbo.unbind();
//		ebo.unbind();
	}

	public void bind() {
		glBindVertexArray(id);
	}

	public void unbind() {
		glBindVertexArray(0);
	}
}