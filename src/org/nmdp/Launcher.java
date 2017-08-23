package org.nmdp;


import org.nmdp.HLAGene.HLAGene;
import org.nmdp.alignment.AlignmentController;
import org.nmdp.config.Configuration;
import org.nmdp.parseExon.ParseExon;
import org.nmdp.scheduler.Scheduler;
import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.HashMap;

public class Launcher {
    private static Scheduler scheduler;
    private static final String INPUT = "-i";
    private static final String OUTPUT = "-o";
    private static final String GENE_TYPE = "-g";
    private static final String CUSTOM_ALIGNMENT_FILE = "-c";
    private static HLAGene geneType;
    //The map that saves the argument and value pairs.
    private static HashMap<String, String> paranMpa = new HashMap<>();

    public static void main(String[] args) throws URISyntaxException {

        CodeSource codeSource = Launcher.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        //Set jar directory as root
        FileSystem.ROOT = jarDir;
        FileSystem.FOLDER = FileSystem.ROOT + FileSystem.FOLDER;
        System.out.println("Folder is " + FileSystem.FOLDER);
        try {
            getParameters(args);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("parameter is missing. program stopped");
            return;
        }

        if (paranMpa.containsKey(OUTPUT)) {
            Configuration.gfeLoadOutput = paranMpa.get(OUTPUT);
        }


        scheduler = new Scheduler();
        String input = paranMpa.get(INPUT);
        input = input.toLowerCase();

        if (input.contains("hml") || input.contains("xml")) {
            try {
                System.out.println("the input file is " + input.toString());
                scheduler.start(paranMpa.get(INPUT), Configuration.mode, Configuration.expand);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else if (input.contains("fasta") && geneType != null) {

            //process fasta file, each fasta contains one genetype
            File fastaFile = new File(input);
            AlignmentController ac = new AlignmentController(fastaFile);
            ParseExon pe = new ParseExon();

            String fileName = FileSystem.getFileName(fastaFile);
            Task task = new Task(geneType, fileName);
            ac.process(task);
            pe.processFasta(task);

        } else {
            System.out.println("parameter format is not right. program stopped");
        }

    }


    private static void getParameters(String[] args) {
        //get input
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(INPUT)) {
                paranMpa.put(INPUT, args[i + 1]);
                break;
            }
        }
        //get output
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(OUTPUT)) {
                paranMpa.put(OUTPUT, args[i + 1]);
                break;
            }
        }

        //get gene type
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(GENE_TYPE)) {
                paranMpa.put(GENE_TYPE, args[i + 1]);
                break;
            }
        }
        if (paranMpa.containsKey(GENE_TYPE)) {
            int index = Integer.parseInt(paranMpa.get(GENE_TYPE));
            geneType = HLAGene.values()[index];
        }

        //get custom alignment file.
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(CUSTOM_ALIGNMENT_FILE)) {
                Configuration.customAlignFile =  args[i + 1];
                break;
            }
        }

    }

}
