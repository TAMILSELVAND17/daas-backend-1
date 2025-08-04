package com.daascmputers.website.utility;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageStructure<T> extends ResponseStructure<T> {
    private int page;
    private int totalPage;
    private int size;

}
