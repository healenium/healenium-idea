# healenium 
Healenium IntelliJ IDEA plugin for updating selenium locators via context menu
======================

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/14178-healenium.svg)](https://img.shields.io/jetbrains/plugin/v/14178-healenium)

## Plugin for [IntelliJ IDEA](https://plugins.jetbrains.com/plugin/14178-healenium/versions) to support [Healenium](https://github.com/healenium/healenium-web). 
Plugin ID: com.epam.healenium.hlm-idea

To perform updates in code just click with right button on failed locator and select Healing results action. 
After this select new locator with highest score in dropdown list
![](https://i.imgur.com/ov8o9Lt.png)

**Last version (1.0.3) released on 12.11.2020**

Install it automatically from IntelliJ Idea plugin repository.

Tested and supports IntelliJ versions: 2020.1

Installation
------------
### Plugin Installation
- Using IDE built-in plugin system on Windows:
  - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Locator Updater"</kbd> > <kbd>Install Plugin</kbd>
- Using IDE built-in plugin system on MacOs:
  - <kbd>Preferences</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "Locator Updater"</kbd> > <kbd>Install Plugin</kbd>
<!-- 
- Manually:
  - Download the [latest release](https://github.com/mplushnikov/lombok-intellij-plugin/releases/latest) and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>
-->
Restart IDE.

### Plugin project dependency
Make sure you have Healenium dependency added to your project. This plugin **does not** automatically add it for you.

**Please Note:** Using newest version of the Healenium dependency is recommended, but does not guarantee that all the features introduced will be available. <!-- See [Lombok changelog](https://projectlombok.org/changelog.html) for more details. -->
