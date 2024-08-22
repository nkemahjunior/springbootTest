package com.zeco.testingDemo.customer;

import com.zeco.testingDemo.exception.CustomerEmailUnavailableException;
import com.zeco.testingDemo.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    CustomerService underTest;

    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor; // use to capture arguments passed to a method

    @Mock
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        //passing the mock object to the customer service
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void shouldGetAllCustomers() {
        //given
        //when
        underTest.getCustomers();
        //then
        //verify if the findAll() was called by customerRepository when we invoked the getCustomers() method
        verify(customerRepository).findAll();
    }

    @Test
    @Disabled
    void getCustomerById() {

    }

    @Test
    //@Disabled
    void shouldCreateCustomer() {
        //given
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest("leon", "leon@gmail.com","US");
        //when
        underTest.createCustomer(createCustomerRequest);
        //then
                                      //capture the argument passed to the save method
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        assertThat(customerCaptured.getName()).isEqualTo(createCustomerRequest.name());
        assertThat(customerCaptured.getEmail()).isEqualTo(createCustomerRequest.email());
        assertThat(customerCaptured.getAddress()).isEqualTo(createCustomerRequest.address());
    }


    @Test
    void shouldNotCreateCustomerAndThrowExceptionWhenCustomerFindByEmailIsPresent() {
        //given
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest("leon", "leon@gmail.com","US");

        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
        //when
        //then

        assertThatThrownBy( () -> underTest.createCustomer(createCustomerRequest))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email " + createCustomerRequest.email() + " unavailable.");

    }



    @Test
    //@Disabled
    void shouldThrowNotFoundWhenGivenInvalidIDWhileUpdateCustomer() {
        //given
        long id = 5L;
        String name = "leon";
        String email = "leon@gmail.com";
        String address = "US";
        //when
        //then
        assertThatThrownBy( () -> underTest.updateCustomer(id,name,email,address))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't found");

        //not really necessary, you can end at the assertThrownBy
       // verify(customerRepository, never()).save(any());
    }

    @Test
    @Disabled
    void deleteCustomer() {
    }
}