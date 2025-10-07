import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Lendable> items;

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
        List<Lendable> availableItems = new ArrayList<>();
        for (Lendable item : items) {
            if (!availableItems.contains(item)) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }
}