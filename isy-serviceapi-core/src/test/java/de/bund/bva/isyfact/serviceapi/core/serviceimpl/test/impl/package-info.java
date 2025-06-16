
@ExceptionMapping(mappings = { 	@Mapping(	exception = BusinessException.class,
											toException = PlisBusinessToException.class),
								@Mapping(	exception = TechnicalException.class,
											toException = PlisTechnicalToException.class)},
					technicalToException = PlisTechnicalToException.class)
package de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.ExceptionMapping;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.Mapping;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
