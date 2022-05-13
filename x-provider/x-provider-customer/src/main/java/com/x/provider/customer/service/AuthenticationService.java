package com.x.provider.customer.service;

import com.x.provider.customer.model.domain.Customer;

public interface AuthenticationService {
    String signIn(Customer customer);
    void signOut();
    long getAuthenticatedCustomerId(String token);
}
