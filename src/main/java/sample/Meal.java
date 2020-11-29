package sample;

public class Meal {
    private final String  idMeal;
    private final String nameMeal;
    private final String hourMeal;
    private final String totKCal;
    private final String grCarbo;
    private final String grPro;
    private final String grFat;
    private final String grFib;

    public Meal(String idMeal, String nameMeal, String hourMeal, String totKCal, String grCarbo, String grPro, String grFat, String grFib) {
        this.idMeal = idMeal;
        this.nameMeal = nameMeal;
        this.hourMeal = hourMeal;
        this.totKCal = totKCal;
        this.grCarbo = grCarbo;
        this.grPro = grPro;
        this.grFat = grFat;
        this.grFib = grFib;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getNameMeal() {
        return nameMeal;
    }

    public String getHourMeal() {
        return hourMeal;
    }

    public String getTotKCal() {
        return totKCal;
    }

    public String getGrCarbo() {
        return grCarbo;
    }

    public String getGrPro() {
        return grPro;
    }

    public String getGrFat() {
        return grFat;
    }

    public String getGrFib() {
        return grFib;
    }
}
