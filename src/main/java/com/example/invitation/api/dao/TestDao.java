package com.example.invitation.api.dao;

import com.example.invitation.api.vo.BatchMetaData;
import com.example.invitation.api.vo.ColumnMetadata;
import com.example.invitation.api.vo.ESFRSLN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestDao {

    List<ESFRSLN> selectESFRSLN() ;

    int getRowCountBySchemaTblName(@Param("schema_tableName") String tableName) ;

    List<ColumnMetadata> getColumnNameDataType (@Param("schema") String schema ,
                                                @Param("tableName") String tableName  ) ;

    void truncateTableBySchemaTblName(@Param("schema_tableName") String tableName) ;

    void insertBatchLog(BatchMetaData batchMetaData);

    void updateBatchLog (BatchMetaData batchMetaData) ;

}
