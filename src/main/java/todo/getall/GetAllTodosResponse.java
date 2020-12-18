package todo.getall;

import lombok.Value;
import todo.TodoDto;

import java.util.List;

@Value
public class GetAllTodosResponse {
  List<TodoDto> allTodos;
}
