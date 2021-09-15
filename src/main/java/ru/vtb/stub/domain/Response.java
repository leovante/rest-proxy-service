package ru.vtb.stub.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    @NotNull
    @Min(200)
    @Max(399)
    private Integer status;
    private List<Header> headers;
    private JsonNode body;
}
