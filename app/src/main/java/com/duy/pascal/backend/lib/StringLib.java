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

package com.duy.pascal.backend.lib;

import com.duy.pascal.backend.lib.annotations.PascalMethod;
import com.js.interpreter.runtime.VariableBoxer;
import com.js.interpreter.runtime.exception.RuntimePascalException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringLib implements PascalLibrary {


    /**
     * lower case to upper case
     *
     * @param s - input
     * @return - out with upper case
     */
    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")

    public static char upcase(Character s) {
        return Character.toUpperCase(s);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static StringBuilder concat(StringBuilder... s) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object s1 : s) stringBuilder.append(s1);
        return stringBuilder;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int pos(String substring, String s) {
        return s.indexOf(substring) + 1;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int length(String s) {
        System.out.println("length " + s + " " + s.length());
        return s.length();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String replace(String text, String tofind, String replacement) {
        return text.replaceAll("\\Q" + tofind, replacement);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static boolean endswith(String suffix, String tosearch) {
        return tosearch.endsWith(suffix);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String findregex(String tosearch, String regex) {
        Pattern reg = Pattern.compile(regex);
        Matcher m = reg.matcher(tosearch);
        if (!m.find()) {
            return "";
        }
        return tosearch.substring(m.start(), m.end());
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String between(String s1, String s2, String s) {
        int startindex = s.indexOf(s1) + s1.length();
        int endindex = s.indexOf(s2, startindex);
        return s.substring(startindex, endindex);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String capitalize(String s) {
        boolean lastSpace = true;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (lastSpace) {
                chars[i] = Character.toUpperCase(chars[i]);
                lastSpace = false;
            }
            if (chars[i] == ' ') {
                lastSpace = true;
            }
        }
        return new String(chars);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String getletters(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String getnumbers(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String getothers(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static boolean InStrArr(String Str, String[] Arr,
                                   boolean casesensitive) {
        for (String s : Arr) {
            if (casesensitive ? s.equals(Str) : s.equalsIgnoreCase(Str)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static void insert(String s, VariableBoxer<StringBuilder> s1, int pos)
            throws RuntimePascalException {
        s1.set(new StringBuilder(s1.get().insert(pos - 1, s)));
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int LastPosEx(String tofind, String findin, int from) {
        return findin.lastIndexOf(tofind, from);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int LastPos(String tofind, String findin) {
        return findin.lastIndexOf(tofind);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String Left(String s, int count) {
        return s.substring(0, count);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String md5(String s) throws NoSuchAlgorithmException {
        MessageDigest digester = MessageDigest.getInstance("MD5");
        digester.update(s.getBytes());
        return new BigInteger(1, digester.digest()).toString(16);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String padl(String topad, int size) {
        StringBuilder result = new StringBuilder(size);
        for (int i = topad.length(); i < size; i++) {
            result.append(' ');
        }
        result.append(topad);
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String padr(String topad, int size) {
        StringBuilder result = new StringBuilder(size);
        result.append(topad);
        for (int i = topad.length(); i < size; i++) {
            result.append(' ');
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String padz(String topad, int size) {
        StringBuilder result = new StringBuilder(size);
        for (int i = topad.length(); i < size; i++) {
            result.append('0');
        }
        result.append(topad);
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int posex(String tofind, String s, int startindex) {
        return s.indexOf("\\Q" + tofind, startindex);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static int regexpos(String text, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        m.find();
        int i = m.start();
        if (i >= 0) {
            i++;
        }
        return i;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String replaceregex(String text, String regex,
                                      String replacetext) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        return m.replaceAll(replacetext);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String replicate(char c, int times) {
        StringBuilder result = new StringBuilder(times);
        for (int i = 0; i < times; i++) {
            result.append(c);
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String right(String s, int length) {
        return s.substring(s.length() - length, s.length());
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static void setlength(VariableBoxer<StringBuilder> s, int length)
            throws RuntimePascalException {
        String filler = "!@#$%";
        StringBuilder old = s.get();
        if (length <= old.length()) {
            s.set(new StringBuilder(old.subSequence(0, length)));
        } else {
            int extra = length - old.length();
            int count = extra / filler.length();
            StringBuilder result = new StringBuilder(length);
            result.append(old);
            for (int i = 0; i < count; i++) {
                result.append(filler);
            }
            int remaining = extra - count * filler.length();
            result.append(filler.subSequence(0, remaining));
            s.set(result);
        }
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static boolean startswith(String prefix, String s) {
        return s.startsWith(prefix);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static char strget(VariableBoxer<StringBuilder> s, int index)
            throws RuntimePascalException {
        return s.get().charAt(index);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static void strset(char c, int index, VariableBoxer<StringBuilder> s)
            throws RuntimePascalException {

        s.get().setCharAt(index, c);
        s.set(s.get());
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String stringofchar(char c, int times) {
        return replicate(c, times);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String trimex(String delimeter, String s) {
        int beginningindex = 0;
        while (s.startsWith(delimeter, beginningindex)) {
            beginningindex += delimeter.length();
        }
        int endindex = s.length();
        while (s.lastIndexOf(delimeter, endindex) == endindex
                - delimeter.length()) {
            endindex -= delimeter.length();
        }
        return s.substring(beginningindex, endindex);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String trim(String s) {
        return s.trim();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String trimletters(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetter(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String trimnumbers(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static String trimothers(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * function copy in Pascal
     *
     * @param s     - source string
     * @param ifrom - start index
     * @param count - count
     * @return
     */
    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static StringBuilder copy(String s, int ifrom, int count) {
        return new StringBuilder(s.substring(ifrom - 1, ifrom - 1 + count));
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "none", returns = "void")
    public static void delete(VariableBoxer<StringBuilder> s, int start, int count)
            throws RuntimePascalException {
        s.set(s.get().delete(start - 1, start + count - 1));
    }

    @Override
    public boolean instantiate(Map<String, Object> pluginargs) {
        return true;
    }
}
