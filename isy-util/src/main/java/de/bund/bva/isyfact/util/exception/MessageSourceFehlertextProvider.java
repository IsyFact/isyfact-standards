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
/**
 * 
 */
package de.bund.bva.isyfact.util.exception;

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

/**
 * This class implements a {@link FehlertextProvider} that uses the {@link MessageSourceHolder} to load the messages (must be configured as a Spring bean in the application).
 *
 * @deprecated as of IsyFact 3, due to the deprecation of {@link MessageSourceHolder}, an application must implement {@link FehlertextProvider} itself if required (without {@link  MessageSourceHolder}).
 * Class will be deleted in future version.
 */
@Deprecated
public class MessageSourceFehlertextProvider implements FehlertextProvider {

    public String getMessage(String schluessel, String... parameter) {
        return MessageSourceHolder.getMessage(schluessel, parameter);
    }
}
