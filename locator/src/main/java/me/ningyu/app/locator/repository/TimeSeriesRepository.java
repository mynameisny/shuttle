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

    @Value("${spring.influx.batchSize:5000}")
    private int batchSize;

    @Value("${spring.influx.flushInterval:1000}")
    private int flushInterval;

    @Value("${spring.influx.bufferLimit:10000}")
    private int bufferLimit;

    @Value("${spring.influx.jitterInterval:1000}")
    private int jitterInterval;

    @Value("${spring.influx.retryInterval:5000}")
    private int retryInterval;


    public TimeSeriesRepository(InfluxDBClient influxDBClient)
    {
        this.influxDBClient = influxDBClient;
    }

    public void save(String measurement, Map<String, Object> fields)
    {
        WriteOptions writeOptions = WriteOptions.builder().batchSize(batchSize).flushInterval(flushInterval).bufferLimit(bufferLimit).jitterInterval(jitterInterval).retryInterval(retryInterval).build();
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
