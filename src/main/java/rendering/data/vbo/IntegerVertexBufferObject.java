/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.data.vbo;

import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glVertexAttribIPointer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

public class IntegerVertexBufferObject extends VertexBufferObject<Integer> {

  private final IntBuffer buffer;

  public IntegerVertexBufferObject(int location, int dataDimension, int capacity) {
    super(location, dataDimension, capacity, 4);
    this.buffer = MemoryUtil.memAllocInt((int) (size / dataSize));
    LOGGER.debug(
        "Created VBO<int> with id {} at location {} with a size of {} bytes",
        id,
        location,
        this.buffer.capacity());
  }

  @Override
  public <B extends Buffer> void buffer(B data) {
    if (data instanceof IntBuffer) {
      buffer.put((IntBuffer) data);
    }
  }

  public void buffer(Integer data) {
    buffer.put(data);
  }

  public void load() {
    buffer.flip();
    bind();
    glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
    glEnableVertexAttribArray(location);
    // glVertexAttribIPointer() instead of glVertexAttribPointer() to force the type inside the
    // shader to be an integer instead of a float
    glVertexAttribIPointer(location, dataDim, GL_UNSIGNED_INT, 0, 0);
    LOGGER.trace("Filled VBO<int> {} with {} bytes", id, buffer.limit());
    buffer.clear();
  }

  @Override
  public void cleanUp() {
    super.cleanUp();
    MemoryUtil.memFree(buffer);
  }

  @Override
  public Class<Integer> getType() {
    return Integer.class;
  }

  public Class<IntBuffer> getBufferType() {
    return IntBuffer.class;
  }
}