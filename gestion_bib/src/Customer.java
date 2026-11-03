import java.util.ArrayList;
import java.util.List;

class Customer {
    private final String name;
    private final List<Lendable> checkedOutItems;

    public Customer(String name) {
        this.name = name;
        checkedOutItems = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void checkOutItem(Lendable item) {
        if (!item.isBorrowed() && !checkedOutItems.contains(item)) {
            item.borrow();
            checkedOutItems.add(item);
        }
    }

    public void returnItem(Lendable item) {
        if (checkedOutItems.contains(item)) {
            checkedOutItems.remove(item);
            item.returnItem();
        }
    }
}
