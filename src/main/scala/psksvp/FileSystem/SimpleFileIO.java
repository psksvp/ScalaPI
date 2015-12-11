/**
 The BSD 3-Clause License
 Copyright (c) 2015, Pongsak Suvanpong (psksvp@gmail.com)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package psksvp.FileSystem;

/**
 * Created by psksvp on 28/07/2014.
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.reflect.Field;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 *
 * @author @author Pongsak Suvanpong (psksvp@gmail.com)
 * provide a common functions for manipulating simple file io.
 */
public class SimpleFileIO
{
    //private static Logger log = LogManager.getLogger(SimpleFileIO.class.getName());

    /**
     *
     * @param path
     * @return File object of path represented in String.
     */
    public static java.io.File makeFileFromPath(String path)
    {
        return new java.io.File(path);
    }

    /**
     * read text file from within jar file where loaderObject resides..
     * @param loaderObject - object who wants to load the file.
     * @param pathInJar - absolute path in the jar file
     * @return String object of the content of the text file, return empty string if file is not found.
     * @throws IOException
     */
    public static String readTextFromSelfJarBundleFile(Object loaderObject, String pathInJar) throws IOException
    {
        InputStream is = loaderObject.getClass().getResourceAsStream(pathInJar); //"/META-INF/MANIFEST.MF");
        if(null != is)
            return SimpleFileIO.readTextFromInputStream(is);
        else
        {
            //log.error("path not found->" + pathInJar );
            return "";
        }
    }

    /**
     * read text from InputStream objecy
     * @param is - InputStream object
     * @return String object with content of the InputStream
     * @throws IOException
     */
    public static String readTextFromInputStream(InputStream is) throws IOException
    {
        StringBuilder inputStringBuilder = new StringBuilder();
        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line = bufferedReader.readLine();
        while(line != null)
        {
            inputStringBuilder.append(line);
            inputStringBuilder.append("\n");
            line = bufferedReader.readLine();
        }
        return inputStringBuilder.toString();
    }

