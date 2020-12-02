package sample;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintMeals {
    public static final String STRING_SELECT = "SELECT * FROM ";
    String nameFile;
    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String MEALS = "MEALS";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String ALIMENTI = "ALIMENTI";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ID_MEALS = "ID_MEALS";
    public static final String COLUMN_NAME_MEALS = "NAME_MEALS";
    public static final String COLUMN_HOUR_MEALS = "HOUR";
    public static final String COLUMN_ID_FOOD = "ID_FOOD";
    public static final String COLUMN_ID_ALIM = "ID_ALIM";
    public static final String COLUMN_GRAMS = "GRAMS";
    public static final String COLUMN_ALIMENTO = "ALIMENTO";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    public static final String COLUMN_FIBER = "FIB";
    public static final String COLUMN_NOME = "NOME";
    public static final String COLUMN_COGNOME = "COGNOME";
    public static final String COLUMN_ALTEZZA = "ALTEZZA";
    public static final String COLUMN_PESO = "PESO";
    public static final String COLUMN_ALTEZZA_PRINT = "StampaAltezza";
    public static final String COLUMN_PESO_PRINT = "stampaPeso";
    public static final String USER = "USER";

    private static final Logger LOG = LoggerFactory.getLogger(PrintMeals.class);

    public PrintMeals(String nameFile) {
            this.nameFile=nameFile;

    }



    protected void manipulatePdf() throws Exception {
        double totKcal=0;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(nameFile));
        Document doc = new Document(pdfDoc, PageSize.A4);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        Table table = new Table(new float[10]).useAllAvailableWidth();

        mealHeaderListGenerator(doc, font, table);
        generateSpace(doc);

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet searchResult = conn.createStatement().executeQuery(
                     STRING_SELECT + MEALS + " ORDER BY " + COLUMN_HOUR_MEALS + " ASC")) {
            while (searchResult.next()) {
                table = tableSetup();
                Cell cell = headerMeal(font, searchResult.getString(COLUMN_NAME_MEALS), searchResult.getString(COLUMN_HOUR_MEALS));

                table.addCell(cell);

                secondHeaderMeals(font, table); // aggiungo l'header del Body

                totKcal+=generateTableBody(doc, font, table, searchResult);

            }
        } catch (SQLException e) {
            LOG.error("Database connection error : {}", e.toString());
        }
        table = new Table(new float[10]).useAllAvailableWidth();

        Cell cell = new Cell(1, 10).add(new Paragraph(" Kcal Complessivi: " + String.format("%.0f", (totKcal)))
                .setFont(font));
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setPadding(5);
        cell.setBackgroundColor(new DeviceRgb(211,211,211));
        table.addCell(cell);
        doc.add(table);

        doc.close();
        Desktop.getDesktop().open(new File("elenco_pasti.pdf"));

    }

    private double generateTableBody(Document doc, PdfFont font, Table table, ResultSet searchResult) throws SQLException {
        double totKcal=0;
        String sql = STRING_SELECT + MEALS_CONFIG + " WHERE " + COLUMN_ID_MEALS + " = " + searchResult.getInt(COLUMN_ID) + " ORDER BY " + COLUMN_ID + " ASC";
        try (Connection connDetails = DriverManager.getConnection(CONNECTION_STRING);
             Statement statementSearchDetails = connDetails.createStatement();
             ResultSet resultsDetails = statementSearchDetails.executeQuery(sql)) {


            while (resultsDetails.next()) {

                totKcal += bodyMeals(table, resultsDetails);

            }

            totalKcalGenerator(totKcal, font, table);

            doc.add(table);
            generateSpace(doc);
            return totKcal;
        }
    }

    private Table tableSetup() {
        Table table;
        table = new Table(new float[7]).useAllAvailableWidth();
        table.setMarginTop(0);
        table.setMarginBottom(0);
        return table;
    }

    private void totalKcalGenerator(double totKcal, PdfFont font, Table table) {
        Cell cell;
        cell = new Cell().add(new Paragraph(" ")
                .setWidth(100)
                .setFont(font));
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Totale")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));

        table.addCell(cell);

        cell = new Cell().add(new Paragraph(String.format("%.0f", (totKcal))))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font);

        table.addCell(cell);
    }

    private void generateSpace(Document doc) {
        doc.add(new Paragraph("\n"));
    }

    private void mealHeaderListGenerator(Document doc, PdfFont font, Table table) {
        table.setMarginTop(0);
        table.setMarginBottom(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String sqlUserData = STRING_SELECT + USER;
        try (Connection connDetailsFood = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet resultsUserData = connDetailsFood.createStatement().executeQuery(sqlUserData)) {


            if (resultsUserData.next()) {
                String cellString="Elenco dei pasti di "+  resultsUserData.getString(COLUMN_NOME) + " " + resultsUserData.getString(COLUMN_COGNOME)+". ";
                if(resultsUserData.getInt(COLUMN_ALTEZZA_PRINT)==1){
                    DecimalFormat formatter = new DecimalFormat("#0.00");

                    cellString+="Altezza: " + formatter.format(resultsUserData.getDouble(COLUMN_ALTEZZA)/100) +" Mt. " ;
                }

                if(resultsUserData.getInt(COLUMN_PESO_PRINT)==1){
                    cellString+="Peso: " + resultsUserData.getString(COLUMN_PESO) + " Kg.";
                }

                cellString+=" Generato il "+ dtf.format(now);

                Cell cell = new Cell(1, 10).add(new Paragraph(cellString)
                        .setFont(font));
                cell.setFontColor(new DeviceRgb(255, 255, 255));
                cell.setTextAlignment(TextAlignment.LEFT);
                cell.setPadding(5);
                cell.setBackgroundColor(new DeviceRgb(43, 87, 151));
                table.addCell(cell);

                doc.add(table);


            }
        }catch(SQLException ex){
            LOG.error("Error: " , ex);
        }





    }

    private double bodyMeals(Table table, ResultSet resultsDetails) throws SQLException {
        double sumKcal = 0;
        String sqlDetailsFood = STRING_SELECT + ALIMENTI + " WHERE " + COLUMN_ID_ALIM + " = " + resultsDetails.getInt(COLUMN_ID_FOOD);
        try (Connection connDetailsFood = DriverManager.getConnection(CONNECTION_STRING);
             Statement statementSearchDetailsFood = connDetailsFood.createStatement();
             ResultSet resultsDetailsFood = statementSearchDetailsFood.executeQuery(sqlDetailsFood)) {




            if (resultsDetailsFood.next()) {
                table.addCell(resultsDetailsFood.getString(COLUMN_ALIMENTO)).setTextAlignment(TextAlignment.LEFT);

                Cell cell = new Cell().add(new Paragraph(resultsDetails.getString(COLUMN_GRAMS)));
                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);

                String totalKcal = Double.toString(Math.round(resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_CARBOHIDRATE) / 100 * 4 +
                        resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_PROTEINS) / 100 * 4 +
                        resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_FAT) / 100 * 9));
                sumKcal += Double.parseDouble(totalKcal);

                String rounded = String.format("%.0f", Double.parseDouble(totalKcal));


                cell = new Cell().add(new Paragraph(rounded));
                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);


                cell = new Cell().add(new Paragraph(String.format("%.0f", (double)Math.round(resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_CARBOHIDRATE)
                        / 100 * 4))));


                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.format("%.0f", (double)(Math.round(resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_PROTEINS) / 100 * 4)))));
                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.format("%.0f", (double)Math.round(resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_FAT) / 100 * 9))));
                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);

                cell = new Cell().add(new Paragraph(String.format("%.0f", (double)Math.round(resultsDetails.getDouble(COLUMN_GRAMS) * resultsDetailsFood.getDouble(COLUMN_FIBER) / 100))));
                cell.setTextAlignment(TextAlignment.RIGHT);
                table.addCell(cell);


            }
            return sumKcal;
        }
    }

    private void secondHeaderMeals(PdfFont font, Table table) {


        Cell cell = new Cell().add(new Paragraph("Alimento")
                .setWidth(100)
                .setFont(font));
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Qta Gr.")
                .setWidth(60)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);


        cell = new Cell().add(new Paragraph("Kcal")
                .setWidth(50)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Carboidrati Kcal")
                .setWidth(70)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Proteine Kcal")
                .setWidth(50)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Grassi Kcal")
                .setWidth(50)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);
        cell = new Cell().add(new Paragraph("Fibra Gr.")
                .setWidth(50)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFont(font));
        //
        table.addCell(cell);
    }

    private Cell headerMeal(PdfFont font, String nameMeal, String hourMeal) {
        Cell cell = new Cell(1, 10).add(new Paragraph(nameMeal + " - " + hourMeal)
                .setFont(font));
        cell.setFontColor(new DeviceRgb(255, 255, 255));
        cell.setTextAlignment(TextAlignment.LEFT);
        cell.setPadding(5);
        cell.setBackgroundColor(new DeviceRgb(45, 137, 239));
        return cell;
    }
}
