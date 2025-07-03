package com.me2u.gateway.controller.utils;

import org.springframework.web.server.ServerWebExchange;

import java.util.Locale;

public class Utils {
  public static Locale getDefaultLocale(ServerWebExchange exchange) {
    Locale locale = exchange.getLocaleContext().getLocale();
    if (locale == null) {
      locale = Locale.ENGLISH;
    }
    return locale;
  }
}
