// Classe représentant un livre numérique
class DigitalBook extends Book {
    private int fileSizeMB;

    public DigitalBook(String title, String author, int fileSizeMB) {
        super(title, author);
        this.fileSizeMB = fileSizeMB;
    }

    public int getFileSizeMB() {
        return fileSizeMB;
    }
}
