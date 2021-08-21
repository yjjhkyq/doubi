package com.paascloud.provider.customer.controller.frontend;

import com.paascloud.core.utils.ApiAssetUtil;
import com.paascloud.core.web.api.R;
import com.paascloud.core.web.api.ResultCode;
import com.paascloud.core.web.controller.BaseFrontendController;
import com.paascloud.provider.api.oss.service.OssRpcService;
import com.paascloud.provider.customer.enums.SystemCustomerAttributeName;
import com.paascloud.provider.customer.model.ao.*;
import com.paascloud.provider.customer.model.domain.Customer;
import com.paascloud.provider.customer.model.domain.CustomerRelation;
import com.paascloud.provider.customer.model.vo.CustomerHomePageVO;
import com.paascloud.provider.customer.model.vo.FlowFansListItemVO;
import com.paascloud.provider.customer.service.CustomerRelationService;
import com.paascloud.provider.customer.service.CustomerService;
import io.swagger.models.auth.In;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/frontend/customer")
public class CustomerController extends BaseFrontendController {

    private final CustomerService customerService;
    private final CustomerRelationService customerRelationService;
    private final OssRpcService ossRpcService;

    public CustomerController(CustomerService customerService,
                              CustomerRelationService customerRelationService,
                              OssRpcService ossRpcService){
        this.customerService = customerService;
        this.customerRelationService = customerRelationService;
        this.ossRpcService = ossRpcService;
    }

    @PostMapping("/register")
    public R<Void> register(@RequestBody @Validated UserNamePasswordRegisterAO userNamePasswordRegisterAO){
        customerService.register(userNamePasswordRegisterAO);
        return R.ok();
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody @Validated UserNamePasswordLoginAO userNamePasswordLoginAO){
        return R.ok(customerService.login(userNamePasswordLoginAO));
    }

    @PostMapping("/logout")
    public R<Void> logout(){
        customerService.logout(getBearAuthorizationToken());
        return R.ok();
    }

    @PostMapping("/changePassword")
    public R<Void> changePassword(@RequestBody @Validated ChangePasswordByOldPasswordAO changePasswordByOldPasswordAO){
        customerService.changePassword(getCurrentCustomerId(), changePasswordByOldPasswordAO);
        return R.ok();
    }

    @PostMapping("/changeUserName")
    public R<Void> changeUserName(@RequestBody @Validated ChangeUserNameAO changeUserNameAO){
        customerService.changeUserName(getCurrentCustomerId(), changeUserNameAO);
        return R.ok();
    }

    @PostMapping("/setCustomerAttribute")
    public R<Void> setCustomerAttribute(@RequestBody @Validated SetCustomerAttributeAO setCustomerAttribute){
        customerService.setCustomerDraftAttribute(getCurrentCustomerId(), SystemCustomerAttributeName.valueOf(setCustomerAttribute.getAttributeName()), setCustomerAttribute.getValue());
        return R.ok();
    }

    @GetMapping("/getCustomerHomePage")
    public R<CustomerHomePageVO> getCustomerHomePage(@RequestParam long customerId){
        Customer customer = customerService.getCustomer(customerId);
        ApiAssetUtil.isTrue(!customer.isSystemAccount(), ResultCode.FORBIDDEN);
        CustomerHomePageVO customerHomePage = new CustomerHomePageVO();
        BeanUtils.copyProperties(customer, customerHomePage);
        Map<String, String> customerAttribute = customerService.listCustomerAttribute(customerId);
        prepareCustomerAttribute(customerAttribute);
        customerHomePage.setAttributes(customerAttribute);
        return R.ok(customerHomePage);
    }

    @GetMapping("/getRelation")
    public R<Integer> getRelation(@RequestParam @Validated @Min(1) long toCustomerId){
        return R.ok(customerRelationService.getRelation(getCurrentCustomerId(), toCustomerId).getValue());
    }

    @PostMapping("/relation/following")
    public R<Void> following(@RequestParam @Validated @Min(1) long toCustomerId){
        customerRelationService.following(getCurrentCustomerId(), toCustomerId);
        return R.ok();
    }

