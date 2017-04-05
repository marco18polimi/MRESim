package exploration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by marco on 05/04/2017.
 */
public class Reserve {
    public static void log(String data,String filename){
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file;
            file = new File("C:/Users/marco/Documents/Tesi/MRESim/logs/"+filename+".txt");
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
