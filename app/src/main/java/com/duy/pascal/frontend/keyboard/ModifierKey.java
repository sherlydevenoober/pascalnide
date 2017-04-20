/*
 *  Copyright 2017 Tran Le Duy
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

package com.duy.pascal.frontend.keyboard;

public class ModifierKey {
    /**
     * The state engine for a modifier key. Can be pressed, released, locked,
     * and so on.
     */
    private static final int UNPRESSED = 0;
    private static final int PRESSED = 1;
    private static final int RELEASED = 2;
    private static final int USED = 3;
    private static final int LOCKED = 4;
    private static final int ACTIVE = 5;
    private static final int OFF = 6;
    private int mState;

    /**
     * Construct a modifier key. UNPRESSED by default.
     */
    public ModifierKey() {
//            mState = UNPRESSED;
        mState = OFF;
    }

    public void onPress() {
            /*switch (mState) {
            case PRESSED:
                // This is a repeat before use
                break;
            case RELEASED:
                mState = LOCKED;
                break;
            case USED:
                // This is a repeat after use
                break;
            case LOCKED:
                mState = UNPRESSED;
                break;
            default:
                mState = PRESSED;
                break;
            }*/
        mState = ACTIVE;
    }

    public void onRelease() {
            /*switch (mState) {
            case USED:
                mState = UNPRESSED;
                break;
            case PRESSED:
                mState = RELEASED;
                break;
            default:
                // Leave state alone
                break;
            }*/
        mState = OFF;
    }

    public void adjustAfterKeypress() {
        mState = OFF;

            /*switch (mState) {
            case PRESSED:
                mState = USED;
                break;
            case RELEASED:
                mState = UNPRESSED;
                break;
            default:
                // Leave state alone
                break;
            }*/
    }

    public boolean isActive() {
//            return mState != UNPRESSED;
        return mState == ACTIVE;
    }
}
