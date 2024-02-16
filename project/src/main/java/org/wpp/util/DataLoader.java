package org.wpp.util;

import java.io.StringReader;
import java.util.List;

import com.opencsv.CSVReader;

public class DataLoader
{
    public static List<String[]> loadCSV(String filepath) throws Exception
    {
        String data = Util.readFileAsString(filepath);
        try (CSVReader reader = new CSVReader(new StringReader(data))) {
            return reader.readAll();
        }
    }
}
