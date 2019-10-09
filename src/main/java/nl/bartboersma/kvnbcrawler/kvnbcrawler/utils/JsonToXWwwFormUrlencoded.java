package nl.bartboersma.kvnbcrawler.kvnbcrawler.utils;

import lombok.extern.slf4j.Slf4j;
import nl.bartboersma.kvnbcrawler.kvnbcrawler.models.KnvbSearchClubRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonToXWwwFormUrlencoded {

  public static String translateJsonToXWwwFormUrlencoded(KnvbSearchClubRequest request) {
    StringBuilder sb = new StringBuilder();

    for (Field field : request.getClass().getDeclaredFields()) {
      try {
        sb.append(field.getName()).append("=").append(URLEncoder.encode(field.get(request).toString(), StandardCharsets.UTF_8.toString())).append("&");
      } catch (IllegalAccessException | UnsupportedEncodingException e) {
        log.error("Something bad happend while creating the x-www-form-url-encoded: {}", e.getMessage());
      }
    }

    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }

    return sb.toString();
  }
}
