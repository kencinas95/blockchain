package ar.com.kaiju.blockchain.utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;

import com.google.common.collect.Lists;
import com.google.common.io.CharSource;

import lombok.SneakyThrows;

public final class CSVUtils 
{
    @SneakyThrows
    public static List<Map<String, String>> getRecordsFromFile(String rawCsv)
    {            
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader()
            .parse(CharSource.wrap(rawCsv).openStream());        
        return Lists.newArrayList(records).stream()
            .map(CSVRecord::toMap)
            .collect(Collectors.toList());
    }

    public static String readFile(DataBuffer buffer)
    {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        DataBufferUtils.release(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
