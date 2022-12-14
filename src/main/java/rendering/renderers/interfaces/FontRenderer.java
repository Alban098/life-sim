/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.renderers.interfaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.joml.Vector4f;
import rendering.Texture;
import rendering.data.VertexArrayObject;
import rendering.fonts.Font;
import rendering.fonts.FontManager;
import rendering.interfaces.element.property.Properties;
import rendering.interfaces.element.text.Character;
import rendering.interfaces.element.text.TextLabel;
import rendering.interfaces.element.text.Word;
import rendering.renderers.Renderer;
import rendering.shaders.ShaderAttribute;
import rendering.shaders.ShaderAttributes;
import rendering.shaders.ShaderProgram;
import rendering.shaders.uniform.*;

/**
 * An implementation of {@link Renderer} in charge of rendering Text present on a {@link
 * rendering.interfaces.UserInterface} Fonts a rendered by precomputing a quad for each character,
 * then rendering a subtexture from a font atlas onto it. Only support Bitmap SDF fonts for now
 */
public class FontRenderer implements Renderer {

  /** The {@link ShaderProgram} to use for font rendering */
  private final ShaderProgram shader;
  /** The VAO in which to batch the {@link Character}s for rendering */
  private final VertexArrayObject vao;

  /** A Set of all registered Font Atlas {@link Texture}s */
  private final Set<Texture> textures = new HashSet<>();

  /** The number of draw calls for the last frame */
  private int drawCalls = 0;

  /** The number of {@link Character}s rendered during the last frame */
  private int nbObjects = 0;

  /**
   * Creates a new FontRenderer and create the adequate {@link ShaderProgram}s and {@link
   * VertexArrayObject}s
   */
  public FontRenderer() {
    this.shader =
        new ShaderProgram(
            "src/main/resources/shaders/interface/font/simple.vert",
            "src/main/resources/shaders/interface/font/simple.geom",
            "src/main/resources/shaders/interface/font/simple.frag",
            new ShaderAttribute[] {
              ShaderAttributes.TEXT_TEXTURE_POS, ShaderAttributes.TEXT_TEXTURE_SIZE
            },
            new Uniform[] {
              new UniformVec4(Uniforms.COLOR.getName(), new Vector4f(0, 0, 0, 1f)),
              new UniformFloat(Uniforms.FONT_WIDTH.getName(), 0.4f),
              new UniformFloat(Uniforms.FONT_BLUR.getName(), 0.15f),
            });
    this.vao = shader.createCompatibleVao(64, true);
  }

  /**
   * Renders a Text into the screen (or the currently bounded render target)
   *
   * @param element the text to render
   */
  public void render(TextLabel element) {
    // skip empty texts
    if (element.getText().equals("")) {
      return;
    }
    // retrieve the font and bind its texture
    Font font =
        FontManager.getFont(element.getProperties().get(Properties.FONT_FAMILY, String.class));
    textures.add(font.getAtlas());

    // bind the ShaderProgram and Texture
    shader.bind();
    font.getAtlas().bind();

    // loads all the uniforms for rendering
    shader
        .getUniform(Uniforms.COLOR, UniformVec4.class)
        .load(element.getProperties().get(Properties.FONT_COLOR, Vector4f.class));
    shader
        .getUniform(Uniforms.FONT_WIDTH, UniformFloat.class)
        .load(element.getProperties().get(Properties.FONT_WIDTH, Float.class));
    shader
        .getUniform(Uniforms.FONT_BLUR, UniformFloat.class)
        .load(element.getProperties().get(Properties.FONT_BLUR, Float.class));

    // batch all the Characters of the text
    for (Word word : element) {
      for (Character character : word) {
        // If batching size exceeded, draw and start a new batch
        if (!vao.batch(character)) {
          vao.draw();
          drawCalls++;
          nbObjects++;
          vao.batch(character);
        }
      }
    }
    // draw all batched Characters
    vao.draw();
    drawCalls++;

    // unbind ShaderProgram and Texture
    font.getAtlas().unbind();
    shader.unbind();
  }

  /** Clear the Renderer by clearing its {@link ShaderProgram}s and {@link VertexArrayObject}s */
  @Override
  public void cleanUp() {
    shader.cleanUp();
    vao.cleanUp();
  }

  /**
   * Returns all the currently used {@link Texture}s (used for the last frame)
   *
   * @return all the currently used {@link Texture}s
   */
  @Override
  public Collection<Texture> getTextures() {
    return textures;
  }

  /**
   * Returns the number of draw calls for the last frame
   *
   * @return the number of draw calls for the last frame
   */
  @Override
  public int getDrawCalls() {
    return drawCalls;
  }

  /**
   * Returns the number of rendered {@link Character}s for the last frame
   *
   * @return the number of rendered {@link Character}s for the last frame
   */
  @Override
  public int getNbObjects() {
    return nbObjects;
  }

  /**
   * Return a Collection of all the {@link VertexArrayObject}s of this Renderer
   *
   * @return a Collection of all the {@link VertexArrayObject}s of this Renderer
   */
  @Override
  public Collection<VertexArrayObject> getVaos() {
    return Collections.singleton(vao);
  }

  /**
   * Return a Collection of all the {@link ShaderProgram}s of this Renderer
   *
   * @return a Collection of all the {@link ShaderProgram}s of this Renderer
   */
  @Override
  public Collection<ShaderProgram> getShaders() {
    return Collections.singleton(shader);
  }

  /** Prepare the Renderer for the next frame */
  public void prepare() {
    drawCalls = 0;
    nbObjects = 0;
    textures.clear();
  }
}
