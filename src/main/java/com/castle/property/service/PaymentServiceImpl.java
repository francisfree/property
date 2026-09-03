package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.entity.PaymentMonth;
import com.castle.property.entity.PaymentWeeklyEntry;
import com.castle.property.repository.PaymentMonthRepository;
import com.castle.property.repository.PaymentWeeklyEntryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PaymentMonthRepository paymentMonthRepository;
    private final PaymentWeeklyEntryRepository paymentWeeklyEntryRepository;

    private boolean validateHeaderRow(Row row, DataFormatter dataFormatter) {
        boolean noColumn = dataFormatter.formatCellValue(row.getCell(0)).trim().equalsIgnoreCase("No");
        boolean nameColumn = dataFormatter.formatCellValue(row.getCell(1)).trim().equalsIgnoreCase("Name");
        //header column names not standardize

        return noColumn && nameColumn;
    }

    private Map<Integer, String> extractWeeklyEntries(Row row, DataFormatter dataFormatter) {
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("M-d-yy");
        DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("d/M/yy");
        DateTimeFormatter cleanDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy");

        Map<Integer, String> weeklyEntries = new HashMap<>();
        for (int columnIndex = 0; columnIndex < 17; columnIndex++) {
            String cellData = dataFormatter.formatCellValue(row.getCell(columnIndex));
            LocalDate date;
            if (StringUtils.hasText(cellData)) {
                try {
                    date = LocalDate.parse(cellData, dateTimeFormatter1);
                    weeklyEntries.put(columnIndex, date.format(cleanDateTimeFormatter));
                } catch (DateTimeParseException e) {
                    try {
                        date = LocalDate.parse(cellData, dateTimeFormatter2);
                        weeklyEntries.put(columnIndex, date.format(cleanDateTimeFormatter));
                    } catch (DateTimeParseException ignore) {
                    }
                }
            }
        }
        return weeklyEntries;
    }

    private List<Predicate> createPredicates(Integer revisionCount, YearMonth yearMonth, String blockName, String searchParam, CriteriaBuilder cb, Root<PaymentMonth> root) {
        final List<Predicate> andPredicates = new ArrayList<>();

        Predicate deletedPredicate = cb.equal(root.get("deleted"), Boolean.FALSE);

        andPredicates.add(deletedPredicate);

        if (revisionCount != null) {
            andPredicates.add(cb.equal(root.get("revisionCount"), revisionCount));
        }

        if (yearMonth != null) {
            LocalDate localDateMonth =  LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
            andPredicates.add(cb.equal(root.get("month"), localDateMonth));
        }

        if (blockName != null) {
            andPredicates.add(cb.equal(root.get("blockName"), blockName));
        }

        if (searchParam != null && searchParam.trim().length() >= 3) {
            final List<Predicate> orPredicates = new ArrayList<>();
            String searchPattern = "%" + searchParam.toUpperCase() + "%";
            orPredicates.add(cb.like(cb.upper(root.get("houseNumber")), searchPattern));
            orPredicates.add(cb.like(cb.upper(root.get("occupantName")), searchPattern));
            orPredicates.add(cb.like(cb.upper(root.get("occupantPhoneNumber")), searchPattern));

            Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            andPredicates.add(p);
        }
        return andPredicates;
    }

    @Override
    public void uploadPaymentFile(MultipartFile multipartFile, YearMonth month, String blockName) {
        try {

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(multipartFile.getBytes());
            Workbook workbook = new XSSFWorkbook(byteArrayInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Map<Integer, String> weeklyEntries = extractWeeklyEntries(sheet.getRow(0), dataFormatter);
            if (weeklyEntries.isEmpty()) {
                throw new ApplicationOperationException("payment.weekly.entries.not.found");
            }
            List<Integer> weeklyEntriesColumIndexes = new ArrayList<>(weeklyEntries.keySet());
            int weeklyEntryStartIndexColumn = weeklyEntriesColumIndexes.get(0);
            int weeklyEntryLastIndexColumn = weeklyEntriesColumIndexes.get(weeklyEntriesColumIndexes.size() - 1);

            List<PaymentMonth> monthlyList = new ArrayList<>();


            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                //validate row is a header
                int columnIndex = 0;
                Row row = sheet.getRow(rowIndex);
                boolean headerRow = validateHeaderRow(row, dataFormatter);
                if (!headerRow) {
                    PaymentMonth paymentMonth = new PaymentMonth();
                    paymentMonth.setHouseNumber(dataFormatter.formatCellValue(row.getCell(columnIndex), evaluator).trim());
                    paymentMonth.setOccupantName(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setOccupantPhoneNumber(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setRentCurrentMonth(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setRentPreviousMonth(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setArrearsBroughtForward(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());

                    if (paymentMonth.getHouseNumber().isBlank() &&
                            paymentMonth.getOccupantName().isBlank() &&
                            paymentMonth.getOccupantPhoneNumber().isBlank() &&
                            paymentMonth.getRentCurrentMonth().isBlank() &&
                            paymentMonth.getRentPreviousMonth().isBlank()) {
                        break;
                    }

                    if (++columnIndex == weeklyEntryStartIndexColumn) {
                        columnIndex = weeklyEntryLastIndexColumn;
                    }
                    paymentMonth.setTotalPayment(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setPreviousWaterUnit(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setCurrentWaterUnit(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setPricePerUnit(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setUnitsConsumed(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());
                    paymentMonth.setWaterBill(dataFormatter.formatCellValue(row.getCell(++columnIndex), evaluator).trim());

                    //extract weeklyEntries
                    int weeklyRowIndex = rowIndex;
                    AtomicInteger weekCount = new AtomicInteger();
                    weeklyEntries.forEach((weeklyColumIndex, weekName) -> {
                        Row weeklyRow1 = sheet.getRow(weeklyRowIndex);
                        Row weeklyRow2 = sheet.getRow(weeklyRowIndex + 1);
                        Row weeklyRow3 = sheet.getRow(weeklyRowIndex + 2);

                        PaymentWeeklyEntry paymentWeeklyEntry = new PaymentWeeklyEntry();
                        paymentWeeklyEntry.setPaymentMonth(paymentMonth);
                        paymentWeeklyEntry.setWeekName(weekName);
                        paymentWeeklyEntry.setWeekCount(weekCount.incrementAndGet());
                        paymentWeeklyEntry.setCash(extractValueAmount(weeklyColumIndex, dataFormatter, weeklyRow1, evaluator));
                        paymentWeeklyEntry.setTill(extractValueAmount(weeklyColumIndex, dataFormatter, weeklyRow2, evaluator));
                        paymentWeeklyEntry.setMpesa(extractValueAmount(weeklyColumIndex, dataFormatter, weeklyRow3, evaluator));

                        paymentMonth.getWeeklyList().add(paymentWeeklyEntry);
                    });

                    monthlyList.add(paymentMonth);
                    rowIndex += 2;
                }
            }

            LocalDate localDateMonth =  LocalDate.of(month.getYear(), month.getMonthValue(), 1);
            Integer current = paymentMonthRepository.countByMonthAndBlockNameIgnoreCase(localDateMonth, blockName);
            int revisionCount =(current == null ? 0 : current) + 1;

            monthlyList.forEach(paymentMonthly -> {
                paymentMonthly.setRevisionCount(revisionCount);
                paymentMonthly.setMonth(localDateMonth);
                paymentMonthly.setBlockName(blockName);
            });

            List<PaymentMonth> savedMonthlyList = paymentMonthRepository.saveAll(monthlyList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String extractValueAmount(Integer weeklyColumIndex, DataFormatter dataFormatter, Row weeklyRow1, FormulaEvaluator evaluator) {
        String value = dataFormatter.formatCellValue(weeklyRow1.getCell(weeklyColumIndex), evaluator).trim();
        if (value.isEmpty()) return "0";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[A-Z]-(.+)").matcher(value);
        return matcher.matches() ? matcher.group(1) : "0";
    }

    @Override
    public Page<PaymentMonth> getMonthlyPayments(Integer revisionCount, YearMonth yearMonth, String blockName, String searchParam, PageRequest pageRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentMonth> mainQuery = cb.createQuery(PaymentMonth.class);
        Root<PaymentMonth> root = mainQuery.from(PaymentMonth.class);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PaymentMonth> countRoot = countQuery.from(PaymentMonth.class);

        mainQuery.distinct(true);

        final List<Predicate> mainQueryPredicates = createPredicates(revisionCount, yearMonth, blockName, searchParam, cb, root);
        final List<Predicate> countQueryPredicates = createPredicates(revisionCount, yearMonth, blockName, searchParam, cb, countRoot);

        mainQuery.where(mainQueryPredicates.toArray(new Predicate[mainQueryPredicates.size()])).orderBy(cb.asc(root.get("id")));

        TypedQuery<PaymentMonth> query = entityManager
                .createQuery(mainQuery)
                .setMaxResults(pageRequest.getPageSize())
                .setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        List<PaymentMonth> queryResultList = query.getResultList();

        countQuery.select(cb.count(countRoot));
        countQuery.where(countQueryPredicates.toArray(new Predicate[countQueryPredicates.size()]));
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(queryResultList, pageRequest, count);
    }

    @Override
    public Set<String> getBlockNamesByYearMonth(YearMonth yearMonth) {
        LocalDate localDateMonth =  LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        return paymentMonthRepository.getBlockNameDistinctByMonth(localDateMonth);
    }

    @Override
    public Set<Integer> getRevisionCountByYearMonthAndBlockName(YearMonth yearMonth, String blockName) {
        LocalDate localDateMonth =  LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        return paymentMonthRepository.getRevisionDistinctByMonth(localDateMonth, blockName);
    }
}
