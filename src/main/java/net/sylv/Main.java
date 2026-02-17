package net.sylv;

import net.sylv.Renderer.Renderer;
import net.sylv.Util.*;
import net.sylv.Util.Shaders.Shader;
import net.sylv.Util.Shaders.ShaderProgram;
import net.sylv.Util.Shaders.Uniform;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
	public static final Camera camera = new Camera();

	public static final Window window = new Window(camera);

	public static ShaderProgram basicProgram;

	public static ArrayList<Consumer<Double>> tasks = new ArrayList<>(8);

	public static Uniform projectionUniform;

	public static Renderer renderer;

	public static Config config = new Config();

	public static void main(String[] args) throws IOException {
		// read config!!

		config.loadConfig();

		window.init();

		Texture noTex = new Texture((String) null, 0);
		noTex.setTexture(1,1, BufferUtils.createIntBuffer(1).put(0xFFFFFFFF).flip());

		renderer = new Renderer();

		// setup shaders & program
		Shader basicVertex = Shader.fromFile(GL_VERTEX_SHADER,"shaders/vertex.vs.glsl");
		Shader basicFragment = Shader.fromFile(GL_FRAGMENT_SHADER, "shaders/frag.fs.glsl");

		basicProgram = new ShaderProgram(basicVertex, basicFragment);

		basicVertex.delete();
		basicFragment.delete();

		Texture defaultTex = new Texture("default.png", 0);
		Texture birdTex = new Texture("bird.png", 0);

		// handle input i guess?
		Mouse mouse = new Mouse();
		mouse.init(window);

		glfwSetKeyCallback(window.id, (long win, int key, int scan, int action, int mods) -> {
			if (action == GLFW_RELEASE) return;

			switch(key) {
				case GLFW_KEY_ESCAPE -> {
					System.exit(0);
				}

//				case GLFW_KEY_LEFT_CONTROL -> {
//					if (mouse.enabled) mouse.disableCursor();
//					else mouse.enableCursor();
//				}

				// TODO: read from color attachment?
				case GLFW_KEY_U -> {
					int w = window.mainBuffer.width;
					int h = window.mainBuffer.height;
					int[] pixels = new int[w*h];

					glBindTexture(GL_TEXTURE_2D, window.mainBuffer.renderColor);
					glGetTexImage(GL_TEXTURE_2D, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, pixels);

					new Thread(() -> {
						BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

						int i = 0;
						for (int y = h-1; y >= 0; y--) {
							for (int x = 0; x < w; x++, i++) {
								im.setRGB(x, y, pixels[i]);
							}
						}

						try {
							ImageIO.write(im, "png", new File("uwu_test.png"));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}

						System.out.printf("wrote texture to %s!%n", new File("uwu_test.png").getAbsolutePath());
					}).start();
				}
			}
		});

		projectionUniform = new Uniform(basicProgram, "projection");

		double delta = 1d/120;
		double last = glfwGetTime()-delta;

		// update projection matrix ( we only do render 2d so we don't have to do in render loop yay)
		camera.updateOrthoMatrix(window, projectionUniform);

		while (!glfwWindowShouldClose(window.id)) {
			double now = glfwGetTime();

			delta = now - last;
			last = now;

			window.glRenderPre();

			// do any tasks we need to do
			{
				int len = tasks.size();
				for (int i = 0; i < len; i++) {
					Consumer<Double> task = tasks.get(0);
					task.accept(delta);
					tasks.remove(0);
				}

				// resize framebuf if needed
				if (now > window.framebufferTime) {
					window.framebufferTime = Double.MAX_VALUE;
					window.mainBuffer.resize(window.width, window.height);
				}
			}

			mouse.lastX = mouse.x;
			mouse.lastY = mouse.y;

			camera.updateOrthoMatrix(window, projectionUniform);

			// setup state
			basicProgram.use();
			glDisable(GL_CULL_FACE);
			glEnable(GL_DEPTH_TEST);

			int width = window.width;
			int height = window.height;

			defaultTex.bind();

			glDisable(GL_DEPTH_TEST);
			window.mainBuffer.draw(0, true);

			// non super-sampled rendering
			width = window.width;
			height = window.height;

			basicProgram.use();
			glEnable(GL_DEPTH_TEST);

			renderer.startRendering();

			renderer.draw();

			window.glRenderPost();


		}

		System.out.println("bye");
		glfwTerminate();
	}
}