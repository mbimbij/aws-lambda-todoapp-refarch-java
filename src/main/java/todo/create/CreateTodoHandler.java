package todo.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import todo.GatewayResponse;
import todo.TodoDto;
import todo.TodoState;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class CreateTodoHandler implements RequestStreamHandler {
  private final TodoItemRepository todoItemRepository;
  private static final ObjectMapper mapper = new ObjectMapper();

  public CreateTodoHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @SneakyThrows
  @Override
  public void handleRequest(InputStream input, OutputStream output, Context context) {
    JsonNode jsonNode = mapper.readTree(input);
    log.info("received event {}", jsonNode.toString());
    CreateTodoRequest request = mapper.readValue(jsonNode.at("/body").textValue(), CreateTodoRequest.class);
    TodoItemEntity entity = new TodoItemEntity(request.getName(), TodoState.TODO.name());
    todoItemRepository.save(entity);
    TodoDto responseBody = new TodoDto(entity.getId(), entity.getName(), entity.getState());
    mapper.writeValue(output, GatewayResponse.createOkResponse(responseBody, 201));
  }
}
