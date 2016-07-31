package org.nmdp.alignment;

import org.nmdp.config.Configuration;
import org.nmdp.scheduler.Scheduler;
import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;

import java.io.IOException;


/**
 * Created by wwang on 6/4/16.
 */
public class AlignmentController {

    Scheduler scheduler;

    public AlignmentController(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public void process(Task task){
        if(FileSystem.getCluFile(task.getGene(), task.getFileName()).exists()){
            return ;
        }
        String command = buildCommand(task);
        Process p;
        try{
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String buildCommand(Task task) {
        String input = FileSystem.getFastaFile(task.getGene(), task.getFileName()).getAbsolutePath();
        String ref = FileSystem.getRefFile(task.getGene()).getAbsolutePath();
        String output = FileSystem.getCluFile(task.getGene(), task.getFileName()).getAbsolutePath();

        String[] command = new String[9];
        command[0] = Configuration.CLUSTALO;
        command[1] = "-i";
        command[2] = input;
        command[3] = "--p1="+ref;
        command[4] = "--outfmt=clu";
        command[5] = "--output-order=input-order";
        command[6] = "--wrap=9000";
        command[7] = "-o";
        command[8] = output;

        StringBuilder sb = new StringBuilder();

        for(String s : command){
            sb.append(s);
            sb.append(" ");
        }
        System.out.println(sb.toString());
        return sb.toString();

    }
}
