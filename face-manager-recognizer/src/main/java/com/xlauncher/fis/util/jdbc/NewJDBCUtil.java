package com.xlauncher.fis.util.jdbc;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/25 0025
 * @Desc :抓拍图片
 **/
public class NewJDBCUtil {

    public static void main(String[] args) throws Exception {
        NewJDBCUtil jdbcutil = new NewJDBCUtil();
        jdbcutil.getImage(args[0], args[1], args[2], Integer.parseInt(args[3]));

//        getImageByIdCard("652722198802241013", "d:\\images", 1);
//        jdbcutil.getImage("2019-03-20 14:00:00", "2019-03-21 14:00:00", "d:\\images", 0);
//        jdbcutil.getCount("2019-03-20 14:00:00", "2019-03-25 14:00:00");
    }

    private static Connection con;
    private static final String LINUX = "linux";
    private static final String WIN = "win";
    private static String SEPARATOR;
    private static final int CODE_OK = 200;
    private static final String DATA = "data";

    /**
     * 获取数据库连接
     *
     * @return Connection
     */
    private synchronized Connection getConnection() throws Exception{
        String hostname = "127.0.0.1";
        String port = "3306";
        String user = "root";
        String password = "docker@1302";
        String database = "fcs";
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false&serverTimezone=GMT%2B8";
        System.out.println("url." + url);

        try {
            // 1.加载驱动程序
            // Class.forName()加载一个类，返回的该类的类名
            // newInstance()方法可以创建一个Class对象的实例，使用该方法实例化一个类时该类必须已经被加载了
            // new关键字实例化一个类时先加载再实例化
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DriverManager.setLoginTimeout(30);
            // 2.通过连接池创建Connection连接
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Err.Connection refused!" + e);
            throw new Exception("Err.Connection refused!" + e);
        }
        return con;
    }

    /**
     * 获取图片
     *
     * @param startTime
     * @param endTime
     * @throws Exception
     */
    private void getImage(String startTime, String endTime, String path, int code) throws Exception {
        System.out.println("startTime." + startTime);
        System.out.println("endTime." + endTime);
        System.out.println("path." + path);
        System.out.println("code." + code);
        int count = getCount(startTime, endTime, code);
        if (count != 0) {
            // 获取数据库连接
            Connection con = getConnection();
            Statement st = null;
            ResultSet rs = null;
            String sql;
            if (code == 1) {
                System.out.println("code is 1, 执行存储抓拍图片程序!");
                // face_predict
                sql = "SELECT face_photo FROM face_predict WHERE face_photo IS NOT NULL AND predict_time > ' " + startTime + "' AND predict_time < ' " + endTime + "'";
                System.out.println("FACE SQL。" + sql);
            } else if (code == 0) {
                // syn_user
                System.out.println("code is 0, 执行存储用户身份证图片程序!");
                sql = "SELECT user_image FROM syn_user WHERE model_calculation IS NOT NULL AND user_image IS NOT NULL AND checkin_time > ' " + startTime + "' AND checkin_time < ' " + endTime + "'";
                System.out.println("USER SQL。" + sql);
            } else {
                System.out.println("-----------------");
                System.out.println("无效的参数!" + code);
                System.out.println("-----------------");
                return;
            }
            try {
                // 获取Statement
                st = con.createStatement();
                // 执行查询操作
                rs = st.executeQuery(sql);
                int index = 0;
                while (rs.next()) {
                    index ++;
                    byte[] bytes = rs.getBytes(1);
                    System.out.println("保存图片..." + bytes.length);
                    storeImage(bytes, path, index);
                    System.out.println("已保存图片：" + index + " 张!");
                }

                System.out.println("查询结束!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("获取图片getImage异常" + e);
            }

            System.out.println("----------释放资源-----------");

            try {
                if (st != null) {
                    st.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("获取图片释放资源异常!" + e);
            }
        } else {
            System.out.println("*******************");
            System.out.println("查询无结果!");
            System.out.println("*******************");
        }

    }


    /**
     * 存储图片
     *
     * @param bytes
     */
    private void storeImage(byte[] bytes, String path, int index) {
        try {
            String os = System.getProperties().getProperty("os.name");
            os = os.toUpperCase();
            // 判断操作系统
            if (os.toLowerCase().startsWith(LINUX)) {
                SEPARATOR = "/";
            } else if(os.toLowerCase().startsWith(WIN)) {
                SEPARATOR = "\\";
            }
            // byte数组保存图片
            String fileName = path + SEPARATOR + System.currentTimeMillis() + "T" + index + ".jpg";
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileName));
            imageOutput.write(bytes, 0, bytes.length);
            imageOutput.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("保存图片异常" + e);
        }
    }

