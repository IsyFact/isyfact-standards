package de.bund.bva.isyfact.exception.util;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

public class TestExceptionFactory {

	public static final String AUSNAHME_ID = "BaseException";
	public static final String PARAMETER = "param";
	public static final TestFehlertextProviderImpl PROVIDER = new TestFehlertextProviderImpl();
	
	private TestExceptionFactory(){}
	
	public static MyBusinessException getBusinessException(){
		return new MyBusinessException();
	}
	
	public static MyBusinessException getBusinessException(Throwable e){
		return new MyBusinessException(e);
	}
	
	public static MyTechnicalException getTechnicalException(){
		return new MyTechnicalException();
	}	
	
	public static MyTechnicalException getTechnicalException(Throwable e) {
		return new MyTechnicalException(e);
	}
	
	public static MyTechnicalRuntimeException getTechnicalRuntimeException(){
		return new MyTechnicalRuntimeException();
	}
	
	public static MyTechnicalRuntimeException getTechnicalRuntimeException(Throwable e){
		return new MyTechnicalRuntimeException(e);
	}
	
	public static class MyBusinessException extends BusinessException {
		 
		MyBusinessException(){
			this(AUSNAHME_ID, PROVIDER, PARAMETER);
		}
		
		MyBusinessException(Throwable t){
			this(AUSNAHME_ID, t, PROVIDER, PARAMETER);
		}
		
		MyBusinessException(String ausnahmeId, FehlertextProvider provider, String... parameters) {
			super(ausnahmeId, provider, parameters);
		}
		
		MyBusinessException(String ausnahmeId, Throwable t, FehlertextProvider provider, String... parameters) {
			super(ausnahmeId, t, provider, parameters);
		}
	}
	
	public static class MyTechnicalException extends TechnicalException {
		
		MyTechnicalException(){
			this(AUSNAHME_ID, PROVIDER, PARAMETER);
		}
		
		MyTechnicalException(Throwable t){
			this(AUSNAHME_ID, t, PROVIDER, PARAMETER);
		}
		
		MyTechnicalException(String ausnahmeId, FehlertextProvider fehlertextProvider,
		        String... parameter) {
			super(ausnahmeId, fehlertextProvider, parameter);
		}
		
		MyTechnicalException(String ausnahmeId, Throwable throwable,
		        FehlertextProvider fehlertextProvider, String... parameter) {
			super(ausnahmeId, throwable, fehlertextProvider, parameter);
		}
	}
	
	public static class MyTechnicalRuntimeException extends TechnicalRuntimeException {
		
		MyTechnicalRuntimeException(){
			this(AUSNAHME_ID, PROVIDER, PARAMETER);
		}
		
		MyTechnicalRuntimeException(Throwable t){
			this(AUSNAHME_ID, t, PROVIDER, PARAMETER);
		}
		
		MyTechnicalRuntimeException(String ausnahmeId, FehlertextProvider fehlertextProvider,
		        String... parameter) {
			super(ausnahmeId, fehlertextProvider, parameter);
		}
		
		MyTechnicalRuntimeException(String ausnahmeId, Throwable throwable,
		        FehlertextProvider fehlertextProvider, String... parameter) {
			super(ausnahmeId, throwable, fehlertextProvider, parameter);
		}
	}
}
