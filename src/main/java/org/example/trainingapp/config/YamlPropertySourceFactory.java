package org.example.trainingapp.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertySourceFactory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    @NonNull
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        Resource resource = encodedResource.getResource();
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(resource);

        Properties properties = factoryBean.getObject();
        if (properties == null) {
            properties = new Properties(); // fallback to empty props
        }

        String sourceName = (name != null)
                ? name
                : Objects.requireNonNullElse(resource.getFilename(), "application.yml");

        return new PropertiesPropertySource(sourceName, properties);
    }
}
