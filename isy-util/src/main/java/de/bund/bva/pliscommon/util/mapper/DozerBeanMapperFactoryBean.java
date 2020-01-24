package de.bund.bva.pliscommon.util.mapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.CustomFieldMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.builder.BeanMappingsBuilder;
import com.github.dozermapper.core.cache.CacheManager;
import com.github.dozermapper.core.config.processors.SettingsProcessor;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import com.github.dozermapper.core.util.DozerClassLoader;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ApplicationObjectSupport;

/**
 * FactoryBean für das Erzeugen von Dozer-Mapper.
 * Wrapper für den DozerBeanMapperBuilder, sodass die Erzeugung mittels Spring-XML-Beans erfolgen kann
 */
public class DozerBeanMapperFactoryBean extends ApplicationObjectSupport
    implements InitializingBean, FactoryBean<Mapper> {

    /**
     * Builder an den das Bauen delegiert wird.
     */
    private final DozerBeanMapperBuilder builder;

    /**
     * Mapper, der erzeugt wird.
     */
    private Mapper mapper;

    public DozerBeanMapperFactoryBean() {
        builder = DozerBeanMapperBuilder.create();
    }

    public void setMappingFiles(List<String> mappingFiles) {
        builder.withMappingFiles(mappingFiles);
    }

    public void setClassLoader(DozerClassLoader classLoader) {
        builder.withClassLoader(classLoader);
    }

    public void setClassLoader(ClassLoader classLoader) {
        builder.withClassLoader(classLoader);
    }

    public void setCustomConverter(CustomConverter customConverter) {
        builder.withCustomConverter(customConverter);
    }

    public void setCustomConverters(List<CustomConverter> customConverters) {
        builder.withCustomConverters(customConverters);
    }

    public void setXmlMapping(Supplier<InputStream> xmlMappingSupplier) {
        builder.withXmlMapping(xmlMappingSupplier);
    }

    public void setMappingBuilder(BeanMappingBuilder mappingBuilder) {
        builder.withMappingBuilder(mappingBuilder);
    }

    public void setMappingBuilders(List<BeanMappingBuilder> mappingBuilders) {
        builder.withMappingBuilders(mappingBuilders);
    }

    public void setBeanMappingsBuilders(BeanMappingsBuilder beanMappingsBuilder) {
        builder.withBeanMappingsBuilders(beanMappingsBuilder);
    }

    public void setBeanMappingsBuilders(List<BeanMappingsBuilder> beanMappingsBuilder) {
        builder.withBeanMappingsBuilders(beanMappingsBuilder);
    }

    public void setEventListener(EventListener eventListener) {
        builder.withEventListener(eventListener);
    }

    public void setEventListeners(List<EventListener> eventListeners) {
        builder.withEventListeners(eventListeners);
    }

    public void setCustomFieldMapper(CustomFieldMapper customFieldMapper) {
        builder.withCustomFieldMapper(customFieldMapper);
    }

    public void setCustomConverterWithId(String converterId, CustomConverter converter) {
        builder.withCustomConverterWithId(converterId, converter);
    }

    public void setCustomConvertersWithIds(Map<String, CustomConverter> customConvertersWithId) {
        builder.withCustomConvertersWithIds(customConvertersWithId);
    }

    public void setBeanFactory(String factoryName, BeanFactory beanFactory) {
        builder.withBeanFactory(factoryName, beanFactory);
    }

    public void setBeanFactorys(Map<String, BeanFactory> beanFactories) {
        builder.withBeanFactorys(beanFactories);
    }

    public void setSettingsProcessor(SettingsProcessor processor) {
        builder.withSettingsProcessor(processor);
    }

    public void setELEngine(ELEngine elEngine) {
        builder.withELEngine(elEngine);
    }

    public void setCacheManager(CacheManager cacheManager) {
        builder.withCacheManager(cacheManager);
    }

    public void afterPropertiesSet() {
        this.mapper = builder.build();
    }

    public Mapper getObject() {
        return this.mapper;
    }

    public Class<Mapper> getObjectType() {
        return Mapper.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
