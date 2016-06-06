package org.nmdp.scheduler;

import org.apache.commons.io.FileUtils;
import org.nmdp.HLAGene.HLAGene;
import org.nmdp.alignment.AlignmentController;
import org.nmdp.parseExon.ParseExon;
import org.nmdp.parseHML.FastaGenerator;
import org.nmdp.parseHML.Mode;
import org.nmdp.translate.Translator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Will on 6/4/16.
 */
public class Scheduler {
    LinkedList<Task> taskQueue;

    FastaGenerator generetor;

    public Scheduler(){
        taskQueue = new LinkedList<>();


    }

    public void addTask(Task task){
        taskQueue.addLast(task);
    }

    public boolean isTaskEmpty(){
        return taskQueue.isEmpty();
    }

    public Task geTask(){
        return taskQueue.pollFirst();
    }


    public void onFastaIsReady(){
        //start to do alignment
    }

    public void start(Mode mode, boolean expand) throws IOException, SAXException, ParserConfigurationException {
        generetor = new FastaGenerator(mode, expand, this);
        File folder = new File("./input");
        File outputFolder = new File("./output");
        try {
            if (outputFolder.exists()) {
                FileUtils.cleanDirectory(outputFolder);
            }else{
                outputFolder.mkdir();
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        File[] inputList = folder.listFiles();
        for (int i = 0; i < inputList.length; i++) {
            String fileNameFull = inputList[i].getName().toLowerCase();
            if (fileNameFull.contains("hml") || fileNameFull.contains("xml")) {
                generetor.run(inputList[i]);
            }
        }

        AlignmentController ac = new AlignmentController(this);
        ParseExon pe = new ParseExon();
        Translator ts = new Translator();

        while (!isTaskEmpty()){
            Task task = geTask();
            ac.process(task);
            pe.process(task);
            ts.process(task);

        }


    }
}
