package todo.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoDto;
import todo.TodoState;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

@Slf4j
public class CreateTodoHandler implements RequestHandler<CreateTodoRequest, TodoDto> {
  private final TodoItemRepository todoItemRepository;

  public CreateTodoHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @Override
  public TodoDto handleRequest(CreateTodoRequest createRequest, Context context) {
    TodoItemEntity entity = new TodoItemEntity(createRequest.getName(), TodoState.TODO.name());
    todoItemRepository.save(entity);
    return new TodoDto(entity.getId(), entity.getName(), entity.getState());
  }
}
