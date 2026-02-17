package net.sylv.Util.Shaders;

import java.util.Arrays;
import java.util.HashSet;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
	public final int id;

	public HashSet<Integer> attachedShaderPhases = new HashSet<>();

	public ShaderProgram(Shader... shaders) {
		id = glCreateProgram();

		Arrays.stream(shaders).forEach(this::attach);

		link();
	}

	public void attach(Shader shader) {
		if (attachedShaderPhases.contains(shader.type)) {
			throw new IllegalStateException("Shader with type %d already attached.".formatted(shader.type));
		}

		attachedShaderPhases.add(shader.type);
		glAttachShader(id, shader.id);
	}

	public void link() {
		glLinkProgram(id);

		int success = glGetProgrami(id, GL_LINK_STATUS);
		if (success == 0) {
			throw new RuntimeException(glGetProgramInfoLog(id));
		}
	}

	public void use() {
		glUseProgram(id);
	}
}
