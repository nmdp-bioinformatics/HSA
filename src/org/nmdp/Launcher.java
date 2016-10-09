package org.nmdp;


import org.nmdp.config.Configuration;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.scheduler.Scheduler;
import org.nmdp.util.FileSystem;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Launcher {
    private static Scheduler scheduler;
    private static final String INPUT = "-i";
    private static final String OUTPUT = "-o";
    private static HashMap<String, String> paranMpa = new HashMap<>();

    public static void main(String[] args) {
        try{
            getParameters(args);
        }catch (IndexOutOfBoundsException e){

            System.out.println("parameter is missing. program stopped");
            HSALogger.log("parameter is missing. program stopped");
        }


        try {

            FileSystem.ROOT = paranMpa.get(OUTPUT);
            Configuration.loadSetting();
        } catch (FileNotFoundException e) {
            System.out.println("config file is missing. program stopped");
            HSALogger.log("config file is missing. program stopped");
        }

        //set up database
        DatabaseUtil.connectDatabase();
        DatabaseUtil.createSeqTable();
        DatabaseUtil.creatExonTable();
        DatabaseUtil.createGfeTable();

        scheduler = new Scheduler();

        try {
            scheduler.start(Configuration.mode,Configuration.expand);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        //clean up connection to database
        DatabaseUtil.cleanUp();

    }

    private static void getParameters(String[] args) {
        //get input
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(INPUT)) {
                paranMpa.put(INPUT, args[i++]);
            }
        }
        //get output
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(OUTPUT)) {
                paranMpa.put(OUTPUT, args[i++]);
            }
        }
    }
    
}
