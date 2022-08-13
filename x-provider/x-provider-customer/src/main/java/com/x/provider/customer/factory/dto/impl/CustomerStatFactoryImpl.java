package com.x.provider.customer.factory.dto.impl;

import cn.hutool.core.convert.Convert;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerAttributeDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.CustomerStatDTO;
import com.x.provider.api.customer.model.dto.GenericAttributeDTO;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.factory.dto.CustomerDTOBuilder;
import com.x.provider.customer.model.domain.CustomerStat;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.CustomerStatService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
@Service("customerStatFactory")
public class CustomerStatFactoryImpl implements CustomerDTOBuilder {

   private final CustomerService customerService;
   private final OssRpcService ossRpcService;
   private final CustomerStatService customerStatService;

   public CustomerStatFactoryImpl(CustomerService customerService,
                                  OssRpcService ossRpcService,
                                  CustomerStatService customerStatService){
      this.customerService = customerService;
      this.ossRpcService = ossRpcService;
      this.customerStatService = customerStatService;
   }

   @Override
   public void build(ListCustomerRequestDTO listCustomerAO, Map<Long, CustomerDTO> dest) {
      if (!listCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER_STAT.name())){
         return;
      }
      Map<Long, CustomerStat> statistics = customerStatService.list(listCustomerAO.getCustomerIds());
      statistics.entrySet().forEach(item -> {
         dest.get(item.getKey()).setStatistic(BeanUtil.prepare(item.getValue(), CustomerStatDTO.class));
      });
   }

   private void prepareAttribute(Collection<CustomerDTO> src){
      src.stream().forEach(item -> {
         item.setCustomerAttribute(prepare(null, item.getCustomerAttributeList()));
      });
   }

   private CustomerAttributeDTO prepare(CustomerAttributeDTO customerAttribute, List<GenericAttributeDTO> customerAttributeList){
      if (customerAttribute == null){
         customerAttribute = new CustomerAttributeDTO();
      }
      final Map<String, GenericAttributeDTO> attributeMap = customerAttributeList.stream().collect(Collectors.toMap(item -> item.getKey(), item -> item));
      customerAttribute.setNickName(attributeMap.get(SystemCustomerAttributeName.NICK_NAME.name()).getValue());
      customerAttribute.setSignature(attributeMap.get(SystemCustomerAttributeName.SIGNATURE.name()).getValue());
      customerAttribute.setAvatarId(attributeMap.get(SystemCustomerAttributeName.AVATAR_ID).getValue());
      customerAttribute.setAvatarUrl(attributeMap.get(SystemCustomerAttributeName.AVATAR_ID).getValueUrl());
      customerAttribute.setPersonalHomePageBackgroundId(attributeMap.get(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID).getValue());
      customerAttribute.setPersonalHomePageBackgroundUrl(attributeMap.get(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID).getValue());
      customerAttribute.setGender(Convert.toInt(attributeMap.get(SystemCustomerAttributeName.GENDER).getValue()));
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
