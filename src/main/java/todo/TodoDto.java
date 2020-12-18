package todo;

import lombok.Value;

@Value
public class TodoDto {
  String id;
  String name;
  String state;
}
