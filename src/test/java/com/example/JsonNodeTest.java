package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class JsonNodeTest {
  @SneakyThrows
  @Test
  void name() {
    String s = "{\n" +
        "    \"resource\": \"/items/{id}\",\n" +
        "    \"path\": \"/items/12da0c64-6ea1-4e6f-916e-e9f354724356\",\n" +
        "    \"httpMethod\": \"DELETE\",\n" +
        "    \"headers\": null,\n" +
        "    \"multiValueHeaders\": null,\n" +
        "    \"queryStringParameters\": null,\n" +
        "    \"multiValueQueryStringParameters\": null,\n" +
        "    \"pathParameters\": {\n" +
        "        \"id\": \"12da0c64-6ea1-4e6f-916e-e9f354724356\"\n" +
        "    },\n" +
        "    \"stageVariables\": null,\n" +
        "    \"requestContext\": {\n" +
        "        \"resourceId\": \"hjbgfc\",\n" +
        "        \"resourcePath\": \"/items/{id}\",\n" +
        "        \"httpMethod\": \"DELETE\",\n" +
        "        \"extendedRequestId\": \"XyvDMFRyiGYFr7g=\",\n" +
        "        \"requestTime\": \"19/Dec/2020:09:17:59 +0000\",\n" +
        "        \"path\": \"/items/{id}\",\n" +
        "        \"accountId\": \"870103585828\",\n" +
        "        \"protocol\": \"HTTP/1.1\",\n" +
        "        \"stage\": \"test-invoke-stage\",\n" +
        "        \"domainPrefix\": \"testPrefix\",\n" +
        "        \"requestTimeEpoch\": 1608369479600,\n" +
        "        \"requestId\": \"5207ee2f-2dc7-460a-8f92-9aa1d4098fc4\",\n" +
        "        \"identity\": {\n" +
        "            \"cognitoIdentityPoolId\": null,\n" +
        "            \"cognitoIdentityId\": null,\n" +
        "            \"apiKey\": \"test-invoke-api-key\",\n" +
        "            \"principalOrgId\": null,\n" +
        "            \"cognitoAuthenticationType\": null,\n" +
        "            \"userArn\": \"arn:aws:iam::870103585828:user/joseph\",\n" +
        "            \"apiKeyId\": \"test-invoke-api-key-id\",\n" +
        "            \"userAgent\": \"aws-internal/3 aws-sdk-java/1.11.864 Linux/4.9.230-0.1.ac.223.84.332.metal1.x86_64 OpenJDK_64-Bit_Server_VM/25.262-b10 java/1.8.0_262 vendor/Oracle_Corporation\",\n" +
        "            \"accountId\": \"870103585828\",\n" +
        "            \"caller\": \"AIDA4VFRXCQSC7OLFBHTF\",\n" +
        "            \"sourceIp\": \"test-invoke-source-ip\",\n" +
        "            \"accessKey\": \"ASIA4VFRXCQSNJUVC6AV\",\n" +
        "            \"cognitoAuthenticationProvider\": null,\n" +
        "            \"user\": \"AIDA4VFRXCQSC7OLFBHTF\"\n" +
        "        },\n" +
        "        \"domainName\": \"testPrefix.testDomainName\",\n" +
        "        \"apiId\": \"1zutumbg1a\"\n" +
        "    },\n" +
        "    \"body\": null,\n" +
        "    \"isBase64Encoded\": false\n" +
        "}";
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(s);
    String id = jsonNode.at("/pathParameters/id").textValue();
    System.out.println(id);
  }
}
