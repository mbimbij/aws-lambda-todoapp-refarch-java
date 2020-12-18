package todo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;
import lombok.NoArgsConstructor;
import todo.config.DynamoDbConfiguration;

@Data
@NoArgsConstructor
@DynamoDBTable(tableName = DynamoDbConfiguration.TABLE_NAME)
public class TodoItemEntity {
  @DynamoDBHashKey
  @DynamoDBAutoGeneratedKey
  private String id;
  @DynamoDBAttribute
  private String name;
  @DynamoDBAttribute
  private String state;

  public TodoItemEntity(String name, String state) {
    this.name = name;
    this.state = state;
  }

  public static TodoItemEntity createInstanceForDeleteRequest(String id) {
    TodoItemEntity entity = new TodoItemEntity();
    entity.setId(id);
    return entity;
  }
}
