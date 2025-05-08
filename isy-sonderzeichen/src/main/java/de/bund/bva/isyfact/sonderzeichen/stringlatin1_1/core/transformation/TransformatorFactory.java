package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.impl.AbstractTransformator;


/**
 * The factory for the respective transformer.
 *
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinspec91379} instead.
 */
@Deprecated
public class TransformatorFactory implements FactoryBean, InitializingBean {
    
    /** The transformer is set via Spring */
    private AbstractTransformator transformator;

    /** Additional transformation table is set via Spring */
    private String transformationsTabelle;

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() {
        transformator.initialisiere(transformationsTabelle);
    }
    
    public void setTransformationsTabelle(String transformationsTabelle) {
        this.transformationsTabelle = transformationsTabelle;
    }

    public void setTransformator(AbstractTransformator transformator) {
        this.transformator = transformator;
    }

    /**
     * {@inheritDoc}
     */
    public Object getObject() {
        return transformator;
    }

    /**
     * {@inheritDoc}
     */
    public Class getObjectType() {
        if(transformator == null){
            return null;
        } else {
            return Transformator.class.getClass();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
