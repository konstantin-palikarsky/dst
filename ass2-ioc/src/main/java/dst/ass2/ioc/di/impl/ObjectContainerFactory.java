package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;

import java.util.Properties;

public final class ObjectContainerFactory implements IObjectContainerFactory {

    @Override
    public IObjectContainer newObjectContainer(Properties properties) {
        var container = new ObjectContainer();
        var containerProperties = container.getProperties();
        properties.stringPropertyNames().forEach(
                x -> containerProperties.setProperty(x, properties.getProperty(x))
        );

        return container;
    }

}
