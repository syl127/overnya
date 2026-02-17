package net.sylv;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;

public class Mouse {
	public double lastX;

	public double lastY;

	public double x;

	public double y;

	public double sensitivity = 0.1d;

	public boolean firstMouse = true;

	public boolean enabled = true;

	Window window;

	public void init(Window w) {
		this.window = w;
		lastX = (float) w.width /2;
		lastY = (float) w.height /2;

		//glfwSetInputMode(w.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		glfwSetWindowFocusCallback(w.id, (wid, focus) -> {
			if (!focus) firstMouse = true;
		});

		glfwSetCursorPosCallback(w.id, (wid, x, y) -> {
			if (!enabled) return;

			if (firstMouse) {
				lastX = x;
				lastY = y;
				firstMouse = false;
			}

			this.x = x;
			this.y = y;
		});
	}

	public void disableCursor() {
		glfwSetInputMode(window.id, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		enabled = false;
		firstMouse = true;
	}

	public void enableCursor() {
		glfwSetInputMode(window.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		enabled = true;
	}
}
