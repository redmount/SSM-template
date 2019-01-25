package com.redmount.template.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class RandomValidateCodeUtil {
    //private static final String randString = "0123456789";//随机产生只有数字的字符串
    //private static final String randString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生只有字母的字符串
    private static final String randString = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";//随机产生数字与字母组合的字符串
    private static final int width = 95;// 图片宽
    private static final int height = 25;// 图片高
    private static final int lineSize = 40;// 干扰线数量
    private static final int stringNum = 4;// 随机产生字符数量
    private static final Logger logger = LoggerFactory.getLogger(RandomValidateCodeUtil.class);
    private static final Random random = new Random();

    /**
     * 获得字体
     */
    private static Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    /**
     * 获得颜色
     */
    private static Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public static ValidateCodeModel getRandcode() {
        ValidateCodeModel model = new ValidateCodeModel();
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        String base64 = "";
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);//图片大小
        g.setFont(getFont());//字体大小
        g.setColor(getRandColor(110, 133));//字体颜色
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        String randomString = "";
        for (int i = 1; i <= stringNum; i++) {
            randomString = drowString(g, randomString, i);
        }
        logger.info(randomString);
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", baos);
            byte[] bytes = baos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            base64 = encoder.encodeBuffer(bytes).trim();//转换成base64串
            base64 = "data:image/jpeg;base64," + base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            model.initCode(randomString).setImgBase64(base64);
        } catch (Exception e) {
            logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
        }
        return model;
    }

    /**
     * 绘制字符串
     */
    private static String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString
                .length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private static void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    public static String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
}
