package ru.vtb.stub.service.local;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ru.vtb.stub.domain.Request;
import ru.vtb.stub.domain.StubData;
import ru.vtb.stub.dto.GetDataBaseRequest;
import ru.vtb.stub.service.RequestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.vtb.stub.data.DataMap.*;

@Slf4j
@Requires(property = "rest-proxy-stub.storage-mode", value = "ram")
@Singleton
public class RequestServiceImpl implements RequestService {

    private static final String TEMPLATE = "--";

    public void putData(StubData data) {
        String key = "/" + data.getTeam() + data.getPath() + ":" + data.getMethod();

        log.debug("Put data: {} --> {}", key, data);

        if (key.contains(TEMPLATE)) {
            key = buildRegexKey(key);
            dataByRegexMap.put(key, data);
        } else {
            dataByKeyMap.put(key, data);
        }

        List<Request> requests = requestMap.remove(key);
        if (!CollectionUtils.isEmpty(requests)) {
            log.debug("Deleted history: {} --> {}", key, requests);
        }
    }

    public List<StubData> getData(GetDataBaseRequest req) {
        String key = "/" + req.getTeam() + req.getPath() + ":" + req.getMethod();

        StubData data = key.contains(TEMPLATE) ? dataByRegexMap.get(buildRegexKey(key)) : dataByKeyMap.get(key);
        log.debug("Get data: {} --> {}", key, data);
        return List.of(data);
    }

    @Override
    public StubData getDataByPkAndMarkUsed(GetDataBaseRequest key) {
        return null;
    }

    public StubData[] getTeamData(String team) {
        List<StubData> data = new ArrayList<>();
        Stream.of(getTeamValues(dataByKeyMap, team), getTeamValues(dataByRegexMap, team))
                .forEach(data::addAll);
        log.debug("Get data: {} --> {}", team, data);
        return data.toArray(StubData[]::new);
    }

    public void removeData(GetDataBaseRequest req) {
        String key = "/" + req.getTeam() + req.getPath() + ":" + req.getMethod();

        StubData data;
        if (key.contains(TEMPLATE)) {
            key = buildRegexKey(key);
            data = dataByRegexMap.remove(key);
        } else {
            data = dataByKeyMap.remove(key);
        }

        log.debug("Deleted data: {} --> {}", key, data);

        List<Request> requests = requestMap.remove(key);
        if (!CollectionUtils.isEmpty(requests)) {
            log.debug("Deleted history: {} --> {}", key, requests);
        }
    }

    public void removeTeamData(String team) {
        List<String> keys = getTeamKeys(dataByKeyMap, team);
        List<String> regexKeys = getTeamKeys(dataByRegexMap, team);
        if (!keys.isEmpty() || !regexKeys.isEmpty()) {
            log.debug("Start deleting team '{}' data...", team);
        }

        removeTeamValues(dataByKeyMap, keys);
        removeTeamValues(dataByRegexMap, regexKeys);

        List<String> requests = getTeamKeys(requestMap, team);
        if (!requests.isEmpty()) {
            requests.forEach(k -> requestMap.remove(k));
            log.debug("Deleted all history for: {}", team);
        }
    }

    public List<Request> getHistory(GetDataBaseRequest req) {
        String key = "/" + req.getTeam() + req.getPath() + ":" + req.getMethod();

        if (key.contains(TEMPLATE)) {
            key = buildRegexKey(key);
        }

        List<Request> requests = requestMap.get(key);
        log.debug("Get history: {} --> {}", key, requests);
        return CollectionUtils.isEmpty(requests) ? new ArrayList<>() : requests;
    }

    private List<String> getTeamKeys(Map<String, ?> map, String team) {
        return map.keySet().stream()
                .filter(k -> k.startsWith("/" + team))
                .collect(Collectors.toList());
    }

    private List<StubData> getTeamValues(Map<String, StubData> map, String team) {
        return map.entrySet().stream()
                .filter(e -> e.getKey().startsWith("/" + team))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void removeTeamValues(Map<String, StubData> map, List<String> keys) {
        keys.forEach(k -> {
            StubData data = map.remove(k);
            log.debug("Deleted data: {} --> {}", k, data);
        });
    }

    private String buildRegexKey(String key) {
        return key.replaceAll(TEMPLATE, "[a-zA-Z0-9.@%/_-]+")
                .replaceAll("/", "\\/") + "$";
    }

}
