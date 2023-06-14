// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.epam.healenium.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class HealeniumSettingsConfigurable implements Configurable {

  private HealeniumSettingsComponent settingsComponent;

  // A default constructor with no arguments is required because this implementation
  // is registered as an applicationConfigurable EP

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Healenium Plugin";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    settingsComponent = new HealeniumSettingsComponent();
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    HealeniumSettingsState settings = HealeniumSettingsState.getInstance();
    return !settingsComponent.getServerUrl().equals(settings.getServerUrl());
  }

  @Override
  public void apply() {
    HealeniumSettingsState settings = HealeniumSettingsState.getInstance();
    settings.setServerUrl(settingsComponent.getServerUrl());
  }

  @Override
  public void reset() {
    HealeniumSettingsState settings = HealeniumSettingsState.getInstance();
    settingsComponent.setServerUrl(settings.getServerUrl());
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

}
