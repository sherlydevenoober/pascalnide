package com.duy.pascal.interperter.source_include;

import android.support.annotation.Nullable;

import java.io.Reader;

public interface ScriptSource {
    /**
     * List possible script contents by name.
     *
     * @return list of source names
     */
    @Nullable
    String[] list();

    /**
     * Open a stream to a given source content.
     *
     * @param scriptname The name of the source
     * @return A reader attached to that source
     */
    @Nullable
    Reader read(String scriptname);
}