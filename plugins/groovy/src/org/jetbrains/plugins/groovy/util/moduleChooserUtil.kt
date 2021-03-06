// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:JvmName("ModuleChooserUtil")

package org.jetbrains.plugins.groovy.util

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ui.configuration.ModulesAlphaComparator
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.ui.popup.ListPopupStep
import com.intellij.openapi.util.Condition
import com.intellij.util.Consumer
import com.intellij.util.Function
import java.util.*

private const val GROOVY_LAST_MODULE = "Groovy.Last.Module.Chosen"

fun selectModule(project: Project,
                 modules: List<Module>,
                 titleProvider: Function<Module, String>,
                 consumer: Consumer<Module>) {
  modules.singleOrNull()?.let {
    consumer.consume(it)
    return
  }
  Collections.sort(modules, ModulesAlphaComparator.INSTANCE)
  createSelectModulePopup(project, modules, { titleProvider.`fun`(it) }, { consumer.consume(it) }).showCenteredInCurrentWindow(project)
}

fun createSelectModulePopup(project: Project,
                            modules: List<Module>,
                            titleProvider: (Module) -> String,
                            consumer: (Module) -> Unit): ListPopup {
  val step = createSelectModulePopupStep(project, modules, titleProvider, consumer)
  return JBPopupFactory.getInstance().createListPopup(step)
}


fun createSelectModulePopupStep(project: Project,
                                modules: List<Module>,
                                titleProvider: (Module) -> String,
                                consumer: (Module) -> Unit): ListPopupStep<Module> {
  val propertiesComponent = PropertiesComponent.getInstance(project)

  val step = GroovySelectModuleStep(modules, titleProvider) {
    propertiesComponent.setValue(GROOVY_LAST_MODULE, it.name)
    consumer(it)
  }

  propertiesComponent.getValue(GROOVY_LAST_MODULE)?.let { lastModuleName ->
    step.defaultOptionIndex = modules.indexOfFirst { it.name == lastModuleName }
  }

  return step
}

fun formatModuleVersion(module: Module, version: String): String = "${module.name} (${version})"

fun filterGroovyCompatibleModules(modules: Collection<Module>, condition: Condition<Module>): List<Module> {
  return filterGroovyCompatibleModules(modules, condition.toPredicate())
}

fun filterGroovyCompatibleModules(modules: Collection<Module>, condition: (Module) -> Boolean): List<Module> {
  return modules.filter(isGroovyCompatibleModule(condition))
}

fun hasGroovyCompatibleModules(modules: Collection<Module>, condition: Condition<Module>): Boolean {
  return hasGroovyCompatibleModules(modules, condition.toPredicate())
}

fun hasGroovyCompatibleModules(modules: Collection<Module>, condition: (Module) -> Boolean): Boolean {
  return modules.any(isGroovyCompatibleModule(condition))
}

private inline fun isGroovyCompatibleModule(crossinline condition: (Module) -> Boolean): (Module) -> Boolean {
  val sdkTypeCheck = { it: Module ->
    val sdk = ModuleRootManager.getInstance(it).sdk
    sdk != null && sdk.sdkType is JavaSdkType
  }
  return sdkTypeCheck and condition
}
