package dst.ass2.ioc.di.impl;


import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

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
            throw new InvalidDeclarationException(type + " not annotated"+ Arrays.toString(type.getDeclaredAnnotations()));
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
        var fieldsList = Arrays.stream(type.getDeclaredFields()).collect(Collectors.toList());

        var fieldsToInject = fieldsList.stream()
                .filter(x -> x.getAnnotation(Inject.class) != null)
                .collect(Collectors.toList());

        if (fieldsToInject.isEmpty()) {
            return instance;
        }

        fieldsToInject.forEach(x ->
        {
            try {
                var classToInject = x.getAnnotation(Inject.class).targetType();
                classToInject = classToInject == Void.class ? x.getType() : classToInject;

                if (!x.getType().isAssignableFrom(classToInject)){
                    if (!x.getAnnotation(Inject.class).optional()) {
                        throw new InvalidDeclarationException("Attempted to inject non type-compatible dependency");
                    }
                }

                var fieldInstance = getObject(classToInject);
                x.setAccessible(true);
                x.set(instance, fieldInstance);


            } catch (InvalidDeclarationException e) {
                if (!x.getAnnotation(Inject.class).optional()) {
                    throw new InvalidDeclarationException(e.getMessage());
                }
            } catch (ObjectCreationException e) {
                if (!x.getAnnotation(Inject.class).optional()) {
                    throw new ObjectCreationException(e.getMessage());
                }
            } catch (IllegalAccessException | InjectionException e) {
                if (!x.getAnnotation(Inject.class).optional()) {
                    throw new InjectionException(e.getMessage());
                }
            }
        });
        return instance;
    }
}
