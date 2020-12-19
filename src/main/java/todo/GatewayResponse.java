package todo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO containing response object for API Gateway.
 */
@Getter
@JsonAutoDetect
public class GatewayResponse<T> {
  public static final Map<String, String> APPLICATION_JSON = Collections.singletonMap("Content-Type",
      "application/json");
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final String body;
  private final Map<String, String> headers;
  private final int statusCode;


  /**
   * Creates a GatewayResponse object.
   *
   * @param body       body of the response
   * @param headers    headers of the response
   * @param statusCode status code of the response
   */
  @SneakyThrows
  public GatewayResponse(final T body, final Map<String, String> headers, final int statusCode) {
    this.statusCode = statusCode;
    this.body = objectMapper.writeValueAsString(body);
    this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
  }

  public static <T> GatewayResponse<T> createOkResponse(final T body) {
    return new GatewayResponse<>(body, APPLICATION_JSON, 200);
  }
}
