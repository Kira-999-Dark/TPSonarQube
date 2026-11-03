import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class LibraryRegression {
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static void checkBorrowing() {
        Library library = new Library();
        Book book = new Book("Titre", "Auteur");
        DigitalBook digital = new DigitalBook("Numerique", "Auteur", 10);
        Customer first = new Customer("Premier");
        Customer second = new Customer("Second");
        library.addItem(book);
        library.addItem(digital);
        require(library.getAvailableItems().size() == 2, "Initial availability");
        require("Titre".equals(book.getTitle()), "Title getter");
        require("Auteur".equals(book.getAuthor()), "Author getter");
        require(digital.getFileSizeMB() == 10, "Digital file size");
        require("Premier".equals(first.getName()), "Customer name");
        first.checkOutItem(book);
        require(book.isBorrowed(), "Borrow changes state");
        require(library.getAvailableItems().size() == 1, "Borrowed book excluded");
        second.checkOutItem(book);
        second.returnItem(book);
        require(book.isBorrowed(), "Second customer cannot return the borrowed book");
        first.checkOutItem(book);
        first.returnItem(book);
        require(!book.isBorrowed(), "Return restores state");
        require(library.getAvailableItems().size() == 2, "Return restores availability");
        first.checkOutItem(digital);
        require(library.getAvailableItems().size() == 1, "Digital book is excluded");
        first.returnItem(digital);
        library.removeItem(digital);
        require(library.getAvailableItems().size() == 1, "Removal updates inventory");

        first.checkOutItem(book);
        book.returnItem();
        first.checkOutItem(book);
        require(!book.isBorrowed(), "Duplicate checkout is not recorded after an external return");
        capture(() -> first.returnItem(book));
    }

    private static void checkStateMessages() {
        Book book = new Book("Titre", "Auteur");
        book.borrow();
        require(capture(book::borrow).contains("already borrowed"), "Repeated borrow message");
        book.returnItem();
        require(capture(book::returnItem).contains("was not borrowed"), "Repeated return message");
    }

    private static void checkListing() {
        Library library = new Library();
        library.addItem(new Book("Titre", "Auteur"));
        library.addItem(new Lendable() {
            public void borrow() {
                throw new UnsupportedOperationException("This fixture is only used to check listing.");
            }
            public void returnItem() {
                throw new UnsupportedOperationException("This fixture is only used to check listing.");
            }
            public boolean isBorrowed() { return false; }
        });
        String listing = capture(library::listAllItems);
        require(listing.contains("Titre by Auteur"), "Book listing");
        require(listing.contains("Unknown item type"), "Other lendable listing");
    }

    private static void checkNumberDescriptions() {
        Book book = new Book("Titre", "Auteur");
        require(capture(() -> book.complexMethodExample(0)).isEmpty(), "Empty loop");
        String expected = "0 is even\n1 something else\n2 is even\n"
                + "3 divisible by 3\n4 is even\n5 something else\n";
        require(expected.equals(capture(() -> book.complexMethodExample(6))), "Number descriptions");
    }

    private static void checkApplication() {
        String output = capture(() -> LibraryApp.main(new String[0]));
        require(output.contains("The Catcher in the Rye by J.D. Salinger"), "Application books");
        require(output.contains("Digital Fortress by Dan Brown"), "Application digital book");
        require(output.contains("9 divisible by 3"), "Application loop");
    }

    public static void main(String[] args) {
        checkBorrowing();
        checkStateMessages();
        checkListing();
        checkNumberDescriptions();
        checkApplication();
        System.out.println("All regression checks passed.");
    }
}
