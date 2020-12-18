package todo.getall;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import todo.GatewayResponse;
import todo.TodoDto;
import todo.repository.TodoItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GetAllTodosHandler implements RequestHandler<Object, GatewayResponse<GetAllTodosResponse>> {
  private final TodoItemRepository todoItemRepository;

  public GetAllTodosHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @Override
  public GatewayResponse<GetAllTodosResponse> handleRequest(Object getAllRequest, Context context) {
    List<TodoDto> todos = todoItemRepository.findAll().stream()
        .map(entity -> new TodoDto(entity.getId(), entity.getName(), entity.getState()))
        .collect(Collectors.toList());
    return new GatewayResponse<>(new GetAllTodosResponse(todos), GatewayResponse.APPLICATION_JSON,200);
  }
}
