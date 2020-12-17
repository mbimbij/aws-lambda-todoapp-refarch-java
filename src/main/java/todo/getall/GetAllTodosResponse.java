package todo.getall;

import lombok.Value;
import todo.TodoResponse;

import java.util.List;

@Value
public class GetAllTodosResponse {
  List<TodoResponse> allTodos;
}
