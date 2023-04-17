# influx -username $INFLUXDB_ADMIN_USER -password $INFLUXDB_ADMIN_PASSWORD -execute 'GRANT ALL PRIVILEGES TO influx'
influx -username $INFLUXDB_ADMIN_USER -password $INFLUXDB_ADMIN_PASSWORD -execute 'CREATE DATATBASE springboot'
influx -username $INFLUXDB_ADMIN_USER -password $INFLUXDB_ADMIN_PASSWORD -execute 'USE springboot';
influx -username $INFLUXDB_ADMIN_USER -password $INFLUXDB_ADMIN_PASSWORD -execute 'CREATE RETENTION POLICY springbootretention ON springboot DURATION 14d REPLICATION 1';
