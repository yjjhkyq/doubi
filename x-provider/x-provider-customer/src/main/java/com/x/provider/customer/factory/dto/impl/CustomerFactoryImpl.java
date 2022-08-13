package com.x.provider.customer.factory.dto.impl;

import com.x.core.domain.SuggestionTypeEnum;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.customer.factory.dto.CustomerDTOBuilder;
import com.x.provider.customer.factory.dto.CustomerFactory;
import com.x.provider.customer.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
@Service
public class CustomerFactoryImpl implements CustomerFactory{

    private final CustomerService customerService;
    private final List<CustomerDTOBuilder> CUSTOMER_DTO_BUILDER = SpringUtils.getBanListOfType(CustomerDTOBuilder.class);

    public CustomerFactoryImpl(CustomerService customerService){
        this.customerService = customerService;
    }

    @Override
    public Map<Long, CustomerDTO> prepare(ListCustomerRequestDTO listCustomerAO) {
        Map<Long, CustomerDTO> result = new HashMap<>(listCustomerAO.getCustomerIds().size());
        if (listCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER.name())){
            final List<CustomerDTO> customerList = BeanUtil.prepare(customerService.listCustomer(listCustomerAO.getCustomerIds()), CustomerDTO.class);
            customerList.forEach(item -> {
                result.put(item.getId(), item);
            });
        }
        else{
            listCustomerAO.getCustomerIds().forEach(item -> {
                result.put(item, CustomerDTO.builder().id(item).build());
            });
        }
        CUSTOMER_DTO_BUILDER.forEach(item -> {
            item.build(listCustomerAO, result);
        });
        return result;
    }

    @Override
    public Map<Long, SimpleCustomerDTO> prepare(ListSimpleCustomerRequestDTO listSimpleCustomerAO) {
        final ListCustomerRequestDTO listCustomerAO = ListCustomerRequestDTO.builder().customerIds(listSimpleCustomerAO.getCustomerIds()).customerOptions(listSimpleCustomerAO.getCustomerOptions())
                .sessionCustomerId(listSimpleCustomerAO.getSessionCustomerId()).suggestionType(SuggestionTypeEnum.PASS).build();
        if (!listCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER.name())){
            listCustomerAO.getCustomerOptions().add(CustomerOptions.CUSTOMER.name());
        }
        Map<Long, CustomerDTO> customerDTO = prepare(listCustomerAO);
        return customerDTO.entrySet().stream().collect(Collectors.toMap(item -> item.getKey(), item -> prepare(null, item.getValue())));
    }

    public SimpleCustomerDTO prepare(SimpleCustomerDTO dest, CustomerDTO src){
        if (dest == null){
            dest = new SimpleCustomerDTO();
        }
        dest.setId(src.getId());
        dest.setUserName(src.getUserName());
        if (src.getCustomerAttribute() != null) {
            dest.setAvatarUrl(src.getCustomerAttribute().getAvatarUrl());
            dest.setNickName(src.getCustomerAttribute().getNickName());
            dest.setGender(src.getCustomerAttribute().getGender());
        }
        dest.setCustomerRelation(src.getCustomerRelation());
        dest.setCanFollow(src.getCanFollow());
        dest.setStatistic(src.getStatistic());
        return dest;
    }

}
