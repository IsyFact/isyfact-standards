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
package de.bund.bva.isyfact.persistence.usertype;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.persistence.exception.PersistenzException;

public class EnumUserTypeTest {

	private EnumUserType userType;
	
	@Before
	public void setUp(){
		userType = new EnumUserType();
	}
	
    @Test
    public void testSetParameterValues() {        
        Properties prop = new Properties();
        prop.setProperty("enumClass", Vorgangsstatus.class.getName());
        userType.setParameterValues(prop);
        assertEquals("B", userType.convertInstanceToString(Vorgangsstatus.IN_BEARBEITUNG));
        assertEquals(Vorgangsstatus.IN_BEARBEITUNG, userType.convertStringToInstance("B"));        
    }
    
    @Test(expected = NullPointerException.class)
    public void testSetParameterValuesNull() {        
        userType.setParameterValues(null);      
    }
    
    @Test(expected = PersistenzException.class)
    public void testSetParameterValuesNoEnumClassSet() {        
        Properties prop = new Properties();
        userType.setParameterValues(prop);      
    }
    
    @Test(expected = PersistenzException.class)
    public void testSetParameterValuesKeineEnumClass() {        
        Properties prop = new Properties();
        prop.setProperty("enumClass", Object.class.getName());
        userType.setParameterValues(prop);      
    }
    
    @Test(expected = PersistenzException.class)
    public void testSetParameterValuesKeineKlasse() {        
        Properties prop = new Properties();
        prop.setProperty("enumClass", "ObjectA");
        userType.setParameterValues(prop);      
    }

    @Test
    public void testConvertStringToInstance() {        
        userType.setEnumClass(Vorgangsstatus.class);
        assertEquals("B", userType.convertInstanceToString(Vorgangsstatus.IN_BEARBEITUNG));
        assertEquals(Vorgangsstatus.class, userType.returnedClass());
    }

    @Test
    public void testConvertInstanceToString() {        
        userType.setEnumClass(Vorgangsstatus.class);
        assertEquals(Vorgangsstatus.IN_BEARBEITUNG, userType.convertStringToInstance("B"));
    }
    
    @Test(expected = PersistenzException.class)
    public void testKeineAnnotationAnEnumKonstanten(){
    	userType.setEnumClass(Vermerkstyp.class);
    }
    
    @Test(expected = PersistenzException.class)
    public void testDuplicatePersistentValue(){
    	userType.setEnumClass(DuplicatePersistentValueEnum.class);
    }
    
    @Test(expected = PersistenzException.class)
    public void testConvertStringToInstanceKeyNotExists(){
    	userType.setEnumClass(Vorgangsstatus.class);
    	userType.convertStringToInstance("");
    }
    
    @Test(expected = PersistenzException.class)
    public void testConvertInstanceToStringObjectNotExists(){
    	userType.setEnumClass(Vorgangsstatus.class);
    	userType.convertInstanceToString(Vermerkstyp.NACHRICHT_EMPFANGEN);
    }
    
    @Test
    public void testNullSafeGet() throws SQLException{
    	ResultSet rs = mock(ResultSet.class);
    	when(rs.getString(0)).thenReturn("E");
    	when(rs.wasNull()).thenReturn(false);

    	userType.setEnumClass(Vorgangsstatus.class);
    	Object obj = userType.nullSafeGet(rs, 0, null, null);
    	assertEquals(Vorgangsstatus.ERLEDIGT, (Vorgangsstatus)obj);
    }
    
    @Test
    public void testNullSafeGetNull() throws SQLException{
    	ResultSet rs = mock(ResultSet.class);
    	when(rs.getString(0)).thenReturn("NONE");
    	when(rs.wasNull()).thenReturn(true);

    	userType.setEnumClass(Vorgangsstatus.class);
    	Object obj = userType.nullSafeGet(rs, 0, null, null);
    	assertEquals(null, (Vorgangsstatus)obj);
    }
    
    @Test
    public void nullSafeSetNull() throws SQLException{
    	PreparedStatement st = mock(PreparedStatement.class);
    	userType.setEnumClass(Vorgangsstatus.class);
    	userType.nullSafeSet(st, null, 0, null);
    	verify(st, times(1)).setNull(0, userType.getSqlType());
    }
    
    @Test
    public void nullSafeSetVorgangsstatus() throws SQLException{
    	PreparedStatement st = mock(PreparedStatement.class);
    	userType.setEnumClass(Vorgangsstatus.class);
    	userType.nullSafeSet(st, Vorgangsstatus.ERLEDIGT, 0, null);
    	verify(st, times(1)).setString(0, "E");
    }
}
