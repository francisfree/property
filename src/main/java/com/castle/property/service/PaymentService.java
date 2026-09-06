package com.castle.property.service;

import com.castle.property.dto.PaymentMonthResponse;
import com.castle.property.entity.RentalPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

public interface PaymentService {

    void uploadPaymentFile(MultipartFile multipartFile, YearMonth month, String blockName);

    PaymentMonthResponse getMonthlyPayment(UUID publicId);

    Page<RentalPayment> getMonthlyPayments(Integer revisionCount, YearMonth yearMonth, String blockName, String searchParam, PageRequest pageRequest);

    Set<String> getBlockNamesByYearMonth(YearMonth yearMonth);

    Set<Integer> getRevisionCountByYearMonthAndBlockName(YearMonth yearMonth, String blockName);
}