    /**
     * read text from file
     * @param filePath - String object of path to the file.
     * @return  String object with content of the file
     * @throws java.io.IOException
     */
    public static String readTextFromFile(String filePath) throws java.io.IOException
    {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1)
        {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    public static String readStringFromFile(String filePath) throws java.io.IOException
    {
        StringBuilder fileData = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int ch = 0;
        while((ch = reader.read()) != -1)
        {
            fileData.append((char)ch);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * write String object to a text file
     * @param stringToWrite - String  object to write.
     * @param pathToOutputFile - String object of path to file where the content of this text file to be written to
     * @throws IOException
     */
    public static void writeStringToTextFile(String stringToWrite, String pathToOutputFile) throws IOException
    {
        File newTextFile = new File(pathToOutputFile);
        FileWriter fw = new FileWriter(newTextFile);
        fw.write(stringToWrite);
        fw.close();
    }

    public static void writeAbsolutePathOfFilesInListToFile(java.util.List<File> listOfFiles, String outputFilePath) throws FileNotFoundException
    {
        PrintWriter pw = new PrintWriter(new File(outputFilePath));
        for(File file : listOfFiles)
        {
            pw.println(file.getAbsolutePath());
        }
        pw.close();
    }

    public static java.util.List<File> readListOfFileFromFile(String inputFilePath) throws FileNotFoundException
    {
        java.util.List<File> listOfFile = new java.util.LinkedList<File>();
        Scanner scanner = new Scanner(new File(inputFilePath));
        while(true == scanner.hasNextLine())
        {
            listOfFile.add(new File(scanner.nextLine()));
        }

        return listOfFile;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public static File currentWorkingDirectory() throws IOException
    {
        return new java.io.File( "." );
    }

    /**
     *
     */
    public static boolean inCurrentWorkingDirectoryCreateDirectoryName(String directoryName)
    {
        String path = "." + File.separator + directoryName;
        return (new File(path)).mkdir();
    }

    /**
     *
     * @param parentDirectory
     * @param containName
     * @return
     */
    public static File findADirecotryInDirectory(File parentDirectory, String containName)
    {
        File[] folders = parentDirectory.listFiles();
        for(File aFolder : folders)
        {
            if(true == aFolder.isDirectory())
            {
                if(true == aFolder.getName().contains(containName))
                {
                    return aFolder;
                }
            }
        }

        return null;
    }

    /**
     *
     * @param pathToDirectory
     * @return
     * @throws IOException
     */
    public static File[] filesInDirectory(String pathToDirectory) throws IOException
    {
        return SimpleFileIO.filesInDirectory(new File(pathToDirectory));
    }

    /**
     *
     * @param folder
     * @return
     * @throws IOException
     */
    public static File[] filesInDirectory(File folder) throws IOException
    {
        return folder.listFiles();
    }

    /**
     *
     * @param dir - File object for the directory to search
     * @param extensionFilter - String object for the filter
     * @return list of File object of files found.
     */
    public static java.util.List<java.io.File> traverseDirectoryForFiles(java.io.File dir, String extensionFilter)
    {
        if(true == dir.isDirectory())
        {
            java.util.LinkedList<java.io.File> resultList = new java.util.LinkedList<java.io.File>();
            for(java.io.File fb : dir.listFiles())
            {
                java.util.List<java.io.File> list = traverseDirectoryForFiles(fb, extensionFilter);
                if(null != list)
                    resultList.addAll(list);
            }
            return resultList;
        }
        else if(null != extensionFilter)
        {
            if(false == dir.isHidden() && true == extensionFilter.equalsIgnoreCase(SimpleFileIO.extensionOfFileName(dir.getName())))
            {
                java.util.LinkedList<java.io.File> result = new java.util.LinkedList<java.io.File>();
                result.add(dir);
                return result;
            }
            else
            {
                return null;
            }
        }
        else
        {
            if(false == dir.isHidden())
            {
                java.util.LinkedList<java.io.File> result = new java.util.LinkedList<java.io.File>();
                result.add(dir);
                return result;
            }
            else
            {
                return null;
            }
        }
    }


    /**
     *
     * @param fileName
     * @return
     */
    public static String fileNameWithOutExtension(String fileName)
    {
        int indexOfDot = fileName.lastIndexOf(".");
        if( 0 < indexOfDot )
            return fileName.substring(0, indexOfDot);
        else
            return fileName;
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String extensionOfFileName(String fileName)
    {
        int indexOfDot = fileName.lastIndexOf(".");
        if( 0 < indexOfDot && indexOfDot + 1 < fileName.length())
            return fileName.substring(indexOfDot+1);
        else
            return fileName;
    }

    /**
     *  unzip a zip file to a directory
     * @param theZipFile
     * @param directory
     * @return
     * @throws IOException
     */
    public static File unzipFile(File theZipFile, File directory) throws IOException
    {
        File topFolder = null;
        boolean virgin = true;
        ZipFile zfile = new ZipFile(theZipFile);
        Enumeration<? extends ZipEntry> entries = zfile.entries();
        while (entries.hasMoreElements())
        {
            ZipEntry entry = entries.nextElement();
            String fileName = entry.getName();
            if(0 <= fileName.indexOf("__MACOSX") || 0 <= fileName.indexOf(".DS_Store"))
            {
                //psksvp.common.Log.message("skiping -->" + fileName);
            }
            else
            {
                File file = new File(directory, entry.getName());
                if (entry.isDirectory())
                {
                    file.mkdirs();
                    if(true == virgin)
                    {
                        topFolder = file;
                        virgin = false;
                    }
                }
                else
                {
                    file.getParentFile().mkdirs();
                    InputStream in = zfile.getInputStream(entry);
                    try
                    {
                        copy(in, file);
                    }
                    finally
                    {
                        in.close();
                    }
                }
            }
        }
        if(null == topFolder)
            topFolder = directory;
        return topFolder;
    }

    public static void zipDirectory(File directory, File zipfile) throws IOException
    {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty())
            {
                directory = queue.pop();
                for (File kid : directory.listFiles())
                {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    }
                    else
                    {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        }
        finally
        {
            res.close();
        }
    }



    /**
     *
     * @param f
     * @param pathToDirectory
     */
    public static void moveFileToDirectory(File f, String pathToDirectory)
    {
        SimpleFileIO.moveFileToDirectory(f, new File(pathToDirectory + File.separator + f.getName()));
    }

    /**
     *
     * @param f
     * @param directory
     */
    public static void moveFileToDirectory(File f, File directory)
    {
        f.renameTo(directory);
    }

    public static byte[] byteArrayFromInputStream(InputStream aInput) throws IOException
    {
        byte[] bucket = new byte[32*1024];
        ByteArrayOutputStream result = null;

        try
        {
            result = new ByteArrayOutputStream(bucket.length);
            int bytesRead = 0;
            while(bytesRead != -1)
            {
                //aInput.read() returns -1, 0, or more :
                bytesRead = aInput.read(bucket);
                if(bytesRead > 0)
                {
                    result.write(bucket, 0, bytesRead);
                }
            }
        }
        finally
        {
            aInput.close();
        }

        return result.toByteArray();
    }

    /**
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        while (true)
        {
            int readCount = in.read(buffer);
            if (readCount < 0)
            {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    /**
     *
     * @param file
     * @param out
     * @throws IOException
     */
    public static void copy(File file, OutputStream out) throws IOException
    {
        InputStream in = new FileInputStream(file);
        try
        {
            copy(in, out);
        }
        finally
        {
            in.close();
        }
    }

    /**
     *
     * @param in
     * @param file
     * @throws IOException
     */
    public static void copy(InputStream in, File file) throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        try
        {
            copy(in, out);
        }
        finally
        {
            out.close();
        }
    }

    /**
     *
     * @param inFile
     * @param outFile
     * @throws IOException
     */
    public static void copy(File inFile, File outFile) throws IOException
    {
        OutputStream out = new FileOutputStream(outFile);
        InputStream in = new FileInputStream(inFile);
        try
        {
            copy(in, out);
        }
        finally
        {
            out.close();
            in.close();
        }
    }


    /**
     * code is taken from https://github.com/adamheinrich/native-utils
     * @param path
     * @throws IOException
     */
    public static void loadNativeLibraryFromJar(String path) throws IOException
    {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }

        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        // Prepare temporary file
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();

        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }

        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;

        // Open and check input stream
        InputStream is = SimpleFileIO.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }

        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }

        // Finally, load the library
        System.load(temp.getAbsolutePath());
    }

/*
	Class<?> loadClassFromFile(String pathToClassFile) throws IOException
	{
		File classFile = new File(pathToClassFile);
		InputStream is = new FileInputStream(classFile);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = is.read();
        while(data != -1)
        {
            buffer.write(data);
            data = is.read();
        }

        is.close();

        byte[] classData = buffer.toByteArray();

	} */

}

