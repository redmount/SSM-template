package com.redmount.template.util;

import com.redmount.template.system.model.ValidateCodeModel;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class RandomValidateCodeUtil {
    private static final String randNumString = "23456789";//随机产生数字与字母组合的字符串
    private static final String randCharString = "ACEFGHJKLMNPQRSTUVWXY";
    private static final String randChineseString = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";
    private static final String[] randomStringArr = {randNumString, randCharString, randChineseString};
    private static final int width = 95;// 图片宽
    private static final int height = 25;// 图片高
    private static final int lineSize = 40;// 干扰线数量
    private static final int stringLength = 4;// 随机产生字符数量
    private static final Logger logger = LoggerFactory.getLogger(RandomValidateCodeUtil.class);
    private static final Random random = new Random();
    private static String randString = "";
    private static Color backgroundColor = getRandBackgroundColor();

    /**
     * 获得字体
     */
    private static Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 20);
    }

    static String genRandString(int length) {
        java.util.List<Character> charList = new ArrayList<Character>();
        for (int i = 0; i < stringLength; i++) {
            String str = randomStringArr[i % randomStringArr.length];
            charList.add(str.charAt(random.nextInt(str.length())));
        }
        Collections.shuffle(charList);
        StringBuilder builder = new StringBuilder();
        for (char ch : charList) {
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * 取随机背景颜色
     *
     * @return 背景颜色
     */
    static Color getRandBackgroundColor() {
        int r = 255 - random.nextInt(50);
        int g = 255 - random.nextInt(50);
        int b = 255 - random.nextInt(50);
//        int a = 100;
        return new Color(r, g, b);
    }

    /**
     * 生成干扰线颜色
     * @return
     */
    static Color getRandLineColor(){
        int r= 255-50-random.nextInt(20);
        int g= 255-50-random.nextInt(20);
        int b= 255-50-random.nextInt(20);
        return new Color(r, g, b);
    }

    /**
     * 根据背景颜色取文字颜色
     *
     * @return 随机的文字颜色
     */
    static Color getRandFrontColor() {
        int r = random.nextInt(50);
        int g = random.nextInt(50);
        int b = random.nextInt(50);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public static ValidateCodeModel getRandomCode() {
        randString = genRandString(stringLength);
        ValidateCodeModel model = new ValidateCodeModel();
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        backgroundColor = getRandBackgroundColor();
        String base64;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);//图片大小
        g.setFont(getFont());//字体大小
//        g.setColor(getRandColor(110, 133));//字体颜色
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            g.setColor(getRandLineColor());
            drawLine(g);
        }

        for (int i = 0; i <= stringLength - 1; i++) {
            drawString(g, randString, i);
        }
        logger.info(randString);
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", baos);
            byte[] bytes = baos.toByteArray();
            Base64 encoder = new Base64();
            base64 = encoder.encodeToString(bytes).trim();//转换成base64串
            base64 = "data:image/jpeg;base64," + base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
            model.initCode(randString).setImgBase64(base64);
        } catch (Exception e) {
            logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
        }
        return model;
    }

    /**
     * 绘制字符串
     */
    private static String drawString(Graphics g, String str, int i) {
        g.setFont(getFont());
        g.setColor(getRandFrontColor());
        g.translate(random.nextInt(3), random.nextInt(3));
        Graphics2D graphics2d = (Graphics2D) g;
        AffineTransform trans = new AffineTransform();
        trans.rotate((random.nextInt(50) - 25) * 3.14 / 180, 13 * i + 5, 20);
        graphics2d.setTransform(trans);
        g.drawString(String.valueOf(str.charAt(i)), 13 * i + 5, 20);
        return str;
    }

    /**
     * 绘制干扰线
     */
    private static void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    private static String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
}
