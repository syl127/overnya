package net.sylv.Util;

import net.sylv.Main;
import net.sylv.Util.Shaders.Shader;
import net.sylv.Util.Shaders.ShaderProgram;
import net.sylv.Util.Shaders.Uniform;
import net.sylv.Window;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30C;

import java.io.IOException;

import static net.sylv.Main.renderer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32C.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32C.glTexImage2DMultisample;

public class FBO {
	public int fboId;

	public int renderColor;
	public int depthBuffer;

	public int width;
	public int height;

	public double scaling;

	public int scalingType;

	public int lastBuffer;

	public Window window;

	public static ShaderProgram fboProgram;

	public static Uniform scalingUniform;

	static {
		Shader fboVertex;
		Shader fboFragment;
		try {
			fboVertex = Shader.fromFile(GL20.GL_VERTEX_SHADER, "shaders/FBO/vert.glsl");
			fboFragment = Shader.fromFile(GL_FRAGMENT_SHADER, "shaders/FBO/frag.glsl");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		fboProgram = new ShaderProgram(fboVertex, fboFragment);

		fboVertex.delete();
		fboFragment.delete();

		scalingUniform = new Uniform(fboProgram, "scalingType");
	}

	// I HAVE No idea why bicubic looks worse???
	public FBO(int width, int height, int scalingType, Window w) {
		this.scaling = switch(scalingType) {
			case 2 -> 2;
			case 3 -> 4;
			case 4 -> 8;
			default -> 1;
		};

		this.window = w;
		this.scalingType = scalingType;
		resize(width, height);

		createFramebuffers();
	}

	private void createFramebuffers() {
		int prevBuffer = glGetInteger(GL_FRAMEBUFFER_BINDING);

		fboId = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboId);

		// depth buffer!!
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);

		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
		//GL30C.glRenderbufferStorageMultisample(GL_RENDERBUFFER, 2, GL_DEPTH24_STENCIL8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

		// color texture thingy

		renderColor = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, renderColor);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

//		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, renderColor);
//		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, 2, GL_RGBA, width, height, true);
//		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);

		int filtering = scalingType < 2 ? GL_NEAREST : GL_LINEAR;
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filtering);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filtering);

		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderColor, 0);
		//glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, renderColor, 0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("resolved framebuffer is not complete");
		}

		glBindFramebuffer(GL_FRAMEBUFFER, prevBuffer);
	}

	public void bind(int buf) {
		lastBuffer = buf;

		glBindFramebuffer(GL_FRAMEBUFFER, fboId);
		glViewport(0, 0, width, height);
	}

	public void bind() {
		bind(glGetInteger(GL_FRAMEBUFFER_BINDING));
	}

	public void unbind(int buffer) {
		glBindFramebuffer(GL_FRAMEBUFFER, buffer);
		glViewport(0, 0, window.width, window.height);
	}

	public void draw(int framebuf, boolean premul) {
		unbind(framebuf);

		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);

		if (premul) glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		else glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, renderColor);

		fboProgram.use();
		scalingUniform.set(scalingType);

		renderer.startRendering();
		{
			float w = 1;//window.width;
			float h = 1;//window.height;

			renderer.pos(-1, -1, 0).col(1, 1, 1).tex(0, 0).upload();
			renderer.pos(-1, h, 0).col(1, 1, 1).tex(0, 1).upload();
			renderer.pos(w, h, 0).col(1, 1, 1).tex(1, 1).upload();

			renderer.pos(-1, -1, 0).col(1, 1, 1).tex(0, 0).upload();
			renderer.pos(w, -1, 0).col(1, 1, 1).tex(1, 0).upload();
			renderer.pos(w, h, 0).col(1, 1, 1).tex(1, 1).upload();
		}
		renderer.draw();
	}

	public void draw(int framebuf) {
		draw(framebuf, false);
	}

	public void draw() {
		draw(lastBuffer == -1 ? 0 : lastBuffer, false);
	}

	public void destroy() {
		glDeleteFramebuffers(fboId);
		glDeleteTextures(renderColor);
	}

	public void resize(int width, int height) {
		this.width = (int) (width * scaling);
		this.height = (int) (height * scaling);

		destroy();
		createFramebuffers();
	}
}