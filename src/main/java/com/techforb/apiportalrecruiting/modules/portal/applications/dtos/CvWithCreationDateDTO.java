package com.techforb.apiportalrecruiting.modules.portal.applications.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
public class CvWithCreationDateDTO extends CvDTO{
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    private Boolean isLast;
    public CvWithCreationDateDTO(Long id,String cvUrl, String name,LocalDateTime creationDate,Boolean isLast){
        super(id,cvUrl,name);
        this.creationDate=creationDate;
        this.isLast=isLast;
    }
}
