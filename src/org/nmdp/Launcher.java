package org.nmdp;


import org.nmdp.config.Configuration;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.scheduler.Scheduler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Launcher {
    private static Scheduler scheduler;

    public static void main(String[] args) {
        try {
            Configuration.loadSetting();
        } catch (FileNotFoundException e) {
            System.out.println("config file is missing. program stopped");
        }

//        //set up database
//        DatabaseUtil.connectDatabase();
//        DatabaseUtil.createSeqTable();
//        DatabaseUtil.creatExonTable();
//
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
//
//        //clean up connection to database
//        DatabaseUtil.cleanUp();

    }



}
