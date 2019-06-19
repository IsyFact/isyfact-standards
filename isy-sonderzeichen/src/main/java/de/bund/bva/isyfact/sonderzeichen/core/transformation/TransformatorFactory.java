/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.sonderzeichen.core.transformation;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.sonderzeichen.core.transformation.impl.AbstractTransformator;


/**
 * Die Factory für den jeweiligen Transformator.
 * 
 */
public class TransformatorFactory implements FactoryBean, InitializingBean {
    
    /** Der Transformator, wird über Spring gesetzt */
    private AbstractTransformator transformator;
    
    /** Zusätzliche Transformationstabelle, wird über Spring gesetzt */
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
