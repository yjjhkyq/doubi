package com.x.provider.customer.factory.dto.impl;

import cn.hutool.core.convert.Convert;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.GenderEnum;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerAttributeDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.GenericAttributeDTO;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.factory.dto.CustomerDTOBuilder;
import com.x.provider.customer.model.query.CustomerAttributeQuery;
import com.x.provider.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
@Service("customerAttributeFactory")
public class CustomerAttributeFactoryImpl implements CustomerDTOBuilder {

   private final CustomerService customerService;
   private final OssRpcService ossRpcService;

   public CustomerAttributeFactoryImpl(CustomerService customerService,
                                       OssRpcService ossRpcService){
      this.customerService = customerService;
      this.ossRpcService = ossRpcService;
   }

   @Override
   public void build(ListCustomerRequestDTO listCustomerAO, Map<Long, CustomerDTO> dest) {
      if (!listCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER_ATTRIBUTE.name())){
         return;
      }
      customerService.listAndFillDefaultCustomerAttribute(CustomerAttributeQuery.builder().customerIdList(listCustomerAO.getCustomerIds()).suggestionType(listCustomerAO.getSuggestionType()).build()).entrySet().forEach(item -> {
         dest.get(item.getKey()).setCustomerAttributeList(BeanUtil.prepare(item.getValue(), GenericAttributeDTO.class));
      });
      List<GenericAttributeDTO> genericAttributeList = new ArrayList<>(dest.size());
      dest.values().stream().forEach(item -> {
         genericAttributeList.addAll(item.getCustomerAttributeList());
      });
      prepare(genericAttributeList);
      prepareAttribute(dest.values());
   }

   private void prepareAttribute(Collection<CustomerDTO> src){
      src.stream().forEach(item -> {
         item.setCustomerAttribute(prepare(null, item.getCustomerAttributeList()));
      });
   }

   public CustomerAttributeDTO prepare(CustomerAttributeDTO customerAttribute, List<GenericAttributeDTO> customerAttributeList){
      if (customerAttribute == null){
         customerAttribute = new CustomerAttributeDTO();
      }
      final Map<String, GenericAttributeDTO> attributeMap = customerAttributeList.stream().collect(Collectors.toMap(item -> item.getKey(), item -> item));
      customerAttribute.setNickName(attributeMap.getOrDefault(SystemCustomerAttributeName.NICK_NAME.name(), new GenericAttributeDTO()).getValue());
      customerAttribute.setSignature(attributeMap.getOrDefault(SystemCustomerAttributeName.SIGNATURE.name(), new GenericAttributeDTO()).getValue());
      customerAttribute.setAvatarId(attributeMap.getOrDefault(SystemCustomerAttributeName.AVATAR_ID.name(), new GenericAttributeDTO()).getValue());
      customerAttribute.setAvatarUrl(attributeMap.getOrDefault(SystemCustomerAttributeName.AVATAR_ID.name(), new GenericAttributeDTO()).getValueUrl());
      GenericAttributeDTO homePageBackgroundAttribute = attributeMap.getOrDefault(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name(),
              GenericAttributeDTO.builder().build());
      customerAttribute.setPersonalHomePageBackgroundId(homePageBackgroundAttribute.getValue());
      customerAttribute.setPersonalHomePageBackgroundUrl(homePageBackgroundAttribute.getValueUrl());
      customerAttribute.setGender(Convert.toInt(attributeMap.getOrDefault(SystemCustomerAttributeName.GENDER.name(), GenericAttributeDTO.builder().value(GenderEnum.NULL.getValue().toString()).build()).getValue()));
      return customerAttribute;
   }

   public void prepare(List<GenericAttributeDTO> genericAttributeList){
      if (CollectionUtils.isEmpty(genericAttributeList)){
         return;
      }
      final List<GenericAttributeDTO> ossAttributeList = genericAttributeList.stream().filter(item -> CustomerService.CUSTOMER_ATTRIBUTE_NAME_OSS.contains(item.getKey())).collect(Collectors.toList());
      final Set<String> mediaKey = ossAttributeList.stream().map(item -> item.getValue()).collect(Collectors.toSet());
      if (mediaKey.isEmpty()){
         return;
      }
      final Map<String, String> ossUrl = ossRpcService.listObjectBrowseUrl(new ArrayList<>(mediaKey)).getData();
      ossAttributeList.stream().forEach(item -> {
         item.setValueUrl(ossUrl.get(item.getValue()));
      });
   }

}
