package de.bund.bva.pliscommon.exception.service.util.factory;

import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.BusinessToException;
import de.bund.bva.isyfact.exception.service.TechnicalToException;

public class PlisTestExceptionFactory {
    public static final String ausnahmeId = "BaseException";
    public static final String parameter = "param";
    public static final FehlertextProvider provider = new FehlertextProvider() {
        @Override
        public String getMessage(String schluessel, String... parameter) {
            StringBuilder message = new StringBuilder(schluessel);
            for (String string : parameter) {
                string = " " + string;
                message.append(string);
            }
            return message.toString();
        }
    };

    private PlisTestExceptionFactory(){}

    public static MyPlisBusinessException getBusinessException(){
        return new MyPlisBusinessException();
    }

    public static MyPlisTechnicalException getTechnicalException(){
        return new MyPlisTechnicalException();
    }

    public static MyPlisTechnicalRuntimeException getTechnicalRuntimeException(){
        return new MyPlisTechnicalRuntimeException();
    }

    public static class MyPlisBusinessException extends PlisBusinessException {

        MyPlisBusinessException(){
            this(ausnahmeId, provider, parameter);
        }

        MyPlisBusinessException(String ausnahmeId, FehlertextProvider provider, String... parameters) {
            super(ausnahmeId, provider, parameters);
        }
    }

    public static class MyPlisTechnicalException extends PlisTechnicalException {

        MyPlisTechnicalException(){
            this(ausnahmeId, provider, parameter);
        }

        MyPlisTechnicalException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                 String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
        }
    }

    public static class MyPlisTechnicalRuntimeException extends PlisTechnicalRuntimeException {

        MyPlisTechnicalRuntimeException(){
            this(ausnahmeId, provider, parameter);
        }

        MyPlisTechnicalRuntimeException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                        String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
        }
    }

    public static class MyIsyBusinessToException extends BusinessToException {

        public MyIsyBusinessToException(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
        }
    }

    public static class MyIsyTechnicalToException extends TechnicalToException {

        public MyIsyTechnicalToException(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
        }
    }

    public static class MyWrongConstructorToException extends TechnicalToException {
        public MyWrongConstructorToException(){
            super("message", ausnahmeId, "uniqueId");
        }
    }

    public static class MyWrongParameterToException extends TechnicalToException {
        public MyWrongParameterToException(String message, String ausnahmeId, String uniqueId){
            super(message, ausnahmeId, uniqueId);
            if(ausnahmeId.equalsIgnoreCase("foo bar")){
                throw new IllegalArgumentException("Ausnahme ID darf nicht null sein.");
            }
        }
    }
}
