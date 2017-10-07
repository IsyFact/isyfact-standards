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
package test.de.bund.bva.pliscommon.serviceapi.core.aop.test;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.serviceapi.core.aufrufkontext.StelltLoggingKontextBereit;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Eine Klasse zum Testen der {@link @StelltLoggingKontextBereit} Annotation.
 * <p>
 * Bietet Methoden mit verschiedenen Konfigurationen der Annotation an.
 * 
 */
@StelltLoggingKontextBereit
public class LoggingKontextSstTestBean {

	public String stelltAufrufKontextNichtBereitOhneParameter() {
		return MdcHelper.liesKorrelationsId();
	}

	public String stelltAufrufKontextNichtBereitMitParameter(AufrufKontextTo aufrufKontextTo) {
		return MdcHelper.liesKorrelationsId();
	}

	@StelltLoggingKontextBereit
	public String stelltAufrufKontextBereitOhneParameter() {
		return MdcHelper.liesKorrelationsId();
	}

	@StelltLoggingKontextBereit
	public String stelltAufrufKontextBereitMitParameter(AufrufKontextTo aufrufKontextTo) {
		return MdcHelper.liesKorrelationsId();
	}
}
