package arayuz;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class PuzzleParcasi extends ToggleButton {

    BufferedImage resim;

    public PuzzleParcasi(BufferedImage resim) {
        this.resim = resim;
//        setText("✓");
        Image image = SwingFXUtils.toFXImage(resim, null);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        setGraphic(imageView);
    }



    public BufferedImage getResim() {
        return resim;
    }

    public void setResim(BufferedImage resim) {
        this.resim = resim;
    }
}
