package net.sylv.Util.Shaders;

import java.io.*;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
	public final int id;

	public final int type;

	public Shader(int type, String src) {
		this.type = type;

		id = glCreateShader(type);
		glShaderSource(id, src);
		glCompileShader(id);

		int compiled = glGetShaderi(id, GL_COMPILE_STATUS);
		if (compiled == 0) {
			String _err = glGetShaderInfoLog(id);

			throw new RuntimeException(_err);
		}
	}

	public static Shader fromFile(int type, String file) throws IOException {
		try (InputStream stream = Shader.class.getResourceAsStream("/" + file)) {
			if (stream == null) {
				throw new FileNotFoundException("Couldn't find file for shader source: /" + file);
			}

			return new Shader(type, new String(stream.readAllBytes()));
		}
	}

	public void delete() {
		glDeleteShader(id);
	}
}
