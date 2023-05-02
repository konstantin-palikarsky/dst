package dst.ass2.ioc.di.impl;


import dst.ass2.ioc.di.*;
import dst.ass2.ioc.di.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ObjectContainer implements IObjectContainer {
    private final Properties properties = new Properties();
    private final Map<Class<?>, Object> singletonCache = new ConcurrentHashMap<>();
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final Lock propsLock = new ReentrantLock();

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public <T> T getObject(Class<T> type) throws dst.ass2.ioc.di.InjectionException {

        //Ensures class is injectable
        var componentAnnotation = type.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new InvalidDeclarationException(type + " not annotated");
        }

        //Checks cache for singletons
        var isSingleton = componentAnnotation.scope().equals(Scope.SINGLETON);

        if (isSingleton) {
            try {
                cacheLock.readLock().lock();
                if (singletonCache.containsKey(type)) {
                    return (T) singletonCache.get(type);
                }
            } finally {
                cacheLock.readLock().unlock();
            }
        }

        //Access default class constructor
        Constructor<T> constructor;
        try {
            var constructors = type.getDeclaredConstructors();
            if (constructors.length < 1) {
                throw new NoSuchMethodException();
            }

            constructor = (Constructor<T>) Arrays.stream(constructors).filter(
                    x -> x.getParameterCount() < 1).collect(Collectors.toList()).get(0);


        } catch (NoSuchMethodException e) {
            throw new ObjectCreationException(type + " has no default constructor!");
        }


        //Generate new instance, handles abstract class constructors
        T instance;
        try {
            constructor.setAccessible(true);
            instance = constructor.newInstance();
            if (isSingleton) {
                try {
                    cacheLock.writeLock().lock();
                    singletonCache.put(type, instance);
                } finally {
                    cacheLock.writeLock().unlock();
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InjectionException("Cannot instantiate " + type);
        }

        runPropertyInjection(type, instance);
        runConstructorInjection(type, instance);
        runInitialization(type, instance);

        return instance;
    }

    private <T> void runConstructorInjection(Class<T> type, T instance) {
        var fieldsToInject = getFieldsForAnnotation(type, Inject.class);

        fieldsToInject.forEach(x -> injectFieldOfObject(x, instance));
    }

    private <T> void injectFieldOfObject(Field field, T instance) {
        try {
            var fieldTargetClass = field.getAnnotation(Inject.class).targetType();
            fieldTargetClass = fieldTargetClass == Void.class ? field.getType() : fieldTargetClass;

            if (!field.getType().isAssignableFrom(fieldTargetClass)) {
                if (!field.getAnnotation(Inject.class).optional()) {
                    throw new InvalidDeclarationException("Attempted to inject non type-compatible dependency");
                }
            }

            var fieldInstance = getObject(fieldTargetClass);
            field.setAccessible(true);
            field.set(instance, fieldInstance);


        } catch (InvalidDeclarationException e) {
            if (!field.getAnnotation(Inject.class).optional()) {
                throw new InvalidDeclarationException(e.getMessage());
            }
        } catch (ObjectCreationException e) {
            if (!field.getAnnotation(Inject.class).optional()) {
                throw new ObjectCreationException(e.getMessage());
            }
        } catch (IllegalAccessException | InjectionException e) {
            if (!field.getAnnotation(Inject.class).optional()) {
                throw new InjectionException(e.getMessage());
            }
        }
    }

    /**
     * This method will call the initialization methods in order of declaration
     * starting at the top of the class hierarchy and moving down,
     * we make no guarantees of order within the same class' init methods.
     * <p>
     * Singletons are only going to run initialization once
     */
    private <T> void runInitialization(Class<T> type, T instance) {
        var initMethods = getAllMethods(new ArrayList<>(), type)
                .stream()
                .filter(x -> x.getAnnotation(Initialize.class) != null)
                .collect(Collectors.toList());

        initMethods.forEach(x -> {
            if (x.getParameterCount() != 0) {
                throw new InvalidDeclarationException("Initialization method " + x + " has more than 0 parameters");
            }
        });

        initMethods.forEach(
                x -> {
                    try {
                        x.setAccessible(true);
                        x.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ObjectCreationException(e);
                    }
                }
        );

    }

    private <T> void runPropertyInjection(Class<T> type, T instance) {
        var fieldsToInject = getFieldsForAnnotation(type, Property.class);
        Properties currentProperties = (Properties) properties.clone();

        fieldsToInject.forEach(x -> injectFieldFromProperty(x, instance, currentProperties));
    }

    private <T> void injectFieldFromProperty(Field field, T instance, Properties currentProperties) {
        var propertyKey = field.getAnnotation(Property.class).value();
        var propertyString = currentProperties.getProperty(propertyKey);
        if (propertyString == null) {
            throw new ObjectCreationException(
                    "Attempted to inject unknown property " + propertyKey + " in " + field.getName());
        }

        var fieldType = field.getType();
        Object properFieldValue;
        try {
            properFieldValue = transformStringToType(propertyString, fieldType);
        } catch (Exception e) {
            throw new TypeConversionException(e.getMessage());
        }

        try {
            field.setAccessible(true);
            field.set(instance, properFieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Object transformStringToType(String s, Class<T> type) {
        if (type.isAssignableFrom(Integer.class) ||
                type.isAssignableFrom(int.class)) {
            return Integer.parseInt(s);

        } else if (type.isAssignableFrom(Byte.class) ||
                type.isAssignableFrom(byte.class)) {
            return Byte.parseByte(s);

        } else if (type.isAssignableFrom(Short.class) ||
                type.isAssignableFrom(short.class)) {
            return Short.parseShort(s);

        } else if (type.isAssignableFrom(Long.class) ||
                type.isAssignableFrom(long.class)) {
            return Long.parseLong(s);

        } else if (type.isAssignableFrom(Float.class) ||
                type.isAssignableFrom(float.class)) {
            return Float.parseFloat(s);

        } else if (type.isAssignableFrom(Double.class) ||
                type.isAssignableFrom(double.class)) {
            return Double.parseDouble(s);

        } else if (type.isAssignableFrom(Boolean.class) ||
                type.isAssignableFrom(boolean.class)) {
            return Boolean.parseBoolean(s);

        } else if (type.isAssignableFrom(Character.class) ||
                type.isAssignableFrom(char.class)) {
            return s.charAt(0);

        } else if (String.class.equals(type)) {
            return s;

        }

        throw new TypeConversionException("Attempted to inject a property of non-primitive type");
    }

    private List<Field> getFieldsForAnnotation(Class<?> type, Class<? extends Annotation> annotation) {
        return getAllFields(new ArrayList<Field>(), type).stream()
                .filter(x -> x.getAnnotation(annotation) != null)
                .collect(Collectors.toList());
    }

    // Taken from Stack Overflow
    // TODO should handle singletons
    // https://stackoverflow.com/questions/1042798/retrieving-the-inherited-attribute-names-values-using-java-reflection
    public List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public List<Method> getAllMethods(List<Method> fields, Class<?> type) {
        fields.addAll(0, Arrays.asList(type.getDeclaredMethods()));

        try {

            cacheLock.writeLock().lock();
            var superClass = type.getSuperclass();

            if (superClass != null &&
                    superClass.getAnnotation(Component.class) != null &&
                    !singletonCache.containsKey(superClass)
            ) {
                if (superClass.getAnnotation(Component.class).scope().equals(Scope.SINGLETON)) {
                    getObject(superClass);
                    return fields;
                }

                getAllMethods(fields, type.getSuperclass());
            }

        } finally {
            cacheLock.writeLock().unlock();
        }

        return fields;
    }
}
