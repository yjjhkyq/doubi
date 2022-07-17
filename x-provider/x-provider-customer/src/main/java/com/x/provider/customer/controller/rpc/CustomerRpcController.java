package com.x.provider.customer.controller.rpc;

import com.x.core.constant.Constants;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.ao.IncCustomerStatAO;
import com.x.provider.api.customer.model.ao.ListCustomerAO;
import com.x.provider.api.customer.model.ao.ListSimpleCustomerAO;
import com.x.provider.api.customer.model.dto.*;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.customer.configure.ApplicationConfig;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.enums.SystemRoleNameEnum;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.CustomerStat;
import com.x.provider.customer.model.domain.Role;
import com.x.provider.customer.service.AuthenticationService;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.CustomerStatService;
import com.x.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rpc/customer")
public class CustomerRpcController extends BaseRpcController implements CustomerRpcService {
    private final CustomerService customerService;
    private final ApplicationConfig applicationConfig;
    private final CustomerRelationService customerRelationService;
    private final OssRpcService ossRpcService;
    private final CustomerStatService customerStatService;
    private final AuthenticationService authenticationService;

    public CustomerRpcController(CustomerService customerService,
                                 ApplicationConfig applicationConfig,
                                 CustomerRelationService customerRelationService,
                                 OssRpcService ossRpcService,
                                 CustomerStatService customerStatService,
                                 AuthenticationService authenticationService){
        this.customerService = customerService;
        this.applicationConfig = applicationConfig;
        this.customerRelationService = customerRelationService;
        this.ossRpcService = ossRpcService;
        this.customerStatService = customerStatService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/data")
    @Override
    public R<CustomerDTO> getCustomer(@RequestParam("customerId") long customerId,
                                      @RequestParam("customerOptions")List<String> customerOptions) {
        CustomerDTO customerDTO = getCustomerInfo(customerId, customerOptions);
        return R.ok(customerDTO);
    }


    @PostMapping("/list")
    @Override
    public R<Map<Long, CustomerDTO>> listCustomer(@RequestBody ListCustomerAO listCustomerAO) {
        Map<Long, CustomerDTO> customerList = new HashMap<>(listCustomerAO.getCustomerIds().size());
        listCustomerAO.getCustomerIds().forEach(item -> {
            CustomerDTO customer = getCustomerInfo(item, listCustomerAO.getCustomerOptions());
            customerList.put(customer.getId(), customer);
        });
        return R.ok(customerList);
    }

    @PostMapping("/authorize")
    @Override
    public R<Long> authorize(@RequestParam("token") String token, @RequestParam("path") String path){
        logger.info("authorize, token:{} path:{}", token, path);
        long customerId = StringUtils.hasText(token) ? authenticationService.getAuthenticatedCustomerId(token) : 0;
        if (path.startsWith(Constants.FRONT_END_URL_PREFIX)){
            return R.ok(customerId);
        }
        if (applicationConfig.getAuthIgnoreUrls().stream().anyMatch(item -> item.startsWith(path) || path.endsWith(item))){
            return R.ok(customerId);
        }
        List<Role> roles = customerService.listCustomerRole(customerId);
        if (path.startsWith(Constants.ADMIN_URL_PREFIX)){
            ApiAssetUtil.isTrue(roles.stream().allMatch(item -> item.getSystemName().equals(SystemRoleNameEnum.ADMINISTRATORS.toString())));
        }

        if (path.startsWith(Constants.FRONT_END_URL_PREFIX)){
            ApiAssetUtil.isTrue(roles.stream().allMatch(item -> item.getSystemName().equals(SystemRoleNameEnum.REGISTERED.toString())));
        }
        return R.ok(customerId);
    }

    @PostMapping("/follow/list")
    @Override
    public R<List<Long>> listFollow(@RequestParam long customerId) {
        return R.ok(customerRelationService.listFollow(customerId));
    }

    @PostMapping("/simple/list")
    @Override
    public R<Map<Long, SimpleCustomerDTO>> listSimpleCustomer(long loginCustomerId, int customerRelation, String customerIdList) {
        ListSimpleCustomerAO listSimpleCustomerAO = ListSimpleCustomerAO.builder().loginCustomerId(loginCustomerId).customerRelation(customerRelation).customerIds(StringUtil.parse(customerIdList)).build();
        return R.ok(customerService.listCustomer(listSimpleCustomerAO));
    }

    @PostMapping("/simple/list/v2")
    @Override
    public R<Map<Long, SimpleCustomerDTO>> listSimpleCustomerV2(@RequestBody ListSimpleCustomerAO listSimpleCustomer) {
        return R.ok(customerService.listCustomer(listSimpleCustomer));
    }

    @PostMapping("/stat/inc")
    @Override
    public R<Void> incCustomerStatAO(IncCustomerStatAO incCustomerStatAO) {
        customerStatService.inc(BeanUtil.prepare(incCustomerStatAO, CustomerStat.class));
        return R.ok();
    }


    @PostMapping("/notifyCustomerGreenResult")
    public R<Void> notifyCustomerGreenResult(@RequestBody AttributeGreenResultDTO greenResult){
        customerService.onCustomerDraftAttributeGreenFinished(Long.parseLong(greenResult.getEntityId())
                , SystemCustomerAttributeName.valueOf(greenResult.getKey()), greenResult.getValue()
                , SuggestionTypeEnum.valueOf(greenResult.getSuggestionType()));
        return R.ok();
    }

    private CustomerDTO getCustomerInfo(long customerId, List<String> customerOptions) {
        customerOptions = Optional.ofNullable(customerOptions).orElse(Arrays.asList(CustomerOptions.CUSTOMER.toString()));
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);

        customerOptions.forEach(item -> {
            switch (CustomerOptions.valueOf(item)){
                case CUSTOMER:
                    Customer customer = customerService.getCustomer(customerId);
                    BeanUtils.copyProperties(customer, customerDTO);
                    break;
                case CUSTOMER_ROLE:
                    List<Role> roles = customerService.listCustomerRole(customerId);
                    roles.forEach(role -> {
                        RoleDTO roleDTO = new RoleDTO();
                        BeanUtils.copyProperties(role, roleDTO);
                        customerDTO.getRoles().add(roleDTO);
                    });
                    break;
                case CUSTOMER_ATTRIBUTE:
                    Map<String, String> customerAttribute = customerService.listCustomerAttribute(customerId);
                    customerDTO.setCustomerAttribute(CustomerAttributeDTO.builder().avatarId(customerAttribute.get(SystemCustomerAttributeName.AVATAR_ID.name()))
                            .avatarUrl(ossRpcService.getObjectBrowseUrl(customerAttribute.get(SystemCustomerAttributeName.AVATAR_ID.name())).getData())
                            .nickName(customerAttribute.get(SystemCustomerAttributeName.NICK_NAME.name())).personalHomePageBackgroundId(customerAttribute.get(SystemCustomerAttributeName.PERSONAL_HOMEPAGE_BACKGROUND_ID.name()))
                            .signature(customerAttribute.get(SystemCustomerAttributeName.SIGNATURE.name())).build());
                    break;
                case CUSTOMER_STAT:
                    Map<Long, CustomerStat> statistics = customerStatService.list(Arrays.asList(customerId));
                    customerDTO.setStatistic(BeanUtil.prepare(statistics.getOrDefault(customerId, CustomerStat.builder().id(customerId).build()), CustomerStatDTO.class));
                    break;
            }
        });
        return customerDTO;
    }
}
