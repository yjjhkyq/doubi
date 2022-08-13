package com.x.provider.customer.controller.rpc;

import com.x.core.constant.Constants;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.customer.model.dto.IncCustomerStatRequestDTO;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;
import com.x.provider.customer.configure.ApplicationConfig;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.enums.SystemRoleNameEnum;
import com.x.provider.customer.factory.dto.CustomerFactory;
import com.x.provider.customer.model.domain.CustomerStat;
import com.x.provider.customer.model.domain.Role;
import com.x.provider.customer.service.AuthenticationService;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.CustomerStatService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rpc/customer")
public class CustomerRpcController extends BaseRpcController implements CustomerRpcService {

    private final CustomerService customerService;
    private final ApplicationConfig applicationConfig;
    private final CustomerRelationService customerRelationService;
    private final CustomerStatService customerStatService;
    private final AuthenticationService authenticationService;
    private final CustomerFactory customerFactory;

    public CustomerRpcController(CustomerService customerService,
                                 ApplicationConfig applicationConfig,
                                 CustomerRelationService customerRelationService,
                                 CustomerStatService customerStatService,
                                 AuthenticationService authenticationService,
                                 CustomerFactory customerFactory){
        this.customerService = customerService;
        this.applicationConfig = applicationConfig;
        this.customerRelationService = customerRelationService;
        this.customerStatService = customerStatService;
        this.authenticationService = authenticationService;
        this.customerFactory = customerFactory;
    }

    @GetMapping("/data")
    @Override
    public R<CustomerDTO> getCustomer(@RequestParam("customerId") long customerId,
                                      @RequestParam("customerOptions")List<String> customerOptions) {
        return R.ok(listCustomer(ListCustomerRequestDTO.builder().customerIds(Arrays.asList(customerId)).customerOptions(customerOptions).build()).getData().get(customerId));
    }


    @PostMapping("/list")
    @Override
    public R<Map<Long, CustomerDTO>> listCustomer(@RequestBody ListCustomerRequestDTO listCustomerAO) {
        return R.ok(customerFactory.prepare(listCustomerAO));
    }

    @PostMapping("/authorize")
    @Override
    public R<Long> authorize(@RequestParam("token") String token, @RequestParam("path") String path){
        logger.info("authorize, token:{} path:{}", token, path);
        if (path.startsWith(Constants.WS_URL_PREFIX)){
            return R.ok(0L);
        }
        long customerId = StringUtils.hasText(token) ? authenticationService.getAuthenticatedCustomerId(token) : 0;
        if (path.startsWith(Constants.APP_URL_PREFIX)){
            return R.ok(customerId);
        }
        if (applicationConfig.getAuthIgnoreUrls().stream().anyMatch(item -> item.startsWith(path) || path.endsWith(item))){
            return R.ok(customerId);
        }
        List<Role> roles = customerService.listCustomerRole(customerId);
        if (path.startsWith(Constants.ADMIN_URL_PREFIX)){
            ApiAssetUtil.isTrue(roles.stream().allMatch(item -> item.getSystemName().equals(SystemRoleNameEnum.ADMINISTRATORS.toString())));
        }

        if (path.startsWith(Constants.APP_URL_PREFIX)){
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
    public R<Map<Long, SimpleCustomerDTO>> listSimpleCustomer(@RequestBody ListSimpleCustomerRequestDTO listSimpleCustomer) {
        return R.ok(customerFactory.prepare(listSimpleCustomer));
    }

    @PostMapping("/stat/inc")
    @Override
    public R<Void> incCustomerStatAO(IncCustomerStatRequestDTO incCustomerStatAO) {
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

}
