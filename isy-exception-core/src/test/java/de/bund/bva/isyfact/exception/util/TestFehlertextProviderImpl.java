package de.bund.bva.isyfact.exception.util;

import de.bund.bva.isyfact.exception.FehlertextProvider;

public class TestFehlertextProviderImpl implements FehlertextProvider {

	@Override
	public String getMessage(String schluessel, String... parameter) {
		String message = new String(schluessel);
		for (String string : parameter) {
			message+=" "+string;
		}
		return message;
	}

}
 