import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

//ActionListener interface implementálása szükséges
public class MusicPlayer implements ActionListener {
    JFrame frame;
    JLabel songNameLabel=new JLabel();
    JButton zeneValasztoGomb=new JButton("MP3 zene választás");
    JButton playGomb=new JButton("Play");
    JButton pauseGomb=new JButton("Pause");
    JButton resumeGomb=new JButton("Resume");
    JButton stopGomb=new JButton("Stop");
    JFileChooser fileValaszto;
    FileInputStream fileInputStream;
    BufferedInputStream bufferedInputStream;
    File myFile=null;
    String filename;
    String filePath;
    long totalLength;
    long pause;
    Player player;
    Thread playThread;
    Thread resumeThread;
    
    MusicPlayer(){
	    setupGUI();
	    addActionEvents();
	    playThread=new Thread(runnablePlay);
	    resumeThread=new Thread(runnableResume);
    }
    
    public void setupGUI(){
        frame=new JFrame();
        frame.setTitle("Pallas MP3 Player");
        frame.getContentPane().setLayout(null);
        frame.getContentPane().setBackground(Color.lightGray);
        frame.setSize(440,200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        zeneValasztoGomb.setBounds(145,30,150,30);
        frame.add(zeneValasztoGomb);

        songNameLabel.setBounds(145,60,300,30);
        frame.add(songNameLabel);

        playGomb.setBounds(30,110,100,30);
        frame.add(playGomb);

        pauseGomb.setBounds(120,110,100,30);
        frame.add(pauseGomb);

        resumeGomb.setBounds(210,110,100,30);
        frame.add(resumeGomb);

        stopGomb.setBounds(300,110,100,30);
        frame.add(stopGomb);

    }
    public void addActionEvents(){
        //action listener gombokhoz hozzárendelése 
        zeneValasztoGomb.addActionListener(this);
        playGomb.addActionListener(this);
        pauseGomb.addActionListener(this);
        resumeGomb.addActionListener(this);
        stopGomb.addActionListener(this);
    }

    @Override //A mintasablon kitöltése "konkrétumokkal"
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==zeneValasztoGomb){
            //Dialógus ablak file-választáshoz
            fileValaszto=new JFileChooser();
            fileValaszto.setCurrentDirectory(new File("D:\\Zene\\"));
            fileValaszto.setDialogTitle("Select MP3");
            fileValaszto.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileValaszto.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
            if(fileValaszto.showOpenDialog(zeneValasztoGomb)==JFileChooser.APPROVE_OPTION){
                myFile=fileValaszto.getSelectedFile();
                filename=fileValaszto.getSelectedFile().getName();
                filePath=fileValaszto.getSelectedFile().getPath();
            }
        }
        if(e.getSource()==playGomb){
            //Lejátszás kezdete
          playThread.start();
          songNameLabel.setText("     Zene: "+filename);
        }
        if(e.getSource()==pauseGomb){
            //Pillanat stop üzemmód
                 if(player!=null){
                     try {
                         pause=fileInputStream.available();
                         player.close();
                     } catch (IOException e1) {
                         e1.printStackTrace();
                     }
                 }
        }

        if(e.getSource()==resumeGomb){
            //Lejátszás folytatása pillanat stop módból
           resumeThread.start();
        }
        if(e.getSource()==stopGomb){
            //Stop gomb
            if(player!=null){
                player.close();
                songNameLabel.setText("");
            }

        }

    }

  Runnable runnablePlay=new Runnable() {
      @Override
      public void run() {
          try {
              //Lejátszás gomb
              fileInputStream=new FileInputStream(myFile);
              bufferedInputStream=new BufferedInputStream(fileInputStream);
              player=new Player(bufferedInputStream);
              totalLength=fileInputStream.available();
              player.play();//A zene lejátszás indulhat
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (JavaLayerException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  };

    Runnable runnableResume=new Runnable() {
        @Override
        public void run() {
            try {
                //Lejátszás folytatása
                fileInputStream=new FileInputStream(myFile);
                bufferedInputStream=new BufferedInputStream(fileInputStream);
                player=new Player(bufferedInputStream);
                fileInputStream.skip(totalLength-pause); //Előző pozíciótól folytatás
                player.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
