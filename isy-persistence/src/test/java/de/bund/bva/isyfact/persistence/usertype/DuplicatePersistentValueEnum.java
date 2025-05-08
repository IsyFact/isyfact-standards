package de.bund.bva.isyfact.persistence.usertype;

import de.bund.bva.isyfact.persistence.annotation.EnumId;
import de.bund.bva.isyfact.persistence.annotation.PersistentValue;

public enum DuplicatePersistentValueEnum {

	@PersistentValue("A")
	A,
	@PersistentValue("A")
	B;
	
	@EnumId
	public String getId(){
		return "A";
	}
}
