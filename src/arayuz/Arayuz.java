package arayuz;

import araclar.Araclar;
import araclar.Puan;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Arayuz extends Application {
    private BorderPane anaPanel;
    private Stage stage;
    private File secilenDosya;
    private BufferedImage[] orjinalResimler;
    private List<PuzzleParcasi> buttonList;
    private boolean parcaDegisimeHazirmi = false; // mesela bir parcaya basti artik yeni parcaya basarsa yerlerini degistirebiliriz
    private GridPane ortaPanel;
    private int puan = 0;
    private long baslama = -1, bitis = -1; //dosya seciminden itibaren oyunu bitirene kadar olan sure
    private String DOSYA_ADI = "enyüksekskor.txt";

    private boolean karistirSonrasiEnAzBirParcaDogruYerindemi = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        anaPanel = new BorderPane();
        ustPaneliOlustur();
        puanlariGosterPanel();

        Scene scene = new Scene(anaPanel, 1500, 700);
        stage.setScene(scene);
        stage.setTitle("Puzzle Oyunu");
        stage.show();
    }

    private void ortaPaneliOlustur(BufferedImage[] resimler) {
        int k = 0;
        ortaPanel = new GridPane();

        buttonList = new ArrayList<>();


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                PuzzleParcasi puzzleParcasi = new PuzzleParcasi(resimler[k]);
                puzzleParcasi.setOnAction(event -> {
                    if (karistirSonrasiEnAzBirParcaDogruYerindemi) {
                        kontrolEt();
                    } else {
                        uyariGoster("Hata", "En az 1 parca dogru yere gelen kadar karıştırın", Alert.AlertType.WARNING);
                    }
                });

                buttonList.add(puzzleParcasi);
                k++;
            }
        }

        resimleriKaristir();
        enAzBirParcaYerindemiKontrolEt();
        if (tumParcalarDogruYerindemi()) {
            puan = 100;
        }

        k = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                PuzzleParcasi puzzleParcasi = buttonList.get(k);
                ortaPanel.add(puzzleParcasi, j, i);
                k++;
            }
        }
        anaPanel.setCenter(ortaPanel);
    }

    private void enAzBirParcaYerindemiKontrolEt() {
        for (int i = 0; i < orjinalResimler.length; i++) {
            if (Araclar.resimleriKarsilastir(orjinalResimler[i], buttonList.get(i).getResim())) {
                karistirSonrasiEnAzBirParcaDogruYerindemi = true;
                break;
            }
        }
    }

    private void kontrolEt() {
        if (parcaDegisimeHazirmi == false) {
            parcaDegisimeHazirmi = true;
        } else { //ikinci bir parca tiklanmis yerlerini degistirebiliriz
            parcaDegisimeHazirmi = false;

            int puzzleParcasi1Index = -1;
            int puzzleParcasi2Index = -1;

            for (int i = 0; i < buttonList.size(); i++) {
                if (buttonList.get(i).isSelected() && puzzleParcasi1Index == -1) {
                    puzzleParcasi1Index = i;
                } else if (buttonList.get(i).isSelected() && puzzleParcasi1Index != -1 && puzzleParcasi2Index == -1) {
                    puzzleParcasi2Index = i;
                    break;
                }
    }
            listedekiTumSecimleriIptalEt();

            if (puzzleParcasi1Index != puzzleParcasi2Index && puzzleParcasi1Index > -1 && puzzleParcasi2Index > -1) {
                Collections.swap(buttonList, puzzleParcasi1Index, puzzleParcasi2Index);
            }


            boolean hepsiEslestimi = tumParcalarDogruYerindemi();

            if (hepsiEslestimi) {
                uyariGoster("Tebrikler", "Puzzle'ı tamamladınız!!!", Alert.AlertType.INFORMATION);
                bitis = System.currentTimeMillis();
                float sec = (bitis - baslama) / 1000F;

                puan = (int) (100 - sec);
                puanKaydet();
                puanlariGosterPanel();
            }

            ekraniYenidenCiz();
        }
    }

    private void listedekiTumSecimleriIptalEt() {
        for (PuzzleParcasi puzzleParcasi : buttonList) {
            puzzleParcasi.setSelected(false);
        }
    }

    private void ekraniYenidenCiz() {
        ortaPanel.getChildren().clear();

        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                PuzzleParcasi puzzleParcasi = buttonList.get(k);
                ortaPanel.add(puzzleParcasi, j, i);
                k++;
            }
        }

    }


    private void resimleriKaristir() {
        Collections.shuffle(buttonList);
    }

    private void orjinaliGoster() {
        GridPane gridPane = new GridPane();
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Image image = SwingFXUtils.toFXImage(orjinalResimler[k], null);

                DataBuffer dbB = orjinalResimler[k].getData().getDataBuffer();

                System.out.println("i: " + i + " j: " + j + " -> " + dbB.getSize());
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);

                Button button = new Button("" + k);
                button.setGraphic(imageView);

                gridPane.add(button, j, i);
                k++;
            }
        }

        gridPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        anaPanel.setLeft(gridPane);
    }


    private void ustPaneliOlustur() {
        HBox hBox = new HBox(10);

        FileChooser fileChooser = new FileChooser();
        Button btnDosyaSec = new Button("Dosya Sec");
        btnDosyaSec.setOnAction(e -> {
            secilenDosya = fileChooser.showOpenDialog(stage);
            puan = 0;
            karistirSonrasiEnAzBirParcaDogruYerindemi = false;
            baslama = System.currentTimeMillis();
            bitis = 0;
        });

        Button btnKaristir = new Button("Karıştır");
        btnKaristir.setOnAction(e -> {

            try {
                orjinalResimler = Araclar.resimleriParcala(secilenDosya.getAbsolutePath());
                ortaPaneliOlustur(orjinalResimler);
                boolean hepsiEslestimi = tumParcalarDogruYerindemi();
                if (hepsiEslestimi) {
                    puan = 100;
                    puanKaydet();
                }
                //orjinaliGoster();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        hBox.getChildren().addAll(btnDosyaSec, btnKaristir);


        anaPanel.setTop(hBox);
    }

    private void puanKaydet() {
        try {
            String isim = getIsim();
            Puan puan = new Puan(isim, this.puan);

            Files.write(Paths.get(DOSYA_ADI), puan.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private void uyariGoster(String hataBasligi, String hata, Alert.AlertType alertType) {
        Alert errorAlert = new Alert(alertType);
        errorAlert.setHeaderText(hataBasligi);
        errorAlert.setContentText(hata);
        errorAlert.showAndWait();
    }

    /**
     * resimlerin hepsi dogru yerdemi ?
     */
    private boolean tumParcalarDogruYerindemi() {
        boolean hepsiEslestimi = true;
        for (int i = 0; i < orjinalResimler.length; i++) {
            if (!Araclar.resimleriKarsilastir(orjinalResimler[i], buttonList.get(i).getResim())) {
                hepsiEslestimi = false;
            }
        }
        return hepsiEslestimi;
    }

    private String getIsim() {
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Tebrikler");
        tid.setHeaderText("Oyunu Kazandınız");
        tid.setContentText("Adınızı Giriniz");
        Optional<String> result = tid.showAndWait();
        result.ifPresent(name -> System.out.println("Your name: " + name));

        return result.get();
    }

    private void puanlariGosterPanel() {
        ListView<String> lw = new ListView<>();
        List<Puan> puanlar = new ArrayList<>();
        File file = new File(DOSYA_ADI);
        Scanner sc = null;
        try {
            sc = new Scanner(file);

            String satir;
            while (sc.hasNextLine()) {
                satir = sc.nextLine();

                puanlar.add(new Puan(satir));
            }
            Collections.sort(puanlar, (o2, o1) -> ((Integer) o2.getPuan()).compareTo(o1.getPuan()));

            for (Puan puan1 : puanlar) {
                lw.getItems().add(puan1.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        anaPanel.setRight(null);
        anaPanel.setRight(lw);
        /*
        Popup popup = new Popup();


        HBox hBox = new HBox(10);
        hBox.getChildren().add(lw);

        Button button = new Button("x");
        button.setOnAction(e -> {
            popup.hide();
        });
        hBox.getChildren().add(button);

        popup.getContent().add(hBox);

        popup.show(stage);
        */
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
