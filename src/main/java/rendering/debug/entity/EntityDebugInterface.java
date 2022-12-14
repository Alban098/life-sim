/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.debug.entity;

import imgui.ImGui;
import rendering.debug.DebugUtils;
import rendering.debug.Debugger;
import rendering.entities.Entity;

public class EntityDebugInterface extends RenderableDebugInterface<Entity> {

  @Override
  public Class<Entity> getEntityClass() {
    return Entity.class;
  }

  @Override
  protected void renderTabs(Debugger caller, Entity entity) {
    drawHierarchyTab(caller, (Entity) entity);
  }

  @Override
  protected boolean showComponentTab() {
    return true;
  }

  @Override
  protected boolean showChildrenTab() {
    return true;
  }

  @Override
  protected boolean showRenderingTab() {
    return true;
  }

  private void drawHierarchyTab(Debugger caller, Entity entity) {
    if (showChildrenTab() && ImGui.beginTabItem("Hierarchy")) {
      if (entity.getParent() != null) {
        ImGui.separator();
        ImGui.textColored(255, 0, 0, 255, "Parent");
        ImGui.newLine();
        ImGui.sameLine(20);
        ImGui.beginChild("entity_parent", 250, 63);
        ImGui.separator();
        ImGui.textColored(0, 0, 255, 255, entity.getParent().getName());
        DebugUtils.drawAttrib("Type", entity.getParent().getClass().getSimpleName(), 20, 90);
        DebugUtils.drawAttrib("Children", entity.getParent().getChildren().size(), 20, 90);
        ImGui.endChild();
        if (ImGui.isItemClicked()) {
          caller.setSelectedEntity(entity.getParent());
        }
      }
      ImGui.separator();
      if (!entity.getChildren().isEmpty()) {
        ImGui.textColored(
            255, 0, 0, 255, String.format("Children (%d)", entity.getChildren().size()));
        for (Entity child : entity.getChildren()) {
          ImGui.newLine();
          ImGui.sameLine(20);
          ImGui.beginChild("entity_children_" + child.hashCode(), 280, 55);
          ImGui.separator();
          ImGui.textColored(0, 0, 255, 255, child.getName());
          DebugUtils.drawAttrib("Type", child.getClass().getSimpleName(), 20, 90);
          DebugUtils.drawAttrib("Children", child.getChildren().size(), 20, 90);
          ImGui.endChild();
          if (ImGui.isItemClicked()) {
            caller.setSelectedEntity(child);
          }
        }
      }
      ImGui.endTabItem();
    }
  }
}
