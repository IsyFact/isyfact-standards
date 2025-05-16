package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.sicherheit.Recht;
import de.bund.bva.isyfact.sicherheit.common.exception.RollenRechteMappingException;
import de.bund.bva.isyfact.sicherheit.impl.RollenRechteMapping;
import de.bund.bva.isyfact.sicherheit.impl.XmlAccess;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmlAccessTest {

	private static final String ROLLEN_RECHTE_XML_PATH = "/resources/sicherheit/";
	
	private XmlAccess xmlAccess;
	
	@Before
	public void setUp(){
		xmlAccess = new XmlAccess();
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testLeereAnwendungsid() {
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_LeereAnwendungsId.xml");		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRollenRechteDateiExistiertNicht(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "datei.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testFalschesDateiFormat(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_FalschesFormat.xml");
	}

	
	@Test(expected = RollenRechteMappingException.class)
	public void testMehrereRechteIdsInRecht() {
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_MehrereRechteIdsInRecht.xml");		
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRechtOhneID(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RechtOhneId.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRechtLeereID(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RechtLeereId.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testPropertyOhneName(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_propertyOhneName.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testPropertyOhneValue(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_propertyOhneValue.xml");
	}
	
	@Test
	public void testRechtMitProperties(){
		RollenRechteMapping mapping =  xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_property.xml");
		assertEquals("Benutzerverzeichnis" ,mapping.getAnwendungsId());
		boolean found = false;
		for (Recht recht : mapping.getAlleDefiniertenRechte()) {
			if(recht.getId().equalsIgnoreCase("Recht_C")){
				found = true;
				assertNotNull(recht.getProperty("propName_C1"));
				assertEquals("propValue_C1", (String)recht.getProperty("propName_C1"));
			}
		}
		assertTrue(found);
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRolleOhneId(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RolleOhneId.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRolleLeereId(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RolleLeereId.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRollenLeere(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RollenLeer.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRechtOhneIdElement(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RechtOhneIdElement.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testUnbekannteRechteIdInRolle(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_UnbekannteRechteIdInRolle.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testLeereRechteIdInRolle(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_LeereRechteIdInRolle.xml");
	}
	
	@Test(expected = RollenRechteMappingException.class)
	public void testRechtOhneIdElementInRolle(){
		xmlAccess.parseRollenRechteFile(ROLLEN_RECHTE_XML_PATH + "rollenrechte_RechteOhneIdElementInRolle.xml");
	}
}
