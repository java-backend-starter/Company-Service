package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetTypeDto {
    private String superCode;
    private String type;
    private int usefulYear;
    private String depreciationMethod;
}
