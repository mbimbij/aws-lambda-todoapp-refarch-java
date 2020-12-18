package todo.delete;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DeleteTodoRequest {
  private String id;
}
