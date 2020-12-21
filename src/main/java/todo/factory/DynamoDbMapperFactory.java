package todo.factory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import todo.config.DynamoDbConfiguration;
import todo.repository.TodoItemRepository;

public class DynamoDbMapperFactory {
  public static DynamoDBMapper createDynamoDBMapper() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withRegion(DynamoDbConfiguration.REGION.getName())
        .build();
    return new DynamoDBMapper(client);
  }
}