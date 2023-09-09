package org.example;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyViewerTest {
    private static final String RESOURCES_DIR_NAME = "src/test/resources/";
    private ByteArrayOutputStream customOutputStream;

    @BeforeEach
    public void setCustomOutputStream() {
        customOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(customOutputStream));
    }

    private String getOutput() {
        return customOutputStream.toString().replaceAll("\n", "");
    }

    @InjectMocks
    private CurrencyViewer currencyViewer = new CurrencyViewer();
    @Mock
    private APIConnection apiConnection = new APIConnection();

    @Test
    public void testGetRatesWithNotEmptyArray() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("getrates_response.json"));

        String inputBase = "USD";
        String[] inputCurrencies = new String[]{"EUR", "CAD"};

        Mockito.when(apiConnection.getRates(inputBase, inputCurrencies)).thenReturn(expectedJSON);

        currencyViewer.getRates(inputBase, inputCurrencies);

        String actual = getOutput();
        String expected = getFileContent("getrates_result.txt");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRatesWithEmptyArray() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("getrates_response_empty_arr.json"));

        String inputBase = "USD";
        String[] inputCurrencies = new String[]{};

        Mockito.when(apiConnection.getRates(inputBase, inputCurrencies)).thenReturn(expectedJSON);

        currencyViewer.getRates(inputBase, inputCurrencies);

        String actual = getOutput();
        String expected = getFileContent("getrates_result_empty_arr.txt");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRatesWithNullArray() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("getrates_response_empty_arr.json"));

        String inputBase = "USD";

        Mockito.when(apiConnection.getRates(inputBase, null)).thenReturn(expectedJSON);

        currencyViewer.getRates(inputBase, null);

        String actual = getOutput();
        String expected = getFileContent("getrates_result_empty_arr.txt");

        assertEquals(expected, actual);
    }

    @Test
    public void testConvert() throws IOException{
        JSONObject expectedJSON = new JSONObject(getFileContent("convert_response.json"));

        double inputAmount = 50.0;
        String inputCurrencyFrom = "USD";
        String inputCurrencyTo = "KGS";

        Mockito.when(apiConnection.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo)).thenReturn(expectedJSON);

        currencyViewer.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo);

        String actual = getOutput();
        String expected = getFileContent("convert_result.txt");

        assertEquals(expected, actual);
    }

    @Test
    public void testConvertWithAmountLessThanZero() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("convert_response.json"));

        double inputAmount = -50.0;
        String inputCurrencyFrom = "USD";
        String inputCurrencyTo = "KGS";

        Mockito.when(apiConnection.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo)).thenReturn(expectedJSON);

        currencyViewer.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo);

        String actual = getOutput();
        String expected = "The amount cannot be less or equal to 0.";

        assertEquals(expected, actual);
    }

    @Test
    public void testConvertWithAmountEqualToZero() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("convert_response.json"));

        double inputAmount = 0;
        String inputCurrencyFrom = "USD";
        String inputCurrencyTo = "KGS";

        Mockito.when(apiConnection.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo)).thenReturn(expectedJSON);

        currencyViewer.convert(inputAmount, inputCurrencyFrom, inputCurrencyTo);

        String actual = getOutput();
        String expected = "The amount cannot be less or equal to 0.";

        assertEquals(expected, actual);
    }

    @Test
    public void testViewCurrencies() throws IOException {
        JSONObject expectedJSON = new JSONObject(getFileContent("viewcurs_response.json"));

        Mockito.lenient().when(apiConnection.getAvailableCurrencies()).thenReturn(expectedJSON);

        currencyViewer.viewCurrencies();

        String actual = getOutput();
        String expected = getFileContent("viewcurs_result.txt");

        assertEquals(expected, actual);

    }

    @Test
    public void testCurrencyIsNotSupported() throws Exception {
        String inputCurrency = "ABC";
        boolean actual = (Boolean) getIsNotSupportedMethod().invoke(currencyViewer, inputCurrency);
        assertTrue(actual);
    }

    @Test
    public void testCurrencyIsSupported() throws Exception {
        String inputCurrency = "EUR";
        boolean actual = (Boolean) getIsNotSupportedMethod().invoke(currencyViewer, inputCurrency);
        assertFalse(actual);
    }

    private String getFileContent(String fileName) throws IOException {
        List<String> fileLines = Files.readAllLines(Paths.get(RESOURCES_DIR_NAME + fileName));
        StringBuilder sb = new StringBuilder();
        for (String s : fileLines) {
            sb.append(s);
        }
        return sb.toString();
    }

    private Method getIsNotSupportedMethod() throws NoSuchMethodException{
        Method isNotSupportedMethod = CurrencyViewer.class.getDeclaredMethod("isNotSupported", String.class);
        isNotSupportedMethod.setAccessible(true);
        return isNotSupportedMethod;
    }
}