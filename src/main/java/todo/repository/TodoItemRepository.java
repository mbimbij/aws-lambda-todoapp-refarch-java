package todo.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import todo.config.DynamoDbConfiguration;

public class TodoItemRepository extends AbstractRepository<TodoItemEntity, String> {
  public static TodoItemRepository create(){
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withRegion(DynamoDbConfiguration.REGION.getName())
        .build();
    TodoItemRepository todoItemRepository = new TodoItemRepository();
    todoItemRepository.setMapper(new DynamoDBMapper(client));
    return todoItemRepository;
  }
}
