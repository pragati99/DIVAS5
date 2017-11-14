package edu.utdallas.mavs.divas.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class is a helper class that wraps the Java Serialization API to provide a standard mechanism for developers to handle object serialization.
 * <p>
 * Object serialization is the process of saving an object's state to a sequence of bytes, as well as the process of rebuilding those bytes into a live object at some future time.
 */
public class ByteSerializer
{
	/**
	 * Serializes the given object, where an object is represented as a sequence of bytes that includes the object's data as well as information about the object's type and the types of data stored in
	 * the object.
	 * 
	 * @param object
	 *        Object to be serialized.
	 * @return Array of bytes that represent the objects.
	 * @throws IOException
	 */
	public static byte[] serialize(Serializable object) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		oos.writeObject(object);
		oos.close();

		return baos.toByteArray();
	}

	/**
	 * Deserialize the given Array of bytes to recreate the object that represents.
	 * 
	 * @param bytes
	 *        Array of bytes that represent the objects to be deserialized.
	 * @return Object instance
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);

		return ois.readObject();
	}
}
