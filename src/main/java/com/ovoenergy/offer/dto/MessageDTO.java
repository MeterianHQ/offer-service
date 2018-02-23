package com.ovoenergy.offer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "Message", description = "Message related to operation status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    @ApiModelProperty(name = "message", required = true)
    private String message;

}
