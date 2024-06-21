package com.example.invitation.api.dao;

import com.example.invitation.api.vo.ColumnMetadata;
import com.example.invitation.api.vo.ESFRSLN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestDao {

    List<ESFRSLN> selectESFRSLN() ;

    int getRowCountByTblName(@Param("tableName") String tableName) ;

    List<ColumnMetadata> getColumnNameDataType (@Param("schema") String schema ,
                                                @Param("tableName") String tableName  ) ;

}
