package todo.config;

import com.amazonaws.regions.Regions;

public class DynamoDbConfiguration {
  public static final String TABLE_NAME = "todo-table";
  public static final Regions REGION = Regions.EU_WEST_3;

  private DynamoDbConfiguration() {
  }
}
