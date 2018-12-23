/*取数据库中所有表名*/
select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';