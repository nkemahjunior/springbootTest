package com.zeco.testingDemo.customer;

public record CreateCustomerRequest(String name,
                                    String email,
                                    String address) {
}