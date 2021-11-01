/*取数据库中所有表名*/
select concat('tableNames.add("',table_name,'");') from information_schema.tables where table_schema='test' and table_type='BASE TABLE';

/**
取数据库中所有表名,并且判断有没有deleted字段
 */
SELECT
	concat( 'logicDeletionTableMap.add("', tbName, '" ,',
	( CASE column_name WHEN 'deleted' then 'true' ELSE 'false' END ),
	');' ) AS r

FROM
	(
	SELECT DISTINCT
		( a.TABLE_NAME ) AS tbName,
		b.*
	FROM
		information_schema.`COLUMNS` a
		LEFT JOIN ( SELECT * FROM information_schema.`COLUMNS` t WHERE t.TABLE_SCHEMA = 'test' AND t.COLUMN_NAME = 'deleted' ) b ON a.TABLE_NAME = b.table_name
	WHERE
	a.TABLE_SCHEMA = 'test'
	) table_name
