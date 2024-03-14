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
package de.bund.bva.isyfact.exception.util;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

public class TestExceptionFactory {
	
	public static final String ausnahmeId = "BaseException";
	public static final String parameter = "param";
	public static final TestFehlertextProviderImpl provider = new TestFehlertextProviderImpl();
	
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
			this(ausnahmeId, provider, parameter);
		}
		
		MyBusinessException(Throwable t){
			this(ausnahmeId, t, provider, parameter);
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
			this(ausnahmeId, provider, parameter);
		}
		
		MyTechnicalException(Throwable t){
			this(ausnahmeId, t, provider, parameter);
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
			this(ausnahmeId, provider, parameter);
		}
		
		MyTechnicalRuntimeException(Throwable t){
			this(ausnahmeId, t, provider, parameter);
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
