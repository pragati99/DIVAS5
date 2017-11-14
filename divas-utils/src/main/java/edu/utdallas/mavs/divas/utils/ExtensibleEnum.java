package edu.utdallas.mavs.divas.utils;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements an extensible enumeration
 * Note: not serializable
 * 
 * @param <E> the type of the enumeration
 */
public abstract class ExtensibleEnum<E extends ExtensibleEnum<E>>
{
    private static final Map<Class<? extends ExtensibleEnum<?>>, Map<String, ExtensibleEnum<?>>> elements = new LinkedHashMap<Class<? extends ExtensibleEnum<?>>, Map<String, ExtensibleEnum<?>>>();

    private final String                                                                         name;

    public final String name()
    {
        return name;
    }

    private final int ordinal;

    public final int ordinal()
    {
        return ordinal;
    }

    public abstract E[] getEnumValues();

    protected ExtensibleEnum(String name)
    {
        Map<String, ExtensibleEnum<?>> typeElements = elements.get(getClass());
        this.name = name;
        if(typeElements == null)
        {
            typeElements = new LinkedHashMap<String, ExtensibleEnum<?>>();
            elements.put(getExtensibleEnumClass(), typeElements);
        }
        this.ordinal = typeElements.size();
        typeElements.put(name, this);
    }

    protected ExtensibleEnum(String name, int ordinal)
    {
        this.name = name;
        this.ordinal = ordinal;
        Map<String, ExtensibleEnum<?>> typeElements = elements.get(getClass());
        if(typeElements == null)
        {
            typeElements = new LinkedHashMap<String, ExtensibleEnum<?>>();
            elements.put(getExtensibleEnumClass(), typeElements);
        }
        typeElements.put(name, this);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends ExtensibleEnum<?>> getExtensibleEnumClass()
    {
        return (Class<? extends ExtensibleEnum<?>>) getClass();
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public final boolean equals(Object other)
    {
        return this == other;
    }

    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    public final int compareTo(E other)
    {
        ExtensibleEnum<?> self = this;
        if(self.getClass() != other.getClass() && // optimization
                self.getDeclaringClass() != other.getDeclaringClass())
            throw new ClassCastException();
        return self.ordinal - other.ordinal();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final Class<E> getDeclaringClass()
    {
        Class clazz = getClass();
        Class zuper = clazz.getSuperclass();
        return (zuper == ExtensibleEnum.class) ? clazz : zuper;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ExtensibleEnum<T>> T valueOf(Class<T> enumType, String name)
    {
        return (T) elements.get(enumType).get(name);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @SuppressWarnings("unused")
    private void readObjectNoData() throws ObjectStreamException
    {
        throw new InvalidObjectException("can't deserialize enum");
    }

    @Override
    protected final void finalize()
    {}

    public static <E> ExtensibleEnum<? extends ExtensibleEnum<?>>[] values()
    {
        throw new IllegalStateException("Sub class of ExtensibleEnum must implement method valus()");
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] values(Class<E> enumType)
    {
        Collection<ExtensibleEnum<?>> values = elements.get(enumType).values();
        int n = values.size();
        E[] typedValues = (E[]) Array.newInstance(enumType, n);
        int i = 0;
        for(ExtensibleEnum<?> value : values)
        {
            Array.set(typedValues, i, value);
            i++;
        }

        return typedValues;
    }

    public static <E> void clear(Class<E> enumType)
    {
        elements.get(enumType).clear();
    }
}
