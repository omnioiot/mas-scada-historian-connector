/*
 * IBM Confidential
 * OCO Source Materials
 * 5725-S86, 5900-A0N, 5737-M66, 5900-AAA
 * (C) Copyright IBM Corp. 2022
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright Office.
 */

package com.ibm.mas.scada.historian.connector.configurator;

import java.util.concurrent.*;
import java.util.logging.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.ibm.mas.scada.historian.connector.utils.Constants;
import com.ibm.mas.scada.historian.connector.utils.Copyright;
import com.ibm.mas.scada.historian.connector.utils.RestClient;

public class TagDimension {

    public static final String COPYRIGHT = Copyright.COPYRIGHT;
    private static Logger logger = Logger.getLogger(Constants.LOGGER_CLASS);
    private static TagDataCache tc;
    private static JSONObject connConfig;
    private static JSONObject mappingConfig;
    private static JSONObject tagDimension;
    private static JSONArray deviceTypes;
    private static String baseUrl;
    private static String tenantId;
    private static String devToken;
    private static RestClient restClient;

    public TagDimension(Config config, TagDataCache tc) {
        if (config == null || tc == null) {
            throw new NullPointerException("TagDimension: config or tc parameter cannot be null");
        }
        this.tc = tc;
        this.connConfig = config.getConnectionConfig();
        this.mappingConfig = config.getMappingConfig();
        this.tagDimension = mappingConfig.optJSONObject("dimensions");
        this.deviceTypes = mappingConfig.optJSONArray("deviceTypes");
        if (deviceTypes.length() == 0 || tagDimension.length() == 0) {
            throw new IllegalArgumentException("Specified parameter is not valid");
        }

        JSONObject iotp = connConfig.optJSONObject("iotp");
        this.devToken = iotp.getString("devToken");
        String asKey = iotp.getString("X_API_KEY");
        String asToken = iotp.getString("X_API_TOKEN");
        String asEmail = iotp.getString("MAM_USER_EMAIL");
        this.baseUrl = "https://" + iotp.getString("asHost");
        this.tenantId = iotp.getString("tenantId");

        // Create Monitor API REST client
        this.restClient = new RestClient(baseUrl, Constants.AUTH_HEADER, asKey, asToken, asEmail, tenantId, 1);

        if (!validateDevToken(devToken)) {
            logger.warning("DevToken is not valid. Devices won't be created");
            System.out.println("DevToken is not valid");
            System.exit(1);
        }
    }

    public void startDimensionProcess() {
        startDimensionThread();
    }

    private static boolean validateDevToken(String devToken) {

        /**
         * Validates that the provided devToken fulfils the requirements
         * to be used as a valid device authentication token:
         * - 8-36 characters
         * - An upper case character
         * - A lower case character
         * - A number
         * - A special allowed character
         * - No repeated characters
         */
        List<Character> allowedChars = Arrays.asList('@', '!', '_', '.', '-');

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        // check length restriction
        if (!(devToken.length() >= 8 && devToken.length() <= 36)) {
            return false;
        }
        // check format
        for (int i = 0; i < devToken.length(); i++) {
            char ch = devToken.charAt(i);

            // validate for duplicate
            if (devToken.indexOf(ch) != devToken.lastIndexOf(ch)) {
                return false;
            }

            // validate string contains atleast one uppercase, one lowercase, one digit
            if (Character.isLetter(ch)) {
                if (!hasUpper && Character.isUpperCase(ch)) {
                    hasUpper = true;
                } else if (!hasLower && Character.isLowerCase(ch)) {
                    hasLower = true;
                }
            } else if (Character.isDigit(ch)) {
                if (!hasDigit) {
                    hasDigit = true;
                }
            } else {
                // character is special character
                if (!allowedChars.contains(ch)) {
                    return false;
                }
            }
        }
        return hasDigit && hasLower && hasUpper;
    }

