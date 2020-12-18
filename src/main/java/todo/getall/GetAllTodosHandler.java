package todo.getall;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoDto;
import todo.repository.TodoItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GetAllTodosHandler implements RequestHandler<GetAllTodosRequest, GetAllTodosResponse> {
  private TodoItemRepository todoItemRepository;

  public GetAllTodosHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @Override
  public GetAllTodosResponse handleRequest(GetAllTodosRequest getAllRequest, Context context) {
    List<TodoDto> todos = todoItemRepository.findAll().stream()
        .map(entity -> new TodoDto(entity.getId(), entity.getName(), entity.getState()))
        .collect(Collectors.toList());
    return new GetAllTodosResponse(todos);
  }
}
