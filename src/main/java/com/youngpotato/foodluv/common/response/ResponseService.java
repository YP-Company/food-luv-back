package com.youngpotato.foodluv.common.response;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    public <T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse<T> singleResponse = SingleResponse.<T>builder().data(data).build();
        setSuccessResponse(singleResponse);

        return singleResponse;
    }

    public <T> ListResponse<T> getListResponse(List<T> dataList) {
        ListResponse<T> listResponse = ListResponse.<T>builder().dataList(dataList).build();
        setSuccessResponse(listResponse);

        return listResponse;
    }

    /**
     * 성공 상태와 메시지를 설정하는 메서드
     */
    private void setSuccessResponse(CommonResponse response) {
        response.setCode(0);
        response.setMessage("SUCCESS");
    }
}
