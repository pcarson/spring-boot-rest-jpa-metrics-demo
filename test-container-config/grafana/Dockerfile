FROM grafana/grafana:latest

# create a datasource and default dashboard
COPY test-container-config/grafana/provisioning/demo-dashboard-config.yml /etc/grafana/provisioning/dashboards/demo-dashboard.yml
COPY test-container-config/grafana/provisioning/influx-datasource-config.yml /etc/grafana/provisioning/datasources/influx-datasource.yml
COPY test-container-config/grafana/java-metrics-dashboard.json /etc/grafana/dashboards/java-metrics-dashboard.json

