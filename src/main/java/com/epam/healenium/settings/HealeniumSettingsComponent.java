// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.epam.healenium.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class HealeniumSettingsComponent {

  private final JPanel hlmPanel;
  private final JBTextField hlmBackendUrlField = new JBTextField();

  public HealeniumSettingsComponent() {
    hlmPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Server URL: "), hlmBackendUrlField, 1, false)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
  }

  public JPanel getPanel() {
    return hlmPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return hlmBackendUrlField;
  }

  @NotNull
  public String getServerUrl() {
    return hlmBackendUrlField.getText();
  }

  public void setServerUrl(String newText) {
    hlmBackendUrlField.setText(newText);
  }

}
