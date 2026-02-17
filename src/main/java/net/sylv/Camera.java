package net.sylv;

import net.sylv.Util.Shaders.Uniform;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	public Matrix4f matrix = new Matrix4f();

	public Matrix4f updateOrthoMatrix(Window w, Uniform u) {
		matrix = matrix.identity().ortho(0, w.width, w.height, 0, -1f, 1f);

		if (u != null) {
			u.set(matrix, false);
		}

		return matrix;
	}
}
