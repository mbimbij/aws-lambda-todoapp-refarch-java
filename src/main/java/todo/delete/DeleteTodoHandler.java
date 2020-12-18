package todo.delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoDto;
import todo.TodoState;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

@Slf4j
public class DeleteTodoHandler implements RequestHandler<DeleteTodoRequest, TodoDto> {
  private TodoItemRepository todoItemRepository;

  public DeleteTodoHandler() {
    this.todoItemRepository = TodoItemRepository.getInstance();
  }

  @Override
  public TodoDto handleRequest(DeleteTodoRequest deleteRequest, Context context) {
    log.info("delete item {}", deleteRequest.toString());
    TodoItemEntity entity = TodoItemEntity.createInstanceForDeleteRequest(deleteRequest.getId());
    todoItemRepository.delete(entity);
    return new TodoDto(entity.getId(), entity.getName(), entity.getState());
  }
}
