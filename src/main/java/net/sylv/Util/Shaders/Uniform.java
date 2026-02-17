package net.sylv.Util.Shaders;

import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Uniform {
	public static final boolean THROW_ON_INVALID = false;

	public final ShaderProgram program;

	public final String name;

	public final int id;

	public boolean exists = true;

	// yay values
	private Float floatVal;

	private Integer intVal;

	private Matrix2f m2Val;

	private Matrix3f m3Val;

	private Matrix4f m4Val;

	public Uniform(ShaderProgram program, String name) {
		this.program = program;
		this.name = name;
		this.id = glGetUniformLocation(program.id, name);

		if (id == -1) {
			exists = false;

			String errMsg = "Couldn't find uniform '%s' for program '%d'".formatted(name, program.id);
			if (THROW_ON_INVALID)
				throw new IllegalArgumentException(errMsg);
				else System.err.println(errMsg);
		}
	}

	public boolean checkInvalid() {
		return !this.exists;
	}

	// TODO: check if shader is bound when setting?
	public Uniform set(int i) {
		if (checkInvalid()) return this;

		GL20.glUniform1i(id, i);
		intVal = i;

		return this;
	}
	public Uniform set(int i, int i2) {
		if (checkInvalid()) return this;

		GL20.glUniform2i(id, i, i2);
		intVal = i;

		return this;
	}
	public Uniform set(int i, int i2, int i3) {
		if (checkInvalid()) return this;

		GL20.glUniform3i(id, i, i2, i3);
		intVal = i;

		return this;
	}
	public Uniform set(int i, int i2, int i3, int i4) {
		if (checkInvalid()) return this;

		GL20.glUniform4i(id, i, i2, i3, i4);
		intVal = i;

		return this;
	}

	public Uniform set(float f) {
		if (checkInvalid()) return this;

		GL20.glUniform1f(id, f);
		floatVal = f;
		return this;
	}
	public Uniform set(float f, float f2) {
		if (checkInvalid()) return this;

		GL20.glUniform2f(id, f, f2);
		floatVal = f;

		return this;
	}
	public Uniform set(float f, float f2, float f3) {
		if (checkInvalid()) return this;

		GL20.glUniform3f(id, f, f2, f3);
		floatVal = f;

		return this;
	}
	public Uniform set(float f, float f2, float f3, float f4) {
		if (checkInvalid()) return this;

		GL20.glUniform4f(id, f, f2, f3, f4);
		floatVal = f;

		return this;
	}

	public Uniform set(Matrix2f mat, boolean transpose) {
		if (checkInvalid()) return this;

		float[] f = new float[2*2];

		glUniformMatrix4fv(id, transpose, mat.get(f));
		m2Val = mat;

		return this;
	}

	public Uniform set(Matrix3f mat, boolean transpose) {
		if (checkInvalid()) return this;

		float[] f = new float[3*3];

		glUniformMatrix3fv(id, transpose, mat.get(f));
		m3Val = mat;

		return this;
	}

	public Uniform set(Matrix4f mat, boolean transpose) {
		if (checkInvalid()) return this;

		float[] f = new float[4*4];

		glUniformMatrix4fv(id, transpose, mat.get(f));
		m4Val = mat;

		return this;
	}

	public int getI() {
		return intVal;
	}

	public float getF() {
		return floatVal;
	}

	public Matrix2f getM2() {
		return m2Val;
	}

	public Matrix3f getM3() {
		return m3Val;
	}

	public Matrix4f getM4() {
		return m4Val;
	}

	public float raw_getF() {
		if (checkInvalid()) return 0f;

		return glGetUniformf(program.id, id);
	}

	public int raw_getI() {
		if (checkInvalid()) return 0;


		return glGetUniformi(program.id, id);
	}

	public Matrix2f raw_getM2() {
		if (checkInvalid()) return null;

		FloatBuffer f = BufferUtils.createFloatBuffer(2*2);

		glGetUniformfv(program.id, id, f);

		return new Matrix2f(f);
	}

	public Matrix3f raw_getM3() {
		if (checkInvalid()) return null;

		FloatBuffer f = BufferUtils.createFloatBuffer(3*3);

		glGetUniformfv(program.id, id, f);

		return new Matrix3f(f);
	}

	public Matrix4f raw_getM4() {
		if (checkInvalid()) return null;

		FloatBuffer f = BufferUtils.createFloatBuffer(4*4);

		glGetUniformfv(program.id, id, f);

		return new Matrix4f(f);
	}

	public void raw_get_V(float[] a) {
		if (checkInvalid()) return;

		glGetUniformfv(program.id, id, a);
	}

	public void raw_get_V(int[] a) {
		if (checkInvalid()) return;

		glGetUniformiv(program.id, id, a);
	}

}
