import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class FirstFitMemoryAllocation {

    // Represents a single memory block with its allocation properties
    static class MemoryBlock {
        int blockSize;        
        int allocatedSize;    
        boolean isOccupied;   

        MemoryBlock(int blockSize) {
            this.blockSize = blockSize;
            this.allocatedSize = 0;
            this.isOccupied = false;
        }
    }

    private ArrayList<MemoryBlock> memoryBlocks;
    private JTable memoryTable;
    private MemoryTableModel tableModel;

    public FirstFitMemoryAllocation() {
        memoryBlocks = new ArrayList<>();
        // Initialize memory blocks with predefined sizes
        initializeMemoryBlocks();
    }

    // Create initial memory blocks with different sizes
    private void initializeMemoryBlocks() {
        memoryBlocks.add(new MemoryBlock(200));
        memoryBlocks.add(new MemoryBlock(300));
        memoryBlocks.add(new MemoryBlock(100));
        memoryBlocks.add(new MemoryBlock(500));
        memoryBlocks.add(new MemoryBlock(50));
    }

    // Main method to set up the GUI for memory allocation simulation
    public void createAndShowGUI() {
        JFrame frame = new JFrame("First Fit Memory Allocation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        // Create table model to display memory block information
        tableModel = new MemoryTableModel(memoryBlocks);
        memoryTable = createMemoryTable();

        // Create input panel for user interactions
        JPanel inputPanel = createInputPanel();
        JScrollPane tableScrollPane = new JScrollPane(memoryTable);

        // Set up frame layout
        frame.setLayout(new BorderLayout(10, 10));
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Configure table appearance and renderer
    private JTable createMemoryTable() {
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setDefaultRenderer(Object.class, new CustomRenderer());
        return table;
    }

    // Create input panel with buttons for memory management
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));

        JLabel processLabel = new JLabel("Process Size (KB):");
        processLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField processField = new JTextField(10);
        processField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton allocateButton = createStyledButton("Allocate Memory");
        JButton deallocateButton = createStyledButton("Deallocate Block");
        JButton resetButton = createStyledButton("Reset Memory");

        allocateButton.addActionListener(e -> handleAllocate(processField));
        deallocateButton.addActionListener(e -> handleDeallocate());
        resetButton.addActionListener(e -> handleReset());

        panel.add(processLabel);
        panel.add(processField);
        panel.add(allocateButton);
        panel.add(deallocateButton);
        panel.add(resetButton);

        return panel;
    }

    // Handle memory allocation when the user inputs a process size
    private void handleAllocate(JTextField processField) {
        try {
            int processSize = Integer.parseInt(processField.getText());
            if (processSize <= 0) throw new NumberFormatException();
            allocateMemory(processSize);
            tableModel.fireTableDataChanged();
            processField.setText("");
        } catch (NumberFormatException ex) {
            // Show error for invalid input
            JOptionPane.showMessageDialog(null, "Please enter a valid positive process size.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handle memory block deallocation
    private void handleDeallocate() {
        String blockInput = JOptionPane.showInputDialog("Enter Block Number to Deallocate:");
        if (blockInput != null) {
            try {
                int blockIndex = Integer.parseInt(blockInput) - 1;
                deallocateMemory(blockIndex);
                tableModel.fireTableDataChanged();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid block number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Handle memory reset to clear all allocations
    private void handleReset() {
        resetMemory();
        tableModel.fireTableDataChanged();
    }

    // Create styled buttons with consistent look and feel
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // First Fit memory allocation algorithm
    private void allocateMemory(int processSize) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.isOccupied && block.blockSize >= processSize) {
                block.allocatedSize = processSize;
                block.isOccupied = true;
                JOptionPane.showMessageDialog(null, "Process allocated to Block " + (i + 1), "Allocation Successful", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        // Show error if no suitable block is found
        JOptionPane.showMessageDialog(null, "No suitable block found for process size " + processSize + " KB.", "Allocation Failed", JOptionPane.ERROR_MESSAGE);
    }

    // Deallocate a specific memory block
    private void deallocateMemory(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= memoryBlocks.size()) {
            JOptionPane.showMessageDialog(null, "Invalid block number.", "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MemoryBlock block = memoryBlocks.get(blockIndex);
        if (block.isOccupied) {
            block.allocatedSize = 0;
            block.isOccupied = false;
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " deallocated.", "Deallocation Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Block " + (blockIndex + 1) + " is already free.", "Deallocation Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reset all memory blocks to their initial state
    private void resetMemory() {
        for (MemoryBlock block : memoryBlocks) {
            block.allocatedSize = 0;
            block.isOccupied = false;
        }
        JOptionPane.showMessageDialog(null, "All memory blocks have been reset.", "Reset Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom table model to display memory block details
    static class MemoryTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Block Number", "Block Size (KB)", "Allocated Size (KB)", "Free Size (KB)", "Occupied (Yes/No)"};
        private final ArrayList<MemoryBlock> memoryBlocks;

        MemoryTableModel(ArrayList<MemoryBlock> memoryBlocks) {
            this.memoryBlocks = memoryBlocks;
        }

        @Override
        public int getRowCount() {
            return memoryBlocks.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MemoryBlock block = memoryBlocks.get(rowIndex);
            switch (columnIndex) {
                case 0: return rowIndex + 1;
                case 1: return block.blockSize;
                case 2: return block.allocatedSize;
                case 3: return block.blockSize - block.allocatedSize;
                case 4: return block.isOccupied ? "Yes" : "No";
            }
            return null;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    // Custom table cell renderer to style table appearance
    static class CustomRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value.toString());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            if (row % 2 == 0) {
                label.setBackground(new Color(240, 240, 240));
            } else {
                label.setBackground(Color.WHITE);
            }

            if (column == 4) {
                String isOccupied = value.toString();
                label.setBackground(isOccupied.equals("Yes") ? new Color(144, 238, 144) : new Color(255, 99, 71));
            }

            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FirstFitMemoryAllocation().createAndShowGUI());
    }
}