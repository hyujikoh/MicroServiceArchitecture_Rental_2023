package com.my.rental.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookCatalogChanged {
    private Long bookId;
    private String eventType;
}
