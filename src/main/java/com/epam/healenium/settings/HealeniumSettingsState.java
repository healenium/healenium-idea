// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.epam.healenium.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "com.epam.healenium.settings.HealeniumSettingsState",
        storages = @Storage("SdkSettingsPlugin.xml")
)
public class HealeniumSettingsState implements PersistentStateComponent<HealeniumSettingsState> {

  private String serverUrl = "http://localhost:7878";

  public static HealeniumSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(HealeniumSettingsState.class);
  }

  @Nullable
  @Override
  public HealeniumSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull HealeniumSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String url) {
    this.serverUrl = url;
  }
}