    @PostMapping("/relation/unFollowing")
    public R<Void> unFollowing(@RequestParam @Validated @Min(1) long toCustomerId){
        customerRelationService.unFollowing(getCurrentCustomerId(), toCustomerId);
        return R.ok();
    }

    @GetMapping("/relation/getFansCount")
    public R<Long> getFansCount(@RequestParam @Validated @Min(1)  long customerId){
        return R.ok(customerRelationService.getFansCount(customerId));
    }

    @GetMapping("/relation/getFollowCount")
    public R<Long> getFollowCount(@RequestParam @Validated @Min(1)  long customerId){
        return R.ok(customerRelationService.getFollowCount(customerId));
    }

    @GetMapping("/relation/listFollow")
    public R<List<FlowFansListItemVO>> listFollow(@RequestParam int page, @RequestParam @Validated @Min(1)  long customerId){
        List<CustomerRelation> follows = customerRelationService.listFollow(customerId, getPage(), getDefaultFrontendPageSize());
        List<FlowFansListItemVO> result = prepareFollowFansListIem(true, follows);
        return R.ok(result);
    }

    @GetMapping("/relation/listFans")
    public R<List<FlowFansListItemVO>> listFans(@RequestParam int page, @RequestParam @Validated @Min(1)  long customerId){
        List<CustomerRelation> fans = customerRelationService.listFans(customerId, getPage(), getDefaultFrontendPageSize());
        List<FlowFansListItemVO> result = prepareFollowFansListIem(false, fans);
        return R.ok(result);
    }

    private List<FlowFansListItemVO> prepareFollowFansListIem(boolean follow,  List<CustomerRelation> customers) {
        List<FlowFansListItemVO> result = new ArrayList<>(customers.size());
        customers.forEach(item -> {
            long customerId = follow ? item.getToCustomerId() : item.getFromCustomerId();
            FlowFansListItemVO flowFansListItem = new FlowFansListItemVO();
            flowFansListItem.setCustomerId(customerId);
            flowFansListItem.setCustomerAttributes(customerService.listCustomerAttribute(customerId, Arrays.asList(SystemCustomerAttributeName.AVATAR_ID, SystemCustomerAttributeName.NICK_NAME)));
            result.add(flowFansListItem);
        });
        List<String> objectKeys = new ArrayList<>();
        result.stream().forEach(item -> {
            item.getCustomerAttributes().entrySet().stream().forEach(attribute -> {
                if(CustomerService.MEDIA_CUSTOMER_ATTRIBUTE_NAME.contains(attribute.getKey())){
                    objectKeys.add(attribute.getValue());
                }
            });
        });
        if (objectKeys.size() > 0) {
            final R<Map<String, String>> attributeUrls = ossRpcService.listOjectBrowseUrl(objectKeys);
            result.stream().forEach(item -> {
                item.getCustomerAttributes().entrySet().stream().forEach(attribute -> {
                    if (CustomerService.MEDIA_CUSTOMER_ATTRIBUTE_NAME.contains(attribute.getKey())) {
                        attribute.setValue(attributeUrls.getData().getOrDefault(attribute.getValue(), Strings.EMPTY));
                    }
                });
            });
        }
        return result;

    }

    private void prepareCustomerAttribute(Map<String, String> attributes){
        List<String> allMediaCustomerAttributeNames = CustomerService.MEDIA_CUSTOMER_ATTRIBUTE_NAME;
        Map<String, String> mediaAttribute = attributes.entrySet().stream().filter(item -> allMediaCustomerAttributeNames.contains(item.getKey()))
                .collect(Collectors.toMap(item -> item.getKey(), item -> item.getValue()));
        if (CollectionUtils.isEmpty(mediaAttribute)){
            return;
        }
        final R<Map<String, String>> attributeUrls = ossRpcService.listOjectBrowseUrl(mediaAttribute.values().stream().collect(Collectors.toList()));
        attributes.entrySet().stream().forEach(item -> {
            if (allMediaCustomerAttributeNames.contains(item.getKey())){
                item.setValue(attributeUrls.getData().getOrDefault(item.getValue(), Strings.EMPTY));
            }
        });
    }
}
