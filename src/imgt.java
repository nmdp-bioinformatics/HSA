import org.nmdp.HLAGene.HLAGene;
import org.nmdp.HSALogger;
import org.nmdp.Launcher;
import org.nmdp.config.Configuration;
import org.nmdp.databaseAccess.DatabaseUtil;
import org.nmdp.parseExon.ParseExon;
import org.nmdp.scheduler.Scheduler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;

/**
 * Created by wwang on 8/16/16.
 */
public class imgt {
    public static void main(String[] args) throws URISyntaxException {
        CodeSource codeSource = Launcher.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        String jarDir = jarFile.getParentFile().getPath();
        try {
            Configuration.loadSetting(jarDir + "/config.txt");
        } catch (FileNotFoundException e) {
            System.out.println("config file is missing. program stopped");
        }

        //set up database
        DatabaseUtil.connectDatabase();
        DatabaseUtil.createSeqTable();
        DatabaseUtil.creatExonTable();
        DatabaseUtil.createGfeTable();

        ParseExon pe = new ParseExon();
        pe.process(new File("/Users/wwang/IdeaProjects/HSA/output/clu/HLA_C/PAC041316LR_2016-05-06-100423.clu"),new File("/Users/wwang/IdeaProjects/HSA/output/clu/HLA_C/PAC041316LR_2016-05-06-100423test.csv"), HLAGene.HLA_C );

        //clean up connection to database
        DatabaseUtil.cleanUp();

    }
}
