package integration.todo.create;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import integration.todo.TestContext;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
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
import todo.TodoState;
import todo.config.DynamoDbConfiguration;
import todo.create.CreateTodoHandler;
import todo.factory.DynamoDbMapperFactory;
import todo.repository.TodoItemRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static todo.config.DynamoDbConfiguration.TABLE_NAME;

@Testcontainers
public class CreateTodoHandlerIT {

  private static final String DYNAMODB_SERVICE_NAME = "dynamodb_1";
  @Container
  public static DockerComposeContainer<?> dynamoDbContainer =
      new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
          .withExposedService(DYNAMODB_SERVICE_NAME, 8000)
          .waitingFor(DYNAMODB_SERVICE_NAME,
              Wait
                  .forHttp("/shell")
                  .withStartupTimeout(Duration.ofSeconds(20)));
  private String SERVICE_ENDPOINT;
  private CreateTodoHandler handler;
  private DynamoDbClient dynamoDbClient;
  private ObjectMapper mapper = new ObjectMapper();
  private TodoItemRepository repository;

  @BeforeEach
  void setUp() {
    try (MockedStatic<DynamoDbMapperFactory> mocked = mockStatic(DynamoDbMapperFactory.class)) {
      int dynamodbMappedPort = dynamoDbContainer.getServicePort(DYNAMODB_SERVICE_NAME, 8000);
      SERVICE_ENDPOINT = String.format("http://localhost:%d", dynamodbMappedPort);
      mocked.when(DynamoDbMapperFactory::createDynamoDBMapper).thenReturn(createLocalDynamoDBMapper());
      handler = new CreateTodoHandler();
      dynamoDbClient = dynamoDbClient();
    }
    createTable();
    repository = TodoItemRepository.getInstance();
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

  @SneakyThrows
  @Test
  void whenCreateItem_thenItemPresentInDynamo_andResponseOK() {
    // GIVEN
    Context context = TestContext.builder().build();
    String correlationId = UUID.randomUUID().toString();
    String input = String.format("{\"body\":\"{\\\"name\\\":\\\"%s\\\"}\"}", correlationId);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // WHEN
    handler.handleRequest(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), outputStream, context);

    // THEN
    assertThat(repository.findAll()).anyMatch(entity -> Objects.equals(entity.getName(), correlationId));

    // AND
    Item expectedOutputWrapperWithoutBody = new Item()
        .withMap("headers", Map.of("Content-Type", "application/json"))
        .withInt("statusCode", 201);
    Map<String, Object> expectedBody = Map.of("name", correlationId, "state", TodoState.TODO.name());

    Item ActualOutputWrapper = Item.fromJSON(outputStream.toString());
    Map<String, Object> actualBody = mapper.readValue(ActualOutputWrapper.getString("body"),
        TypeFactory.defaultInstance()
            .constructMapType(Map.class, String.class, Object.class));

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(ActualOutputWrapper.asMap())
//          .usingRecursiveComparison()
//          .ignoringFields("attributes")
          .containsAllEntriesOf(expectedOutputWrapperWithoutBody.asMap());
      softAssertions.assertThat(actualBody)
          .containsAllEntriesOf(expectedBody);
    });
  }

  @Test
  void given2ItemsInDynamo_whenGetAllItems_thenAllItemsRetrieved() {

  }
}
