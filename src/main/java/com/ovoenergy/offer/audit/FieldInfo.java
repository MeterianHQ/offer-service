package com.ovoenergy.offer.audit;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FieldInfo {
    private final Integer fieldIndex;
    private final String fieldName;
}
