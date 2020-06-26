package com.rmd.workflow.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rmd.workflow.entities.Workflow;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkflowTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testGetAllWorkflows() throws Exception {

        ResponseEntity<String> response = graphQLCall("query { allWorkflows { uuid releasedVersionNo versions { versionNo } } }");

        assertThat(response.getStatusCodeValue(),is(200));

        JsonNode jsonResponse = toJson(response);

        assertThat(jsonResponse.at("/data/allWorkflows").isArray(),is(true));
        assertThat(jsonResponse.at("/data/allWorkflows").size(),is(0));

        //doReturn(user).when(userServiceMock).createUser(TEST_USERNAME, TEST_PASSWORD);
        //doReturn(new ArrayList<Workflow>() ).when(workflowService).getAllWorkflows();
        //GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/allWorkflows.graphql");
        //assertThat(response.isOk(),is(true));
        //assertThat(response.get("$.data.createUser.id")).isNotNull();
        //assertThat(response.get("$.data.createUser.username")).isEqualTo(TEST_USERNAME);

    }

    @Test
    public void testCreateWorkflow() throws Exception {

        String req = String.join(" ",
                "mutation { createWorkflow( request: {",
                "name: \\\"testCreateWorkflow\\\",",
                "description: \\\"my first wf\\\",",
                "workflowJson: \\\"{}\\\"",
                "}){",
                "uuid, ",
                "releasedVersionNo, ",
                "versions { uuid, versionNo, name  }}}");

        ResponseEntity<String> response = graphQLCall(req);



        assertThat(response.getStatusCodeValue(),is(200));

        JsonNode jsonResponse = toJson(response);

        assertThat(jsonResponse.has("errors"),is(false));

        assertThat(jsonResponse.at("/data/createWorkflow/uuid").isNull(),is(false));
        assertThat(jsonResponse.at("/data/createWorkflow/releasedVersionNo").asInt(),is(1));

        assertThat(jsonResponse.at("/data/createWorkflow/versions").size(),is(1));
        assertThat(jsonResponse.at("/data/createWorkflow/versions/0/versionNo").asInt(),is(1));
        assertThat(jsonResponse.at("/data/createWorkflow/versions/0/name").asText(),is("testCreateWorkflow"));

    }

    @Test
    public void testCreateWorkflowThenUpdate() throws Exception {

        String req = String.join(" ",
                "mutation { createWorkflow( request: {",
                "name: \\\"testCreateWorkflowThenUpdate\\\",",
                "description: \\\"my first wf\\\",",
                "workflowJson: \\\"{}\\\"",
                "}){",
                "uuid, ",
                "releasedVersionNo, ",
                "versions { uuid, versionNo  }}}");

        ResponseEntity<String> response = graphQLCall(req);

        assertThat(response.getStatusCodeValue(),is(200));
        JsonNode jsonResponse = toJson(response);
        assertThat(jsonResponse.has("errors"),is(false));

        String workflowUuid = jsonResponse.at("/data/createWorkflow/uuid").asText();

        req = String.join(" ",
                "mutation { updateWorkflow( request: {",
                "workflowUuid: \\\"" + workflowUuid + "\\\",",
                "versionNo:"+jsonResponse.at("/data/createWorkflow/releasedVersionNo").asInt(),
                "name: \\\"testCreateWorkflowThenUpdateXXX\\\",",
                "description: \\\"testCreateWorkflowThenUpdateXXX\\\",",
                "workflowJson: \\\"{}\\\"",
                "comment: \\\"changed name\\\"",
                "}){",
                "uuid, ",
                "releasedVersionNo, ",
                "versions { uuid, versionNo, parentVersionNo, name  }}}");

        response = graphQLCall(req);

        assertThat(response.getStatusCodeValue(),is(200));
        jsonResponse = toJson(response);
        assertThat(jsonResponse.has("errors"),is(false));

        assertThat(jsonResponse.at("/data/updateWorkflow/uuid").asText(),is(workflowUuid));
        assertThat(jsonResponse.at("/data/updateWorkflow/releasedVersionNo").asInt(),is(1));

        assertThat(jsonResponse.at("/data/updateWorkflow/versions").size(),is(2));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/0/versionNo").asInt(),is(1));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/0/parentVersionNo").isNull(),is(true));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/0/name").asText(),is("testCreateWorkflowThenUpdate"));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/1/versionNo").asInt(),is(2));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/1/parentVersionNo").asInt(),is(1));
        assertThat(jsonResponse.at("/data/updateWorkflow/versions/1/name").asText(),is("testCreateWorkflowThenUpdateXXX"));

    }

    JsonNode toJson(ResponseEntity<String> entityResponse ) throws Exception{
        ObjectMapper om = new ObjectMapper();
        return om.readTree(entityResponse.getBody());
    }

//    private ResponseEntity<String> graphQLQuery(String query){
//        return graphQLCall("query", query);
//    }

//    private ResponseEntity<String> graphQLMutation(String query) throws Exception{
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE,String.valueOf(MediaType.APPLICATION_JSON));
//        //String payload = "{\"mutation\": \"{ " + query + "}\", \"variables\":null}";
//        String payload = convertToGraphqlString("mutation",query,null);
//        HttpEntity<String> httpEntity = new HttpEntity<>(payload,headers);
//        return restTemplate.exchange("/graphql", HttpMethod.POST,httpEntity,String.class);
//    }

    private ResponseEntity<String> graphQLCall(String query){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE,String.valueOf(MediaType.APPLICATION_JSON));
        String payload = "{\"query\": \"" + query + "\", \"variables\":null}";
        HttpEntity<String> httpEntity = new HttpEntity<>(payload,headers);
        return restTemplate.exchange("/graphql", HttpMethod.POST,httpEntity,String.class);
    }

    private static String convertToGraphqlString(String action, String graphql, ObjectNode variables) throws JsonProcessingException {
        ObjectMapper oMapper = new ObjectMapper();
        ObjectNode oNode = oMapper.createObjectNode();
        oNode.put(action, graphql);
        oNode.set("variables", variables);
        return oMapper.writeValueAsString(oNode);
    }

}

//class GraphQLResponse {
//    private final ResponseEntity<String> entityResponse;
//
//    GraphQLResponse( ResponseEntity<String> entityResponse ){
//        this.entityResponse = entityResponse;
//    }
//}
