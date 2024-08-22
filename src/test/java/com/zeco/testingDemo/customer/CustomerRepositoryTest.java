package com.zeco.testingDemo.customer;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest, slows our test because it loads the whole application context, so lets use DataJpaTest, which load only beans necessary for the database duties
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // use this annotation if you are running the test with a real database and not an in memory database
class CustomerRepositoryTest {


    @Container
    @ServiceConnection//  override the default connection details and use the annotated Testcontainer as the backend service.
    static PostgreSQLContainer<?> postgresSqlContainer = new PostgreSQLContainer<>("postgres:dontKnow");



    @Autowired
    CustomerRepository underTest;

    @BeforeEach
    void setUp() {//runs before each test
        Customer customer = Customer.create("leon", "leon@gmail.com", "us");
        underTest.save(customer);
    }

    @AfterEach
    void tearDown() {// runs after each test
        underTest.deleteAll();
    }



    @Test
    void canEstablishConnection(){
        assertThat(postgresSqlContainer.isCreated()).isTrue();
        assertThat(postgresSqlContainer.isRunning()).isTrue();
    }


    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        //given
        //when
        Optional<Customer> customerByEmail = underTest.findByEmail("leon@gmail.com");
        //then
        assertThat(customerByEmail).isPresent();
    }


    @Test
    void shouldReturnNotCustomerWhenFindByEmailIsNotPresent() {

        //given
        //when
        Optional<Customer> customerByEmail = underTest.findByEmail("leonFake@gmail.com");
        //then
        assertThat(customerByEmail).isNotPresent();
    }
}