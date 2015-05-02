package com.vlls.service;


import com.vlls.jpa.domain.Item;
import com.vlls.jpa.domain.Level;
import com.vlls.jpa.repository.CourseRepository;
import com.vlls.jpa.repository.ItemRepository;
import com.vlls.jpa.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by Hoang Thong on 16/10/2014.
 */
@Service
public class CSVFileService extends AbstractService {

    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private LevelRepository levelRepo;
    @Autowired
    private CourseRepository courseRepo;

    /**
     * Read a CSV file and add to array of Item for future adding to Level in Course
     *
     * @param filePath
     */
    @Transactional
    public static void read(String filePath) {
        try {
            Vector<Item> items = new Vector<Item>();
            File f = new File(filePath);
            if (!f.exists()) return;
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String header = br.readLine();
            String detail;
            while ((detail = br.readLine()) != null) {
                //Split
                StringTokenizer stk = new StringTokenizer(detail, ",");
                while (stk.hasMoreTokens()) {
                    String itemName = stk.nextToken().toString();
                    String itemMeaning = stk.nextToken().toString();
                    String itemPronun = stk.nextToken().toString();
                    String itemType = stk.nextToken().toString();
                    Item item = new Item();
                    item.setName(itemName);
                    item.setMeaning(itemMeaning);
                    item.setPronun(itemPronun);
                    item.setType(itemType);
                    items.add(item);
                }
            }
            for (int i = 0; i < items.size(); i++) {
                System.out.println("- Item " + i + ": \n" + "  + Name: " + items.get(i).getName() + "\n" +
                                "  + Meaning: " + items.get(i).getMeaning() + "\n" +
                                "  + Pronun: " + items.get(i).getPronun() + "\n" +
                                "  + Type: " + items.get(i).getType() + "\n"
                );
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void write(String path) {
        Level level = levelRepo.findOne(1);
        List<Item> items = level.getItems();
        File f = new File(path);
        try {
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("name,meaning,pronun,type");
            for (Item item : items) {
                pw.println(item.getName() + "," + item.getMeaning() + "," + item.getPronun() + "," + item.getType());
            }
        pw.close();
        fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        CSVFileService c = new CSVFileService();
//        read("D:\\My courses\\Capstone\\Project\\Source Code\\vlls\\vlls-back\\testRead.csv");
//        write("D:\\My courses\\Capstone\\Project\\Source Code\\vlls\\vlls-back\\testWrite.csv");
//    }

}
