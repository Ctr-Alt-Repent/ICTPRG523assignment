package CDArchiveProject;

/**
 * The CDRecord class is used to store data corresponding to a specific CD.
 * CDRecord has functions to get() and set() each of its member variables
 * and an isOnLoan() function to evaluate the CD's loan status.
 */
public class CDRecord {
    String title;
    String author;
    String section;
    Integer x;
    Integer y;
    Integer barcode;
    String description;
    boolean onLoan;

    @Override
    public String toString() {
        return "CDRecord{" +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", section='" + section + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", barcode=" + barcode +
                ", description='" + description + '\'' +
                ", onLoan=" + onLoan +
                '}';
    }

    public CDRecord(String title, String author, String section, Integer x, Integer y, Integer barcode, String description, boolean onLoan)
    {
        this.title = title;
        this.author = author;
        this.section = section;
        this.x = x;
        this.y = y;
        this.barcode = barcode;
        this.description = description;
        this.onLoan = onLoan;
    }

    // Necessary for sorting testing
    public CDRecord(Integer barcode)
    {
        this.barcode = barcode;
    }

    public String getTitle() {return title;}
    public String getAuthor() {return author;}
    public String getSection() {return section;}
    public Integer getX() {return x;}
    public Integer getY() {return y;}
    public Integer getBarcode() {return barcode;}
    public String getDescription()
    {
        return description;
    }
    public boolean isOnLoan() {return onLoan;}


}
