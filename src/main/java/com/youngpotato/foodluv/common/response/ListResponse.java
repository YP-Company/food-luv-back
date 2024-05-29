package com.youngpotato.foodluv.common.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ListResponse<T> extends CommonResponse {
    private List<T> dataList;
}
