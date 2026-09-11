package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.datatype.CounterType;
import com.castle.property.entity.RentalPayment;
import com.castle.property.entity.RentalPaymentWeekly;
import com.castle.property.repository.RentalPaymentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private static final String TEMPLATE_NAME = "receipt_template";
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#,##0.00;(#,##0.00)");
    private static final Map<String, File> FONT_CACHE = new ConcurrentHashMap<>();

    private final RentalPaymentRepository rentalPaymentRepository;
    private final HtmlToPdfTemplateService htmlToPdfTemplateService;
    private final CounterService counterService;

    @Override
    public byte[] generatePaymentReceipt(UUID paymentMonthPublicId) {
        RentalPayment rentalPayment = rentalPaymentRepository.findByPublicId(paymentMonthPublicId)
                .orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));

        boolean newlyNumbered = rentalPayment.getReceiptNumber() == null;
        ensureReceiptNumber(rentalPayment);
        RentalPayment savedRentalPayment = newlyNumbered
                ? rentalPaymentRepository.save(rentalPayment)
                : rentalPayment;

        return renderReceiptPdf(savedRentalPayment);
    }

    @Override
    public byte[] generatePaymentReceipts(List<UUID> paymentMonthPublicIds) {
        if (paymentMonthPublicIds == null || paymentMonthPublicIds.isEmpty()) {
            throw new ApplicationOperationException("operation.record.not.found");
        }

        List<RentalPayment> rentalPayments = rentalPaymentRepository.findAllByPublicIdInOrderById(paymentMonthPublicIds);
        if (rentalPayments.size() != paymentMonthPublicIds.size()) {
            throw new ApplicationOperationException("operation.record.not.found");
        }

        Map<UUID, RentalPayment> byPublicId = new HashMap<>();
        for (RentalPayment rentalPayment : rentalPayments) {
            byPublicId.put(rentalPayment.getPublicId(), rentalPayment);
        }

        List<byte[]> pdfs = new ArrayList<>();
        List<RentalPayment> newlyNumbered = new ArrayList<>();
        for (UUID publicId : paymentMonthPublicIds) {
            RentalPayment rentalPayment = byPublicId.get(publicId);
            if (rentalPayment == null) {
                throw new ApplicationOperationException("operation.record.not.found");
            }
            if (rentalPayment.getReceiptNumber() == null) {
                newlyNumbered.add(rentalPayment);
            }
            ensureReceiptNumber(rentalPayment);
            pdfs.add(renderReceiptPdf(rentalPayment));
        }

        if (!newlyNumbered.isEmpty()) {
            rentalPaymentRepository.saveAll(newlyNumbered);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility merger = new PDFMergerUtility();
            for (byte[] pdf : pdfs) {
                merger.addSource(new ByteArrayInputStream(pdf));
            }
            merger.setDestinationStream(outputStream);
            merger.mergeDocuments();
            return outputStream.toByteArray();
        } catch (IOException exception) {
            log.error("Failed to merge {} payment receipts", paymentMonthPublicIds.size(), exception);
            throw new ApplicationOperationException("error.data.processing", exception);
        }
    }

    private void ensureReceiptNumber(RentalPayment rentalPayment) {
        if (rentalPayment.getReceiptNumber() == null) {
            Integer nextCounter = counterService.getNextCounter(CounterType.RentReceipt);
            rentalPayment.setReceiptNumber(String.format("RPT-%d", nextCounter));
        }
    }

    private byte[] renderReceiptPdf(RentalPayment rentalPayment) {
        Map<String, Object> model = toModel(rentalPayment);
        String html = htmlToPdfTemplateService.render(TEMPLATE_NAME, model);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            registerFont(builder, "fonts/arial.ttf");
            registerFont(builder, "fonts/arialbd.ttf");
            registerFont(builder, "fonts/arialbi.ttf");
            registerFont(builder, "fonts/Calibri.ttf");
            builder.toStream(byteArrayOutputStream);
            builder.run();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            log.error("Failed to generate payment receipt PDF", exception);
            throw new ApplicationOperationException("error.data.processing", exception);
        }
    }

    private Map<String, Object> toModel(RentalPayment rentalPayment) {
        Map<String, Object> model = new LinkedHashMap<>();
        String blockName = rentalPayment.getBlockName().toUpperCase().replaceAll("BLOCK", "").replaceAll(" ", "");
        String houseNumber = String.format("%s%s", blockName, rentalPayment.getHouseNumber());

        model.put("receiptNumber", rentalPayment.getReceiptNumber());
        model.put("paymentNumber", rentalPayment.getOccupantPhoneNumber());
        model.put("tenantName", rentalPayment.getOccupantName());
        model.put("tenantPhoneNumber", rentalPayment.getOccupantPhoneNumber());
        model.put("houseNo", houseNumber);
        model.put("month", rentalPayment.getMonth() == null ? "" : rentalPayment.getMonth().format(MONTH_FORMATTER));
        model.put("rentPerMonth", formatToNumericValue(rentalPayment.getRent()));
        model.put("waterBill", formatToNumericValue(rentalPayment.getWaterBill()));
        model.put("previousWaterUnit", rentalPayment.getPreviousWaterUnit());
        model.put("currentWaterUnit", rentalPayment.getCurrentWaterUnit());
        model.put("unitsConsumed", rentalPayment.getUnitsConsumed());
        model.put("pricePerUnit", formatToNumericValue(rentalPayment.getPricePerUnit()));
        model.put("garbage", formatToNumericValue(rentalPayment.getGarbage()));
        model.put("arrearsBroughtForward", formatToNumericValue(rentalPayment.getArrearsBroughtForward()));
        model.put("rentDue", formatToNumericValue(rentalPayment.getTotalRentDue()));
        model.put("weekly", toWeeklyModel(rentalPayment.getWeeklyEntries()));
        model.put("totalCollected", formatToNumericValue((rentalPayment.getTotalRentPaid())));
        model.put("arrearsCarriedForward", formatToNumericValue(rentalPayment.getArrearsCarriedForward()));
        return model;
    }

    private String formatToNumericValue(String strAmount) {
       if (strAmount != null) {
            BigDecimal amount;
            try {
                amount = new BigDecimal(strAmount.replace(",", "").trim());
            } catch (NumberFormatException e) {
                return strAmount;
            }
            return DECIMAL_FORMATTER.format(amount);
        }
        return null;
    }

    private List<Map<String, Object>> toWeeklyModel(List<RentalPaymentWeekly> weeklyList) {
        List<Map<String, Object>> weekly = new ArrayList<>();
        for (RentalPaymentWeekly weeklyEntry : weeklyList) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("weekName", weeklyEntry.getWeekName());
            entry.put("amount", formatToNumericValue(weeklyAmount(weeklyEntry)));
            weekly.add(entry);
        }
        return weekly;
    }

    private String weeklyAmount(RentalPaymentWeekly weeklyEntry) {
        return toNumber(weeklyEntry.getTotalAmount())
                .stripTrailingZeros()
                .toPlainString();
    }

    private BigDecimal toNumber(String value) {
        if (!StringUtils.hasText(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException exception) {
            return BigDecimal.ZERO;
        }
    }

    private void registerFont(PdfRendererBuilder builder, String classpathResource) {
        try {
            switch (classpathResource) {
                case "fonts/arialbd.ttf" ->
                        builder.useFont(fontFile(classpathResource), "Arial", 700, FontStyle.NORMAL, true);
                case "fonts/arialbi.ttf" ->
                        builder.useFont(fontFile(classpathResource), "Arial", 700, FontStyle.ITALIC, true);
                case "fonts/Calibri.ttf" ->
                        builder.useFont(fontFile(classpathResource), "Calibri", 400, FontStyle.NORMAL, true);
                default ->
                        builder.useFont(fontFile(classpathResource), "Arial", 400, FontStyle.NORMAL, true);
            }
        } catch (IOException exception) {
            log.error("Failed to register font {}", classpathResource, exception);
            throw new ApplicationOperationException("error.data.processing", exception);
        }
    }

    private File fontFile(String classpathResource) throws IOException {
        File cached = FONT_CACHE.get(classpathResource);
        if (cached != null && cached.exists()) {
            return cached;
        }
        File tempFile = File.createTempFile("receipt-font-", ".ttf");
        try (InputStream inputStream = new ClassPathResource(classpathResource).getInputStream()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        tempFile.deleteOnExit();
        FONT_CACHE.put(classpathResource, tempFile);
        return tempFile;
    }
}