package com.example.demo;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
/*@EnableIntegration annotation designates this class as a Spring Integration configuration.*/
@EnableIntegration
public class SIConfig {

	@Bean
	public MessageChannel channel() {
		return new DirectChannel();
	}
	//bydefault name of method
	@Bean
	public MessageSource messageSource() {
		FileReadingMessageSource ms= new FileReadingMessageSource();
		ms.setDirectory(new File("C:\\Users\\payal\\Pictures"));
		ms.setFilter(new SimplePatternFileListFilter("*.mp4"));
		return ms;
	}
	
	@Bean
	public MessageHandler handler() {
		FileWritingMessageHandler handler= new FileWritingMessageHandler(new File("C:\\Users\\payal\\Documents\\batch7"));
		handler.setFileExistsMode(FileExistsMode.IGNORE);
		handler.setExpectReply(false);
		return handler;
		
	}
	
	@Bean
	public IntegrationFlow flow() {
		return IntegrationFlows.from(messageSource(),  configurer -> configurer.poller(Pollers.fixedDelay(10000)))
				.channel(channel())
				.handle(handler()).get();
	}

}
