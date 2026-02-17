package net.sylv.Util;

import net.sylv.Renderer.Renderer;

public class GLHelper {
	public static void drawRect(Renderer r, float x, float y, float w, float h) {
		r.pos(x, y, 0).col(1, 1, 1).tex(0, 1).upload();
		r.pos(x, y + h, 0).col(1, 1, 1).tex(0, 0).upload();
		r.pos(x + w, y + h, 0).col(1, 1, 1).tex(1, 0).upload();

		r.pos(x, y, 0).col(1, 1, 1).tex(0, 1).upload();
		r.pos(x + w, y, 0).col(1, 1, 1).tex(1, 1).upload();
		r.pos(x + w, y + h, 0).col(1, 1, 1).tex(1, 0).upload();
	}
}
