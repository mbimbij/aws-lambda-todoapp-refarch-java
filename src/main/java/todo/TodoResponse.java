package todo;

import lombok.Value;

@Value
public class TodoResponse {
  String id;
  String name;
  String state;
}
