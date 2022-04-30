package ru.vtb.stub.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.vtb.stub.domain.Header;
import ru.vtb.stub.domain.Request;
import ru.vtb.stub.domain.Response;
import ru.vtb.stub.domain.StubData;
import ru.vtb.stub.filter.RequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.http.ResponseEntity.BodyBuilder;
import static org.springframework.http.ResponseEntity.status;
import static ru.vtb.stub.data.DataMap.*;

@Slf4j
@Service
public class ResponseService {

    @SneakyThrows
    public ResponseEntity<Object> sendResponse(String rpsRequest, String key, RequestWrapper servletRequest) {

        StubData data = key.endsWith("$") ? dataByRegexMap.get(key) : dataByKeyMap.get(key);

        Request request = Request.builder()
                .date(LocalDateTime.now())
                .path(rpsRequest.split(":")[0])
                .method(rpsRequest.split(":")[1])
                .headers(getHeaders(servletRequest))
                .params(getQueryParams(servletRequest.getQueryString()))
                .body(servletRequest.getReader()
                        .lines()
                        .collect(Collectors.joining(System.lineSeparator())))
                .build();

        Integer wait = data.getWait();
        if (wait != null) {
            log.info("Request to: {} --> Waiting {} ms...", key, wait);
            Thread.sleep(wait);
        }

        Response actualData = getActualData(data);
        Object actualBody = getActualBody(actualData);
        int status = actualData.getStatus();
        List<Header> headers = actualData.getHeaders();

        BodyBuilder response = status(status);

        List<Request> history = requestMap.computeIfAbsent(key, k -> new ArrayList<>());

        if (status >= BAD_REQUEST.value()) {
            updateHistory(history, request, key);
            if (actualBody != null) {
                log.info("Request to: {} --> Response with error: {}, body: {}", key, status, actualBody);
                return status(status).body(actualBody);
            } else {
                log.info("Request to: {} --> Response with error: {}", key, status);
            }
            throw new ResponseStatusException(valueOf(status), "Test error message");
        }

        if (!ObjectUtils.isEmpty(headers)) {
            headers.forEach(h -> response.header(h.getName(), h.getValue()));
        }

        log.info("Request to: {} --> {}", key, actualData);
        updateHistory(history, request, key);
        return actualBody != null ? response.body(actualBody) : response.build();
    }

    private Map<String, String> getHeaders(RequestWrapper request) {
        Enumeration<String> headers = request.getHeaderNames();
        return ObjectUtils.isEmpty(headers)
                ? null
                : Collections.list(headers)
                        .stream()
                        .collect(Collectors.toMap(h -> h, request::getHeader));
    }

    private Map<String, String> getQueryParams(String queryString) {
        String[] params = queryString.split("&");
        return params.length == 0
                ? null
                : Arrays.stream(params)
                        .map(p -> p.split("="))
                        .skip(2)
                        .collect(Collectors.toMap(p -> p[0], p -> p.length > 1 ? p[1] : ""));
    }

    private Response getActualData(StubData data) {
        List<Response> responseList = data.getResponses();

        // Если одновременно заполнены поля responses и response, то приоритет у responses
        if (!ObjectUtils.isEmpty(responseList)) {
            int count = data.getCount();
            // При повторном запросе будет отдан следующий элемент responseList
            int next = count + 1;
            data.setCount((next >= responseList.size()) ? 0 : next);
            return responseList.get(count);
        } else {
            return data.getResponse();
        }
    }

    private Object getActualBody(Response actualData) {
        JsonNode jsonBody = actualData.getBody();
        String stringBody = actualData.getStringBody();

        // Если одновременно заполнены поля body (json) и stringBody, то приоритет у body
        if (jsonBody != null) {
            return jsonBody;
        } else if (stringBody != null) {
            return stringBody.getBytes(StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private void updateHistory(List<Request> history, Request request, String key) {
        history.add(request);
        log.debug("Updated history: {} --> {}", key, requestMap.get(key));
    }
}
