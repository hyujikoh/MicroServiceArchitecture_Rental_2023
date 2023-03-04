package com.my.rental.adaptor;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;

// 메시지 발송을 위한 아웃바운드 어댑터 인터페이스
public interface RentalProducer {
    //도서 서비스의 도서 상태 변경
    void updateBookStatus(Long bookId, String bookStatus)
        throws ExecutionException, InterruptedException, JsonProcessingException;

    //사용자 서비스의 포인트 적입
    void savePoints(Long bookId, int pointPerBooks)
        throws ExecutionException, InterruptedException, JsonProcessingException;

    // 도서 카탈로그 서비스의 도서 상태 변경
    void updateBookCatalogStatus(Long bookId, String eventType)
        throws ExecutionException, InterruptedException, JsonProcessingException;
}
