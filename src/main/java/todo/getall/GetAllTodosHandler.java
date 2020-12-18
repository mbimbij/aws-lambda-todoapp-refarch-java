package todo.getall;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoResponse;
import todo.repository.TodoItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GetAllTodosHandler implements RequestHandler<GetAllTodosRequest, GetAllTodosResponse> {
  private TodoItemRepository todoItemRepository;

  public GetAllTodosHandler() {
    log.info("coucou init");
    this.todoItemRepository = TodoItemRepository.create();
  }

  @Override
  public GetAllTodosResponse handleRequest(GetAllTodosRequest helloRequest, Context context) {
    List<TodoResponse> todos = todoItemRepository.findAll().stream()
        .map(entity -> new TodoResponse(entity.getId(), entity.getName(), entity.getState()))
        .collect(Collectors.toList());
    return new GetAllTodosResponse(todos);
  }
}
