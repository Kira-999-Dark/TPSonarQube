import java.util.ArrayList;
import java.util.List;

class Customer {
    private String name;
    private List<Lendable> checkedOutItems;

    public Customer(String name) {
        this.name = name;
        checkedOutItems = new ArrayList<>();
    }

    public void checkOutItem(Lendable item) {
        if (!checkedOutItems.contains(item)) {
            checkedOutItems.add(item);
            item.borrow();
        }
    }

    public void returnItem(Lendable item) {
        if (checkedOutItems.contains(item)) {
            checkedOutItems.remove(item);
            item.returnItem();
        }
    }
}
