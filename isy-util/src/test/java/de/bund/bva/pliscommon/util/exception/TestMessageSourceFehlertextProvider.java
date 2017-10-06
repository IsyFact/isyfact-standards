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
package de.bund.bva.pliscommon.util.exception;

import static org.junit.Assert.*;

import de.bund.bva.pliscommon.util.exception.MessageSourceFehlertextProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.pliscommon.util.spring.MessageSourceHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testMessageSourceFehlertextProvider.xml" })
public class TestMessageSourceFehlertextProvider {

	@Test
	public void testGetMessage() {
		MessageSourceFehlertextProvider provider = new MessageSourceFehlertextProvider();
		assertNotNull(provider);
		
		String result = provider.getMessage("message2", "Hans");
		assertEquals("Hallo Hans", result);
		
		result = MessageSourceHolder.getMessage("message1");
		assertEquals("Hallo Welt", result);		
		
	}
	
	@Test
	public void testGetMessageWrongMessageCode(){
		MessageSourceFehlertextProvider provider = new MessageSourceFehlertextProvider();		
		assertNotNull(provider);
		
		String result = provider.getMessage("message3", (String[])null);
		assertEquals("message3", result);
		
		result = provider.getMessage("message3", new String[]{});
		assertEquals("message3", result);
		
		result = provider.getMessage("message3", "Hans", "Mustermann");
		assertEquals("message3: Hans, Mustermann", result);
	}
}
