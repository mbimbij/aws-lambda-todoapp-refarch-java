package integration.todo.create;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.*;
import todo.config.DynamoDbConfiguration;
import todo.create.CreateTodoHandler;
import todo.factory.DynamoDbMapperFactory;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static todo.config.DynamoDbConfiguration.TABLE_NAME;

@Testcontainers
public class CreateTodoHandlerIT {

  private static final String DYNAMODB_SERVICE_NAME = "dynamodb_1";
  @Container
  public static DockerComposeContainer dynamoDbContainer =
      new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
          .withExposedService(DYNAMODB_SERVICE_NAME, 8000)
          .waitingFor(DYNAMODB_SERVICE_NAME,
              Wait
                  .forHttp("/shell")
                  .withStartupTimeout(Duration.ofSeconds(20)));
  private String SERVICE_ENDPOINT;
  CreateTodoHandler handler;
  DynamoDbClient dynamoDbClient;


  @BeforeEach
  void setUp() {
    try (MockedStatic<DynamoDbMapperFactory> mocked = mockStatic(DynamoDbMapperFactory.class)) {
      int dynamodbMappedPort = dynamoDbContainer.getServicePort(DYNAMODB_SERVICE_NAME, 8000);
      SERVICE_ENDPOINT = String.format("http://localhost:%d", dynamodbMappedPort);
      mocked.when(DynamoDbMapperFactory::createDynamoDBMapper).thenReturn(createLocalDynamoDBMapper());
      handler = new CreateTodoHandler();
      dynamoDbClient = dynamoDbClient();
      createTable();
    }
  }

  public DynamoDBMapper createLocalDynamoDBMapper() {
    AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
        SERVICE_ENDPOINT,
        DynamoDbConfiguration.REGION.getName());
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(endpointConfig)
        .build();
    return new DynamoDBMapper(client);
  }

  DynamoDbClient dynamoDbClient() {
    DynamoDbClientBuilder builder = DynamoDbClient.builder();
    builder.httpClient(ApacheHttpClient.builder().build());
    builder.endpointOverride(URI.create(SERVICE_ENDPOINT));
    return builder.build();
  }

  public void createTable() {
    dynamoDbClient.createTable(CreateTableRequest.builder()
        .tableName(TABLE_NAME)
        .keySchema(KeySchemaElement.builder()
            .keyType(KeyType.HASH)
            .attributeName("id")
            .build())
        .attributeDefinitions(
            AttributeDefinition.builder()
                .attributeName("id")
                .attributeType(ScalarAttributeType.S)
                .build())
        .provisionedThroughput(
            ProvisionedThroughput.builder()
                .readCapacityUnits(1L)
                .writeCapacityUnits(1L)
                .build())
        .build());

  }

  @Test
  void name() {
    TodoItemRepository repository = TodoItemRepository.getInstance();
    List<TodoItemEntity> all = repository.findAll();
    assertThat(all).isEmpty();
  }
}
