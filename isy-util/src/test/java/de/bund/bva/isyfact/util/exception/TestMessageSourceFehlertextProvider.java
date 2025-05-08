package de.bund.bva.isyfact.util.exception;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.isyfact.util.spring.MessageSourceHolder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestMessageSourceFehlertextProvider.TestConfig.class)
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

	@Configuration
	public static class TestConfig {

		@Bean
		public MessageSource messageSource() {
			ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
			messageSource.setBasename("locale/messages");
			return messageSource;
		}

		@Bean
		public MessageSourceHolder messageSourceHolder(MessageSource messageSource) {
			MessageSourceHolder holder = new MessageSourceHolder();
			holder.setMessageSource(messageSource);
			return holder;
		}
	}
}
