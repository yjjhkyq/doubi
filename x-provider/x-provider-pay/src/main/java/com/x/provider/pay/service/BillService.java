package com.x.provider.pay.service;

import com.x.provider.api.pay.model.ao.CreateBillAo;
import com.x.provider.pay.model.domain.Bill;

public interface BillService {
    void insert(Bill bill);

    Bill createBill(CreateBillAo createBillAo);

    Bill launchBill(String billSerialNumber);

    Bill launchBill(Bill bill);

    Bill receiveBill(String billSerialNumber);

    Bill receiveBill(Bill bill);

    Bill getBill(String billSerialNumber);

    void updateBill(Bill bill);
}