    /**
     * count
     *
     * @param startTime
     * @param endTime
     * @return
     * @throws Exception
     */
    private int getCount(String startTime, String endTime, int code) throws Exception {
        int rowCount = 0;
        // 获取数据库连接
        Connection con = getConnection();
        Statement st = null;
        ResultSet rs = null;
        String count = "SELECT COUNT(*) FROM user";
        if (code == 1) {
            // face_predict
            count = "SELECT COUNT(face_photo) FROM face_predict WHERE predict_time > ' " + startTime + "' AND predict_time < ' " + endTime + "'";
        }  else if (code == 0) {
            // syn_user
            count = "SELECT COUNT(user_image) FROM syn_user WHERE model_calculation IS NOT NULL AND checkin_time > ' " + startTime + "' AND checkin_time < ' " + endTime + "'";
        }
        try {
            // 获取Statement
            st = con.createStatement();
            // 执行查询操作
            rs = st.executeQuery(count);

            // 光标先后移动，判断是否存在下一个元素
            while (rs.next()) {
                rowCount=rs.getInt(1);
            }
            System.out.println("返回查询数据总数 is " + rowCount);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("获取数据总数异常getCount.Err." +e);
        }
        try {
            release(rs,st,con);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("获取数据总数释放资源异常getCount.release.Err." +e);
        }
        return rowCount;
    }


    /**
     * 根据身份证号码获取用户图片
     *
     * @param idCard
     * @param path
     * @param count
     */
    private static void getImageByIdCard(String idCard, String path, int count) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        // 运营云同步用户信息接口
        String cloudUrl = "http://139.159.142.50:3344/fdcservice/queryPerson/byIdcard";
        // 传入参数
        Map<String, String> postMap = new HashMap<>(1);
        postMap.put("id", idCard);
        // Header设置
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            System.out.println("------");
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                Map map = responseEntity.getBody();
                assert map != null;
                if (map.get(DATA) != null) {
                    try {
                        Map mapData = (Map) map.get(DATA);
                        // 获取用户身份证图片
                        String photo = (String) mapData.get("photo");
                        // BASE64解码
                        byte[] image = stringToByte(photo);
                        String os = System.getProperties().getProperty("os.name");
                        os = os.toUpperCase();
                        // 判断操作系统
                        if (os.toLowerCase().startsWith(LINUX)) {
                            SEPARATOR = "/";
                        } else if(os.toLowerCase().startsWith(WIN)) {
                            SEPARATOR = "\\";
                        }
                        System.out.println("保存图片...");
                        if (image.length != 0) {
                            // byte数组保存图片
                            String fileName = path + SEPARATOR + System.currentTimeMillis() + "T" + idCard + ".jpg";
                            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileName));
                            imageOutput.write(image, 0, image.length);
                            imageOutput.close();
                        }
                        System.out.println("已保存图片：" + count + " 张!");
                    } catch (IOException e) {
                        System.err.println("[Err.人脸识别预测线程异常]" + e);
                    }
                }
            }
        } catch (RestClientException e) {
            System.err.println("[Err.按身份证号码获取图片信息queryPersonByIdCard()异常!]" + e);
        }
    }

    /**
     * base64String 转换成byte[]数组
     *
     * @param string
     * @return
     */
    private static byte[] stringToByte(String string){
        byte[] bytes = new byte[0];
        // 对字节数组Base64解码
        BASE64Decoder decoder = new BASE64Decoder();
        // 返回Base64解码过的字节数组
        try {
            bytes = decoder.decodeBuffer(string);
        } catch (IOException e) {
            System.err.println("Err. BASE64字符串转字节数组异常!" + e);
        }
        return bytes;
    }

    /**
     * 释放连接
     *
     * @param rs ResultSet
     * @param st Statement
     * @param con Connection
     */
    private synchronized static void release(ResultSet rs, Statement st, Connection con) throws SQLException {
        try {
            if(rs != null) {
                rs.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            System.err.println("rs.close ERR!" + e1);
        }
        try {
            if(st != null) {
                st.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("st.close ERR!" + e);
        } finally {
            if(con != null) {
                con.close();
            }
        }
    }
}
