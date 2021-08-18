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
 *  Project    :   NTA-Basic
 *  Class      :   LogRotate.java
 *  Author     :   Kevin Nathan
 *  Created    :   Jul 31, 2021 @ 9:46:17 PM
 *  Modified   :   Jul 31, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Jul 31, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kevin Nathan &lt;knathan at linux54 dot com&gt;
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class LogRotate {

    private final String appId;
    final private ArrayList<String> fileNames;
    private String logDir;
    private Integer maxBackups;
    private String logBaseName;

    /**
     * Creates a new instance of the {@code LogRotate} class.
     *
     * @param appId the {@code org.jdesktop.Application} object in which this
     * instance is being used.
     * @param maxBackups the maximum number of backups to keep. This number
     * <strong><em>does not</em></strong> include the current file.
     */
    public LogRotate(String appId, int maxBackups) {
        fileNames = new ArrayList<>();
        this.appId = appId;
        this.maxBackups = maxBackups;
        logDir = FileUtils.getApplicationLogDirectory(
                appId).getAbsolutePath();
    }

    /**
     *
     * @return name of directory containing the log files to use.
     */
    public String getLogDir() {
        return logDir;
    }

    /**
     *
     * @param logDir name of directory containing the log files to use.
     */
    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    /**
     *
     * @return the maximum number of backups to keep. This number
     * <strong><em>does not</em></strong> include the current file.
     * 
     */
    public Integer getMaxBackups() {
        return maxBackups;
    }

    /**
     *
     * @param maxBackups the maximum number of backups to keep. This number
     * <strong><em>does not</em></strong> include the current file.
     * 
     */
    public void setMaxBackups(Integer maxBackups) {
        this.maxBackups = maxBackups;
    }

    /**
     *
     * @return log filename to analyze, minus the extensions.
     * 
     */
    public String getLogBaseName() {
        return logBaseName;
    }

    /**
     *
     * @param logBaseName log filename to analyze, minus the extensions.
     * 
     */
    public void setLogBaseName(String logBaseName) {
        this.logBaseName = logBaseName;
    }

    /**
     *
     * @param logBaseName log filename to analyze, minus the extensions.
     * @throws IOException
     */
    public void rotateLogFiles(String logBaseName) throws IOException {
        this.logBaseName = logBaseName;
        ArrayList<File> files;
        files = getFiles();

        File tmpFile;
        String tmpFilename;
        int extensionPosition;
        String extensionString;
        boolean hadDeletions = false;
        for (int i = 0; i < files.size(); i++) {
            tmpFile = files.get(i);
            tmpFilename = tmpFile.getName();
            extensionPosition = tmpFilename.lastIndexOf('.');
            extensionString = tmpFilename.substring(extensionPosition + 1);
            if (extensionString.equalsIgnoreCase("log")) {
                // continue;
            } else {
                if (Integer.parseInt(extensionString) >= this.maxBackups) {
                    if (tmpFile.delete()) {
                        hadDeletions = true;
                    }
                }
            }
        }
        files = getFiles(); // Re-read directory

        Integer originalFileIndex = -1;
        if (hadDeletions) {
            String baseFileName;
            String newFileName;
            for (int i = maxBackups - 1; i > -1; i--) {
                tmpFilename = files.get(i).getName();
                extensionPosition = tmpFilename.lastIndexOf('.');
                extensionString = tmpFilename.substring(extensionPosition + 1);
                baseFileName = tmpFilename.substring(0, extensionPosition);
                if (!extensionString.equalsIgnoreCase("log")) {
                    extensionString = bumpExtension(extensionString);
                    newFileName = baseFileName + "." + extensionString;
                    renameLogFile(files.get(i), newFileName);
                } else { // active log shows up first, rename later
                    originalFileIndex = i;
                }
            }
            if (originalFileIndex >= 0) {
                renameLogFile(files.get(originalFileIndex), files.get(originalFileIndex).getName() + ".1");
            }
        }
    }

    private ArrayList<File> getFiles() {
        File topDir = new File(logDir);
        Pattern MY_PATTERN = Pattern.compile(logBaseName + ".*log.*");
        ArrayList<File> matchFiles = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>(Arrays.asList(topDir.listFiles()));
        files.forEach(file -> {
            Matcher m = MY_PATTERN.matcher(file.getName());
            if (m.find()) {
                matchFiles.add(file);
            }
        });
        if (matchFiles.size() > 0) {
            Collections.sort(matchFiles);
            return matchFiles;
        } else {
            return null;
        }
    }

    private String bumpExtension(String oldExtension) {
        Integer newExtension = (Integer.parseInt(oldExtension) + 1);
        return newExtension.toString();
    }

    private void renameLogFile(File orgFile, String newName) {
        Path source = Paths.get(orgFile.getPath()); // Java.nio.file is supposed to be platform independent
        try {
            // rename a file in the same directory
            Files.move(source, source.resolveSibling(newName));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

}
