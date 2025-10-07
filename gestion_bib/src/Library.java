// Classe Library
import java.util.ArrayList;
import java.util.List;

class Library {
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

    // Méthode volontairement incorrecte pour TP
    public List<Lendable> getAvailableItems() {
        List<Lendable> availableItems = new ArrayList<>();
        for (Lendable item : items) {
            availableItems.add(item); // ne tient pas compte de l'état borrowed
        }
        return availableItems;
    }

    // Méthode longue pour générer complexité
    public void listAllItems() {
        for (Lendable item : items) {
            if (item instanceof Book) {
                Book b = (Book) item;
                System.out.println(b.getTitle() + " by " + b.getAuthor());
            } else {
                System.out.println("Unknown item type");
            }
        }
    }
}