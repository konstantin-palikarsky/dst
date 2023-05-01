package dst.ass2.ioc.di.impl;


import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ObjectContainer implements IObjectContainer {
    private final Properties properties = new Properties();
    private final Map<Class<?>, Object> singletonCache = new ConcurrentHashMap<>();

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
            if (singletonCache.containsKey(type)) {
                return (T) singletonCache.get(type);
            }
        }

        //Access default class constructor
        Constructor<T> constructor;
        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ObjectCreationException(type + " has no default constructor!");
        }

        //Generate new instance, handles abstract class constructors
        T instance;
        try {
            instance = constructor.newInstance();
            if (isSingleton) {
                singletonCache.put(type, instance);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InjectionException("Cannot instantiate " + type);
        }

        var fieldsToInject = Arrays
                .stream(type.getDeclaredFields())
                .filter(x -> x.getAnnotation(Inject.class) != null)
                .collect(Collectors.toList());

        fieldsToInject.forEach(x -> setFieldOfObject(x, instance));

        var initMethods = Arrays
                .stream(type.getDeclaredMethods())
                .filter(x -> x.getAnnotation(Initialize.class) != null)
                .collect(Collectors.toList());

        initMethods.forEach(x -> {
            if (x.getParameterCount() != 0) {
                throw new InvalidDeclarationException("Initialization method " + x + " has more than 0 parameters");
            }
        });

        initMethods.forEach(
                x-> {
                    try {
                        x.setAccessible(true);
                        x.invoke(instance);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new ObjectCreationException(e);
                    }
                }
        );


        return instance;
    }

    private void setFieldOfObject(Field field, Object instance) {
        try {
            var classToInject = field.getAnnotation(Inject.class).targetType();
            classToInject = classToInject == Void.class ? field.getType() : classToInject;

            if (!field.getType().isAssignableFrom(classToInject)) {
                if (!field.getAnnotation(Inject.class).optional()) {
                    throw new InvalidDeclarationException("Attempted to inject non type-compatible dependency");
                }
            }

            var fieldInstance = getObject(classToInject);
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
}
