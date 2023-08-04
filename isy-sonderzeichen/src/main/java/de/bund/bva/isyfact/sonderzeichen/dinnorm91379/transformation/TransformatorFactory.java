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
package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

import de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl.AbstractTransformator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * The factory for the respective transformer.
 */
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
