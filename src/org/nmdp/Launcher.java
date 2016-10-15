package org.nmdp;


import org.apache.commons.io.FilenameUtils;
import org.nmdp.HLAGene.HLAGene;
import org.nmdp.alignment.AlignmentController;
import org.nmdp.config.Configuration;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.parseExon.ParseExon;
import org.nmdp.scheduler.Scheduler;
import org.nmdp.scheduler.Task;
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
    private static final String GENE_TYPE = "-g";
    private static HLAGene geneType;
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

        if (paranMpa.get(INPUT).contains("hml") && paranMpa.get(INPUT).contains("xml")) {
            try {
                scheduler.start(INPUT, Configuration.mode,Configuration.expand);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }else if(paranMpa.get(INPUT).contains("fasta") && geneType != null){
            //process fasta file
            AlignmentController ac = new AlignmentController(scheduler);
            ParseExon pe = new ParseExon();

            String fileName = FilenameUtils.removeExtension(paranMpa.get(INPUT));
            Task task = new Task(geneType, fileName);
            ac.process(task);
            pe.process(task);

        }else{
            System.out.println("parameter format is not right. program stopped");
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

        //get gene type
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(GENE_TYPE)) {
                paranMpa.put(GENE_TYPE, args[i++]);
            }
        }
        if(paranMpa.containsKey(GENE_TYPE)){
            int index = Integer.parseInt(paranMpa.get(GENE_TYPE));
            geneType = HLAGene.values()[index];
        }
    }

}
