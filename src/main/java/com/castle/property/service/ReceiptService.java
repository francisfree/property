package com.castle.property.service;

import java.util.List;
import java.util.UUID;

public interface ReceiptService {

    byte[] generatePaymentReceipt(UUID paymentMonthPublicId);

    byte[] generatePaymentReceipts(List<UUID> paymentMonthPublicIds);
}