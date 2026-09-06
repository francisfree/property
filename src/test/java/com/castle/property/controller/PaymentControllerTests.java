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
                .get(baseUrl + "/{paymentMonthId}/receipt", publicId)
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
