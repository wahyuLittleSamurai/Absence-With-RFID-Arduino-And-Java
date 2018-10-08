package javaapplication2;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RifqiTh0kz
 */
import gnu.io.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Serial implements SerialPortEventListener {

    /**
     * Deklasrasi objek dari class Monitor
     */
    form window = null;
    private Enumeration port = null;
    private final HashMap portMap = new HashMap();
    private CommPortIdentifier portIdentifier = null;
    private SerialPort serialPort = null;
    private InputStream inPut = null;
    private OutputStream outPut = null;
    private boolean serialConnected = false;
    final static int TIMEOUT = 2000;
    String dataIn = "";
    String statusPort = "";
    String stringSerial="";

    public Serial(form window) {
        this.window = window;
    }

    /**
     * Cek PORT yang tersedia
     */
    public void cekSerialPort() {
        port = CommPortIdentifier.getPortIdentifiers();
        while (port.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) port.nextElement();
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                window.jComboBox1.addItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }

    final public boolean getConnected() {
        return serialConnected;
    }

    public void setConnected(boolean serialConnected) {
        this.serialConnected = serialConnected;
    }

    public void connect() {
        String selectedPort = (String) window.jComboBox1.getSelectedItem();
        portIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
        CommPort commPort = null;
        try {
            commPort = portIdentifier.open(null, TIMEOUT);
            serialPort = (SerialPort) commPort;
            setConnected(true);
            window.jButton1.setText("Disconnect");
        } catch (PortInUseException e) {
            statusPort = selectedPort + " is in use. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
        } catch (Exception e) {
            statusPort = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
        }
    }

    public void disconnect() {
         try {
            serialPort.removeEventListener();
            serialPort.close();
            inPut.close();
            setConnected(false);
            statusPort = "PORT disconnect successfully";            
            JOptionPane.showMessageDialog(null, statusPort);
            window.jButton1.setText("Connect");
        } catch (Exception e) {
            statusPort = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
        }
    }
   
    public boolean initIOStream() {
        boolean successful = false;
        try {
            inPut = serialPort.getInputStream();
            outPut = serialPort.getOutputStream();
            
            successful = true;
            return successful;
        } catch (IOException e) {
            statusPort = "I/O Streams failed to open. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusPort);
            return successful;
        }
    }

    public void kirimData(Byte a){
        try {
            outPut.write(a);
            outPut.write(10);
            outPut.flush();
        } catch (IOException ex) {
            System.out.println("Kirim Gagal");
        }
    }
    
    public void initListener() {
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    public ImageIcon ResizeImage(String ImagePath)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(window.jLabel8.getWidth(), window.jLabel8.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
    public void serialEvent(SerialPortEvent evt) {
        char dataSerial = 0; // Untuk menampung input dari serial port 
        
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                
                dataSerial = (char) inPut.read();
                if(dataSerial != 13){
                    
                    stringSerial += dataSerial;
                }
                else
                {
                    window.jTextField3.setText("");
                    window.jTextField5.setText("");
                    window.jTextArea1.setText("");
                    window.jLabel8.setIcon(null);
                    window.jLabel8.revalidate();
                    window.jLabel8.setText("Browse");
                    stringSerial = stringSerial.replaceAll("[^a-zA-Z0-9]", "");
                    window.jTextField4.setText(String.valueOf(stringSerial));
                    
                    Connection connection4 = connect.getConnection();
                    String myQuery4 = "SELECT * FROM tblsiswa WHERE rfid = '"+stringSerial+"'";
                    PreparedStatement statement4 = (PreparedStatement) connection4.prepareStatement(myQuery4);
                    ResultSet rs4 = statement4.executeQuery();
                    while (rs4.next()) {   
                        System.out.println("masuk query");
                        String rfid = rs4.getString(2);
                        String nama = rs4.getString(3);
                        String status = rs4.getString(8);
                        String kelas = rs4.getString(4);
                        String alamat = rs4.getString(5);
                        String namaImg = rs4.getString(6);
                        String waktu = window.jLabel9.getText() +" "+ window.jLabel10.getText();
                        
                        if(rfid.equals(stringSerial))
                        {
                            String getNameOfImages;
                            String path;
                            String namaFile;
                            
                            System.out.println(rfid);
                            System.out.println(nama);
                            System.out.println(status);
                            
                            window.jTextField3.setText(nama);
                            window.jTextField4.setText(rfid);
                            window.jTextField5.setText(kelas);
                            window.jTextArea1.setText(alamat);
                            getNameOfImages = namaImg;


                            path = "src\\images\\"+namaImg;
                            namaFile = getNameOfImages;
                            window.jLabel8.setIcon(ResizeImage(path));
                            window.jLabel8.setText("");
                            
                            if(status.equals("out")){
                                Connection connection2 = connect.getConnection();
                                String myQuery2 = "INSERT INTO tblreport(rfid, nama, keterangan, waktu) VALUES('"+rfid+"','"+nama+"','in','"+waktu+"')";
                                PreparedStatement statement2 = (PreparedStatement) connection2.prepareStatement(myQuery2);
                                statement2.execute();
                                Connection connection3 = connect.getConnection();
                                String myQuery3 = "UPDATE tblsiswa SET keterangan='in' WHERE rfid = '"+rfid+"'";
                                PreparedStatement statement3 = (PreparedStatement) connection3.prepareStatement(myQuery3);
                                statement3.execute();
                            }
                            else{
                                Connection connection2 = connect.getConnection();
                                String myQuery2 = "INSERT INTO tblreport(rfid, nama, keterangan, waktu) VALUES('"+rfid+"','"+nama+"','out','"+waktu+"')";
                                PreparedStatement statement2 = (PreparedStatement) connection2.prepareStatement(myQuery2);
                                statement2.execute();
                                Connection connection3 = connect.getConnection();
                                String myQuery3 = "UPDATE tblsiswa SET keterangan='out' WHERE rfid = '"+rfid+"'";
                                PreparedStatement statement3 = (PreparedStatement) connection3.prepareStatement(myQuery3);
                                statement3.execute();
                            } 
                            
                        }
                        else
                        {
                            System.out.println(rfid);
                            System.out.println(stringSerial);
                        }
                    }
                    stringSerial="";
                }
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            } catch (SQLException ex) {
                Logger.getLogger(Serial.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
