package todo.getall;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import todo.TodoResponse;

import java.util.List;

public class GetAllTodosHandler implements RequestHandler<GetAllTodosRequest, GetAllTodosResponse> {
  @Override
  public GetAllTodosResponse handleRequest(GetAllTodosRequest helloRequest, Context context) {
    List<TodoResponse> allTodos = List.of(
        new TodoResponse("id1", "task1", "todo"),
        new TodoResponse("id2", "task2", "doing"),
        new TodoResponse("id3", "task3", "done")
    );
    return new GetAllTodosResponse(allTodos);
  }
}
