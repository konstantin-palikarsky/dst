package dst.ass2.ioc.di.impl;


import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ObjectContainer implements IObjectContainer {
    private final Properties properties = new Properties();
    private final Map<Class<?>, Object> singletonCache = new HashMap<>();

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public <T> T getObject(Class<T> type) throws dst.ass2.ioc.di.InjectionException {
        //This function autowires an object of given class

        //invalid declaration exception if no @Component annotation
        var componentAnnotation = type.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            throw new InvalidDeclarationException(type + " not annotated");
        }
        var isSingleton = componentAnnotation.scope().equals(Scope.SINGLETON);

        if (Modifier.isAbstract(type.getModifiers()) ||
                Modifier.isInterface(type.getModifiers())) {
        }

        if (isSingleton) {
            if (singletonCache.containsKey(type)) {
                return (T) singletonCache.get(type);
            }
        }

        Constructor<T> constructor;
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ObjectCreationException(type + " has no default constructor!");
        }

        T instance;
        try {
            instance = constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InjectionException("Cannot instantiate " + type);
        }

        if (isSingleton) {
            singletonCache.put(type, instance);
        }


        //iterate over every field of the class
        //create every single dependency however many layers deep for the object graph
        //optional dependancies exist
        //handle target type injections
        //no handling of circular dependencies
        //wrap and pass all exceptions

        return instance;
    }
}
