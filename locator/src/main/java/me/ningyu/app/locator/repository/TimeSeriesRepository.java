package me.ningyu.app.locator.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public class TimeSeriesRepository
{
    private final InfluxDBClient influxDBClient;

    @Value("${spring.influx.org:''}")
    private String org;

    @Value("${spring.influx.bucket:''}")
    private String bucket;


    public TimeSeriesRepository(InfluxDBClient influxDBClient)
    {
        this.influxDBClient = influxDBClient;
    }

    public void save(String measurement, Map<String, Object> fields)
    {
        WriteOptions writeOptions = WriteOptions.builder().batchSize(5000).flushInterval(1000).bufferLimit(10000).jitterInterval(1000).retryInterval(5000).build();
        try (WriteApi writeApi = influxDBClient.makeWriteApi(writeOptions))
        {
            Point point = Point.measurement(measurement).addFields(fields).time(Instant.now(), WritePrecision.NS);
            writeApi.writePoint(bucket, org, point);
        }
    }

    public List<FluxTable> findAll()
    {
        String flux = String.format("from(bucket: \"%s\") |> range(start: 0)", bucket);
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, org);
        return tables;
    }
}
