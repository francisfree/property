package com.castle.property;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(locations="classpath:application-test.properties")
public abstract class PropertyApplicationTests {
    @Value("${local.server.port}")
    public int port;

    @Autowired
    private WebApplicationContext context;

    public MockMvc mvc;

    @Before
    public void setUpGlobal() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

    }

    public void saveExport(Response response, String downloadFileName) throws IOException {
        Assert.assertEquals(200, response.getStatusCode());

        String downloadFolder = "src/test/resources/downloads";
        File outputPath = new File(downloadFolder);

        // create the folder structure if it does not exist
        outputPath.mkdirs();

        // For the purpose of the test, if the file already exists then I will delete it

        File checkDownloaded = new File(outputPath.getPath(), downloadFileName);
        if (checkDownloaded.exists()) {
            checkDownloaded.delete();
        }
        File outputFile = new File(outputPath.getPath(), downloadFileName);

        // I might choose to use the mime type of the file to control the file extension
        // here I am just outputting it to the console to demonstrate how to get the type
        System.out.println("Downloaded an " + response.getHeader("Content-Type"));

        // get the contents of the file
        byte[] fileContents = response.getBody().asByteArray();

        // output contents to file
        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream(outputFile);
            outStream.write(fileContents);
        } catch (Exception e) {
            Assert.fail("Error writing file " + outputFile.getAbsolutePath() + ": " + e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException closeException) {
                    Assert.fail("Error closing file " + outputFile.getAbsolutePath() + ": " + closeException.getMessage());
                }
            }
        }
        Assert.assertTrue("Downloaded file is empty: " + outputFile.getAbsolutePath(), outputFile.length() > 0);
    }
}
