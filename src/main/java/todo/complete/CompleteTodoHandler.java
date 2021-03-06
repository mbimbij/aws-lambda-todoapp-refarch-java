package todo.complete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import todo.GatewayResponse;
import todo.TodoDto;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class CompleteTodoHandler implements RequestStreamHandler {
  private final TodoItemRepository todoItemRepository;
  private static final ObjectMapper mapper = new ObjectMapper();

  public CompleteTodoHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @SneakyThrows
  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context) {
    JsonNode jsonNode = mapper.readTree(input);
    log.info("received event {}", jsonNode.toString());
    String id = jsonNode.at("/pathParameters/id").textValue();
    todoItemRepository.complete(id);
    mapper.writeValue(output, GatewayResponse.createOkResponse(null));
  }
}
