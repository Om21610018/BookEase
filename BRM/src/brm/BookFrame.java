package brm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class BookFrame {
    Connection con;
    PreparedStatement ps;
    JFrame frame = new JFrame("BRM Project");
    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel insertPanel, viewPanel;
    JLabel l1, l2, l3, l4, l5;
    JTextField t1, t2, t3, t4, t5;
    JButton saveButton, updateButton, deleteButton;
    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel tm;
    String columnNames[] = {"Bookid", "Title", "Price", "Author", "Publisher"};

    JTable temp;
    DefaultTableModel tempd;

    void initComponents() {
        //Components for insert form
        l1 = new JLabel();
        l1.setText("Book ID : ");
        l2 = new JLabel();
        l2.setText("Title : ");
        l3 = new JLabel();
        l3.setText("Price : ");
        l4 = new JLabel();
        l4.setText("Author : ");
        l5 = new JLabel();
        l5.setText("Publisher : ");
        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();
        t5 = new JTextField();
        saveButton = new JButton("Save");
        l1.setBounds(100, 100, 100, 20);
        l2.setBounds(100, 150, 100, 20);
        l3.setBounds(100, 200, 100, 20);
        l4.setBounds(100, 250, 100, 20);
        l5.setBounds(100, 300, 100, 20);
        t1.setBounds(250, 100, 100, 20);
        t2.setBounds(250, 150, 100, 20);
        t3.setBounds(250, 200, 100, 20);
        t4.setBounds(250, 250, 100, 20);
        t5.setBounds(250, 300, 100, 20);
        saveButton.setBounds(100, 350, 100, 30);


        //Save button event HANDLING
        saveButton.addActionListener(new InsertBookRecord());


        //insertPanel;
        insertPanel = new JPanel();
        insertPanel.setLayout(null);// by default flowlayout hota hai
        insertPanel.add(l1);
        insertPanel.add(l2);
        insertPanel.add(l3);
        insertPanel.add(l4);
        insertPanel.add(l5);
        insertPanel.add(t1);
        insertPanel.add(t2);
        insertPanel.add(t3);
        insertPanel.add(t4);
        insertPanel.add(t5);
        insertPanel.add(saveButton);

        updateButton = new JButton("Update");

        //UPDATE EVENT HANDLING
        deleteButton = new JButton("Delete");
        //DELETE EVENT HANDLING
        viewPanel = new JPanel();
        viewPanel.add(updateButton);
        viewPanel.add(deleteButton);
        ArrayList<Book> bookList = fetchBookRecord();
        setDataOnTable(bookList);
        scrollPane = new JScrollPane(table);

        viewPanel.add(scrollPane);


        tabbedPane.add("Insert", insertPanel);
        tabbedPane.add("View", viewPanel);

        //Tab event handling; TO RESOLVE ISSUE OF CHANGE IN THE MYSQL TABLE
        tabbedPane.addChangeListener(new TabChangeHandler());


        frame.add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);


    }

    public BookFrame() {
        getConnectionFromMySQL();
        initComponents();
    }

    void getConnectionFromMySQL() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db1", "root", "Omkar@123");
            System.out.println("Connection Established");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // It will fetch all the book record;
    ArrayList<Book> fetchBookRecord() {
        ArrayList<Book> bookList = new ArrayList<Book>();// now we can store book type data in this arraylist
        String query = "SELECT * FROM Book";
        try {
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setBookid(rs.getInt(1));// rs is pointer which is pointing to the single row
                b.setTitle(rs.getString(2));
                b.setPrice(rs.getDouble(3));
                b.setAuthor(rs.getString(4));
                System.out.println("EXECUTED");
                b.setPublisher(rs.getString(5));
                bookList.add(b);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            return bookList;
        }
    }


    // INNER CLASS TO INSERT book record;
    class InsertBookRecord implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Book b1 = readFromData();
            String query = "INSERT into Book (bookid,title,price,author,publisher)values (?,?,?,?,?)";
            try {
                ps = con.prepareStatement(query);
                ps.setInt(1, b1.getBookid());
                ps.setString(2, b1.getTitle());
                ps.setDouble(3, b1.getPrice());
                ps.setString(4, b1.getAuthor());
                ps.setString(5, b1.getPublisher());
                ps.execute();// yaha kuch return nhi hoga isliye execute() no need of executeQuery();

                //Emptying the textfields after adding the data
                t1.setText(null);
                t2.setText(null);
                t3.setText(null);
                t4.setText(null);
                t5.setText(null);

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        // Function which will take data from the components and save it into database and table
        Book readFromData() {
            Book b1 = new Book();
            b1.setBookid(Integer.parseInt(t1.getText()));
            b1.setTitle(t2.getText());
            b1.setPrice(Double.parseDouble(t3.getText()));
            b1.setAuthor(t3.getText());
            b1.setPublisher(t4.getText());
            return b1;

        }
    }


    void setDataOnTable(ArrayList<Book> bookList) {
        Object[][] rowData = new Object[bookList.size()][5];// rows == bookList.size(), columns == 5
        for (int i = 0; i < bookList.size(); i++) {
            rowData[i][0] = bookList.get(i).getBookid();
            rowData[i][1] = bookList.get(i).getTitle();
            rowData[i][2] = bookList.get(i).getPrice();
            rowData[i][3] = bookList.get(i).getAuthor();
            rowData[i][4] = bookList.get(i).getPublisher();
        }


        tm = new DefaultTableModel(rowData, columnNames);
        table = new JTable(tm);

//        tm = new DefaultTableModel();
//        table = new JTable();
//        tm.setColumnCount(5);
//        tm.setRowCount(bookList.size());
//        tm.setColumnIdentifiers(columnNames);
//        for (int i = 0; i < bookList.size(); i++) {
//            tm.setValueAt(rowData[i][0], i, 0);
//            tm.setValueAt(rowData[i][1], i, 1);
//            tm.setValueAt(rowData[i][2], i, 2);
//            tm.setValueAt(rowData[i][3], i, 3);
//            tm.setValueAt(rowData[i][4], i, 4);
//        }


    }

    class TabChangeHandler implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            int index = tabbedPane.getSelectedIndex();//0 based;
            if (index == 0) {
                System.out.println("Insert tab selected");
            }
            if (index == 1) {
                System.out.println("View tab selected");
                ArrayList<Book> bookList = fetchBookRecord();
                updateTable(bookList);
            }

        }
    }

    void updateTable(ArrayList<Book> bookList) {
        Object[][] obj = new Object[bookList.size()][5];
        for (int i = 0; i < bookList.size(); i++) {
            obj[i][0] = bookList.get(i).getBookid();
            obj[i][1] = bookList.get(i).getTitle();
            obj[i][2] = bookList.get(i).getPrice();
            obj[i][3] = bookList.get(i).getAuthor();
            obj[i][4] = bookList.get(i).getPublisher();
        }
        tm.setRowCount(bookList.size());
        for (int i = 0; i < bookList.size(); i++) {
            tm.setValueAt(obj[i][0], i, 0);
            tm.setValueAt(obj[i][1], i, 1);
            tm.setValueAt(obj[i][2], i, 2);
            tm.setValueAt(obj[i][3], i, 3);
            tm.setValueAt(obj[i][4], i, 4);
        }
        table.setModel(tm);
    }

    ArrayList<Book> fetchBookRecords() {
        ArrayList<Book> bookList = new ArrayList<Book>();
        String q = "select * from book";
        try {
            ps = con.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setBookid(rs.getInt(1));
                b.setTitle(rs.getString(2));
                b.setPrice(rs.getDouble(3));
                b.setAuthor(rs.getString(4));
                b.setPublisher(rs.getString(5));
                bookList.add(b);
            }
        } catch (SQLException ex) {
            System.out.println("Exception: " + ex.getMessage());
        } finally {
            return bookList;
        }
    }

    class DeleteBookRecord implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int rowNo = table.getSelectedRow();
            if (rowNo != -1) {
                int id = (int) table.getValueAt(rowNo, 0);
                String q = "delete from book where bookid=?";
                try {
                    ps = con.prepareStatement(q);
                    ps.setInt(1, id);
                    ps.execute();

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                } finally {
                    ArrayList<Book> bookList = fetchBookRecords();
                    updateTable(bookList);
                }

            }

        }
    }

}
