import DBHelper.DBHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainFrame extends JDialog {
    private JPanel contentPane;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtTel;
    private JRadioButton rdoMale;
    private JRadioButton rdoFemale;
    private JComboBox cbxNationality;
    private JButton btnSave;
    private JButton btnNew;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JTable tbList;
    private JButton buttonOK;
    private JButton buttonCancel;

    public MainFrame() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

//        buttonOK.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onOK();
//            }
//        });

//        buttonCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Block code of mine
        loadNationality();
        initTable();
        loadTable();

        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtId.setText("");
                txtName.setText("");
                txtAddress.setText("");
                txtTel.setText("");
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = "insert into Person(name, address, tel, gender, nationality) " +
                        "values(?,?,?,?,?)";
                try (
                        Connection connection = DBHelper.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql,
                                PreparedStatement.RETURN_GENERATED_KEYS)
                ) {
                    preparedStatement.setString(1, txtName.getText());
                    preparedStatement.setString(2, txtAddress.getText());
                    preparedStatement.setString(3, txtTel.getText());
                    //
                    preparedStatement.setBoolean(4, rdoMale.isSelected());
                    //
                    var selectedNationality = (Nationality) cbxNationality.getSelectedItem();
                    preparedStatement.setString(5, selectedNationality.getId());
                    //
                    int rows = preparedStatement.executeUpdate();
                    //
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    if (resultSet.next()) {
                        int id = resultSet.getInt(1);
                        txtId.setText("" + id);
                    }
                    JOptionPane.showMessageDialog((Component) e.getSource(), "Information is saved", "Error", JOptionPane.INFORMATION_MESSAGE);
                    //
                    loadTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog((Component) e.getSource(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog((Component) e.getSource(), "Do you want to update?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                    return;
                }
                String sql = "update person " +
                        "set name = ?, address = ?, tel = ?, gender = ?, nationality = ? " +
                        "where id = ?";
                try (
                        Connection connection = DBHelper.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
                    preparedStatement.setString(1, txtName.getText());
                    preparedStatement.setString(2, txtAddress.getText());
                    preparedStatement.setString(3, txtTel.getText());
                    //
                    preparedStatement.setBoolean(4, rdoMale.isSelected());
                    //
                    var selectedNationality = (Nationality) cbxNationality.getSelectedItem();
                    preparedStatement.setString(5, selectedNationality.getId());
                    //
                    preparedStatement.setInt(6, Integer.parseInt(txtId.getText()));
                    //
                    int rows = preparedStatement.executeUpdate();
                    //
                    JOptionPane.showMessageDialog((Component) e.getSource(), "Information is saved", "Error", JOptionPane.INFORMATION_MESSAGE);
                    //
                    loadTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog((Component) e.getSource(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog((Component) e.getSource(), "Do you want to delete?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                    return;
                }
                String sql = "delete from person " +
                        "where id = ?";
                try (
                        Connection connection = DBHelper.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
                    preparedStatement.setInt(1, Integer.parseInt(txtId.getText()));
                    //
                    int rows = preparedStatement.executeUpdate();
                    //
                    JOptionPane.showMessageDialog((Component) e.getSource(), "Information is saved", "Error", JOptionPane.INFORMATION_MESSAGE);
                    //
                    loadTable();
                    //Đang bị lỗi chỗ này!
                    //btnNew.actionPerformed(e);
                    //btnNew.getAction().actionPerformed(e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog((Component) e.getSource(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        tbList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    int selectRow = tbList.getSelectedRow();
                    if (selectRow >= 1) {
                        txtId.setText(tbList.getValueAt(selectRow, 0).toString());
                        txtName.setText(tbList.getValueAt(selectRow, 1).toString());

                        var isMale = tbList.getValueAt(selectRow, 2).toString().equals("Male");
                        rdoMale.setSelected(isMale);
                        rdoFemale.setSelected(!isMale);

                        txtAddress.setText(tbList.getValueAt(selectRow, 3).toString());
                        txtTel.setText(tbList.getValueAt(selectRow, 4).toString());
                        for (int i = 0; i < cbxNationality.getItemCount(); i++) {
                            var item = cbxNationality.getItemAt(i);
                            var selectedId = tbList.getValueAt(selectRow, 5).toString();
                            Nationality nationality = (Nationality) item;
                            if (selectedId.trim().equals(nationality.getId())) {
                                cbxNationality.setSelectedItem(item);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MainFrame dialog = new MainFrame();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private DefaultComboBoxModel<Nationality> model;
    private DefaultTableModel tblModel;

    private void loadNationality() {
        model = new DefaultComboBoxModel<>();
        model.addElement(new Nationality("VN", "Vietnam"));
        model.addElement(new Nationality("JP", "Japan"));
        model.addElement(new Nationality("US", "USA"));
        model.addElement(new Nationality("LA", "Laos"));
        model.addElement(new Nationality("CM", "Compodia"));
        cbxNationality.setModel(model);
    }

    private void initTable() {
        tblModel = new DefaultTableModel();
        tblModel.setColumnIdentifiers(new String[]{"ID", "Name", "Gender", "Address", "Tel", "Nationality"});
        tbList.setModel(tblModel);
        tbList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);   //Search Gemini
        tbList.setAutoCreateRowSorter(true);                        //Search Gemini
        tbList.setDefaultEditor(Object.class, null);// Vô hiệu hóa tính năng click để thay đổi row

//        DefaultTableModel tableModel = new DefaultTableModel();
//        String[] columnNames = {"ID", "Name", "Gender", "Address", "Tel", "Nationality"};
//        tableModel.setColumnIdentifiers(columnNames);
//        tbList.setModel(tableModel);

    }

    private void loadTable() {
        String sql = "select * from person";
        try (
                Connection connection = DBHelper.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                tblModel.setRowCount(0);
                while (resultSet.next()) {
                    Object[] row = new Object[]{
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getBoolean("gender") ? "Male" : "Female",
                            resultSet.getString("address"),
                            resultSet.getString("tel"),
                            resultSet.getString("nationality")
                    };
                    tblModel.addRow(row);
                }
                tblModel.fireTableDataChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
