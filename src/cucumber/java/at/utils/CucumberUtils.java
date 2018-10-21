package at.utils;

import cucumber.api.DataTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static at.stubs.CryptoCompareStubs.makeCryptoCompareStub;
import static java.math.BigInteger.ZERO;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class CucumberUtils {

    public static final int PORT =10000;
    public static final String GET = "GET";
    private static ClientAndProxy proxy;
    private static ClientAndServer mockServer;
    private final static String DATA_KEY = "Data";
    private final static String  TEMP_PATH_KEY = "/tmp/";
    private final static String EMPTY_STRING = "";
    private final static String BACK_SLASH_REGEX = "\\\\";

    public static void startMockServer(Environment env, MockServerEndPointTriggerCriteria criteria, String stubResponseFile) throws IOException {
        stopMockServer();
        mockServer = startClientAndServer(PORT);
        if(proxy !=null) { proxy = startClientAndProxy(1090);}
        //TODO dont import factory for static gettrigger!! rename createtrigger or produce trigger call it trggerfactory?
        makeCryptoCompareStub(MockServerEndpointTriggerFactory.getTrigger(criteria).createMockServerParameterObject(env, stubResponseFile));//TODO rename stubResponseTrigger??
    }

    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static JSONObject makeStubFromDataTable(String fileName, DataTable dataTable, String... columnsToIgnore) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray data = convertDataTableToJSONArray(dataTable, EMPTY_STRING);
        jsonObject.put(DATA_KEY, data);
        Files.write(Paths.get(TEMP_PATH_KEY +fileName), jsonObject.toString(4).replaceAll(BACK_SLASH_REGEX, EMPTY_STRING).getBytes());
        return jsonObject;
    }

    public static JSONArray convertDataTableToJSONArray(DataTable dataTable, String... columnsToIgnore) throws JSONException {
        List<List<String>> table = dataTable.raw();
        JSONArray jsonArray = new JSONArray();
        boolean headerRow = true;
        List<String> keys = table.get(ZERO.intValue());
        for (List<String> row : table) {
            jsonArray = (headerRow) ?  jsonArray : jsonArray.put(convertRowToJSONObject(keys, row, columnsToIgnore));
            headerRow=false;
        }
        return jsonArray;
    }
//TODO inner class!!
    private static JSONObject convertRowToJSONObject(List<String> keys, List<String> row, String... columnsToIgnore) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        int columnCounter = 0;
        for (String column : row) {
            jsonObject.put(keys.get(columnCounter), column);
            columnCounter++;
        }
        for(String column : columnsToIgnore) {
            jsonObject.remove(column);
        }
        return jsonObject;
    }

    private static void stopMockServer() {
        if(proxy!=null) {
            proxy.stop();
        }
        if(mockServer!=null) {
            mockServer.stop();
        }
    }

}