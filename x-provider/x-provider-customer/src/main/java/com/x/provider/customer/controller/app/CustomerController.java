package com.x.provider.customer.controller.app;

import com.x.core.domain.SuggestionTypeEnum;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.mc.model.dto.SendVerificationCodeDTO;
import com.x.provider.api.mc.service.SmsRpcService;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.factory.dto.CustomerFactory;
import com.x.provider.customer.factory.vo.CustomerVOFactory;
import com.x.provider.customer.model.ao.*;
import com.x.provider.customer.model.domain.CustomerRelation;
import com.x.provider.customer.model.vo.CustomerHomePageVO;
import com.x.provider.customer.model.vo.SimpleCustomerVO;
import com.x.provider.customer.service.AuthenticationService;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.CustomerStatService;
import com.x.provider.customer.service.impl.authext.ExternalAuthEngine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户服务")
@RestController
@RequestMapping("/app/customer")
public class CustomerController extends BaseFrontendController {

    private final CustomerService customerService;
    private final CustomerRelationService customerRelationService;
    private final OssRpcService ossRpcService;
    private final SmsRpcService smsRpcService;
    private final CustomerStatService customerStatService;
    private final AuthenticationService authenticationService;
    private final ExternalAuthEngine externalAuthEngine;
    private final CustomerFactory customerFactory;
    private final CustomerVOFactory customerVOFactory;

    public CustomerController(CustomerService customerService,
                              CustomerRelationService customerRelationService,
                              OssRpcService ossRpcService,
                              SmsRpcService smsRpcService,
                              CustomerStatService customerStatService,
                              AuthenticationService authenticationService,
                              ExternalAuthEngine externalAuthEngine,
                              CustomerFactory customerFactory,
                              CustomerVOFactory customerVOFactory){
        this.customerService = customerService;
        this.customerRelationService = customerRelationService;
        this.ossRpcService = ossRpcService;
        this.smsRpcService = smsRpcService;
        this.customerStatService = customerStatService;
        this.authenticationService = authenticationService;
        this.externalAuthEngine = externalAuthEngine;
        this.customerFactory = customerFactory;
        this.customerVOFactory = customerVOFactory;
    }

    @ApiOperation(value = "用户名密码注册")
    @PostMapping("/register")
    public R<Void> register(@RequestBody @Validated UserNamePasswordRegisterAO userNamePasswordRegisterAO){
        customerService.register(userNamePasswordRegisterAO);
        return R.ok();
    }

    @ApiOperation(value = "根据密码登陆,返回token,下次方法其它接口是在此Token至于http header Authorization 中，值为 Bear token")
    @PostMapping("/login/by/password")
    public R<String> loginByPassword(@RequestBody @Validated LoginByPasswordAO userNamePasswordLoginAO){
        return R.ok(customerService.loginByPassword(userNamePasswordLoginAO));
    }

    @ApiOperation(value = "根据短信验证码登陆或者注册, 返回token,下次方法其它接口是在此Token至于http header Authorization 中，值为 Bear token")
    @PostMapping("/login/register/by/sms")
    public R<String> loginOrRegisterBySms(@RequestBody @Validated LoginOrRegBySmsAO loginOrRegBySmsAO){
        return R.ok(customerService.loginOrRegisterBySms(loginOrRegBySmsAO));
    }

    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/sms/verification/code/send")
    public R<Void> loginOrRegisterBySms(@RequestBody @Validated SendSmsVerificationCodeAO sendSmsVerificationCodeAO){
        return smsRpcService.sendVerificationCode(SendVerificationCodeDTO.builder().phoneNumber(sendSmsVerificationCodeAO.getPhoneNumber()).build());
    }

    @ApiOperation(value = "第三方登陆，目前支持微信小程序, 返回token,下次方法其它接口是在此Token至于http header Authorization 中，值为 Bear token")
    @PostMapping("/login/external")
    public R<String> loginExternal(@RequestBody @Validated ExternalAuthenticationAO externalAuthenticationAO){
        return R.ok(externalAuthEngine.authenticate(externalAuthenticationAO));
    }

    @ApiOperation(value = "注销")
    @PostMapping("/logout")
    public R<Void> logout(){
        authenticationService.signOut();
        return R.ok();
    }

    @ApiOperation("验证手机是否被绑定且发验证码")
    @PostMapping("/phone/bind/validate")
    public R<Void> bindPhone(@RequestBody @Validated ValidatePhoneAO validatePhoneAO) {
        customerService.checkPhoneBound(getCurrentCustomerId(), validatePhoneAO);
        return R.ok();
    }

