class Book implements Lendable {
    private final String title;
    private final String author;
    private boolean borrowed;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }

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

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean isBorrowed() {
        return borrowed;
    }

    public void complexMethodExample(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(i + " " + describeNumber(i));
        }
    }

    private String describeNumber(int number) {
        if (number % 2 == 0) {
            return "is even";
        }
        if (number % 3 == 0) {
            return "divisible by 3";
        }
        return "something else";
    }
}
