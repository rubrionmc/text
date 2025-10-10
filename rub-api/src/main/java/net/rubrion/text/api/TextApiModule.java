/**
 * RPL-LICENSE NOTICE
 * <br><br>
 * This Sourcecode is under the RPL-LICENSE. <br>
 * License at: <a href="https://github.com/rubrionmc/.github/blob/main/licensens/RUBRION_PUBLIC">GITHUB</a>
 * <br><br>
 * Copyright (c) LeyCM <leycm@proton.me> <br>
 * Copyright (c) maintainers <br>
 * Copyright (c) contributors
 */
package net.rubrion.text.api;

import net.kyori.adventure.text.Component;
import net.rubrion.common.api.api.ApiModule;
import net.rubrion.text.api.adapter.TextAdapter;
import net.rubrion.text.api.lang.Label;
import net.rubrion.text.api.lang.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface TextApiModule extends ApiModule {

    Component translate(String key, Component text, Locale locale, TextAdapter<String> adapter);

    void reload();

    TextAdapter<String> readerAdapter();

    TextAdapter<String> adapterFor(String key);

    Label createLabel(String key, @Nullable String text, @NotNull TextAdapter<String> adapter, Translator translator);
    Label createLabel(String label);

}
