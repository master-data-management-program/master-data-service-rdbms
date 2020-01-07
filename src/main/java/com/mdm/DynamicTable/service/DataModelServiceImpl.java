package com.mdm.DynamicTable.service;

import com.mdm.DynamicTable.dao.DataModelDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DataModelServiceImpl implements DataModelService {

  @Autowired
  private DataModelDAO dataModelDAO;

  @Override
  @Transactional
  public void createTable(String createTableSqlString) {
    dataModelDAO.executeCreateTable(createTableSqlString);
  }
}
