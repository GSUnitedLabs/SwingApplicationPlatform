/*
 * Copyright (C) 2021 GS United Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * *****************************************************************************
 *  Project    :   SAP
 *  Class      :   StringUtils.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 4:58:40 PM
 *  Modified   :   Oct 23, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Oct 22, 2021  Sean Carrick         Initial creation.
 *  Oct 24, 2021  Sean Carrick         All unit tests for insertTabLeader and
 *                                     wrap have passed. The wrap method now
 *                                     takes into account existing newline
 *                                     characters in the source text, as well as
 *                                     hyphenated words. The wrap method also no
 *                                     longer breaks a line within the bounds of
 *                                     a word. Only whole words start a new line.
 * *****************************************************************************
 */
package com.gs.platform.utils;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class StringUtils {

    private StringUtils() {
        /* For internal use only */ }

    /**
     * This method will take the `source` string and wrap it at the specified
     * `width`.
     * <p>
     * When performing the wrapping of the `source` string, this method first
     * checks to see if there are any newline characters included within it. If
     * there are, the `source` will first be broken into parts at the newline
     * characters. Once that is done, the elements of the created array will be
     * split on the space characters. Once all of this is complete, the string
     * will be rebuilt allowing for wrapping to take place at the specified
     * `width`, without breaking within a single word.</p>
     * <p>
     * Furthermore, this method takes into account any hyphens contained within
     * the `source` string. If the built up line is getting close to the `width`
     * of the requested string and the next word will go beyond the specified
     * `width`, this method will check for a hyphen within the next word. If a
     * hyphen exists, the word will be temporarily broken on the hyphen to see
     * if that portion of the word will fit on the current line. If it will, it
     * will be added to the current line of text. If not, then the entire
     * hyphenated word will be placed at the beginning of the next line of text.
     * </p>
     *
     * @param source the source string to be wrapped
     * @param width the width at which the `source` string should be wrapped
     * @return the `source` string, properly wrapped at the specified `width`
     * @throws IllegalArgumentException if `source` is `null`, empty, or blank,
     * or if `width` is less than or equal to zero
     */
    public static String wrap(String source, int width) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }
        if (source.isBlank()) {
            throw new IllegalArgumentException("blank source");
        }
        if (source.isEmpty()) {
            throw new IllegalArgumentException("empty source");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("width <= 0");
        }

        StringBuilder sb = new StringBuilder();

        // First, split on newline characters. If there are none the lines array
        //+ will only have a single element, so the loop will only run once.
        String[] lines = source.split("\n");
        String holder = "";

        for (String line : lines) {
            // Split the line into its individual words.
            String[] words = line.split(" ");

            if (line.length() <= width) {
                sb.append(line).append("\n");
            } else {

                for (String word : words) {
                    // Check if the holder string is less than the desired width.
                    if (holder.length() < width) {
                        // Check to see if the next word and space will go beyond
                        //+ the desired width.
                        if ((holder.length() + word.length() + 1) <= width) {
                            // If not, add the space and the word to the holder.
                            if (!holder.endsWith(".")) {
                                holder += word + " ";
                            } else {
                                holder += " " + word + " ";
                            }
                        } else {
                            // If so, check to see if the next word has a hyphen.
                            if (word.contains("-")) {
                                // If so, split the word on the hyphen.
                                String[] hyphenated = word.split("-");

                                // Check if hyphenated only has a length of two.
                                if (hyphenated.length == 2) {
                                    // Check if the first element will fit within
                                    //+ the desired width.
                                    if ((holder.length() + hyphenated[0].length()
                                            + 1) <= width) {
                                        // If so, add the first element.
                                        holder += hyphenated[0];

                                        // Append holder to the StringBuilder, 
                                        //+ while adding back the hypen with
                                        //+ a newline character at the end.
                                        sb.append(holder.trim()).append("-");
                                        sb.append("\n");

                                        // Reset holder for the next line.
                                        holder = hyphenated[1] + " ";
                                    }
                                } else if (hyphenated.length > 2) {
                                    // If multiple hyphens, add the first element.
                                    holder += hyphenated[0];

                                    // Append holder to the StringBuilder, 
                                    //+ while adding back the hypen with
                                    //+ a newline character at the end.
                                    sb.append(holder.trim()).append("-").append("\n");

                                    // Add the remaining hyphenated parts to
                                    //+ holder.
                                    for (int x = 1; x < hyphenated.length; x++) {
                                        if (x > 1) {
                                            // Reset holder for the next line.
                                            holder = "";

                                            // Add back the hyphen and the next
                                            //+ part.
                                            holder += "-" + hyphenated[x];
                                        } else {
                                            // Simply add the hyphenated part.
                                            holder += hyphenated[x];
                                        }
                                    }
                                }
                            } else {
                                // The word is not hyphenated, so append holder to
                                //+ the StringBuilder, with a newline character.
                                sb.append(holder.trim()).append("\n");

                                // Reset holder and add the current word to it.
                                holder = word + " ";
                            }
                        }
                    }
                }
            }
        }

        // Once the end of the lines are reached, we need to add whatever text
        //+ is remaining in holder to our StringBuilder.
        sb.append(holder.trim());

        String ret = sb.toString().trim();

        return (ret == null || ret.isBlank() || ret.isEmpty()) ? null : ret;
    }

    /**
     * Inserts a tab leader with the specified character as the leader.
     * <p>
     * A tab leader is such that a character is inserted between two words or
     * phrases, such as:</p>
     * <pre>
     * OS.....................Microsoft Windows
     * Version...............................11
     * </pre><p>
     * When using a tab leader, the right word or phrase is right-aligned with
     * all words and phrases below it and the leader character appears to the
     * left of the word or phrase. The leader character starts on the left at
     * the right edge of the left word or phrase.<p>
     *
     * @param leftWord the word or phrase that is on the left side of the tab
     * @param rightWord the word or phrase that is on the right side of the tab
     * @param rightMargin the farthest right the text should be placed
     * @param leader the character to repeat in the intervening space between
     * the `rightWord` and `leftWord`. This character needs to be one of
     * underscore (_), period (.), dash (-), or space ( )
     * @return a string formatted with the `leftWord` separated from the
     * `rightWord` by a repeated `leader` character and the `rightWord`s
     * right-aligned
     * @throws IllegalArgumentException if `leftWord`, or `rightWord` are
     * `null`, blank, or empty; if `rightMargin` is less than the length of
     * `leftWord` and `rightWord` plus three (3); if leader is not a symbol
     */
    public static String insertTabLeader(String leftWord, String rightWord,
            int rightMargin, char leader) {
        if (leftWord == null) {
            throw new IllegalArgumentException("null leftWord");
        } else if (leftWord.isBlank()) {
            throw new IllegalArgumentException("blank leftWord");
        } else if (leftWord.isEmpty()) {
            throw new IllegalArgumentException("empty leftWord");
        }
        if (rightWord == null) {
            throw new IllegalArgumentException("null rightWord");
        } else if (rightWord.isBlank()) {
            throw new IllegalArgumentException("blank rightWord");
        } else if (rightWord.isEmpty()) {
            throw new IllegalArgumentException("empty rightWord");
        }
        if (rightMargin < (leftWord.length() + rightWord.length() + 3)) {
            throw new IllegalArgumentException("insufficient space to process");
        }
        if (leader != ' ' && leader != '-' && leader != '.' && leader != '_') {
            throw new IllegalArgumentException("invalid leader character");
        }

        int space = rightMargin - (leftWord.length() + rightWord.length());
        return leftWord + repeatChar(leader, space) + rightWord;
    }

    private static String repeatChar(char toRepeat, int times) {
        String repeated = "";

        for (int x = 1; x <= times; x++) {
            repeated += toRepeat;
        }

        return repeated;
    }

}
