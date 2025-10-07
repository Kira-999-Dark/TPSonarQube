// Classe représentant un livre classique
class Book implements Lendable {
    private String title;
    private String author;
    private boolean borrowed;

    // Constructeur
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }

    // Méthodes de l'interface
    @Override
    public void borrow() {
        if (!borrowed) {
            borrowed = true;
        } else {
            System.out.println("This book is already borrowed.");
        }
    }

    @Override
    public void returnItem() {
        if (borrowed) {
            borrowed = false;
        } else {
            System.out.println("This book was not borrowed.");
        }
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isBorrowed() { return borrowed; }

    // Méthode volontairement complexe pour le TP
    public void complexMethodExample(int n) {
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                System.out.println(i + " is even");
            } else if (i % 3 == 0) {
                System.out.println(i + " divisible by 3");
            } else {
                System.out.println(i + " something else");
            }
        }
    }
}