package com.me2u.gateway.util;

import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class AppUtil {
  public static Locale getDefaultLocale(ServerWebExchange exchange) {
    Locale locale = exchange.getLocaleContext().getLocale();
    if (locale == null) {
      locale = Locale.ENGLISH;
    }
    return locale;
  }
}
