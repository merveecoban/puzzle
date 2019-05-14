package araclar;

public class Puan {
    private String isim;
    private int puan;


    public Puan(String isim, int puan) {
        this.isim = isim;
        this.puan = puan;

        if(puan < 1)
            puan = 10;
    }

    public Puan(String s) {
        String[] split = s.split("\t");
        this.isim = split[0].trim();
        this.puan = Integer.parseInt(split[1].trim());
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }

    @Override
    public String toString() {
        return isim + "\t" + puan + "\n";
    }
}
