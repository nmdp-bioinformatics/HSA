package org.nmdp.scheduler;

import org.nmdp.HLAGene.HLAGene;

/**
 * Created by Will on 6/4/16.
 */
public class Task {
    HLAGene gene;
    String fileName;

    public Task(HLAGene gene, String name){
        this.gene = gene;
        fileName = name;
    }

    public HLAGene getGene(){
        return gene;
    }

    public String getFileName(){
        return fileName;
    }
}
