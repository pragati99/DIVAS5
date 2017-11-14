package edu.utdallas.mavs.divas.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a helper class that wraps the I/O read/write operations. This provides a standard mechanism for developers to handle I/O read/write operations.
 */

public class FileIOHelper
{
    private static final Logger logger = LoggerFactory.getLogger(FileIOHelper.class);

    /**
     * Saves the given object to the specified path and name.
     * 
     * @param name
     *        The file name with the path.
     * @param o
     *        Serializable object to be saved.
     * @throws IOException
     */
    public static <T extends Serializable> void save(String name, T o) throws IOException
    {
        File file = new File(name);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(ByteSerializer.serialize(o));
        fos.close();
    }

    /**
     * Loads the file which name and path is given to the memory.
     * 
     * @param name
     *        The file name with the path.
     * @return Object instance that is created from deserializing the given file.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String name) throws IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(new File(name));
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        fis.close();

        return (T) ByteSerializer.deserialize(bytes);
    }

    /**
     * Copies source file to target location.
     * 
     * @param source
     *        The source path of the file.
     * @param target
     *        The target location path.
     * @param options
     *        The options parameter specify how the file is copied.
     * @throws IOException
     */
    public static void copy(Path source, Path target, CopyOption... options)
    {
        try
        {
            Files.copy(source, target, options);
        }
        catch(FileAlreadyExistsException e)
        {
            logger.warn("{} already exists.", source.toString(), target.toFile());
        }
        catch(IOException e)
        {
            logger.warn("An error occurred when copying {} to {}", source.toString(), target.toString());
        }
    }
}
