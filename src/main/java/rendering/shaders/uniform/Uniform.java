/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.shaders.uniform;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Uniform {

  private static final int NOT_FOUND = -1;
  private static final Logger LOGGER = LoggerFactory.getLogger(Uniform.class);

  private final String name;
  private int location;

  /**
   * Create a new Uniform
   *
   * @param name name of the uniform, must be the same as in the Shader program
   */
  Uniform(String name) {
    this.name = name;
  }

  /**
   * Allocate GPU RAM for this Uniform to the shader
   *
   * @param programID shader ID
   */
  public void storeUniformLocation(int programID) {
    location = glGetUniformLocation(programID, name);
    if (location == NOT_FOUND) {
      LOGGER.error("Uniform {} not found for shader : {}", name, programID);
    }
  }

  /**
   * Return the location of the Uniform
   *
   * @return uniform location
   */
  int getLocation() {
    return location;
  }

  public abstract Object getDefault();

  public abstract void loadDefault();

  public String getName() {
    return name;
  }
}
