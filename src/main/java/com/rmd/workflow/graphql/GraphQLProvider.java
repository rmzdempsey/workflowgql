package com.rmd.workflow.graphql;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
@Slf4j
public class GraphQLProvider {

    private GraphQL graphQL;

    @Autowired
    GraphQLDataFetchers dataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        log.info("graphql initialised");
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()

                .type(newTypeWiring("Query")
                        .dataFetcher("allWorkflows", dataFetchers.allWorkflowsDataFetcher())
                )
                .type(newTypeWiring("Workflow").dataFetcher("versions", dataFetchers.getVersionsDataFetcher()))
                .type(newTypeWiring("WorkflowVersion").dataFetcher("createdOn", dataFetchers.getCreatedOnDataFetcher()))
                .type(newTypeWiring("Mutation")
                        .dataFetcher("createWorkflow", dataFetchers.createWorkflowDataFetcher())
                )
                .scalar(GraphQLScalarType.newScalar()
                        .name("LocalDateTime")
                        .coercing(new Coercing<LocalDateTime, String>() {
                            private DateTimeFormatter sdf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            @Override
                            public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                                return ((LocalDateTime)dataFetcherResult).format(sdf);
                            }

                            @Override
                            public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                                return LocalDateTime.parse((String)input);
                            }

                            @Override
                            public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                                return LocalDateTime.parse((String)input);
                            }
                        })
                        .build())
                .build();
    }
}
