package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.datatype.CounterType;
import com.castle.property.entity.RentalPayment;
import com.castle.property.entity.PaymentWeeklyEntry;
import com.castle.property.repository.RentalPaymentRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private static final Map<String, File> FONT_CACHE = new ConcurrentHashMap<>();

    private final RentalPaymentRepository rentalPaymentRepository;
    private final HtmlToPdfTemplateService htmlToPdfTemplateService;
    private final CounterService counterService;

    @Override
    public byte[] generatePaymentReceipt(UUID paymentMonthPublicId) {
        RentalPayment rentalPayment = rentalPaymentRepository.findByPublicId(paymentMonthPublicId)
                .orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));

        RentalPayment savedRentalPayment;

        if (rentalPayment.getReceiptNumber() == null) {
            Integer nextCounter = counterService.getNextCounter(CounterType.RentReceipt);
            rentalPayment.setReceiptNumber(String.format("RPT-%d", nextCounter));
            savedRentalPayment = rentalPaymentRepository.save(rentalPayment);
        } else {
            savedRentalPayment = rentalPayment;
        }

        Map<String, Object> model = toModel(savedRentalPayment);
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
            log.error("Failed to generate payment receipt for payment month publicId {}", paymentMonthPublicId, exception);
            throw new ApplicationOperationException("error.data.processing", exception);
        }
    }

    private Map<String, Object> toModel(RentalPayment rentalPayment) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("receiptNumber", rentalPayment.getReceiptNumber());
        model.put("paymentNumber", rentalPayment.getOccupantPhoneNumber());
        model.put("tenantName", rentalPayment.getOccupantName());
        model.put("houseNo", rentalPayment.getHouseNumber());
        model.put("paybillHouseNo", rentalPayment.getHouseNumber());
        model.put("month", rentalPayment.getMonth() == null ? "" : rentalPayment.getMonth().format(MONTH_FORMATTER));
        model.put("rentPerMonth", rentalPayment.getRentCurrentMonth());
        model.put("waterBill", rentalPayment.getWaterBill());
        model.put("arrearsBroughtForward", rentalPayment.getArrearsBroughtForward());
        model.put("weekly", toWeeklyModel(rentalPayment.getWeeklyEntries()));
        model.put("totalCollected", rentalPayment.getTotalPayment());
        model.put("arrearsCarriedForward", rentalPayment.getArrearsBroughtForward());
        return model;
    }

    private List<Map<String, Object>> toWeeklyModel(List<PaymentWeeklyEntry> weeklyList) {
        List<Map<String, Object>> weekly = new ArrayList<>();
        for (PaymentWeeklyEntry weeklyEntry : weeklyList) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("weekName", weeklyEntry.getWeekName());
            entry.put("amount", weeklyAmount(weeklyEntry));
            weekly.add(entry);
        }
        return weekly;
    }

    private String weeklyAmount(PaymentWeeklyEntry weeklyEntry) {
        return toNumber(weeklyEntry.getCash())
                .add(toNumber(weeklyEntry.getTill()))
                .add(toNumber(weeklyEntry.getMpesa()))
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