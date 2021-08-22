package com.x.provider.customer.controller.rpc;

import com.x.core.constant.Constants;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.RoleDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.customer.configure.ApplicationConfig;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.enums.SystemRoleNameEnum;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.Role;
import com.x.provider.customer.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rpc/customer")
public class CustomerRpcController extends BaseRpcController implements CustomerRpcService {
    private final CustomerService customerService;
    private final ApplicationConfig applicationConfig;

    public CustomerRpcController(CustomerService customerService,
                                 ApplicationConfig applicationConfig){
        this.customerService = customerService;
        this.applicationConfig = applicationConfig;
    }

    @GetMapping("/getCustomer")
    @Override
    public R<CustomerDTO> getCustomer(@RequestParam("customerId") long customerId,
                                      @RequestParam("customerOptions")List<String> customerOptions) {
        customerOptions =Optional.ofNullable(customerOptions).orElse(Arrays.asList(CustomerOptions.CUSTOMER.toString()));
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
            }
        });
        return R.ok(customerDTO);
    }

    @PostMapping("/authorize")
    public R<Long> authorize(@RequestParam("token") String token, @RequestParam("path") String path){
        long customerId = StringUtils.hasText(token) ? customerService.validateToken(token) : 0;
        if (applicationConfig.getAuthIgnoreUrls().stream().anyMatch(item -> item.startsWith(path))){
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

    @PostMapping("/notifyCustomerGreenResult")
    public R<Void> notifyCustomerGreenResult(@RequestBody AttributeGreenResultDTO greenResult){
        customerService.onCustomerDraftAttributeGreenFinshed(Long.parseLong(greenResult.getEntityId())
                , SystemCustomerAttributeName.valueOf(greenResult.getKey()), greenResult.getValue()
                , SuggestionTypeEnum.valueOf(greenResult.getSuggestionType()));
        return R.ok();
    }
}
