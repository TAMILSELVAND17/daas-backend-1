package com.daascmputers.website.utility;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ErrorStructure<T> {
    private int status;
    private String message;
    private T rootCause;
}
