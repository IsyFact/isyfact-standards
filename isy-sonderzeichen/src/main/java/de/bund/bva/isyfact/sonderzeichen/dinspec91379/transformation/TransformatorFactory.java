package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl.AbstractTransformator;


/**
 * The factory for the respective transformer.
 *
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinnorm91379} instead.
 */
@Deprecated
public class TransformatorFactory implements FactoryBean<Object>, InitializingBean {

    /** The transformer is set via Spring. */
    private AbstractTransformator transformator;

    /** Additional transformation table is set via Spring. */
    private String transformationsTabelle;

    @Override
    public void afterPropertiesSet() {
        transformator.initialisiere(transformationsTabelle);
    }

    public void setTransformationsTabelle(String transformationsTabelle) {
        this.transformationsTabelle = transformationsTabelle;
    }

    public void setTransformator(AbstractTransformator transformator) {
        this.transformator = transformator;
    }

    @Override
    public Object getObject() {
        return transformator;
    }

    @Override
    public Class<?> getObjectType() {
        if (transformator == null) {
            return null;
        } else {
            return Transformator.class;
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
