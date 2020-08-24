package de.bund.bva.isyfact.exception.service.util.factory;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class IsyTestExceptionFactory {
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

    private IsyTestExceptionFactory(){}

    public static MyIsyBusinessException getBusinessException(){
        return new MyIsyBusinessException();
    }

    public static MyIsyTechnicalException getTechnicalException(){
        return new MyIsyTechnicalException();
    }

    public static MyIsyTechnicalRuntimeException getTechnicalRuntimeException(){
        return new MyIsyTechnicalRuntimeException();
    }

    public static class MyIsyBusinessException extends BusinessException {

        MyIsyBusinessException(){
            this(ausnahmeId, provider, parameter);
        }

        MyIsyBusinessException(String ausnahmeId, FehlertextProvider provider, String... parameters) {
            super(ausnahmeId, provider, parameters);
        }
    }

    public static class MyIsyTechnicalException extends TechnicalException {

        MyIsyTechnicalException(){
            this(ausnahmeId, provider, parameter);
        }

        MyIsyTechnicalException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
        }
    }

    public static class MyIsyTechnicalRuntimeException extends TechnicalRuntimeException {

        MyIsyTechnicalRuntimeException(){
            this(ausnahmeId, provider, parameter);
        }

        MyIsyTechnicalRuntimeException(String ausnahmeId, FehlertextProvider fehlertextProvider,
                                       String... parameter) {
            super(ausnahmeId, fehlertextProvider, parameter);
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
        public MyWrongConstructorToException(){
            super("message", ausnahmeId, "uniqueId");
        }
    }

    public static class MyWrongParameterToException extends PlisTechnicalToException {
        public MyWrongParameterToException(String message, String ausnahmeId, String uniqueId){
            super(message, ausnahmeId, uniqueId);
            if(ausnahmeId.equalsIgnoreCase("foo bar")){
                throw new IllegalArgumentException("Ausnahme ID darf nicht null sein.");
            }
        }
    }
}
