class Book implements Lendable {
	//classe Book
	//classe Book
	//classe Book
	//classe Book
    private String title;
    private String author;
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
}
