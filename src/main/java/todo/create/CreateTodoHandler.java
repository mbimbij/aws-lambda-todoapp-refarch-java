package todo.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoDto;
import todo.TodoState;
import todo.getall.GetAllTodosRequest;
import todo.getall.GetAllTodosResponse;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CreateTodoHandler implements RequestHandler<CreateTodoRequest, TodoDto> {
  private TodoItemRepository todoItemRepository;

  public CreateTodoHandler() {
    this.todoItemRepository = TodoItemRepository.create();
  }

  @Override
  public TodoDto handleRequest(CreateTodoRequest helloRequest, Context context) {
    TodoItemEntity entity = new TodoItemEntity(helloRequest.getName(), TodoState.TODO.name());
    todoItemRepository.save(entity);
    return new TodoDto(entity.getId(), entity.getName(), entity.getState());
  }
}
