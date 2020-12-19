package todo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Slf4j
public abstract class AbstractRepository<T> {

  protected DynamoDBMapper mapper;
  protected final Class<T> entityClass;

  protected AbstractRepository() {
    ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();

    // This entityClass refers to the actual entity class in the subclass declaration.

    // For instance, ProductInfoDAO extends AbstractDAO<ProductInfo, String>
    // In this case entityClass = ProductInfo, and ID is String type
    // which refers to the ProductInfo's partition key string value
    this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
  }

  public void save(T t) {
    log.info("saving item: {}", t.toString());
    mapper.save(t);
  }

  /**
   * <strong>WARNING:</strong> It is not recommended to perform full table scan
   * targeting the real production environment.
   *
   * @return All items
   */
  public List<T> findAll() {
    DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
    return mapper.scan(entityClass, scanExpression);
  }

  public void delete(T t){
    log.info("deleting item: {}", t.toString());
    mapper.delete(t);
  }

  public void update(T t){
    log.info("updating item: {}", t.toString());
    mapper.save(t, DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES.config());
  }

  public void setMapper(DynamoDBMapper dynamoDBMapper) {
    this.mapper = dynamoDBMapper;
  }

}

