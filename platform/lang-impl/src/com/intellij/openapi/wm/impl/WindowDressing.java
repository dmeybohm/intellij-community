/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.wm.impl;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bas Leijdekkers
 */
public class WindowDressing extends AbstractProjectComponent {

  public WindowDressing(@NotNull Project project) {
    super(project);
  }

  public void projectOpened() {
    getWindowActionGroup().addProject(myProject);
  }

  public void projectClosed() {
    getWindowActionGroup().removeProject(myProject);
  }

  public static ProjectWindowActionGroup getWindowActionGroup() {
    return (ProjectWindowActionGroup)ActionManager.getInstance().getAction("OpenProjectWindows");
  }
}
