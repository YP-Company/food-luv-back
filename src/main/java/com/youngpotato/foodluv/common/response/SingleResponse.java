package com.youngpotato.foodluv.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleResponse<T> extends CommonResponse {
    private T data;
}
