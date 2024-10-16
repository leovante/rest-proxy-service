package ru.vtb.stub.service;

import ru.vtb.stub.domain.Request;
import ru.vtb.stub.domain.StubData;
import ru.vtb.stub.dto.GetDataBaseRequest;

import java.util.List;

public interface RequestService {

    void putData(StubData data);

    List<StubData> getData(GetDataBaseRequest key);

    StubData getDataByPkAndMarkUsed(GetDataBaseRequest key);

    StubData[] getTeamData(String team);

    void removeData(GetDataBaseRequest key);

    void removeTeamData(String team);

    List<Request> getHistory(GetDataBaseRequest key);

}
