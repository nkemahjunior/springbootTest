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



        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy( () -> underTest.updateCustomer(id,name,email,address))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't found");

        //not really necessary, you can end at the assertThrownBy
       // verify(customerRepository, never()).save(any());
    }


    @Test
    void shouldOnlyUpdateCustomerName(){
        //given
        long id = 5L;
        Customer customer = new Customer(id, "leon", "leon@gmail.com", "US");

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        //when
        String newName = "leon mark";
        underTest.updateCustomer(id, newName, null,null);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

       // System.out.println(capturedCustomer.getEmail());
       // System.out.println(capturedCustomer.getAddress());

        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
    }



    @Test
    void shouldThrowEmailUnavailableWhenGivenEmailAlreadyPresentedWhileUpdateCustomer(){
        //given
        long id = 5L;
        Customer customer = new Customer(id, "leon", "leon@gmail.com", "US");
        String newEmail = "leonaldo@gmail.com";
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(new Customer()));
        //when
        //then

        assertThatThrownBy( () -> underTest.updateCustomer(id, null, newEmail, null))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email \"" + newEmail + "\" unavailable to update");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyCustomerEmail(){
        //given
        long id = 5L;
        Customer customer = new Customer(id, "leon", "leon@gmail.com", "US");

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        //when
        String newEmail = "leonaldo@gmail.com";
        underTest.updateCustomer(id, null, newEmail,null);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // System.out.println(capturedCustomer.getEmail());
        // System.out.println(capturedCustomer.getAddress());

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
    }



    @Test
    void shouldOnlyUpdateCustomerAddress(){
        //given
        long id = 5L;
        Customer customer = new Customer(id, "leon", "leon@gmail.com", "US");

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        //when
        String newAddress = "UK";
        underTest.updateCustomer(id, null, null,newAddress);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // System.out.println(capturedCustomer.getEmail());
        // System.out.println(capturedCustomer.getAddress());

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }


    @Test
    void shouldUpdateAllAttributeWhenUpdateCustomer(){
        //given
        long id = 5L;
        Customer customer = new Customer(id, "leon", "leon@gmail.com", "US");

        String newAddress = "UK";
        String newName = "leonaldo";
        String newEmail = "leonaldo@gmail.com";

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        //when

        underTest.updateCustomer(id, newName, newEmail, newAddress);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        // System.out.println(capturedCustomer.getEmail());
        // System.out.println(capturedCustomer.getAddress());

        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }



    @Test
    //@Disabled
    void shouldThrowNotFoundWhenGivenDoesNotExistWhileDeleteCustomer() {
        //given
        long id = 5L;
        when(customerRepository.existsById(id)).thenReturn(false);
        //when
        //then
        assertThatThrownBy( () -> underTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't exist.");

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
        //@Disabled
    void shouldDeleteCustomer() {
        //given
        long id = 5L;
        when(customerRepository.existsById(id)).thenReturn(true);
        //when
        underTest.deleteCustomer(id);
        //then


        verify(customerRepository).deleteById(id);
    }
}