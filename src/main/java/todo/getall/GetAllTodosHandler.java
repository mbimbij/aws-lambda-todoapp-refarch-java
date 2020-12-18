package todo.getall;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import todo.TodoResponse;
import todo.repository.TodoItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GetAllTodosHandler implements RequestHandler<GetAllTodosRequest, GetAllTodosResponse> {

  private DynamoDB dynamoDb;
  private String DYNAMODB_TABLE_NAME = "todo-table";
  private Regions REGION = Regions.EU_WEST_3;
  private TodoItemRepository todoItemRepository;

  public GetAllTodosHandler() {
    log.info("coucou init");
    this.initDynamoDbClient();
  }

  @Override
  public GetAllTodosResponse handleRequest(GetAllTodosRequest helloRequest, Context context) {

//    List<TodoResponse> todos = new ArrayList<>();
//    for (Item item : dynamoDb.getTable(DYNAMODB_TABLE_NAME).scan()) {
//      log.info("item: " + item.toJSON());
//      TodoResponse todo = new TodoResponse(item.getString("id"), item.getString("name"), item.getString("state"));
//      todos.add(todo);
//    }
    return new GetAllTodosResponse(todoItemRepository.findAll().stream()
        .map(entity -> new TodoResponse(entity.getId(), entity.getName(), entity.getState()))
        .collect(Collectors.toList()));
  }

  private void initDynamoDbClient() {
    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        .withRegion(REGION.getName())
        .build();
    this.dynamoDb = new DynamoDB(client);
    todoItemRepository = new TodoItemRepository();
    todoItemRepository.setMapper(new DynamoDBMapper(client));
  }
}
