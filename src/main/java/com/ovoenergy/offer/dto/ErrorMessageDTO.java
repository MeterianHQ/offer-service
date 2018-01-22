package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "Error Message", description = "Error message representation in user friendly format with internal error code")
@Data
@AllArgsConstructor
@JsonSerialize
public class ErrorMessageDTO {

    @ApiModelProperty(name = "code", required = true)
    private String code;

    @ApiModelProperty(name = "message", required = true)
    private String message;

}
