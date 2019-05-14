package araclar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Araclar {

    public static BufferedImage[] resimleriParcala(String resim) throws IOException {
        File file = new File(resim);
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);

        int satir = 4;
        int sutun = 4;
        int parcalar = satir * sutun;


        int parcaGenisligi = image.getWidth() / sutun;

        int parcaYuksekligi = image.getHeight() / satir;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[parcalar];
        for (int x = 0; x < satir; x++) {
            for (int y = 0; y < sutun; y++) {
                imgs[count] = new BufferedImage(parcaGenisligi, parcaYuksekligi, image.getType());


                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, parcaGenisligi, parcaYuksekligi, parcaGenisligi * y, parcaYuksekligi * x, parcaGenisligi * y + parcaGenisligi, parcaYuksekligi * x + parcaYuksekligi, null);
                gr.dispose();
            }
        }
        return imgs;
    }

    public static boolean resimleriKarsilastir(BufferedImage biA, BufferedImage biB) {
        try {
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();

            if (sizeA == sizeB) {
                for (int i = 0; i < sizeA; i++) {
                    if (dbA.getElem(i) != dbB.getElem(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Parcaciklar kontrol edilirken hata meydana geldi\nHata: " + e.getMessage());
            return false;
        }
    }
}
