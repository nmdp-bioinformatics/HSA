package org.nmdp.translate;

import org.nmdp.config.Configuration;
import org.nmdp.scheduler.Task;
import org.nmdp.util.FileSystem;

import java.io.IOException;

/**
 * Created by Will on 6/5/16.
 */
public class Translator {

    public void process(Task task){
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
        String input = FileSystem.getExonFile(task.getGene(), task.getFileName()).getAbsolutePath();
        String output = FileSystem.getProteinFile(task.getGene(), task.getFileName()).getAbsolutePath();
        String[] command = new String[6];
        command[0] = "perl "+ Configuration.nt2aa;
        command[1] = "-s";
        command[2] = input;
        switch (task.getGene()){
            case HLA_A:
            case HLA_B:
            case HLA_C:
                command[3] = "-f -1";
                break;
            default:
                command[3] = "-f -3";
        }

        command[4] = " > ";
        command[5] = output;

        StringBuilder sb = new StringBuilder();

        for(String s : command){
            sb.append(s);
            sb.append(" ");
        }
        System.out.println(sb.toString());
        return sb.toString();
    }

}
