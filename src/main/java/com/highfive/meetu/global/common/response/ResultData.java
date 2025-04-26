package com.highfive.meetu.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResultData<T> {
    private int count;      // 데이터 개수 (예: 전체 목록 수, 단건이면 1, 실패 시 0)
    private String msg;     // 응답 메시지 ("success", "fail" 또는 상세 메시지)
    private T data;         // 응답 데이터 본문

    // 기본 생성자
    public static<T> ResultData<T> of(int count, String msg, T data) {
        return new ResultData<T>(count, msg, data);
    }

    /**
     * 성공 응답용 헬퍼
     * 사용 예시: ResultData.success(list.size(), resumeList) 또는 ResultData.success(1, resume)
     */
    public static<T> ResultData<T> success(int count, T data) {
        return of(count,  "success", data);
    }

    /**
     * 실패 응답용 헬퍼
     * 사용 예시: ResultData.fail()
     */
    public static<T> ResultData<T> fail(){
        return of(0, "fail", null);
    }

    /**
     * 실패 응답용 헬퍼
     * 사용 예시: ResultData.fail("메시지")
     */
    public static<T> ResultData<T> fail(String msg) {
        return of(0, msg, null);
    }

    // 공통 에러코드를 받아서 실패 응답 생성
    public static <T> ResultData<T> fail(ErrorCode errorCode) {
        return new ResultData<>(0, errorCode.getMessage(), null);
    }
}
