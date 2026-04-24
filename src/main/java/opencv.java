import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

public class opencv {
    public static void main(String[] args) {
        // 自动加载 OpenCV 4.9.0 原生库
        nu.pattern.OpenCV.loadLocally();
        System.out.println("OpenCV 版本: " + Core.VERSION);
        // 测试图片地址
        String imgPath = "D:/B4.jpg";

        // 读取图片
        Mat img = Imgcodecs.imread(imgPath);

        // 安全判断：防止图片为空崩溃
        if (img.empty()) {
            System.out.println("图片加载失败！");
            return;
        }

        System.out.println("图片加载成功，尺寸: " + img.width() + "x" + img.height());

        // 生成灰度图
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        // 保存图片
        String outputPath = "./result.png";
        Imgcodecs.imwrite(outputPath, gray);
        System.out.println("灰度图已保存到: " + outputPath);
        imshow("Original Image", img);

        // 显示灰度图（窗口名要改！不然会覆盖）
        imshow("Gray Image", gray);

        // ====================== 关键代码 ======================
        // 等待按键按下再关闭窗口（0=无限等待）
        waitKey(0);
        // 释放资源
        img.release();
        gray.release();

        System.out.println("测试完成！");
    }
}
