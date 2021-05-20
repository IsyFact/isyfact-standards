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
package de.bund.bva.pliscommon.serviceapi.common.exception.test;

import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class TestExceptionFactory {

    public static final String ausnahmeId = "PlisException";
    public static final String parameter = "param";
    public static final FehlertextProvider provider = (schluessel, parameter) -> {
        StringBuilder message = new StringBuilder(schluessel);
        for (String string : parameter) {
            message.append(" ").append(string);
        }
        return message.toString();
    };

    private TestExceptionFactory() {
    }

    public static MyPlisBusinessException getPlisBusinessException() {
        return new MyPlisBusinessException();
    }

    public static MyPlisBusinessException getPlisBusinessException(Throwable e) {
        return new MyPlisBusinessException(e);
    }

    public static MyPlisTechnicalException getPlisTechnicalException() {
        return new MyPlisTechnicalException();
    }

    public static MyPlisTechnicalException getPlisTechnicalException(Throwable e) {
        return new MyPlisTechnicalException(e);
    }

    public static MyPlisTechnicalRuntimeException getPlisTechnicalRuntimeException() {
        return new MyPlisTechnicalRuntimeException();
    }

    public static MyPlisTechnicalRuntimeException getPlisTechnicalRuntimeException(Throwable e) {
        return new MyPlisTechnicalRuntimeException(e);
    }

    public static class MyPlisBusinessException extends PlisBusinessException {

        MyPlisBusinessException() {
            this(ausnahmeId, provider, parameter);
        }

        MyPlisBusinessException(Throwable t) {
            this(ausnahmeId, t, provider, parameter);
        }

        MyPlisBusinessException(String ausnahmeId, FehlertextProvider provider, String... parameters) {
            super(ausnahmeId, provider, parameters);
        }

        MyPlisBusinessException(String ausnahmeId, Throwable t, FehlertextProvider provider, String... parameters) {
            super(ausnahmeId, t, provider, parameters);
        }

    }

    public static class MyPlisTechnicalException extends PlisTechnicalException {

        MyPlisTechnicalException() {
            this(ausnahmeId, provider, parameter);
        }

        MyPlisTechnicalException(Throwable t) {
            this(ausnahmeId, t, provider, parameter);
        }

        MyPlisTechnicalException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                 String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
        }

        MyPlisTechnicalException(String ausnahmeId, Throwable throwable,
                                 FehlertextProvider fehlertextProvider, String... parameter) {
            super(ausnahmeId, throwable, fehlertextProvider, parameter);
        }

    }

    public static class MyPlisTechnicalRuntimeException extends PlisTechnicalRuntimeException {

        MyPlisTechnicalRuntimeException() {
            this(ausnahmeId, provider, parameter);
        }

        MyPlisTechnicalRuntimeException(Throwable t) {
            this(ausnahmeId, t, provider, parameter);
        }

        MyPlisTechnicalRuntimeException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                        String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
        }

        MyPlisTechnicalRuntimeException(String ausnahmeId, Throwable throwable,
                                        FehlertextProvider fehlertextProvider, String... parameter) {
            super(ausnahmeId, throwable, fehlertextProvider, parameter);
        }

    }

    public static class MyPlisBusinessToException extends PlisBusinessToException {

        public MyPlisBusinessToException(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
        }

    }

    public static class MyPlisTechnicalToException extends PlisTechnicalToException {

        public MyPlisTechnicalToException(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
        }

    }

    public static class MyWrongConstructorToException extends PlisTechnicalToException {

        public MyWrongConstructorToException() {
            super("message", ausnahmeId, "uniqueId");
        }

    }

    public static class MyWrongParameterToException extends PlisTechnicalToException {

        public MyWrongParameterToException(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
            if (ausnahmeId.equalsIgnoreCase("foo bar")) {
                throw new IllegalArgumentException("Ausnahme ID darf nicht null sein.");
            }
        }

    }

}
