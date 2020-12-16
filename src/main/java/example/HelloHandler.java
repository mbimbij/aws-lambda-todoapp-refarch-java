package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class HelloHandler implements RequestHandler<HelloRequest,HelloResponse> {
  @Override
  public HelloResponse handleRequest(HelloRequest helloRequest, Context context) {
    return new HelloResponse("Hello from "+helloRequest.getCountry());
  }
}
