package com.github.rawsanj.handler;

import com.github.rawsanj.messaging.RedisChatMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration(proxyBeanMethods = false)
public class WebHttpHandler {

	@Bean
	public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/static/index.html") Resource html,
			RedisChatMessagePublisher redisChatMessagePublisher) {
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html)).andRoute(
				POST("/message"),
				request -> request.bodyToMono(Message.class)
						.flatMap(message -> redisChatMessagePublisher.publishChatMessage(message.getMessage()))
						.flatMap(aLong -> ServerResponse.ok().bodyValue(new Message("Message Sent Successfully!."))));
	}

	public static class Message {
		private String message;

		public Message() {

		}

		/**
		 * @param message
		 */
		public Message(String message) {
			super();
			this.message = message;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}

	}

}
