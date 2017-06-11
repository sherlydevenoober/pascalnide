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

package com.duy.pascal.frontend.view.exec_screen.console;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.duy.pascal.frontend.view.exec_screen.ScreenObject;

import static android.R.attr.width;


/**
 * Text renderer interface
 */

public class TextRenderer implements ScreenObject {
    /**
     * mode text, low-high-normal
     */
    public static final int NORMAL_TEXT_ALPHA = 200;
    public static final int LOW_TEXT_ALPHA = 150;
    public static final int HIGH_TEXT_ALPHA = 255;
    private static final String TAG = "TextRenderer";
    /**
     * character attributes
     */
    private int mCharHeight;
    private int mCharAscent;
    private int mCharDescent;
    private float mCharWidth;
    private int mTextMode = 0;
    private Typeface typeface = Typeface.MONOSPACE;
    private Paint mTextPaint = new Paint();
    private Paint backgroundPaint = new Paint();
    private int textColor = Color.WHITE;
    private int textBackgroundColor = Color.BLACK;
    private int alpha = NORMAL_TEXT_ALPHA;
    private int textWidth = 2;
    private boolean fixedWidthFont;

    public TextRenderer(float textSize) {
        init(textSize);
    }

    public int getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public void setTextBackgroundColor(int color) {
        this.textBackgroundColor = color;
    }

    private void init(float textSize) {
        mTextPaint.setTypeface(typeface);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);

        mCharHeight = (int) Math.ceil(mTextPaint.getFontSpacing());
        mCharAscent = (int) Math.ceil(mTextPaint.ascent());
        mCharDescent = mCharHeight + mCharAscent;
        mCharWidth = mTextPaint.measureText("M");
        this.fixedWidthFont = mCharWidth == mTextPaint.measureText(".");
    }


    /**
     * draw text
     *
     * @param x     - start x
     * @param y     - start y
     * @param text  - input text
     * @param start - start index of array text[]
     */
    public void drawText(Canvas canvas, int x, int y, char[] text, int start, int count) {
        canvas.drawText(text, start, count, x, y, mTextPaint);
    }

    public void drawText(Canvas canvas, int x, int y, String text, int start, int count) {
        canvas.drawText(text, start, start + count, x, y, mTextPaint);
    }

    public void drawText(Canvas canvas, float x, float y, TextConsole[] text, int start, int count) {
        float y1 = y + mCharAscent;
        for (int i = start; i < start + count; i++) {
            if (!fixedWidthFont) mCharWidth = mTextPaint.measureText(text[i].getSingleString());

            backgroundPaint.setColor(text[i].getTextBackground());
            canvas.drawRect(x, y + mCharAscent, x + width, y + mCharDescent, backgroundPaint);


            mTextPaint.setColor(text[i].getTextColor());
            mTextPaint.setAlpha(text[i].getAlpha());
            canvas.drawText(text[i].getSingleString(), x, y, mTextPaint);

            x += mCharWidth;
        }
    }

    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        mTextPaint.setTypeface(typeface);
    }

    public int getCharHeight() {
        return mCharHeight;
    }

    public void setCharHeight(int mCharHeight) {
        this.mCharHeight = mCharHeight;
    }

    public int getCharAscent() {
        return mCharAscent;
    }

    public void setCharAscent(int mCharAscent) {
        this.mCharAscent = mCharAscent;
    }

    public int getCharDescent() {
        return mCharDescent;
    }

    public void setCharDescent(int mCharDescent) {
        this.mCharDescent = mCharDescent;
    }

    public float getCharWidth() {
        return mCharWidth;
    }

    public void setCharWidth(int mCharWidth) {
        this.mCharWidth = mCharWidth;
    }


    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextMode() {
        return mTextMode;
    }

    public void setTextMode(int mTextMode) {
        this.mTextMode = mTextMode;
    }

    void setReverseVideo(boolean reverseVideo) {

    }

    float getCharacterWidth() {
        return 0;

    }

    int getCharacterHeight() {
        return 0;

    }

    @Override
    public void draw(Canvas canvas) {

    }

    public int getBackgroundColor() {
        return textBackgroundColor;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
}
