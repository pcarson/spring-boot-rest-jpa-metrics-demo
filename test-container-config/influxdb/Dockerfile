FROM influxdb:1.8

# WE have to overwrite the existing conf to enable http auth, which is false by default ...
COPY test-container-config/influxdb/setup/config.yml /docker-entrypoint-initdb.d/etc/influxdb2/config.yml

# create a retention policy directly
ADD test-container-config/influxdb/setup/influx-db-setup.sh /docker-entrypoint-initdb.d/zzinflux-db-setup.sh
