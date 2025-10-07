
public class LibraryApp {
    public static void main(String[] args) {
        Library library = new Library();

        Book book1 = new Book("The Catcher in the Rye", "J.D. Salinger");
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee");

        //library.addBook(book1);
        //library.addBook(book2);

        System.out.println("List of Books in the Library:");
        //library.listBooks();
    }
}