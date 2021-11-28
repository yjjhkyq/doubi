package com.x.provider.pay.controller.rpc;

import com.x.core.utils.ApiAssetUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.pay.model.ao.BalanceChangeAo;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.model.ao.LaunchTransferAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.WalletRpcService;
import com.x.provider.pay.annotation.GenerateBill;
import com.x.provider.pay.enums.PayResultCode;
import com.x.provider.pay.enums.bill.BillStatus;
import com.x.provider.pay.mapper.BillMapper;
import com.x.provider.pay.model.domain.Bill;
import com.x.provider.pay.service.BillService;
import com.x.provider.pay.service.WalletService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/rpc/wallet")
public class WalletRpcController extends BaseRpcController implements WalletRpcService {

    @Autowired
    private WalletService walletService;


    @PostMapping("/wallet")
    @Override
    public R<String> createWallet(@RequestBody CreateWalletAO createWalletAO) {
        walletService.createWallet(createWalletAO);
        return R.ok();
    }

    @PostMapping("/recharge")
    @Override
    public R<BillDto> rechargeWallet(@RequestBody BigDecimal amount) {
        Bill bill = walletService.rechargeWallet(amount, getCurrentCustomerId());
        BillDto billDto = bill.toDto();
        return R.ok(billDto);
    }

    @Override
    public R<BillDto> balanceChange(BalanceChangeAo balanceChangeAo) {
        return null;
    }


}
