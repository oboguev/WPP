package org.wpp;

import org.wpp.util.Util;

public class Main
{
    private static final String DataDir = "F:\\WINAPPS\\WPP\\data";
    private static final String DataFileName = "WPP2022_GEN_F01_DEMOGRAPHIC_INDICATORS_COMPACT_REV1-Values.csv";
    
    private static final String[][] countries = {{ "Венесуэла", "Venezuela (Bolivarian Republic of)" },
                                                 { "Коста-Рика", "Costa Rica" },
                                                 { "Гватемала", "Guatemala" },
                                                 { "Колумбия", "Colombia" },
                                                 { "Эквадор", "Ecuador" },
                                                 { "Бразилия", "Brazil" },
                                                 { "Парагвай", "Paraguay" },
                                                 { "Перу", "Peru" },
                                                 { "Боливия", "Bolivia (Plurinational State of)" },
                                                 { "Сальвадор", "El Salvador" },
                                                 { "Чили", "Chile" },
                                                 { "Аргентина", "Argentina" },
                                                 { "Мексика", "Mexico" }};
    
    public static void main(String[] args)
    {
        try 
        {
            WPP wpp = new WPP(Util.dirFile(DataDir, DataFileName));
            
            // wpp.printMidyearPopulation("Mexico");
            // wpp.listCountries();

            wpp.birthRate(countries);
            wpp.deathRate(countries);
            
        }
        catch (Exception ex)
        {
            Util.err("Exception: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        
        Util.out("");
        Util.out("*** Completed.");
    }
}
