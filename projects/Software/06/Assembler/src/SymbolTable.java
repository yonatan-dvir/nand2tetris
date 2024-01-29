import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Integer> symbol_table;

    // Creates an empty HashMap to be the symbol table.
    public SymbolTable(){
        this.symbol_table = new HashMap<>();
    }

    // Add symbol and it's address to the symbol level
    public void addEntry(String symbol, int address){
        this.symbol_table.put(symbol, address);
    }

    // If the symbol table contain the given symbol return true
    public boolean contains(String symbol){
        return this.symbol_table.containsKey(symbol);
    }

    // Return the address of the given symbol
    public int getAddress(String symbol){
        return symbol_table.get(symbol);
    }

    public void printTable() {
        System.out.println("Symbol Table contents:");
        for (HashMap.Entry<String, Integer> entry : symbol_table.entrySet()) {
            System.out.println("Symbol: " + entry.getKey() + ", Address: " + entry.getValue());
        }
    }
}

