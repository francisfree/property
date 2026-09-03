package com.castle.property.service;

import com.castle.property.entity.PaymentMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.Set;

public interface PaymentService {

    void uploadPaymentFile(MultipartFile multipartFile, YearMonth month, String blockName);

    Page<PaymentMonth> getMonthlyPayments(Integer revisionCount, YearMonth yearMonth, String blockName, String searchParam, PageRequest pageRequest);

    Set<String> getBlockNamesByYearMonth(YearMonth yearMonth);

    Set<Integer> getRevisionCountByYearMonthAndBlockName(YearMonth yearMonth, String blockName);
}
