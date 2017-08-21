package org.nmdp.alignment;

import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;

import java.io.File;
import java.io.IOException;


/**
 *  The aligment controll uses clustalo to execute alignment.
 */
public class AlignmentController {

    private File input = null;

    public AlignmentController(){
    }
    public AlignmentController(File input){
        this.input = input;
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

        String input ;
        if(this.input!= null){
            input = this.input.getAbsolutePath();
        }else {
            input = FileSystem.getFastaFile(task.getGene(), task.getFileName()).getAbsolutePath();
        }
        String ref = FileSystem.getRefFile(task.getGene()).getAbsolutePath();
        String output = FileSystem.getCluFile(task.getGene(), task.getFileName()).getAbsolutePath();

        String[] command = new String[9];

        command[0] = "clustalo";
        command[1] = "-i";
        command[2] = input;
        command[3] = "--p1="+ref;
        command[4] = "--outfmt=clu";
        command[5] = "--output-order=input-order";
        command[6] = "--wrap=20000";
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
