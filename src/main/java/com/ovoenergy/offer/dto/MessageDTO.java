package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@ApiModel(value = "Message", description = "Message related to operation status")
@Data
@AllArgsConstructor
@JsonSerialize
public class MessageDTO {

    @ApiModelProperty(name = "message", required = true)
    private String message;

}
