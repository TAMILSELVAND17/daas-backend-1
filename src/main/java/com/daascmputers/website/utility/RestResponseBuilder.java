package com.daascmputers.website.utility;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RestResponseBuilder {

    public <T> ResponseEntity<ResponseStructure<T>> success(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(ResponseStructure.<T>builder()
                        .status(status.value())
                        .message(message)
                        .data(data)
                        .build());
    }

    public <T> ResponseEntity<ErrorStructure<T>> error(HttpStatus status, String message, T rootCause) {
        return ResponseEntity.status(status)
                .body(ErrorStructure.<T>builder()
                        .status(status.value())
                        .message(message)
                        .rootCause(rootCause)
                        .build());
    }

    public <T> ResponseEntity<PageStructure<T>> success(HttpStatus status, String message, T data, int page, int totalPages, int size) {
        PageStructure<T> pageStructure = new PageStructure<T>();
        pageStructure.setStatus(status.value());
        pageStructure.setMessage(message);
        pageStructure.setData(data);
        pageStructure.setPage(page);
        pageStructure.setTotalPage(totalPages);
        pageStructure.setSize(size);

        return ResponseEntity.status(status)
                .body(pageStructure);

    }
}

