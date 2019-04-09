package com.xlauncher.fgs.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/22 0022
 * @Desc :读取.TXT文件 摄像头设备信息、RabbitMQ消息队列信息
 **/
public class ReadFileUtil {
    private static Logger logger = Logger.getLogger(ReadFileUtil.class);

    /**
     * 读取配置文件服务信息
     *
     * @return Map
     */
    public static Map<String, String> readFile() {
        Map<String, String> map = new HashMap<>(1);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "\\service.txt"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            List<String> list = new ArrayList<>(1);

            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }

            list.forEach(s -> {
                String[] splits = s.split("=");
                map.put(splits[0], splits[1]);
            });
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            logger.error("Err.读取配置文件异常" + e);
        }
        return map;
    }

    public static void writeFile(String fileName, String line) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("*." + line);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readLineNumber(String fileName, int num) {
        File file = new File(fileName);
        try {
            FileReader fileReader = new FileReader(file);
            LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
            String line = lineNumberReader.readLine();
            if (num <0 || num > totalLines(fileName)) {
                System.out.println("不在文件的行数范围之内!");
            } else {
                while (line != null) {
                    System.out.println("当前行号为:" + lineNumberReader.getLineNumber());
                    lineNumberReader.setLineNumber(num);
                    System.out.println("更改后行号为:"
                            + lineNumberReader.getLineNumber());
                    System.out.println(line);
                    System.exit(0);
                    line = lineNumberReader.readLine();

                }
            }
            lineNumberReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int totalLines(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        System.out.println(lines);
        reader.close();
        in.close();
        return lines;
    }
}
