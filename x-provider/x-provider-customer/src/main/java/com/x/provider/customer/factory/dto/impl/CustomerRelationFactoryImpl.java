package com.x.provider.customer.factory.dto.impl;

import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.customer.factory.dto.CustomerDTOBuilder;
import com.x.provider.customer.model.domain.CustomerRelation;
import com.x.provider.customer.service.CustomerRelationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
@Service("customerRelationFactory")
public class CustomerRelationFactoryImpl implements CustomerDTOBuilder {

   private static final Set<Integer> CAN_FOLLOW_RELATION = Set.of(CustomerRelationEnum.NO_RELATION.getValue(), CustomerRelationEnum.FANS.getValue());

   private final CustomerRelationService customerRelationService;

   public CustomerRelationFactoryImpl(CustomerRelationService customerRelationService){
      this.customerRelationService = customerRelationService;
   }

   @Override
   public void build(ListCustomerRequestDTO listCustomerAO, Map<Long, CustomerDTO> dest) {
      if (!listCustomerAO.getCustomerOptions().contains(CustomerOptions.CUSTOMER_RELATION.name()) || listCustomerAO.getSessionCustomerId() <= 0){
         return;
      }
      final Map<Long, Integer> relationMap = prepare(listCustomerAO.getCustomerIds(), listCustomerAO.getSessionCustomerId());
      dest.entrySet().forEach(item -> {
         item.getValue().setCustomerRelation(relationMap.get(item.getKey()));
         item.getValue().setCanFollow(listCustomerAO.getSessionCustomerId() <= 0 || !listCustomerAO.getSessionCustomerId().equals(item.getValue().getId())
                 && CAN_FOLLOW_RELATION.contains(item.getValue().getCustomerRelation()));
      });
   }

   public Map<Long, Integer> prepare(List<Long> customerIdList, Long sessionCustomerId){
      if (sessionCustomerId <= 0 || CollectionUtils.isEmpty(customerIdList)){
         return new HashMap<>();
      }
      final Map<Long, CustomerRelation> followRelation = customerRelationService.listRelationMap(sessionCustomerId, customerIdList, CustomerRelationEnum.FOLLOW);
      final Map<Long, CustomerRelation> fansRelation = customerRelationService.listRelationMap(sessionCustomerId, customerIdList, CustomerRelationEnum.FANS);
      Map<Long, Integer> relationMap = new HashMap<>(customerIdList.size());
      customerIdList.stream().forEach(item -> {
         relationMap.put(item, prepare(followRelation.get(item), fansRelation.get(item)));
      });
      return relationMap;
   }

   private Integer prepare(CustomerRelation followRelation, CustomerRelation fansRelation){
      if (followRelation != null && followRelation.getFollow()){
         if (followRelation.getFriend()){
            return CustomerRelationEnum.FRIEND.getValue();
         }
         return CustomerRelationEnum.FOLLOW.getValue();
      }
      if (fansRelation != null && fansRelation.getFollow()){
         if (fansRelation.getFriend()){
            return CustomerRelationEnum.FRIEND.getValue();
         }
         return CustomerRelationEnum.FANS.getValue();
      }
      return CustomerRelationEnum.NO_RELATION.getValue();
   }
}