    private static String createV2DeviceType(String entityTypeName) {
        String uuid = "";
        logger.info(String.format("Create V2 device type: %s", entityTypeName));
        String etypeAPI = "/api/v2/deviceTypes";

        JSONArray entityObj = new JSONArray();
        JSONObject entityTypeObj = new JSONObject();
        JSONArray dataItemDtoArray = new JSONArray();
        JSONArray evtDtoArray = new JSONArray();

        entityTypeObj.put("name", entityTypeName);
        entityTypeObj.put("description", entityTypeName);

        // Defining the metrics
        dataItemDtoArray.put(createDataDtoObjectV2("evt_timestamp", "scadaevent", "TIMESTAMP", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("value", "scadaevent", "NUMBER", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("type", "scadaevent", "LITERAL", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("unit", "scadaevent", "LITERAL", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("decimalAccuracy", "scadaevent", "NUMBER", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("tag", "scadaevent", "LITERAL", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("name", "scadaevent", "LITERAL", "METRIC"));
        dataItemDtoArray.put(createDataDtoObjectV2("label", "scadaevent", "LITERAL", "METRIC"));
        // Defining the dimensions
        dataItemDtoArray.put(createDataDtoObjectV2("TAGID", "scadaevent", "LITERAL", "DIMENSION"));
        dataItemDtoArray.put(createDataDtoObjectV2("TAGPATH", "scadaevent", "LITERAL", "DIMENSION"));
        dataItemDtoArray.put(createDataDtoObjectV2("DATATYPE", "scadaevent", "LITERAL", "DIMENSION"));
        dataItemDtoArray.put(createDataDtoObjectV2("SITE", "scadaevent", "LITERAL", "DIMENSION"));
        entityTypeObj.put("dataItemDto", dataItemDtoArray);

        // evtDtoArray.put(createEventDtoObjectV2("EventA", "evt_timestamp"));
        evtDtoArray.put(createEventDtoObjectV2("scadaevent", "evt_timestamp"));
        entityTypeObj.put("eventDto", evtDtoArray);
        entityObj.put(entityTypeObj);

        try {
            restClient.post(etypeAPI, entityTypeObj.toString());
            int httpCode = restClient.getResponseCode();
            logger.info(String.format("EntityType POST Status Code: %d", httpCode));

            if (httpCode != 200) {
                logger.fine(String.format("EntityTypeObj: \n%s", entityTypeObj.toString()));
                logger.info(String.format("EntityType POST Body: %s", restClient.getResponseBody()));
            } else {
                JSONObject retObject = new JSONObject(restClient.getResponseBody());
                uuid = retObject.getString("uuid");
                logger.info(String.format("EntityType uuid: %s", uuid));
            }
        } catch (Exception ex) {
            logger.warning("EntityType " + entityTypeName + " failed to create. Exception: " + ex.getMessage());
            logger.log(Level.FINE, ex.getMessage(), ex);
        }

        return uuid;
    }

    private static int createDimensionsV2() {
        int dimAdded = 0;
        int dimRegistered = 0;
        int batchCount = 0;
        JSONArray dimensionObj = null;
        Set<String> tagList = tc.getTagList();
        int totalCountInCache = tagList.size();
        int totalCount = totalCountInCache;
        int retval = 0;
        String uuid = null;

        /* Create entity types */
        /*
         * for (int i = 0; i < deviceTypes.length(); i++) {
         * JSONObject deviceType = deviceTypes.getJSONObject(i);
         * String entityTypeName = deviceType.getString("type");
         * 
         * logger.info(String.format("Process device type: %s", entityTypeName));
         * 
         * String etypeAPI = "/api/v2/deviceTypes";
         * }
         * 
         * logger.info(String.format("Create Dimension Data: Tagpath InCache:%d",
         * totalCountInCache));
         */

        boolean done = true;
        List<String> doneTags = new ArrayList<String>();
        Iterator<String> it = tagList.iterator();
        while (it.hasNext()) {
            if (batchCount == 0) {
                dimensionObj = new JSONArray();
                batchCount = 1;
            }

            String id = it.next();
            TagData td = tc.get(id);
            if (td == null) {
                totalCount = totalCount - 1;
                continue;
            }

            String deviceId = td.getDeviceId();
            String deviceType = td.getDeviceType();
            if (uuid == null) {
                uuid = td.getDeviceTypeUUID();
            }
            if (uuid == null || uuid.length() == 0) {
                uuid = createV2DeviceType(deviceType);
                if (!uuid.equals("")) {
                    /* set uuid in tag data */
                    td.setDeviceTypeUUID(uuid);
                    tc.update(id, td);
                }
            }
            String dimensionsString = td.getDimensions();
            JSONObject dimensionsJson = new JSONObject(dimensionsString);
            String tagpath = td.getTagPath();
            int dimensionStatus = td.getDimensionStatus();
            String dimAPI = "/api/v2/core/deviceTypes/" + uuid + "/devices/dimensions";

            /* create device */
            if (td.getDeviceStatus() == 0) {
                String deviceAPI = "/api/v2/deviceTypes/" + uuid + "/devices";
                JSONArray deviceObj = new JSONArray();
                JSONObject devItem = new JSONObject();
                devItem.put("name", deviceId);
                devItem.put("alias", "null");
                devItem.put("authToken", devToken);
                deviceObj.put(devItem);

                try {
                    logger.fine("V2 Device Object: " + deviceObj.toString());
                    restClient.post(deviceAPI, deviceObj.toString());
                    logger.info(String.format("V2 Device create Status Code: %d", restClient.getResponseCode()));
                    if (restClient.getResponseCode() == 200) {
                        /*
                         * Device linked to the tag was created.
                         * Update DeviceStatus in cache for the tag
                         */
                        td.setDeviceStatus(1);
                        tc.update(id, td);
                    } else {
                        logger.info(String.format("EntityType POST Body: %s", restClient.getResponseBody()));
                    }
                } catch (Exception ex) {
                    logger.info("V2 Device Create Exception message: " + ex.getMessage());
                    logger.log(Level.FINE, ex.getMessage(), ex);
                }
            }

            if (dimensionStatus == 0) {
                logger.info(
                        String.format("Add dimension: Type:%s TypeUUID:%s DIM:%s", deviceType, uuid, dimensionsString));

                Iterator<String> keys = dimensionsJson.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String val = dimensionsJson.getString(key);
                    dimensionObj.put(createDimItem(deviceId, key.toUpperCase(), "LITERAL", val));
                    batchCount += 1;
                }
                td.setDimensionStatus(1);
                tc.update(id, td);
                doneTags.add(id);
                dimAdded += 1;
            } else {
                logger.fine("Dimension was already added: tagpath: " + tagpath + "    Dimension ID: " + deviceId);
                dimRegistered += 1;
            }

            if (dimensionStatus == 0) {
                for (int retry = 0; retry < 5; retry++) {
                    done = true;
                    if (batchCount == 1)
                        break;
                    try {
                        logger.fine("DIMENSION Object: " + dimensionObj.toString());
                        restClient.post(dimAPI, dimensionObj.toString());
                        logger.info(String.format("Dimension POST Status Code: %d", restClient.getResponseCode()));
                        logger.fine(String.format("Dimension POST Status Body: %s", restClient.getResponseBody()));
                    } catch (Exception ex) {
                        logger.info("Exception message: " + ex.getMessage());
                        logger.log(Level.FINE, ex.getMessage(), ex);
                        done = false;
                    }
                    if (done)
                        break;
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }
                    logger.info(String.format("Retry REST call: retry count=%d", retry));
                }
                batchCount = 0;
            }
            if (done == false)
                break;
            totalCount = totalCount - 1;
        }

        if (done == false) {
            // cycle didn't complete successfully, reset dimension state so that these can
            // be retried in the next cycle
            logger.info(
                    "Could not register dimensions of all tags in the cycle. Backing out status of unregistered tags.");
            Iterator<String> doneTagList = doneTags.iterator();
            while (doneTagList.hasNext()) {
                String id = doneTagList.next();
                TagData td = tc.get(id);
                td.setDimensionStatus(0);
                dimRegistered -= 1;
            }
        } else {
            retval = 1;
        }

        logger.info(String.format("Total=%d New=%d AlreadyRegistered=%d", totalCountInCache, dimAdded, dimRegistered));
        return retval;
    }

    private static JSONObject createDimItem(String id, String name, String type, String devToken) {
        JSONObject dimItem = new JSONObject();
        dimItem.put("id", id);
        dimItem.put("name", name);
        dimItem.put("type", type);
        dimItem.put("devToken", devToken);
        return dimItem;
    }

    private static JSONObject createDataDtoObjectV2(String dtoName, String evtName, String colType, String dtoType) {
        JSONObject dtoObj = new JSONObject();
        dtoObj.put("name", dtoName);
        dtoObj.put("eventName", evtName);
        dtoObj.put("columnType", colType);
        dtoObj.put("type", dtoType);
        dtoObj.put("transient", false);
        return dtoObj;
    }

    private static JSONObject createEventDtoObjectV2(String evtName, String evtTS) {
        JSONObject evtObj = new JSONObject();
        evtObj.put("name", evtName);
        evtObj.put("metricTimestampColumn", evtTS);
        return evtObj;
    }

    /* Thread to create dimensions */
    private static void startDimensionThread() {
        Runnable thread = new Runnable() {
            public void run() {
                while (true) {
                    int status;
                    status = createDimensionsV2();
                    if (status == 1) {
                        break;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                    }
                }
            }
        };
        logger.info("Starting Dimensions thread ...");
        new Thread(thread).start();
        logger.info("Dimension creation thread is started");
        return;
    }
}
