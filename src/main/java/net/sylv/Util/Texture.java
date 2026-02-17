package net.sylv.Util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NativeType;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.EXTABGR.GL_ABGR_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

// TODO: mipmaps
public class Texture {
	public final int id;

	public BufferedImage internalImage;

	public int width;

	public int height;

	public boolean deallocated = false;

	public int defaultTexture = GL_TEXTURE0;

	public int wrapping = GL_REPEAT;

	public Texture(BufferedImage img, int texture) {
		defaultTexture = texture;
		id = glGenTextures();

		setTexture(img);

		setSampling();
	}

	public Texture(String f, int texture) {
		defaultTexture = texture;

		id = glGenTextures();

		if (f != null) {
			BufferedImage img;
			try {
				img = ImageIO.read(Texture.class.getResourceAsStream("/" + f));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			setTexture(img);
		}

		setSampling();
	}

	public void deallocate() {
		if (deallocated) throw new IllegalStateException("Texture not allocated");

		glDeleteTextures(id);
		deallocated = true;
	}

	public void bind(@NativeType("GLenum") int texture) {
		glEnable(GL_TEXTURE_2D);

		glActiveTexture(GL_TEXTURE0 + texture);
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void bind() {
		bind(defaultTexture);
	}

	public void setWrapping(int wrapping) {
		bind();

		this.wrapping = wrapping;
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapping);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapping);
	}

	public void setTexture(BufferedImage img) {
		internalImage = img;

		DataBuffer dataBuf = img.getRaster().getDataBuffer();
		width = img.getWidth();
		height = img.getHeight();
//		if (dataBuf instanceof DataBufferByte) {
//			// write byte image directly
//			// most images are this, so we want it to be faster than converting to int (even though this isn't really any faster :disappointed:)
//			byte[] pixels = ((DataBufferByte) dataBuf).getData();
//
//			ByteBuffer imgBuffer;
//			boolean trans = true;
//
//			if (img.getType() == BufferedImage.TYPE_3BYTE_BGR) {
//				// ihy!!
//				trans = false;
//				imgBuffer = BufferUtils.createByteBuffer(width * height * 3);
//
//				GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//			} else {
//				imgBuffer = BufferUtils.createByteBuffer(pixels.length);
//			}
//
//			imgBuffer.put(pixels);
//			imgBuffer.flip();
//			setTexture(width, height, imgBuffer, trans);
//
//			if (!trans)
//				GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
//
//			return;
//		}

		if (!(dataBuf instanceof DataBufferInt)) {
			// convert to integer for any unknown formats
			img = toIntARGB(img);
			dataBuf = img.getRaster().getDataBuffer();
		}

		int[] pixels = ((DataBufferInt) dataBuf).getData();

		IntBuffer imgBuffer = BufferUtils.createIntBuffer(pixels.length);
		imgBuffer.put(pixels);
		imgBuffer.flip();

		setTexture(width, height, imgBuffer);
	}

	public void setTexture(int w, int h, IntBuffer buf) {
		width = w;
		height = h;

		bind();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, w, h, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, buf);
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	public void setTexture(int w, int h, ByteBuffer buf, boolean trans) {
		width = w;
		height = h;

		bind();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, trans ? GL_ABGR_EXT : GL_BGRA, GL_UNSIGNED_BYTE, buf); // unsure abt this one
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	// TODO: option for different sampling on texture class?
	public void setSampling() {
		bind();

		minTexFilter(GL_LINEAR_MIPMAP_LINEAR);
		maxTexFilter(GL_LINEAR);
	}

	public void minTexFilter(@NativeType("GLint") int filter) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
	}

	public void maxTexFilter(@NativeType("GLint") int filter) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
	}

	public static BufferedImage toIntARGB(BufferedImage img) {
		if (img.getType() == BufferedImage.TYPE_INT_ARGB) return img;

		BufferedImage converted = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = converted.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return converted;
	}

	public void _write(String id, int format, int type) throws IOException {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, this.id);

		double mapWidth = this.width;
		double mapHeight = this.height;
		for (int ix = 0; ix <= 0; ix++) {
			int imW = Math.max(1, (int) mapWidth);
			int imH = Math.max(1, (int) mapHeight);
			IntBuffer imgBuf = BufferUtils.createIntBuffer(imW * imH);
			glGetTexImage(GL_TEXTURE_2D, ix, format, type, imgBuf);

			int[] arr = new int[imgBuf.limit()];
			imgBuf.get(arr);
			BufferedImage im = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_ARGB);

			int i = 0;
			for (int y = 0; y < imH; y++) {
				for (int x = 0; x < imW; x++, i++) {
					im.setRGB(x, y, arr[i]);
				}
			}

			String path = id + "_level_" + ix + ".png";
			ImageIO.write(im, "png", new File(path));
			System.out.printf("wrote texture to %s!%n", new File(path).getCanonicalPath());

			mapWidth /= 2;
			mapHeight /= 2;
		}

		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);
	}
}
