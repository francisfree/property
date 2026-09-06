package com.castle.property.controller;

import com.castle.property.PropertyApplicationTests;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.charset.StandardCharsets;

public class PaymentControllerTests extends PropertyApplicationTests {

    private static final String PAYMENT_EXCEL_RESOURCE = "/document/payment_august.xlsx";

    private final String baseUrl = "/api/v1/payments";

    @Before
    public void setUpBaseUri() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "";
        RestAssured.port = port;
    }

    @Test
    public void uploadPaymentWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .multiPart("file", loadPaymentTestFile())
                .param("month", "2026-08")
                .param("blockName", "Block A")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(200);
    }

    @Test
    public void uploadEmptyFileReturns500() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .multiPart("file", "empty.xlsx", new byte[0])
                .param("month", "2026-08")
                .param("blockName", "Block A")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(500);
    }

    @Test
    public void uploadPaymentWithoutAuthReturns401() throws Exception {
        given()
                .multiPart("file", loadPaymentTestFile())
                .param("month", "2026-08")
                .param("blockName", "Block A")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(401);
    }

    @Test
    public void uploadInvalidFileFormatReturns500() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .multiPart("file", "notes.txt", "this is not an excel file".getBytes())
                .param("month", "2026-08")
                .param("blockName", "Block A")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(500);
    }

    @Test
    public void getMonthlyPaymentsWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", notNullValue());
    }

    @Test
    public void getMonthlyPaymentsDefaultsPagination() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("page", is(0))
                .body("size", is(25));
    }

    @Test
    public void getMonthlyPaymentsWithSearchWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .queryParam("searchParam", "JACOB")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    public void getMonthlyPaymentsShortSearchIgnored() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .queryParam("searchParam", "ab")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    public void getMonthlyPaymentsPaginationWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("page", 0)
                .queryParam("size", 1)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("size", is(1));
    }

    @Test
    public void getMonthlyPaymentsWithoutAuthReturns401() throws Exception {
        given()
                .queryParam("revisionCount", 0)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block A")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(401);
    }

    @Test
    public void getMonthlyPaymentsNoResultsReturnsEmpty() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 0)
                .queryParam("month", "2020-01")
                .queryParam("blockName", "NonExistentBlock")
                .when()
                .get(baseUrl)
                .then()
                .statusCode(200)
                .body("totalElements", is(0));
    }

    @Test
    public void uploadThenRetrievePaymentsWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .multiPart("file", loadPaymentTestFile())
                .param("month", "2026-08")
                .param("blockName", "Block B")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(200);

        Response response = given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 1)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block B")
                .when()
                .get(baseUrl)
                .then().log().all()
                .statusCode(200)
                .extract().response();

        long totalElements = response.jsonPath().getLong("totalElements");
        org.junit.Assert.assertTrue("expected at least one monthly payment to be persisted", totalElements >= 1);

        String publicId = response.jsonPath().getString("content[0].publicId");

        Response receiptResponse = given()
                .auth().oauth2(token)
                .get(baseUrl + "/receipts/{publicId}", publicId)
                .andReturn();

        Assert.assertEquals(200, receiptResponse.statusCode());
        saveExport(receiptResponse, "Receipt_01.pdf");

        Response listAfterReceipt = given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 1)
                .queryParam("month", "2026-08")
                .queryParam("blockName", "Block B")
                .when()
                .get(baseUrl)
                .then().log().all()
                .statusCode(200)
                .extract().response();

        String receiptNumber = listAfterReceipt.jsonPath().getString("content[0].receiptNumber");
        org.junit.Assert.assertTrue("expected a receipt number to be assigned after download",
                receiptNumber != null && receiptNumber.startsWith("RPT-"));

        Response singleResponse = given()
                .auth().oauth2(token)
                .get(baseUrl + "/{publicId}", publicId)
                .then().log().all()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(publicId, singleResponse.jsonPath().getString("publicId"));
        Assert.assertEquals(receiptNumber, singleResponse.jsonPath().getString("receiptNumber"));
        Assert.assertTrue(singleResponse.jsonPath().getString("houseNumber") != null);
    }

    @Test
    public void combinedReceiptsDownloadWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        given()
                .auth().oauth2(token)
                .multiPart("file", loadPaymentTestFile())
                .param("month", "2026-09")
                .param("blockName", "Block C")
                .when()
                .post(baseUrl + "/upload")
                .then()
                .statusCode(200);

        Response listResponse = given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 1)
                .queryParam("month", "2026-09")
                .queryParam("blockName", "Block C")
                .when()
                .get(baseUrl)
                .then().log().all()
                .statusCode(200)
                .extract().response();

        long totalElements = listResponse.jsonPath().getLong("totalElements");
        org.junit.Assert.assertTrue("expected at least two monthly payments", totalElements >= 2);

        String firstId = listResponse.jsonPath().getString("content[0].publicId");
        String secondId = listResponse.jsonPath().getString("content[1].publicId");
        Assert.assertNotEquals(firstId, secondId);

        Response combinedResponse = given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("{\"publicIds\": [\"" + firstId + "\", \"" + secondId + "\"]}")
                .when()
                .post(baseUrl + "/receipts")
                .then().log().all()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(200, combinedResponse.statusCode());
        Assert.assertTrue("expected application/pdf content type",
                combinedResponse.getContentType().startsWith("application/pdf"));

        byte[] pdfBytes = combinedResponse.getBody().asByteArray();
        Assert.assertTrue("expected a non-empty combined PDF", pdfBytes.length > 0);
        Assert.assertEquals("%PDF", new String(pdfBytes, 0, 4, StandardCharsets.UTF_8));
        saveExport(combinedResponse, "Receipts_Combined.pdf");

        Response listAfter = given()
                .auth().oauth2(token)
                .queryParam("revisionCount", 1)
                .queryParam("month", "2026-09")
                .queryParam("blockName", "Block C")
                .when()
                .get(baseUrl)
                .then().log().all()
                .statusCode(200)
                .extract().response();

        String firstReceiptNumber = listAfter.jsonPath().getString("content[0].receiptNumber");
        String secondReceiptNumber = listAfter.jsonPath().getString("content[1].receiptNumber");
        org.junit.Assert.assertTrue("expected a receipt number to be assigned after combined download",
                firstReceiptNumber != null && firstReceiptNumber.startsWith("RPT-"));
        org.junit.Assert.assertTrue("expected a receipt number to be assigned after combined download",
                secondReceiptNumber != null && secondReceiptNumber.startsWith("RPT-"));
    }

    private File loadPaymentTestFile() throws Exception {
        return new ClassPathResource(PAYMENT_EXCEL_RESOURCE, this.getClass().getClassLoader()).getFile();
    }

    private String getAuthToken(String username, String password) {
        Response response = given()
                .contentType("application/json")
                .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        return response.jsonPath().getString("token");
    }
}
