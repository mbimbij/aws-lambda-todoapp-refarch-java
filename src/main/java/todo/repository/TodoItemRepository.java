package todo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import todo.TodoState;
import todo.factory.DynamoDbMapperFactory;

public class TodoItemRepository extends AbstractRepository<TodoItemEntity> {
  private static TodoItemRepository instance;

  private TodoItemRepository() {
    super();
  }

  public static TodoItemRepository getInstance() {
    if (instance == null) {
      DynamoDBMapper dynamoDBMapper = DynamoDbMapperFactory.createDynamoDBMapper();
      instance = new TodoItemRepository();
      instance.setMapper(dynamoDBMapper);
    }
    return instance;
  }

  public void complete(String id) {
    TodoItemEntity entityToComplete = TodoItemEntity.createInstanceForDeleteOrUpdateRequest(id);
    entityToComplete.setState(TodoState.DONE.name());
    instance.update(entityToComplete);
  }
}
