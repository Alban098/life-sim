/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform<Vector3f> {

  private final Vector3f defaultValue;

  /**
   * Create a new Uniform of type vec3
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  public UniformVec3(String name, Vector3f defaultValue) {
    super(name);
    this.defaultValue = defaultValue;
    this.currentValue = new Vector3f();
  }

  @Override
  public Object getDefault() {
    return defaultValue;
  }

  public void loadDefault() {
    load(defaultValue);
  }

  @Override
  public int getDimension() {
    return 12;
  }

  @Override
  public String getType() {
    return "vec3";
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param vector vector to load
   */
  public void load(Vector3f vector) {
    load(vector.x, vector.y, vector.z);
  }

  /**
   * Load a vector in GPU RAM
   *
   * @param x x component of the vector
   * @param y y component of the vector
   * @param z z component of the vector
   */
  public void load(float x, float y, float z) {
    if (!currentValue.equals(x, y, z)) {
      currentValue.set(x, y, z);
      GL20.glUniform3f(super.getLocation(), x, y, z);
    }
  }
}
