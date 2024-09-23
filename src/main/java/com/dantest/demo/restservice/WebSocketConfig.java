package com.dantest.demo.restservice;

  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.socket.config.annotation.EnableWebSocket;
  import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
  import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

  @Configuration
  @EnableWebSocket
  public class WebSocketConfig implements WebSocketConfigurer {

      @Autowired
      private ShowerStatusWebSocketHandler showerStatusWebSocketHandler;


      @Override
      public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
          registry.addHandler(showerStatusWebSocketHandler, "/shower-status")
                  .setAllowedOrigins("*");
      }
  }
