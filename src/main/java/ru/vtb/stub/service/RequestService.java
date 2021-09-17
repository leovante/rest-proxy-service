package ru.vtb.stub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vtb.stub.domain.Request;
import ru.vtb.stub.domain.StubData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.vtb.stub.data.DataMap.dataMap;
import static ru.vtb.stub.data.DataMap.requestMap;

@Slf4j
@Service
public class RequestService {

    public void putData(StubData data) {
        String key = "/" + data.getTeam() + data.getPath() + ":" + data.getMethod();
        log.debug("Put data: {} --> {}", key, data);
        dataMap.put(key, data);
    }

    public StubData getDataByKey(String key) {
        var data = dataMap.get(key);
        log.debug("Get data: {} --> {}", key, data);
        return data;
    }

    public StubData[] getTeamData(String team) {
        var data = dataMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith("/" + team))
                .map(Map.Entry::getValue)
                .toArray(StubData[]::new);
        log.debug("Get data: {} --> {}", team, data);
        return data;
    }

    public StubData removeDataByKey(String key) {
        var requests = requestMap.remove(key);
        log.debug("Deleted history: {} --> {}", key, requests);
        var data = dataMap.remove(key);
        log.debug("Deleted data: {} --> {}", key, data);
        return data;
    }

    public StubData[] removeTeamData(String team) {
        var requests = requestMap.keySet().stream()
                .filter(k -> k.startsWith("/" + team))
                .collect(Collectors.toList());
        if (!requests.isEmpty()) {
            requests.forEach(k -> requestMap.remove(k));
            log.debug("Deleted all history for: {}", team);
        }

        var keys = dataMap.keySet().stream()
                .filter(k -> k.startsWith("/" + team))
                .collect(Collectors.toList());
        if (keys.isEmpty()) return null;

        log.debug("Start deleting team '{}' data...", team);
        List<StubData> removed = new ArrayList<>();
        keys.forEach(k -> {
            var data = dataMap.remove(k);
            removed.add(data);
            log.debug("Deleted data: {} --> {}", k, data);
        });
        return removed.toArray(StubData[]::new);
    }

    public List<Request> getHistoryByKey(String key) {
        var requests = requestMap.get(key);
        log.debug("Get history: {} --> {}", key, requests);
        return requests;
    }
}
