import java.util.ArrayList;
import java.util.List;

class Library {
    private final List<Lendable> items;

    public Library() {
        items = new ArrayList<>();
    }

    public void addItem(Lendable item) {
        items.add(item);
    }

    public void removeItem(Lendable item) {
        items.remove(item);
    }

    public List<Lendable> getAvailableItems() {
        List<Lendable> availableItems = new ArrayList<>(items);
        availableItems.removeIf(Lendable::isBorrowed);
        return availableItems;
    }

    public void listAllItems() {
        for (Lendable item : items) {
            printItem(item);
        }
    }

    private void printItem(Lendable item) {
        if (item instanceof Book book) {
            System.out.println(book.getTitle() + " by " + book.getAuthor());
        } else {
            System.out.println("Unknown item type");
        }
    }
}
