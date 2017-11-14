package edu.utdallas.mavs.divas.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class is a helper class that wraps the Java resources management and class loader operations.
 * <p>
 * It provides a standard mechanism for developers to handle resources management operations.
 */
public class ResourceManager
{
    /**
     * The file separator character in the system.
     */
    protected static String separator = System.getProperty("file.separator");

    private static ClassLoader getClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * @param name
     *        Name of the specified resource.
     * @return An input stream for reading the specified resource.
     */
    public static InputStream getResourceStream(String name)
    {   
        return ClassLoader.getSystemResourceAsStream(name);
        //return getClassLoader().getResourceAsStream(name);
    }

    /**
     * Finds the resource with the given path parts.
     * 
     * @param pathParts
     *        The path to look in represented as an array of strings. This is useful in different platforms where each
     *        platform has it's own file separator.
     * @return The resource with the given path parts.
     */
    public static URL getResourceURL(String... pathParts)
    {
        return getClassLoader().getResource(compilePath(pathParts));
    }

    /**
     * Finds the URI of the resource with the given path parts.
     * 
     * @param pathParts
     *        The path to look in represented as an array of strings. This is useful in different platforms where each
     *        platform has it's own file separator.
     * @return The resource with the given path parts converted to URI.
     */
    public static URI getResourceURI(String... pathParts)
    {
        try
        {
            return getResourceURL(compilePath(pathParts)).toURI();
        }

        catch(URISyntaxException e)
        {
            return null;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * Gets the file with the given path parts.
     * 
     * @param pathParts
     *        The path to look in represented as an array of strings. This is useful in different platforms where each
     *        platform has it's own file separator.
     * @return A file.
     */
    @Deprecated
    public static File getResourceFile(String... pathParts)
    {
        try
        {
            return new File(getResourceURI(compilePath(pathParts)));
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * Gets the classes in a given package.
     * 
     * @param pkg
     *        Package to look in.
     * @return Array of classes.
     */
    @Deprecated
    public static Class<?>[] getClasses(Package pkg)
    {
        return getClasses(pkg, true);
    }

    /**
     * Gets the classes in a given package.
     * 
     * @param pkg
     *        Package to look in.
     * @param allowAbstract
     *        Flag to indicate if the class abstract to be find.
     * @return Array of classes.
     */
    @Deprecated
    public static Class<?>[] getClasses(Package pkg, boolean allowAbstract)
    {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        for(File f : getFiles(getResourceFile(pkg.getName().replace(".", separator)), "class"))
        {
            try
            {
                Class<?> clazz = getClassLoader().loadClass(pkg.getName() + "." + f.getName().split("\\.")[0]);

                if(allowAbstract || !Modifier.isAbstract(clazz.getModifiers()))
                    classes.add(clazz);
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        return classes.toArray(new Class<?>[classes.size()]);
    }

    /**
     * Gets the sub directories in the given directory.
     * 
     * @param folder
     *        Folder to look in.
     * @return Array of sub directories.
     */
    public static File[] getSubDirs(File folder)
    {
        ArrayList<File> subDirs = new ArrayList<File>();

        for(File f : folder.listFiles())
            if(f.isDirectory() && !f.isHidden())
                subDirs.add(f);

        File subDirArray[] = new File[subDirs.size()];

        return subDirs.toArray(subDirArray);
    }

    /**
     * Gets all the files in the given folder.
     * 
     * @param folder
     *        Folder to look in.
     * @return Array of files in the given folder.
     */
    public static File[] getAll(File folder)
    {
        return folder.listFiles();
    }

    /**
     * Gets the files in a given folder.
     * 
     * @param folder
     *        The folder to look in.
     * @param extensions
     *        extensions of the files to look for.
     * @return Array of files that satisfy the search criteria.
     */
    public static File[] getFiles(File folder, String... extensions)
    {
        ArrayList<File> subFiles = new ArrayList<File>();

        for(File f : folder.listFiles())
        {
            if(!f.isDirectory() && !f.isHidden())
            {
                if(extensions.length < 1)
                    subFiles.add(f);
                else
                {
                    String fileExt = f.getName().toUpperCase();

                    for(String ext : extensions)
                    {
                        if(fileExt.endsWith("." + ext.toUpperCase()))
                        {
                            subFiles.add(f);
                            break;
                        }
                    }
                }
            }
        }

        return subFiles.toArray(new File[subFiles.size()]);
    }

    /**
     * Gets the file with given search criteria.
     * 
     * @param folder
     *        The folder to look in.
     * @param name
     *        The starting part of the file name.
     * @param extensions
     *        Extensions to look for.
     * @return Files satisfy the search criteria.
     */
    public static File getFileOfTypes(File folder, String name, String... extensions)
    {
        for(File f : folder.listFiles())
        {
            if(!f.isDirectory() && !f.isHidden())
            {
                if(f.getName().startsWith(name))
                {
                    String fileExt = f.getName().toUpperCase();

                    for(String ext : extensions)
                    {
                        if(fileExt.endsWith("." + ext.toUpperCase()))
                        {
                            return f;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Creates a file with the given name in the given folder.
     * 
     * @param folder
     *        The folder in which the file should be created.
     * @param name
     *        The name of the file to be created.
     * @return The <code>File</code> created by this method.
     * @throws IOException
     */
    public static File createFile(File folder, String name) throws IOException
    {
        if(!folder.exists())
            folder.mkdirs();

        File file = new File(folder.getPath() + separator + name);
        file.createNewFile();

        return file;
    }

    private static String compilePath(String parts[])
    {
        String pathStr = ".";

        if(parts.length == 1)
            pathStr = parts[0];
        else
            for(String part : parts)
                pathStr += separator + part;

        return pathStr;
    }
}
