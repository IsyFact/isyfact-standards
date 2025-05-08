package de.bund.bva.isyfact.persistence.usertype;

import de.bund.bva.isyfact.persistence.annotation.EnumId;
import de.bund.bva.isyfact.persistence.annotation.PersistentValue;

public enum DuplicateIdGetterEnum {

	@PersistentValue("A")
	A,
	@PersistentValue("B")
	B;
	
	@EnumId
	public String getId1(){
		return "A";
	}
	
	@EnumId
	public String getId2(){
		return "B";
	}
}