    @ApiOperation("绑定手机")
    @PostMapping("/phone/bind")
    public R<Void> bindPhone(@RequestBody @Validated BindPhoneAO bindPhoneAO) {
        customerService.bindPhone(getCurrentCustomerId(), bindPhoneAO);
        return R.ok();
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/password/change")
    public R<Void> changePassword(@RequestBody @Validated ChangePasswordByOldPasswordAO changePasswordByOldPasswordAO){
        customerService.changePassword(getCurrentCustomerId(), changePasswordByOldPasswordAO);
        return R.ok();
    }

    @ApiOperation(value = "更改手机号码")
    @PostMapping("/phone/change")
    public R<Void> changePhone(@RequestBody @Validated ChangePhoneAO changePhoneAO){
        customerService.changePhone(getCurrentCustomerId(), changePhoneAO);
        return R.ok();
    }

    @ApiOperation(value = "修改用户名")
    @PostMapping("/user/name/change")
    public R<Void> changeUserName(@RequestBody @Validated ChangeUserNameAO changeUserNameAO){
        customerService.changeUserName(getCurrentCustomerId(), changeUserNameAO);
        return R.ok();
    }

    @ApiOperation(value = "设置用户属性")
    @PostMapping("/attribute/set")
    public R<Void> setCustomerAttribute(@RequestBody @Validated SetCustomerAttributeAO setCustomerAttribute){
        customerService.setCustomerAttribute(getCurrentCustomerId(), SystemCustomerAttributeName.valueOf(setCustomerAttribute.getAttributeName()), setCustomerAttribute.getValue());
        return R.ok();
    }

    @ApiOperation(value = "个人主页信息")
    @GetMapping("/homepage")
    public R<CustomerHomePageVO> getCustomerHomePage(@RequestParam(required = false) @ApiParam(value = "用户id") Long customerId){
        if (customerId == null || customerId <= 0){
            customerId = getCurrentCustomerIdAndNotCheckLogin();
        }
        final CustomerDTO customerDTO = customerFactory.prepare(ListCustomerRequestDTO.builder().sessionCustomerId(getCurrentCustomerIdAndNotCheckLogin()).customerIds(Arrays.asList(customerId))
                .suggestionType(customerId.equals(getCurrentCustomerIdAndNotCheckLogin()) ? null : SuggestionTypeEnum.PASS)
                .customerOptions(Arrays.asList(CustomerOptions.CUSTOMER.name(), CustomerOptions.CUSTOMER_ATTRIBUTE.name(), CustomerOptions.CUSTOMER_STAT.name(),
                        CustomerOptions.CUSTOMER_RELATION.name())).build()).get(customerId);
        return R.ok(customerVOFactory.prepare(null, customerDTO));
    }

    @ApiOperation(value = "关注,取消关注")
    @PostMapping("/follow/or/unfollow")
    public R<Void> following(@RequestBody FollowOrUnFollowAO followOrUnFollowAO){
        if (followOrUnFollowAO.getFollow()) {
            customerRelationService.following(getCurrentCustomerId(), followOrUnFollowAO.getToCustomerId());
        } else {
            customerRelationService.unFollowing(getCurrentCustomerId(), followOrUnFollowAO.getToCustomerId());
        }
        return R.ok();
    }

    @ApiOperation(value = "查询关注列表")
    @GetMapping("/following/list")
    public R<PageList<SimpleCustomerVO>> listCustomerRelation(@RequestParam Long cursor,
                                                              @RequestParam Integer pageSize,
                                                              @RequestParam @Validated @Min(0)  @ApiParam(value = "用户id") long customerId,
                                                              @RequestParam @Validated @Min(1)  @ApiParam(value = "用户关系 0 没有关系 1 关注关系 2 朋友关系 3 粉丝关系 ")
                                                                          Integer customerRelation){
        if (CustomerRelationEnum.FRIEND.getValue() == customerRelation.intValue()){
            customerId = getCurrentCustomerId();
        }
        PageList<CustomerRelation> relationList = customerRelationService.listCustomerRelation(customerId, CustomerRelationEnum.valueOf(customerRelation), getPageDomain());
        if (relationList.getList().isEmpty()){
            return R.ok(new PageList<>());
        }
        List<Long> relationCustomerIdList = customerRelation.equals(CustomerRelationEnum.FANS.getValue()) ? relationList.getList().stream().map(item -> item.getFromCustomerId()).collect(Collectors.toList()) :
                relationList.getList().stream().map(item -> item.getToCustomerId()).collect(Collectors.toList());
        return R.ok(PageList.map(relationList, customerVOFactory.prepare(relationCustomerIdList, getCurrentCustomerIdAndNotCheckLogin())));
    }
}
