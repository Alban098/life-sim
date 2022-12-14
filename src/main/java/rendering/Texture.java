/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents a Texture that can be applied to a Quad */
public class Texture {

  private static final Logger LOGGER = LoggerFactory.getLogger(Texture.class);

  private final int id;
  private final int width;
  private final int height;
  private int size;

  /**
   * Create a new empty Texture from attributes
   *
   * @param id the id of the texture provided by OpenGL
   * @param width the Texture width in pixels
   * @param height the Texture height in pixels
   */
  public Texture(int id, int width, int height, int size) {
    this.id = id;
    this.width = width;
    this.height = height;
    this.size = size;
    LOGGER.debug("Created texture with id {} ({}x{})", id, width, height);
  }

  /**
   * Create a new empty Texture
   *
   * @param width the Texture width in pixels
   * @param height the Texture height in pixels
   */
  public Texture(int width, int height) {
    this.width = width;
    this.height = height;
    size = width * height * 4;
    // Generate the texture
    id = glGenTextures();
    bind();
    // Set the filtering mode
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    // Load the buffer in Video RAM
    glTexImage2D(
        GL_TEXTURE_2D,
        0,
        GL_RGBA,
        width,
        height,
        0,
        GL_RGBA,
        GL_UNSIGNED_BYTE,
        BufferUtils.createByteBuffer(width * height * 4));
    LOGGER.debug("Created texture with id {} ({}x{})", id, width, height);
  }

  /** Bind the texture for rendering */
  public void bind() {
    glBindTexture(GL_TEXTURE_2D, id);
    LOGGER.trace("Bound texture {}", id);
  }

  /**
   * Return the Texture id
   *
   * @return the Texture id
   */
  public int getId() {
    return id;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public float getAspectRatio() {
    return (float) width / height;
  }

  /**
   * Load a byte buffer in the texture
   *
   * @param buf the buffer to load
   */
  public void load(ByteBuffer buf) {
    bind();
    this.size = buf.limit();
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
  }

  /** Cleanup the Texture */
  public void cleanup() {
    glDeleteTextures(id);
    LOGGER.trace("texture {} cleaned up", id);
  }

  /** Unbind the texture after use */
  public void unbind() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  public int getSize() {
    return size;
  }

  public String getTypeDescriptor() {
    return "RGBA 8 bit/ch";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Texture texture = (Texture) o;
    return id == texture.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
