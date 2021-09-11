package ru.vtb.stub.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static ru.vtb.stub.data.ResponseData.validateData;

@Slf4j
@Service
public class AdminValidateService {

    @Value("${prefix.header}")
    private  String headerPrefix;
    @Value("${prefix.query}")
    private  String queryPrefix;

    public Object getValidateData(String key) {
        var data = validateData.get(key);
        if (data != null)
            log.debug("Admin validate service. Get validate data: {}", data);
        else
            log.debug("Admin validate service. No validate data for key: {}", key);
        return data;
    }

    public void putValidateData(String key, Map<String, String> headers, String body) {
        if (!validateData.containsKey(key)) validateData.put(key, new HashMap<>());
        var data = validateData.get(key);

        headers.forEach((k, v) -> {
            if (k.startsWith(queryPrefix)) {
                log.debug("Admin validate service. Set expected query: {} --> {}", k, v);
                data.put(k, v);
            }
        });
        headers.forEach((k, v) -> {
            if (k.startsWith(headerPrefix)) {
                log.debug("Admin validate service. Set expected header: {} --> {}", k, v);
                data.put(k, v);
            }
        });
        if (body != null) {
            try {
                new JSONTokener(body);
            } catch (JSONException e) {
                log.debug("Admin validate service. Body is not a JSON: {}", body);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            log.debug("Admin validate service. Set expected json schema: {}", body);
            data.put("body", body);
        }

        if (data.isEmpty()) {
            log.debug("Admin validate service. Data does not contain body and not headers starts with 'query-' or 'header-'");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Validate data does not contain body and not headers starts with 'query-' or 'header-'");
        }
    }

    public Object removeValidateData(String key) {
        var data = validateData.remove(key);
        if (data != null)
            log.debug("Admin validate service. Delete validate data: {}", data);
        else
            log.debug("Admin validate service. No validate data for key: {}", key);
        return data;
    }
}
