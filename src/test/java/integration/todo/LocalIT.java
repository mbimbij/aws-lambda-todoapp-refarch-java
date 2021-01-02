package integration.todo;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
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
import todo.GatewayResponse;
import todo.TodoDto;
import todo.TodoState;
import todo.complete.CompleteTodoHandler;
import todo.config.DynamoDbConfiguration;
import todo.create.CreateTodoHandler;
import todo.delete.DeleteTodoHandler;
import todo.factory.DynamoDbMapperFactory;
import todo.getall.GetAllTodosHandler;
import todo.repository.TodoItemEntity;
import todo.repository.TodoItemRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static todo.config.DynamoDbConfiguration.TABLE_NAME;

@Testcontainers
public class LocalIT {

  private static final String DYNAMODB_SERVICE_NAME = "dynamodb_1";
  @Container
  public static DockerComposeContainer<?> dynamoDbContainer =
      new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
          .withExposedService(DYNAMODB_SERVICE_NAME, 8000)
          .waitingFor(DYNAMODB_SERVICE_NAME,
              Wait
                  .forHttp("/shell")
                  .withStartupTimeout(Duration.ofSeconds(20)));
  private static String SERVICE_ENDPOINT;
  private static CreateTodoHandler createTodoHandler;
  private static GetAllTodosHandler getAllTodosHandler;
  private static CompleteTodoHandler completeTodoHandler;
  private static DeleteTodoHandler deleteTodoHandler;
  private static DynamoDbClient dynamoDbClient;
  private final ObjectMapper mapper = new ObjectMapper();
  private static TodoItemRepository repository;
  private String correlationId;
  private final Context context = TestContext.builder().build();

  @BeforeEach
  void setUp() {
    correlationId = UUID.randomUUID().toString();
  }

  @BeforeAll
  public static void beforeAll() {
    try (MockedStatic<DynamoDbMapperFactory> mocked = mockStatic(DynamoDbMapperFactory.class)) {
      int dynamodbMappedPort = dynamoDbContainer.getServicePort(DYNAMODB_SERVICE_NAME, 8000);
      SERVICE_ENDPOINT = String.format("http://localhost:%d", dynamodbMappedPort);
      mocked.when(DynamoDbMapperFactory::createDynamoDBMapper).thenReturn(createLocalDynamoDBMapper());
      createTodoHandler = new CreateTodoHandler();
      getAllTodosHandler = new GetAllTodosHandler();
      completeTodoHandler = new CompleteTodoHandler();
      deleteTodoHandler = new DeleteTodoHandler();
      dynamoDbClient = dynamoDbClient();
    }
    createTable();
    repository = TodoItemRepository.getInstance();
  }

  public static DynamoDBMapper createLocalDynamoDBMapper() {
    AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
        SERVICE_ENDPOINT,
        DynamoDbConfiguration.REGION.getName());
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(endpointConfig)
        .build();
    return new DynamoDBMapper(client);
  }

  static DynamoDbClient dynamoDbClient() {
    DynamoDbClientBuilder builder = DynamoDbClient.builder();
    builder.httpClient(ApacheHttpClient.builder().build());
    builder.endpointOverride(URI.create(SERVICE_ENDPOINT));
    return builder.build();
  }

  public static void createTable() {
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
    String input = String.format("{\"body\":\"{\\\"name\\\":\\\"%s\\\"}\"}", correlationId);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // WHEN
    createTodoHandler.handleRequest(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), outputStream, context);

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
          .containsAllEntriesOf(expectedOutputWrapperWithoutBody.asMap());
      softAssertions.assertThat(actualBody)
          .containsAllEntriesOf(expectedBody);
    });
  }

  @SneakyThrows
  @Test
  void given2ItemsInDynamo_whenGetAllItems_thenAllItemsRetrieved() {
    // GIVEN
    TodoItemEntity item1 = new TodoItemEntity("item1", TodoState.TODO.name());
    TodoItemEntity item2 = new TodoItemEntity("item2", TodoState.DONE.name());
    repository.save(item1);
    repository.save(item2);

    String input = "{\"body\":\"{}\"}";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // WHEN
    GatewayResponse<List<TodoDto>> actualResponse = getAllTodosHandler.handleRequest(input, context);

    // THEN
    TodoDto expectedItem1 = new TodoDto("ignored", "item1", TodoState.TODO.name());
    TodoDto expectedItem2 = new TodoDto("ignored", "item2", TodoState.DONE.name());

    List<TodoDto> actualTodoItems = mapper.readValue(actualResponse.getBody(),
        TypeFactory.defaultInstance()
            .constructCollectionType(List.class, TodoDto.class));
    List<TodoDto> expectedTodoItems = List.of(expectedItem1, expectedItem2);

    assertThat(actualTodoItems)
        .usingElementComparatorIgnoringFields("id")
        .containsAll(expectedTodoItems);
  }

  @SneakyThrows
  @Test
  void givenAnItemWithStateEqualsTODO_whenCompleteItem_thenItemIsCompletedInDynamo_andResponseIsAsExpected() {
    // GIVEN
    TodoItemEntity item = new TodoItemEntity(correlationId, TodoState.TODO.name());
    repository.save(item);
    Map<String, Object> inputMap = Map.of("pathParameters", Map.of("id", item.getId()));
    String inputString = mapper.writeValueAsString(inputMap);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // WHEN
    completeTodoHandler.handleRequest(new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8)), outputStream, context);

    // THEN
    TodoItemEntity expectedItemInDynano = new TodoItemEntity(correlationId,TodoState.DONE.name());
    expectedItemInDynano.setId(item.getId());

    assertThat(repository.findAll()).contains(expectedItemInDynano);

    // AND
    Item expectedOutputWrapperWithoutBody = new Item()
        .withMap("headers", Map.of("Content-Type", "application/json"))
        .withInt("statusCode", 200);

    Item ActualOutputWrapper = Item.fromJSON(outputStream.toString());
    Map<String, Object> actualBody = mapper.readValue(ActualOutputWrapper.getString("body"),
        TypeFactory.defaultInstance()
            .constructMapType(Map.class, String.class, Object.class));

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(ActualOutputWrapper.asMap())
          .containsAllEntriesOf(expectedOutputWrapperWithoutBody.asMap());
      softAssertions.assertThat(actualBody).isNull();
    });
  }

  @SneakyThrows
  @Test
  void givenSomeInsertedItem_whenCallDeleteHandler_thenItemIsDeleted() {
    // GIVEN
    TodoItemEntity item = new TodoItemEntity(correlationId, TodoState.TODO.name());
    repository.save(item);
    assertThat(repository.findAll()).contains(item);
    Map<String, Object> inputMap = Map.of("pathParameters", Map.of("id", item.getId()));
    String inputString = mapper.writeValueAsString(inputMap);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    // WHEN
    deleteTodoHandler.handleRequest(new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8)), outputStream, context);

    // THEN
    assertThat(repository.findAll()).doesNotContain(item);

    // AND
    Item expectedOutputWrapperWithoutBody = new Item()
        .withMap("headers", Map.of("Content-Type", "application/json"))
        .withInt("statusCode", 200);

    Item ActualOutputWrapper = Item.fromJSON(outputStream.toString());
    Map<String, Object> actualBody = mapper.readValue(ActualOutputWrapper.getString("body"),
        TypeFactory.defaultInstance()
            .constructMapType(Map.class, String.class, Object.class));

    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(ActualOutputWrapper.asMap())
          .containsAllEntriesOf(expectedOutputWrapperWithoutBody.asMap());
      softAssertions.assertThat(actualBody).isNull();
    });
  }
}
