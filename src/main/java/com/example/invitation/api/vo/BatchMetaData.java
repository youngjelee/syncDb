package com.example.invitation.api.vo;


import lombok.Data;

@Data
public class BatchMetaData {

    private String table_name;
    private String result_txt;
    private String fail_reason;
}
