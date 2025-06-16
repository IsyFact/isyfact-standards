package de.bund.bva.isyfact.persistence.usertype;

import de.bund.bva.isyfact.persistence.annotation.EnumId;
import de.bund.bva.isyfact.persistence.annotation.PersistentValue;

public enum WrongIdEnum {
	@PersistentValue("A")
	A;
	
	@EnumId
	public String getId(){
		return null;
	}
}
