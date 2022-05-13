package com.x.provider.api.customer.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDTO {
    private long id;
    private String userName;
    private String email;
    private boolean active;
    private boolean systemAccount;
    private Date createdOnUtc;
    private Date updatedOnUtc;
    private String phone;

    private CustomerAttributeDTO customerAttribute;
    private Long followCount;
    private Long fansCount;

    private CustomerStatDTO statistic;

    private List<RoleDTO> roles = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSystemAccount() {
        return systemAccount;
    }

    public void setSystemAccount(boolean systemAccount) {
        this.systemAccount = systemAccount;
    }

    public Date getUpdatedOnUtc() {
        return updatedOnUtc;
    }

    public void setUpdatedOnUtc(Date updatedOnUtc) {
        this.updatedOnUtc = updatedOnUtc;
    }

    public Date getCreatedOnUtc() {
        return createdOnUtc;
    }

    public void setCreatedOnUtc(Date createdOnUtc) {
        this.createdOnUtc = createdOnUtc;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public CustomerAttributeDTO getCustomerAttribute() {
        return customerAttribute;
    }

    public void setCustomerAttribute(CustomerAttributeDTO customerAttribute) {
        this.customerAttribute = customerAttribute;
    }

    public Long getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Long followCount) {
        this.followCount = followCount;
    }

    public Long getFansCount() {
        return fansCount;
    }

    public void setFansCount(Long fansCount) {
        this.fansCount = fansCount;
    }

    public CustomerStatDTO getStatistic() {
        return statistic;
    }

    public void setStatistic(CustomerStatDTO statistic) {
        this.statistic = statistic;
    }
}
