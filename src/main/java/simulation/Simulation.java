/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package simulation;

import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.*;
import rendering.debug.component.ComponentDebugInterfaceProvider;
import rendering.debug.entity.EntityDebugInterfaceProvider;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.interfaces.UserInterface;
import rendering.interfaces.element.Button;
import simulation.debug.LightSourceDebugInterface;
import simulation.debug.RotationProviderComponentDebugInterface;
import simulation.entities.ExampleEntity;
import simulation.entities.LightSource;
import simulation.entities.components.RotationProviderComponent;
import simulation.renderer.LightRenderer;

public class Simulation extends AbstractLogic {

  @Override
  public void initDebugger() {
    EntityDebugInterfaceProvider.register(new LightSourceDebugInterface());
    ComponentDebugInterfaceProvider.register(new RotationProviderComponentDebugInterface());
  }

  /**
   * Initialize meshes, models and generate the scene of the simulation
   *
   * @param window the Window when the Simulation will be rendered
   * @param engine the Engine running the logic
   * @throws Exception thrown when models or textures can't be loaded
   */
  @Override
  public void init(Window window, Engine engine) throws Exception {
    super.init(window, engine);
    engine.mapEntityRenderer(LightSource.class, new LightRenderer());
    // generateEntities(50);

    LightSource light = new LightSource(new Vector2f(1f), 1, new Vector3f(1f, 0f, 0));

    Texture texture0 = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("src/main/resources/textures/texture2.png");

    TransformComponent tr0 = new TransformComponent(new Vector2f(2, 0), new Vector2f(.5f, .5f), 0);
    TransformComponent tr1 = new TransformComponent(new Vector2f(0, 2), new Vector2f(.5f, .5f), 0);
    TransformComponent tr2 = new TransformComponent(new Vector2f(-2, 0), new Vector2f(.5f, .5f), 0);
    TransformComponent tr3 = new TransformComponent(new Vector2f(0, -2), new Vector2f(.5f, .5f), 0);

    ExampleEntity parent = new ExampleEntity();
    parent
        .addComponent(new TransformComponent(new Vector2f(2, 0), new Vector2f(2, 3), 0))
        .addComponent(new RenderableComponent(texture0))
        .addComponent(new RotationProviderComponent((float) Math.PI / 5));
    ExampleEntity child0 = createChild(tr0, texture0, texture1);
    ExampleEntity child1 = createChild(tr1, texture0, texture1);
    ExampleEntity child2 = createChild(tr2, texture0, texture1);
    ExampleEntity child3 = createChild(tr3, texture0, texture1);

    // child0.addChild(light);

    parent.addChild(child0);
    parent.addChild(child1);
    parent.addChild(child2);
    parent.addChild(child3);

    // parent.getChildren().forEach(e -> scene.addEntity((ExampleEntity) e, ExampleEntity.class));
    /*parent
    .getChildren()
    .forEach(
        e ->
            e.getChildren()
                .forEach(
                    e1 -> {
                      if (e1 instanceof ExampleEntity)
                        scene.addEntity((ExampleEntity) e1, ExampleEntity.class);
                    }));*/
    scene.add(light, LightSource.class);
    // scene.addEntity(parent, ExampleEntity.class);
    UserInterface ui1 =
        new UserInterface(window, new Vector4f(1f, 1f, 0, 0.5f), "UI_1")
            .setSize(300, 200)
            .setPosition(50, 50);
    UserInterface ui2 =
        new UserInterface(window, new Vector4f(1f, 1f, 1f, 0.5f), "UI_2")
            .setSize(100, 50)
            .setPosition(450, 50);
    // UserInterface ui = new UserInterface(texture0, "UI", window);
    Button button1 =
        new Button(window, new Vector4f(1, 0, 0, 1), "Close").setSize(30, 30).setPosition(265, 5);
    Button button2 =
        new Button(window, new Vector4f(1, 0, 1, 1), "Open").setSize(80, 30).setPosition(10, 10);
    button1.onClick(() -> interfaceManager.hideInterface(ui1));
    button2.onClick(() -> interfaceManager.showInterface(ui1));

    ui1.addElement("Button1", button1);
    ui2.addElement("Button2", button2);

    interfaceManager.add(ui1);
    interfaceManager.add(ui2);
    interfaceManager.showInterface(ui2);
  }

  private ExampleEntity createChild(
      TransformComponent transform, Texture texture, Texture childTexture) {
    ExampleEntity entity = new ExampleEntity();
    entity
        .addComponent(transform)
        .addComponent(new RenderableComponent(texture))
        .addComponent(new RotationProviderComponent((float) (Math.PI * .5f)));

    RenderableComponent childRenderable = new RenderableComponent(childTexture);
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(1.25f, 0), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(0, 1.25f), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(-1.25f, 0), 0.5f, 0))
            .addComponent(childRenderable));
    entity.addChild(
        new ExampleEntity()
            .addComponent(new TransformComponent(new Vector2f(0, -1.25f), 0.5f, 0))
            .addComponent(childRenderable));

    RotationProviderComponent rotationProviderComponentChild =
        new RotationProviderComponent((float) (Math.PI * 0));
    entity.getChildren().forEach(e -> e.addComponent(rotationProviderComponentChild));

    return entity;
  }

  private void generateEntities(int nb) {
    Texture texture0 = ResourceLoader.loadTexture("src/main/resources/textures/texture.png");
    Texture texture1 = ResourceLoader.loadTexture("src/main/resources/textures/texture2.png");
    RenderableComponent r0 = new RenderableComponent(texture0);
    RenderableComponent r1 = new RenderableComponent(texture1);

    Random random = new Random();
    for (int i = 0; i < nb; i++) {
      TransformComponent transform =
          new TransformComponent(
              new Vector2f(random.nextFloat() * 20f - 10f, random.nextFloat() * 20f - 10f),
              random.nextFloat() + 0.2f,
              (float) (random.nextFloat() * Math.PI * 2f));
      scene.add(
          (ExampleEntity)
              (new ExampleEntity()
                  .addComponent(transform)
                  .addComponent(random.nextFloat() < 1f ? r0 : r1)),
          ExampleEntity.class);
    }
  }

  /**
   * Called before all the scene element will be updated, may be called multiple time per frame
   * Entities and components are automatically updated after this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void prepare(Window window, double elapsedTime) {
    scene.prepare(window);
    interfaceManager.prepare(window);
  }

  @Override
  protected void update(Window window, double elapsedTime) {
    scene.update(ExampleEntity.class, elapsedTime);
    scene.update(LightSource.class, elapsedTime);
    interfaceManager.update(elapsedTime);
  }

  /**
   * Called after all the scene element have been updated, may be called multiple time per frame
   * Entities and components are automatically updated before this call
   *
   * @param window the Window where the simulation is rendered
   * @param elapsedTime time elapsed since last update in seconds
   */
  @Override
  protected void finalize(Window window, double elapsedTime) {
    scene.finalize(window);
    interfaceManager.finalize(window);
  }
}
