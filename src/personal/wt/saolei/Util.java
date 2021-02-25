package personal.wt.saolei;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Random;

public class Util {
    private final static String IMG_ROOT = "img";

    private final static Random random = new Random();

    public static int randomInt(int bound){
        return random.nextInt(bound);
    }

    public static boolean randomBoolean(){
        return random.nextBoolean();
    }

    public static Image getImage(String fileName){
        URL url = Util.class.getClassLoader().getResource("");
        System.out.println(url.getPath());
        Image img = new ImageIcon(url.getFile()+ File.separator + IMG_ROOT + File.separator + fileName).getImage();
        return img;
    }

    public static void main(String[] args) {
        /*int x = 50;
        while (x>0){
            System.out.print(randomInt(3) + " ");
            x--;
        }*/

        getImage("cross_mark.png");
    }
}
