package com.example.invitation.api.vo;

import lombok.Data;

@Data

public class ColumnMetadata {

    private final String column_name;
    private final String data_type_name;

    public ColumnMetadata(String column_name, String data_type_name) {
        this.column_name = column_name;
        this.data_type_name = data_type_name;
    }

}
