package com.zeco.testingDemo.customer;

import org.hibernate.type.internal.ParameterizedTypeImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {


    @Container
    @ServiceConnection
//  override the default connection details and use the annotated Testcontainer as the backend service.
    static PostgreSQLContainer<?> postgresSqlContainer = new PostgreSQLContainer<>("postgres:dontKnow");

    @Autowired
    TestRestTemplate testRestTemplate;


    @Test
    void canEstablishConnection(){
        assertThat(postgresSqlContainer.isCreated()).isTrue();
        assertThat(postgresSqlContainer.isRunning()).isTrue();
    }

    @Test
    void getCustomerById() {
    }

    @Test
    void shouldCreateCustomer() {
        //given
        CreateCustomerRequest request = new CreateCustomerRequest("name",
                "email"+ UUID.randomUUID()+"@gmail.com", "address" );
        //when
        String API_CUSTOMERS_API = "/api/v1/customers";
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(API_CUSTOMERS_API,
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class);

        //then
        assertThat(createCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_API,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(allCustomersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);


        Customer customerCreated = allCustomersResponse.getBody()
                .stream()
                .filter(el -> el.getEmail().equals(request.email()))
                .findFirst()
                .orElseThrow();

        assertThat(customerCreated.getName()).isEqualTo(request.name());
        assertThat(customerCreated.getEmail()).isEqualTo(request.email());
        assertThat(customerCreated.getAddress()).isEqualTo(request.address());

    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteCustomer() {
    }
}