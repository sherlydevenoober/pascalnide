/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.interperter.builtin_libraries.android.utils;

import android.content.Context;

import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContextMixin;
import com.duy.pascal.interperter.builtin_libraries.PascalLibrary;
import com.duy.pascal.interperter.builtin_libraries.android.AndroidLibraryManager;
import com.duy.pascal.interperter.builtin_libraries.annotations.PascalMethod;
import com.duy.pascal.interperter.builtin_libraries.annotations.PascalParameter;
import com.duy.pascal.frontend.utils.clipboard.ClipboardManagerCompat;
import com.duy.pascal.frontend.utils.clipboard.ClipboardManagerCompatFactory;

import java.util.Map;

/**
 * Created by Duy on 25-Apr-17.
 */

public class AndroidClipboardLib implements PascalLibrary {
    public static final String NAME = "aclipboard";
    private final Context mContext;
    private ClipboardManagerCompat mClipboard = null;


    public AndroidClipboardLib(AndroidLibraryManager manager) {
        mContext = manager.getContext();
        if (manager.getContext() != null) {
            mClipboard = ClipboardManagerCompatFactory.newInstance(mContext);
        }
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Read text from the clipboard.", returns = "The text in the clipboard.")
    public StringBuilder getClipboard() {
        CharSequence text = getClipboardManager().getText();
        return new StringBuilder(text == null ? "" : text);
    }

    /**
     * Creates a new AndroidFacade that simplifies the interface to various Android APIs.
     *
     * @param text is the {@link Context} the APIs will run under
     */

    @SuppressWarnings("unused")
    @PascalMethod(description = "Put text in the clipboard.")
    public void setClipboard(@PascalParameter(name = "text") StringBuilder text) {
        ClipboardManagerCompat manager = getClipboardManager();
        manager.setText(text);
    }

    @Override
    public boolean instantiate(Map<String, Object> pluginargs) {
        return false;
    }

    @Override
    @PascalMethod(description = "stop")

    public void shutdown() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void declareConstants(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareTypes(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareVariables(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareFunctions(ExpressionContextMixin parentContext) {

    }

    private ClipboardManagerCompat getClipboardManager() {
        return mClipboard;
    }
}
