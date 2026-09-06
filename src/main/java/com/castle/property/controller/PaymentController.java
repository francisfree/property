package com.castle.property.controller;

import com.castle.property.dto.PagedResponse;
import com.castle.property.dto.PaymentMonthResponse;
import com.castle.property.entity.RentalPayment;
import com.castle.property.mapper.PaymentMapper;
import com.castle.property.service.PaymentService;
import com.castle.property.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ReceiptService receiptService;

    @Transactional(timeout = 600)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Payment Excel File", description = "Upload Payment Excel File",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            })
    public void uploadPayment(@RequestParam("file") MultipartFile file,
                              @RequestParam(name = "month") YearMonth yearMonth,
                              @RequestParam(name = "blockName") String blockName) throws Exception {
        if (file.isEmpty())
            throw new Exception("You must upload a file");

        paymentService.uploadPaymentFile(file, yearMonth, blockName);
    }

    @GetMapping
    public PagedResponse<PaymentMonthResponse> getMonthlyPayments(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "25") int size,
                                                                  @RequestParam(name = "revisionCount", required = false) Integer revisionCount,
                                                                  @RequestParam(name = "month", required = false) YearMonth yearMonth,
                                                                  @RequestParam(name = "blockName", required = false) String blockName,
                                                                  @RequestParam(name = "searchParam", required = false) String searchParam) {

        if (revisionCount == null || yearMonth == null || blockName == null) {
            return new PagedResponse<>(Collections.emptyList(), 0, 0, 0, 0);
        }
        Page<RentalPayment> result = paymentService.getMonthlyPayments(revisionCount, yearMonth, blockName, searchParam, PageRequest.of(page, size));
        return PagedResponse.of(result.map(PaymentMapper::toResponse));
    }

    @GetMapping(value = "filters/blockNames")
    public Set<String> getFiltersBlockNames(@RequestParam(name = "month") YearMonth yearMonth) {
        return paymentService.getBlockNamesByYearMonth(yearMonth);
    }

    @GetMapping(value = "filters/revisionCount")
    public Set<Integer> getFiltersRevisionCount(@RequestParam(name = "month") YearMonth yearMonth,
                                                @RequestParam(name = "blockName") String blockName) {
        return paymentService.getRevisionCountByYearMonthAndBlockName(yearMonth, blockName);
    }

    @GetMapping(value = "/{publicId}")
    public PaymentMonthResponse getMonthlyPayment(@PathVariable UUID publicId) {
        return paymentService.getMonthlyPayment(publicId);
    }

    @GetMapping(value = "/{paymentMonthPublicId}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download Rent Receipt PDF", description = "Generates and downloads the rent receipt PDF from a PaymentMonth record",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            })
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable UUID paymentMonthPublicId) {
        byte[] pdf = receiptService.generatePaymentReceipt(paymentMonthPublicId);
        String filename = "receipt-" + paymentMonthPublicId + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .body(pdf);
    }
}