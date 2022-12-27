/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.scene;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rendering.entities.Entity;
import rendering.renderers.MasterRenderer;

public class Scene {

  private static final Logger LOGGER = LoggerFactory.getLogger(Scene.class);

  private final Map<Class<? extends Entity>, List<Entity>> objects;
  private final MasterRenderer renderer;

  private int nbObjects = 0;

  public Scene(MasterRenderer renderer) {
    this.objects = new HashMap<>();
    this.renderer = renderer;
  }

  public void cleanUp() {
    for (Map.Entry<Class<? extends Entity>, List<Entity>> entry : objects.entrySet()) {
      entry.getValue().forEach(Entity::cleanUpInternal);
    }
  }

  public <T extends Entity> void add(T object, Class<T> type) {
    objects.computeIfAbsent(type, t -> new ArrayList<>());
    objects.get(type).add(object);
    nbObjects++;

    renderer.registerEntity(object, type);
    LOGGER.trace("Added an object of type [{}] to the scene", object.getClass().getName());
  }

  public <T extends Entity> void remove(T object, Class<T> type) {
    List<Entity> list = objects.get(type);
    if (list != null) {
      if (list.remove(object)) {
        nbObjects--;
        if (list.isEmpty()) {
          objects.remove(type);
        }
      }
    }
    renderer.unregisterEntity(object, type);
    LOGGER.trace("Removed an object of type [{}] from the scene", object.getClass().getName());
  }

  public List<? extends Entity> getObjects(Class<? extends Entity> ofType) {
    return objects.getOrDefault(ofType, Collections.emptyList());
  }

  public int getTotalObjects() {
    return nbObjects;
  }

  public Collection<Class<? extends Entity>> getTypes() {
    return objects.keySet();
  }

  public void update(Class<? extends Entity> entityClass, double elapsedTime) {
    for (Entity e : getObjects(entityClass)) {
      e.updateInternal(elapsedTime);
    }
  }

  public void update(double elapsedTime) {
    for (Class<? extends Entity> entityClass : getTypes()) {
      update(entityClass, elapsedTime);
    }
  }
}
