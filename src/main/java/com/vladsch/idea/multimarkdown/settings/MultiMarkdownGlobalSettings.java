/*
 * Copyright (c) 2015 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.vladsch.idea.multimarkdown.settings;

import com.intellij.ide.ui.UISettings;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.ui.UIUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.pegdown.Extensions;

@State(
        name = "MultiMarkdownSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/multimarkdown.xml")
)
public class MultiMarkdownGlobalSettings implements PersistentStateComponent<Element>, Disposable {

    final public static int HTML_THEME_DEFAULT = 0;
    final public static int HTML_THEME_DARCULA = 1;
    final public static int HTML_THEME_UI = 2;   // follow the Appearance setting of the application UI

    /** A set of listeners to this object state changes. */
    @Override public void dispose() {

    }

    protected final SettingsNotifierImpl<MultiMarkdownGlobalSettings> notifier = new SettingsNotifierImpl<MultiMarkdownGlobalSettings>(this);
    protected final Settings settings = new Settings(notifier);

    public MultiMarkdownGlobalSettings() {
        // register to settings change on behalf of our listeners. If the UI changes and htmlTheme is Application UI then notify them
        UISettings.getInstance().addUISettingsListener(new UISettingsListener() {
            @Override public void uiSettingsChanged(UISettings source) {
                if (htmlTheme.getValue() == HTML_THEME_UI) {
                    notifier.notifyListeners();
                }
            }
        }, this);
    }


    public static MultiMarkdownGlobalSettings getInstance() {
        return ServiceManager.getService(MultiMarkdownGlobalSettings.class);
    }

    // these self-add to the list of settings
    final public Settings.BooleanSetting abbreviations = settings.BooleanSetting(false, "abbreviations", Extensions.ABBREVIATIONS);
    final public Settings.BooleanSetting anchorLinks = settings.BooleanSetting(false, "anchorLinks", Extensions.ANCHORLINKS);
    final public Settings.BooleanSetting autoLinks = settings.BooleanSetting(false, "autoLinks", Extensions.AUTOLINKS);
    final public Settings.BooleanSetting definitions = settings.BooleanSetting(false, "definitions", Extensions.DEFINITIONS);
    final public Settings.BooleanSetting enableTrimSpaces = settings.BooleanSetting(false, "enableTrimSpaces", 0);
    final public Settings.BooleanSetting fencedCodeBlocks = settings.BooleanSetting(false, "fencedCodeBlocks", Extensions.FENCED_CODE_BLOCKS);
    final public Settings.BooleanSetting forceListPara = settings.BooleanSetting(false, "forceListPara", Extensions.FORCELISTITEMPARA);
    final public Settings.BooleanSetting hardWraps = settings.BooleanSetting(false, "hardWraps", Extensions.HARDWRAPS);
    final public Settings.BooleanSetting headerSpace = settings.BooleanSetting(false, "headerSpace", Extensions.ATXHEADERSPACE);
    final public Settings.BooleanSetting quotes = settings.BooleanSetting(false, "quotes", Extensions.QUOTES);
    final public Settings.BooleanSetting relaxedHRules = settings.BooleanSetting(false, "relaxedHRules", Extensions.RELAXEDHRULES);
    final public Settings.BooleanSetting showHtmlText = settings.BooleanSetting(true, "showHtmlText", 0);
    final public Settings.BooleanSetting showHtmlTextAsModified = settings.BooleanSetting(false, "showHtmlTextAsModified", 0);
    final public Settings.BooleanSetting smarts = settings.BooleanSetting(false, "smarts", Extensions.SMARTS);
    final public Settings.BooleanSetting strikethrough = settings.BooleanSetting(false, "strikethrough", Extensions.STRIKETHROUGH);
    final public Settings.BooleanSetting suppressHTMLBlocks = settings.BooleanSetting(false, "suppressHTMLBlocks", Extensions.SUPPRESS_HTML_BLOCKS);
    final public Settings.BooleanSetting suppressInlineHTML = settings.BooleanSetting(false, "suppressInlineHTML", Extensions.SUPPRESS_INLINE_HTML);
    final public Settings.BooleanSetting tables = settings.BooleanSetting(false, "tables", Extensions.TABLES);
    final public Settings.BooleanSetting taskLists = settings.BooleanSetting(false, "taskLists", Extensions.TASKLISTITEMS);
    final public Settings.BooleanSetting wikiLinks = settings.BooleanSetting(false, "wikiLinks", Extensions.WIKILINKS);
    final public Settings.BooleanSetting todoComments = settings.BooleanSetting(false, "todoComments", 0);
    final public Settings.BooleanSetting iconBullets = settings.BooleanSetting(true, "iconBullets", 0);
    final public Settings.IntegerSetting htmlTheme = settings.IntegerSetting(HTML_THEME_UI, "htmlTheme");
    final public Settings.IntegerSetting maxImgWidth = settings.IntegerSetting(900, "maxImgWidth");
    final public Settings.IntegerSetting parsingTimeout = settings.IntegerSetting(10000, "parsingTimeout");
    final public Settings.IntegerSetting updateDelay = settings.IntegerSetting(1000, "updateDelay");
    final public Settings.StringSetting customCss = settings.StringSetting("", "customCss");
    final public Settings.ElementSetting customCssEditorState = settings.ElementSetting(null, "customCssEditorState");

    public Element getState() {
        Element multiMarkdownSettings = settings.getState("MultiMarkdownSettings");
        return multiMarkdownSettings;
    }

    public void loadState(@NotNull Element element) {
        settings.loadState(element);
    }

    public boolean isDarkHtmlPreview() {
        return isDarkHtmlPreview(htmlTheme.getValue());
    }

    public boolean isDarkHtmlPreview(int htmlTheme) {
        return htmlTheme == HTML_THEME_DARCULA
                || htmlTheme == HTML_THEME_UI && UIUtil.isUnderDarcula();
    }

    public boolean isInvertedHtmlPreview() {
        return UIUtil.isUnderDarcula() != isDarkHtmlPreview();
    }

    public int getExtensionsValue() {
        return settings.getExtensionsValue();
    }

    public void addListener(@NotNull final SettingsListener<MultiMarkdownGlobalSettings> listener) {
        notifier.addListener(listener);
    }

    public void removeListener(@NotNull final SettingsListener<MultiMarkdownGlobalSettings> listener) {
        notifier.removeListener(listener);
    }

    public void startGroupNotifications() {
        notifier.startGroupNotifications();
    }

    public void endGroupNotifications() {
        notifier.endGroupNotifications();
    }

}
