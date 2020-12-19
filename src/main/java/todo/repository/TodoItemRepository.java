package todo.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import todo.TodoState;
import todo.config.DynamoDbConfiguration;

public class TodoItemRepository extends AbstractRepository<TodoItemEntity> {
  private static TodoItemRepository instance;

  private TodoItemRepository() {
    super();
  }

  public static TodoItemRepository getInstance() {
    if (instance == null) {
      AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
          .withRegion(DynamoDbConfiguration.REGION.getName())
          .build();
      instance = new TodoItemRepository();
      instance.setMapper(new DynamoDBMapper(client));
    }
    return instance;
  }

  public TodoItemEntity complete(String id) {
    TodoItemEntity entityToComplete = TodoItemEntity.createInstanceForDeleteOrUpdateRequest(id);
    entityToComplete.setState(TodoState.DONE.name());
    instance.update(entityToComplete);
    return entityToComplete;
  }
}
