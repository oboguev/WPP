package org.wpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wpp.util.DataLoader;
import org.wpp.util.Util;

public class WPP
{
    private List<String[]> wpp;
    private Map<String, List<String[]>> country2rows = new HashMap<>();

    private final int ixCountry;
    private final int ixYear;
    private final int ixPopulationMidyear;
    private final int ixCrudeBirthRate;
    private final int ixCrudeDeathRate;

    public WPP(String filepath) throws Exception
    {
        wpp = DataLoader.loadCSV(filepath);
        ixCountry = ixColumn("Region, subregion, country or area *");
        ixYear = ixColumn("Year");
        ixPopulationMidyear = ixColumn("Total Population, as of 1 July (thousands)");
        ixCrudeBirthRate = ixColumn("Crude Birth Rate (births per 1,000 population)");
        ixCrudeDeathRate = ixColumn("Crude Death Rate (deaths per 1,000 population)");
        
        Map<String,String> mcheck = new HashMap<>();
        
        for (int ix = 1; ix < wpp.size(); ix++)
        {
            String[] row = wpp.get(ix);
            String country = Util.despace(row[ixCountry]).trim();

            if (country.equalsIgnoreCase("Latin America and the Caribbean"))
                country = "Latin America and the Caribbean";
        
            String lc = country.toLowerCase();
            String xc = mcheck.get(lc);
            if (xc == null)
            {
                mcheck.put(lc, country);
            }
            else if (!xc.equals(country))
            {
                // throw new Exception("Ambiguous country record");
            }
            
            List<String[]> rows = country2rows.get(country);
            if (rows == null)
            {
                rows = new ArrayList<>();
                country2rows.put(country, rows);
            }
            
            rows.add(row);
        }

        // Util.noop();
    }

    public void birthRate(String[][] countries)
    {
        Util.out("");
        Util.out("Crude birth rate (per 1000)");
        Util.out("");

        rate(countries, ixCrudeBirthRate);
    }

    public void deathRate(String[][] countries)
    {
        Util.out("");
        Util.out("Crude death rate (per 1000)");
        Util.out("");

        rate(countries, ixCrudeDeathRate);
    }

    private static final int rateStartYear = 1950;
    private static final int rateEndYear = 2019;
    private static final int rateStepYears = 5;

    private void rate(String[][] countries, int ixRate)
    {
        rateHeadRow();
        
        for (String[] cc : countries)
        {
            rate(cc[0], cc[1], ixRate);
        }
    }

    private void rateHeadRow()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append("страна").append("\"");
        
        int bucket = 0;
        int yy1 = 0;

        for (int yy = rateStartYear; yy <= rateEndYear; yy++)
        {
            if (bucket == 0)
            {
                yy1 = yy;
            }

            bucket++;
            
            if (bucket == rateStepYears || yy == rateEndYear)
            {
                int yy2 = yy;
                if (yy1/100 == yy2/100)
                    yy2 = (yy2 % 100);
                String syy2 = String.format(yy2 <= 99 ? "%02d" : "%04d", yy2);
                sb.append(",\"").append(yy1).append("-").append(syy2).append("\"");
                bucket = 0;
            }
        }

        Util.out(sb.toString());
    }

    private void rate(String countryDisplayName, String country, int ixRate)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(countryDisplayName).append("\"");
        Map<Integer, String[]> yy2row = forCountryByYear(country);
        
        int bucket = 0;
        double sum = 0;

        for (int yy = rateStartYear; yy <= rateEndYear; yy++)
        {
            String[] row = yy2row.get(yy);
            if (row == null)
                throw new RuntimeException("Missing data for country: " + country + ", year: " + yy);
            double d = asDouble(row[ixRate]);
            
            sum += d;
            bucket++;
            
            if (bucket == rateStepYears || yy == rateEndYear)
            {
                /* flush */
                String s = String.format("%.3f", sum / bucket);
                sb.append(",").append(s);
                bucket = 0;
                sum = 0;
            }
        }
        
        Util.out(sb.toString());
    }

    public void printMidyearPopulation(String country)
    {
        Util.out("Midyear Population of " + country);
        List<String[]> rows = forCountry(country);
        for (String[] row : rows)
        {
            Util.out(row[ixYear] + "," + asLong(row[ixPopulationMidyear]));
        }
    }

    public void listCountries()
    {
        Set<String> xs = new HashSet<>();

        for (int k = 1; k < wpp.size(); k++)
        {
            String[] row = wpp.get(k);
            xs.add(row[ixCountry].trim());
        }

        List<String> list = new ArrayList<>(xs);
        Collections.sort(list);

        Util.out("Defined countries: ");
        Util.out("");

        for (String s : list)
            Util.out(s);
    }
    
    private Map<Integer, String[]> forCountryByYear(String country)
    {
        Map<Integer, String[]> m = new HashMap<>();
        List<String[]> rows = forCountry(country);
        for (String[] row : rows)
        {
            int yy = Integer.parseInt(row[ixYear].trim());
            m.put(yy, row);
        }

        return m;
    }

    private List<String[]> forCountry(String country)
    {
        List<String[]> rows = country2rows.get(country);
        if (rows == null)
            throw new RuntimeException("Country not found: " + country);
        return rows;
    }

    private int ixColumn(String cname) throws Exception
    {
        String[] titles = wpp.get(0);
        for (int ix = 0; ix < titles.length; ix++)
        {
            if (titles[ix].trim().equalsIgnoreCase(cname))
                return ix;
        }

        throw new Exception("Cannot find column: [" + cname + "]");
    }

    private long asLong(String v)
    {
        v = v.trim().replace(" ", "");
        return Long.parseLong(v);
    }
    
    private double asDouble(String v)
    {
        v = v.replace(" ", "").replace(",", "");
        return Double.parseDouble(v);
    }
}
