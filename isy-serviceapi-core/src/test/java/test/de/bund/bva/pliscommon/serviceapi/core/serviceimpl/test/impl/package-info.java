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
@ExceptionMapping(mappings = { 	@Mapping(	exception = PlisBusinessException.class,
											toException = PlisBusinessToException.class),
								@Mapping(	exception = PlisTechnicalException.class, 
											toException = PlisTechnicalToException.class)}, 
					technicalToException = PlisTechnicalToException.class)
package test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl;

import de.bund.bva.pliscommon.exception.PlisBusinessException;
import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.ExceptionMapping;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.Mapping;
