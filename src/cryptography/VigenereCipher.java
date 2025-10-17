package cryptography;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class VigenereCipher extends SimpleFileVisitor<Path> implements ActionListener{
	//Logic
	private String keyWord;
	private int shift;	
	private int engStartIndex;
	private int engLength;
	private TreeMap<Integer, Character> plainAlphabet;
	private SortedMap<Integer, Character> tempMap;
	private char[][] vigenereSquare;
	private String plainText;
	private String cipher;	
	private int cipherCharsSequencer;
	private Timer cipherPrintTimer;
	private static Thread gameSoundThread;
	private static Thread backgroundSoundThread;
	
	//GUI	
	public int tempPassHash;
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JPanel imagePanel;
	private static BufferedImage img;
	private JPasswordField passwordField;
	private JButton okayButton;
	private JPanel passwordPane;
	private JLabel label;
	private JPanel adjustmentsPane;
	private JPanel actionPane;	
	private JTextField keywordTextField;	
	private JLabel shiftSpinnerLabel;
	private JSpinner shiftSpinner;
	private JPanel infoPanel;
	private JLabel informationLabel;
	private JScrollPane textCipherScrollPane;
	private JTextArea textCipherTextArea;
	private JButton cipherButton;
	private JButton decipherButton;
	private JButton readFileButton;
	private JButton writeFileButton;
	private JButton equalizeButton;
	private JButton clearScreenButton;
	private JButton exitButton;	
	private Random random;
	private Border messageLabelBeveledBorder;
	private int incrementR;
	private int incrementG;
	private int incrementB;	
	private int printRate;
			
	public VigenereCipher() {
		keyWord="shark";
		shift=0;	
		engStartIndex = 97;
		engLength = 26;
		plainText = "";	
		cipher = "";		
		tempPassHash = 1477914;
		img = null;		
		random = new Random();
		incrementR=255;
		incrementG=255;
		incrementB=255;
		printRate = 5;
		
		mainFrame = new JFrame("VigenereCipher");
		mainFrame.setResizable(false);
		try {
    	    img = ImageIO.read(new File("image\\Emblem.jpg"));
    	} catch (IOException e) {
    	} 
		mainPanel = new ImagePanel(new ImageIcon("image\\frameBackground.jpg").getImage());	
		imagePanel = new ImagePanel(new ImageIcon("image\\frameBackgroundBW.jpg").getImage());
		mainPanel.setLayout(new GridBagLayout());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setIconImage(img);
		mainPanel.setPreferredSize(new Dimension(1024,768));
		imagePanel.setPreferredSize(new Dimension(1024,768));
		mainFrame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		imagePanel.setLayout(new GridBagLayout());
		imagePanel.setVisible(false);
		passwordField = new JPasswordField(10);
		passwordField.setActionCommand("Ok");
		passwordField.setPreferredSize(new Dimension(200,22));
		passwordField.setEchoChar((char)926);
		passwordField.setCaretColor(Color.BLUE);
		passwordField.setBackground(new Color(0x18181A));
		passwordField.setForeground(Color.WHITE);
		passwordField.setHorizontalAlignment(JTextField.CENTER);
		passwordField.setFont(new Font("Aerial", Font.BOLD, 14));	
		passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x461215)));
		passwordField.addActionListener(this);	
		okayButton = new JButton("Ok");
		okayButton.setPreferredSize(new Dimension(50,22));
		okayButton.setBackground(new Color(0x18181A));
		okayButton.setForeground(new Color(0x4EA0DC));
		okayButton.setFocusable(false);
		okayButton.setBorder(BorderFactory.createSoftBevelBorder(0, Color.DARK_GRAY, new Color(0x461215)));
		okayButton.addActionListener(this);
		okayButton.setActionCommand("Ok");
		label = new JLabel("Password");
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Times New Roman", Font.ITALIC, 18));
		label.setLabelFor(passwordField);
		passwordPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		passwordPane.setOpaque(false);
		passwordPane.add(label);
		passwordPane.add(passwordField);
		passwordPane.add(okayButton);
		passwordPane.setVisible(true);		
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(620,0,0,0);
		mainPanel.add(passwordPane, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;		
		mainPanel.add(imagePanel, gbc);
				
		adjustmentsPane = new JPanel();
		adjustmentsPane.setPreferredSize(new Dimension(1024,135));
		adjustmentsPane.setBackground(Color.BLUE);
		adjustmentsPane.setOpaque(false);
		adjustmentsPane.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 6;
		imagePanel.add(adjustmentsPane, gbc);
		
		keywordTextField= new JTextField(keyWord,10);
		keywordTextField.setOpaque(false);
		keywordTextField.setCaretColor(Color.RED);
		keywordTextField.setForeground(new Color(0xFFFFFF));
		keywordTextField.setFont(new Font("Aerial", Font.BOLD, 18));		
		keywordTextField.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.DARK_GRAY));		
		keywordTextField.setHorizontalAlignment(JTextField.CENTER);		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,200,0,50);
		adjustmentsPane.add(keywordTextField, gbc);
		
		shiftSpinnerLabel = new JLabel("Shift");
		shiftSpinnerLabel.setForeground(Color.WHITE);
		shiftSpinnerLabel.setFont(new Font("Aerial", Font.ITALIC, 16));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0,0,0,5);
		adjustmentsPane.add(shiftSpinnerLabel, gbc);
		shiftSpinner = new JSpinner();
		shiftSpinner.setModel(new SpinnerNumberModel(0,0,null,1));		
		shiftSpinner.setFont(new Font("Aerial", Font.BOLD, 16));
		shiftSpinner.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		shiftSpinner.setFocusable(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.ipadx = 20;
		gbc.insets = new Insets(0,0,0,75);
		adjustmentsPane.add(shiftSpinner, gbc);
		
		Border infoPanelBeveledBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.DARK_GRAY);
		TitledBorder infoPanelBorder = BorderFactory.createTitledBorder(infoPanelBeveledBorder, "Info", TitledBorder.CENTER, 
				TitledBorder.DEFAULT_POSITION, new Font("Aerial", Font.ITALIC, 14), Color.WHITE);
		
		infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(350,90));		
		infoPanel.setBorder(infoPanelBorder);
		infoPanel.setLayout(new GridBagLayout());
		infoPanel.setOpaque(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;		
		adjustmentsPane.add(infoPanel, gbc);	
		
		messageLabelBeveledBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.BLACK);
		TitledBorder messageLabelBorder = BorderFactory.createTitledBorder(messageLabelBeveledBorder, "Message", TitledBorder.RIGHT, 
				TitledBorder.DEFAULT_POSITION, new Font("Aerial", Font.ITALIC, 14), Color.WHITE);
						
		String s = String.format("%tH:%tM", Calendar.getInstance(), Calendar.getInstance());
		informationLabel = new JLabel(s);
		informationLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		informationLabel.setForeground(new Color(0xFFFFFF));
		informationLabel.setBorder(BorderFactory.createEmptyBorder());
		informationLabel.setFocusable(false);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;		
		gbc.insets = new Insets(0,0,10,0);
		infoPanel.add(informationLabel, gbc);		
		
		textCipherTextArea = new JTextArea(
				"28.\sSummary of Text Aids.\r\n"
				+ "J-----J = Stress Mark\r\n"
				+ "X = Period\r\n"
				+ "Y = Comma\r\n"
				+ "UD = Question Mark\r\n"
				+ "XX = Colon\r\n"
				+ "XY = Semicolon\r\n"
				+ "KK------KK = Parenthesis \r\n"
				+ "YY = Hyphen, Dash, Slant\r\n");
		textCipherTextArea.setColumns(80);
		textCipherTextArea.setLineWrap(true);
		textCipherTextArea.setRows(5);
		textCipherTextArea.setWrapStyleWord(true);		
		textCipherTextArea.setBackground(new Color(20,20,20));
		textCipherTextArea.setForeground(new Color(0x91FFF4));
		textCipherTextArea.setSelectedTextColor(new Color(0xAD0011));
		textCipherTextArea.setSelectionColor(new Color(20,20,20));		
		textCipherTextArea.setFont(new Font("Aerial", Font.BOLD, 18));
		textCipherTextArea.setTabSize(3);		
		textCipherTextArea.setBorder(BorderFactory.createCompoundBorder(messageLabelBorder, 
				BorderFactory.createEmptyBorder(10, 25, 10, 50)));			
        
        textCipherScrollPane = new JScrollPane(textCipherTextArea);
		textCipherScrollPane.setPreferredSize(new Dimension(950,540));
		textCipherScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		textCipherScrollPane.setOpaque(false);		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 6;
		imagePanel.add(textCipherScrollPane, gbc);
		
		actionPane = new JPanel();
		actionPane.setPreferredSize(new Dimension(900,83));
		actionPane.setOpaque(false);
		actionPane.setBorder(BorderFactory.createMatteBorder(2,0,0,0,
  			  new Color(incrementR, incrementG, incrementB)));
		actionPane.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 6;
		imagePanel.add(actionPane, gbc);
		
		cipherButton = new JButton("Cipher");
		cipherButton.setFont(new Font("Verdana", Font.BOLD, 12));
		cipherButton.setBackground(new Color(20,20,20));
		cipherButton.setForeground(Color.LIGHT_GRAY);
		cipherButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(80,80,80), Color.BLACK));
		cipherButton.setFocusable(true);
		cipherButton.setMnemonic(0);
		cipherButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 25;
		gbc.ipady = 10;
		gbc.insets = new Insets(0, 0, 0, 10);
		actionPane.add(cipherButton, gbc);
		cipherButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cipherButtonActionPerformed(e);				
			}
		});
		
		decipherButton = new JButton("Decipher");
		decipherButton.setFont(new Font("Verdana", Font.BOLD, 12));
		decipherButton.setBackground(new Color(20,20,20));
		decipherButton.setForeground(Color.LIGHT_GRAY);
		decipherButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(80,80,80), Color.BLACK));
		decipherButton.setFocusable(true);
		decipherButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.insets = new Insets(0, 0, 0, 50);
		actionPane.add(decipherButton, gbc);
		decipherButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decipherButtonActionPerformed(e);				
			}
		});
		
		readFileButton = new JButton("ReadFile");
		readFileButton.setFont(new Font("Verdana", Font.BOLD, 12));
		readFileButton.setBackground(new Color(20,20,20));
		readFileButton.setForeground(Color.LIGHT_GRAY);
		readFileButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(80,80,80), Color.BLACK));
		readFileButton.setFocusable(false);
		readFileButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.insets = new Insets(0, 0, 0, 10);
		actionPane.add(readFileButton, gbc);
		readFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readFileButtonActionPerformed(e);				
			}
		});
		
		writeFileButton = new JButton("WriteFile");
		writeFileButton.setToolTipText("Write blank screen to erase file");
		writeFileButton.setFont(new Font("Verdana", Font.BOLD, 12));
		writeFileButton.setBackground(new Color(20,20,20));
		writeFileButton.setForeground(Color.LIGHT_GRAY);
		writeFileButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(80,80,80), Color.BLACK));
		writeFileButton.setFocusable(false);
		writeFileButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.insets = new Insets(0, 0, 0, 50);
		actionPane.add(writeFileButton, gbc);
		writeFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){				
					writeFileButtonActionPerformed(e);							
			}
		});
		
		equalizeButton = new JButton("Equalize");
		equalizeButton.setToolTipText("Flattens letters appearance frequency. Currently anavailable");
		equalizeButton.setFont(new Font("Verdana", Font.BOLD, 12));
		equalizeButton.setBackground(new Color(0x7F0000));
		equalizeButton.setForeground(Color.LIGHT_GRAY);
		equalizeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(80,80,80), Color.BLACK));
		equalizeButton.setFocusable(false);
		equalizeButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.ipadx = 20;
		gbc.ipady = 8;
		gbc.insets = new Insets(0, 0, 0, 10);
		actionPane.add(equalizeButton, gbc);
		equalizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				equalizeButtonActionPerformed(e);				
			}
		});
		
		clearScreenButton = new JButton("CLS");
		clearScreenButton.setToolTipText("Clear screen");
		clearScreenButton.setFont(new Font("Verdana", Font.BOLD, 12));
		clearScreenButton.setBackground(new Color(0,97,255));
		clearScreenButton.setForeground(Color.WHITE);
		clearScreenButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.BLACK));
		clearScreenButton.setFocusable(false);
		clearScreenButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		gbc.ipady = 8;
		gbc.insets = new Insets(0, 0, 0, 10);
		actionPane.add(clearScreenButton, gbc);
		clearScreenButton.setActionCommand(clearScreenButton.getName());
		clearScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearScreenButtonActionPerformed(e);				
			}
		});
		
		exitButton = new JButton("EXIT");
		exitButton.setFont(new Font("Verdana", Font.BOLD, 12));
		exitButton.setBackground(new Color(20,20,20));
		exitButton.setForeground(Color.WHITE);
		exitButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(0,75,14),Color.BLACK));
		exitButton.setFocusable(false);
		exitButton.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		gbc.ipady = 5;
		gbc.insets = new Insets(0, 50, 0, 0);
		actionPane.add(exitButton, gbc);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					exitButtonActionPerformed(e);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		});		
		
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);					
		GUIBorderFlash(); 		
	}	
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		new VigenereCipher();
		
		BackgroundMusicPlayback bgmp = new BackgroundMusicPlayback();
		
		backgroundSoundThread = new Thread(bgmp);
		backgroundSoundThread.setDaemon(true);
		backgroundSoundThread.start();
		
		GameSoundPlayback gsp = new GameSoundPlayback();
		gameSoundThread = new Thread(gsp);
		gameSoundThread.setDaemon(true);
		gameSoundThread.start();	
	}

	@Override
	//Password frame
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		String pass = "";
		if (cmd==okayButton.getActionCommand()) { //Process the password.			
			char[] input = passwordField.getPassword();
            for (int i = 0; i < input.length; i++) {
				pass+=input[i];
			}
            if(pass.hashCode()==tempPassHash) {
            	GameSoundPlayback.secureUplinkSoundClip.stop();
    			GameSoundPlayback.welcomeSoundClip.start();
            	passwordPane.setVisible(false);            
            	imagePanel.setVisible(true);
            }
            //Zero out the possible password, for security.
            Arrays.fill(input, '0');

            passwordField.selectAll();
            resetFocus();
        } 
	}
	
	protected void resetFocus() {
        passwordField.requestFocusInWindow();
    }	
	
	protected void vigenereSquareGenerator() {
		plainAlphabet = new TreeMap<Integer, Character>();		
		int charCounter=0;
		for(int i=0; i<engLength-shift%engLength; i++) {			
			plainAlphabet.put(Integer.valueOf(i), Character.valueOf((char)(engStartIndex+shift%engLength+charCounter)));
			charCounter++;
		}			
		charCounter=0;
		for(int i=engLength-shift%engLength; i<engLength; i++) {
			plainAlphabet.put(Integer.valueOf(i), Character.valueOf((char)(engStartIndex+charCounter)));
			charCounter++;
		}					
		vigenereSquare = new char[plainAlphabet.size()][plainAlphabet.size()];
		tempMap = new TreeMap<Integer, Character>();		
			int iterator = 1;
			int columnCounter = 0;
			int rawCounter = 0;
			do {
				tempMap = plainAlphabet.tailMap(iterator);
				for(int k = 0; k < plainAlphabet.size(); k++) {
					if(tempMap.get(k)!=null) {
						vigenereSquare[rawCounter][columnCounter]=tempMap.get(k);
						columnCounter++;
					}
				}
				tempMap = plainAlphabet.headMap(iterator);
				for(int l = 0; l < plainAlphabet.size(); l++) {
					if(tempMap.get(l)!=null) {
						vigenereSquare[rawCounter][columnCounter]=tempMap.get(l);
						columnCounter++;
					}
				}
				columnCounter=0;
				rawCounter++;
				iterator++;					
			}
			while(rawCounter!=vigenereSquare.length);			
	}
	
	protected String cipheringEngine() {
		vigenereSquareGenerator();
		char cipherTempChar;
    	int keyWordIterator=0;
    	int charCounter=0;
    	int columnCounter = 0;
		int rawCounter = 0;
    	for (int i = 0; i < plainText.length(); i++) {
			cipherTempChar = Character.toLowerCase(plainText.charAt(i));
			if(!Character.isLetter(cipherTempChar)) {
				continue;
			}
			if(cipherTempChar>engStartIndex+engLength||cipherTempChar<engStartIndex) {
				informationLabel.setText("English letters only");
				break;
			} 
			if(Character.toLowerCase(keyWord.charAt(keyWordIterator%keyWord.length()))>engStartIndex+engLength||
					Character.toLowerCase(keyWord.charAt(keyWordIterator%keyWord.length()))<engStartIndex) {
				informationLabel.setText("English letters only");
				break;
			} else if(Character.isLetter(cipherTempChar)) {
				for (int j = 0; j < vigenereSquare.length; j++) {
					if(vigenereSquare[j][0]==Character.toLowerCase(keyWord.charAt(keyWordIterator%keyWord.length()))) {
						rawCounter = j;					
					}
				}
				
				for (int k = 0; k < plainAlphabet.size(); k++) {
					if(plainAlphabet.get(k)==cipherTempChar) {
						columnCounter = k;							
					}						
				}
			} else continue;
			
			cipher = cipher.concat(String.valueOf(Character.toUpperCase(vigenereSquare[rawCounter][columnCounter])));
			charCounter++;
			//if(charCounter%50==0)cipher = cipher.concat("\n");
			if(charCounter%4==0)cipher = cipher.concat("\s");			
			keyWordIterator++;
		}
    	return cipher;
	}
	
	protected void cipherButtonActionPerformed(ActionEvent e) {
		cipher = "";
		keyWord = keywordTextField.getText();		
		if (keyWord.length()!=0) {			
			shift = (int)shiftSpinner.getValue();
			plainText = textCipherTextArea.getText();
			cipheringEngine();	
			cipherPrint(printRate);			
			plainText="";
			keyWord = "";
			shift = 0;
		} else {
			informationLabel.setForeground(new Color(0xD80000));
			informationLabel.setText("No keyword");
		}
	}	
	
	protected String decipheringEngine() {
		vigenereSquareGenerator();
		char plainTextTempChar;
    	int keyWordIterator=0;
    	int charCounter=0;
    	int columnCounter = 0;
		int rawCounter = 0;	
		
    	for (int i = 0; i < cipher.length(); i++) {
			plainTextTempChar = cipher.charAt(i);	
			if(Character.isLetter(plainTextTempChar)) {
				for (int j = 0; j < vigenereSquare.length; j++) {
					if(vigenereSquare[j][0]==Character.toLowerCase(keyWord.charAt(keyWordIterator%keyWord.length()))) {
						rawCounter = j;					
					}
				}
				for (int k = 0; k < vigenereSquare.length; k++) {
					if(vigenereSquare[rawCounter][k]==Character.toLowerCase(plainTextTempChar)) {
						columnCounter=k;							
					}						
				}
			}else continue;
			plainText = plainText.concat(String.valueOf(Character.toLowerCase(plainAlphabet.get(columnCounter))));
			keyWordIterator++;
		} 
    	return plainText;
	}
	
	protected void decipherButtonActionPerformed(ActionEvent e) {
		plainText = "";
		keyWord = keywordTextField.getText();		
		if (keyWord.length()!=0) {			
			shift = (int)shiftSpinner.getValue();
			cipher = textCipherTextArea.getText();
			decipheringEngine();
			textCipherTextArea.setText(plainText);
			plainText="";
			cipher="";
			keyWord = "";
			shift = 0;
		} else {
			informationLabel.setForeground(new Color(0xD80000));
			informationLabel.setText("No keyword");
		}
	}	
	
	protected void readFileButtonActionPerformed(ActionEvent e) {
		textCipherTextArea.setText("");
		textCipherTextArea.setEditable(true);
		try ( FileReader fr = new FileReader("files\\source.file")){ 
		      int c; 
		      // Read and display the file. 
		      while((c = fr.read()) != -1) textCipherTextArea.append(String.valueOf((char)c));; 
		 
		    } catch(IOException ioe) { 
		      System.out.println("I/O Error: " + ioe); 
		    }
			informationLabel.setForeground(new Color(0xD8B400));
			informationLabel.setText("Path:\s"+"\\files\\source.file");
			try {
				visitFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	
	protected void writeFileButtonActionPerformed(ActionEvent e) {
		String source = textCipherTextArea.getText();		
			    char[] buffer = new char[source.length()]; 
			    source.getChars(0, source.length(), buffer, 0); 			    
			    try ( FileWriter f0 = new FileWriter("files\\target.file")) { 
			      // write to first file 
			      for (int i=0; i < buffer.length; i += 1) { 
			    	  if(i%50==0&&i!=0)f0.write('\n');
			        f0.write(buffer[i]); 
			      } 
			      informationLabel.setForeground(new Color(0xD8B400));
			      informationLabel.setText("Path:\s"+"\\files\\target.file");			      		 
			    } catch(IOException ioe) { 
			      ioe.printStackTrace(); 
			    }
			    try {
					visitFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	}
	
	protected void equalizeButtonActionPerformed(ActionEvent e) {	
		textCipherTextArea.setEditable(false);
		TreeMap<Character, Integer> openAlphabet = new TreeMap<Character, Integer>();
		for (int i = 0; i < engLength; i++) {
			openAlphabet.put((char)(engStartIndex+i), 0);
		}
		String processedText = textCipherTextArea.getText();
		for (int i = 0; i < processedText.length(); i++) {
			char tempKey = Character.toLowerCase(processedText.charAt(i));
			if(Character.isLetter(tempKey)) {
				int tempValue = openAlphabet.get(tempKey)+1;
				openAlphabet.put(tempKey, tempValue);
				tempValue=0;
			}
		}		
			
		Set<Map.Entry<Character, Integer>> charsEncounter = openAlphabet.entrySet();
		int totalEntries = 0;
		double appearancePercentage = 0;
			
		for(Map.Entry<Character, Integer> me: charsEncounter) {
				totalEntries +=me.getValue();
		}
		textCipherTextArea.append("\n");
		for (int i = 0; i < textCipherTextArea.getColumns(); i++) {
			textCipherTextArea.append("_");
		}
		textCipherTextArea.append("\n");
		textCipherTextArea.append("Total chars:\s"+String.valueOf(totalEntries)+"\n");
		int average = totalEntries/engLength;		
		textCipherTextArea.append("Ch."+"\t"+"Qtty."+"\t"+"\sDist."+"\n");		
		
		int textLengthDivider = 1; 
		if(totalEntries>1000) {
			textLengthDivider = Math.ceilDiv(totalEntries,1000);
		}
		
		TreeMap<Character, Integer> equalizingMap = new TreeMap<Character, Integer>();
		for(Map.Entry<Character, Integer> me: charsEncounter) {
			appearancePercentage = (double)me.getValue()*100/totalEntries;
			textCipherTextArea.append(String.valueOf("\s"+me.getKey()).toUpperCase()+
					"\t\s"+String.valueOf(me.getValue())+"\t"+String.format("%.2f",appearancePercentage)+"%\t");
			for (int i = 0; i < me.getValue(); i++) {	
					if(i%textLengthDivider==0)textCipherTextArea.append("|");
			}	
			int absoluteDifference = me.getValue()-average;
			equalizingMap.put(me.getKey(), absoluteDifference);
			if(absoluteDifference<0) textCipherTextArea.append("\s(-"+String.valueOf(Math.abs(absoluteDifference)+")"+"\n"));
			else if(absoluteDifference>0) textCipherTextArea.append("\s(+"+String.valueOf(Math.abs(absoluteDifference)+")"+"\n"));
			else textCipherTextArea.append("\n");
		}			
		
		for (int i = 0; i < textCipherTextArea.getColumns(); i++) {
			textCipherTextArea.append("_");
		}
	}
	
	protected void clearScreenButtonActionPerformed(ActionEvent e){
		textCipherTextArea.setEditable(true);
		textCipherTextArea.setText("");
	}
	
	public void visitFile() throws IOException{
		Path start = Path.of("files");
		  Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
		         @Override	         
		         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		        	Files.setAttribute(file, "creationTime", FileTime.fromMillis(Long.MAX_VALUE));
		        	Files.setAttribute(file, "lastModifiedTime", FileTime.fromMillis(Long.MAX_VALUE));
		        	Files.setAttribute(file, "lastAccessTime", FileTime.fromMillis(Long.MAX_VALUE));
		        	return FileVisitResult.CONTINUE;
		         }
		         
		         @Override
		         public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
		             if (e == null) {
		                 Files.setAttribute(dir, "creationTime", FileTime.fromMillis(Long.MAX_VALUE));
		                 Files.setAttribute(dir, "lastModifiedTime", FileTime.fromMillis(Long.MAX_VALUE));
			        	 Files.setAttribute(dir, "lastAccessTime", FileTime.fromMillis(Long.MAX_VALUE));
		                 return FileVisitResult.CONTINUE;
		             } else {
		                 // directory iteration failed
		                 throw e;
		             }
		         }
			});
	}
	
	public void cipherPrint(int speed){
		textCipherTextArea.setText("");
		 int delay2 = speed; 
			 if(cipher.length()!=0) {
			 cipherCharsSequencer = 0;
				  ActionListener taskPerformer2 = new ActionListener() {
				      public void actionPerformed(ActionEvent evt) {
				    	  textCipherTextArea.append(String.valueOf(cipher.charAt(cipherCharsSequencer)));
				    	  if(cipherCharsSequencer>=cipher.length()-1) {
				    		  cipherPrintTimer.stop();
				    		  cipherCharsSequencer=0;
				    		  cipher="";
				    	  } else cipherCharsSequencer++;	    	  
				      }
				  };cipherPrintTimer = new Timer(delay2, taskPerformer2);
				cipherPrintTimer.start(); 
			 }else informationLabel.setText("No cipher to print");
	}
	
	public void GUIBorderFlash() {
		int delay = 10000; 	
		  ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  informationLabel.setForeground(new Color(0xFFFFFF));
		    	  String s = String.format("%tH:%tM", Calendar.getInstance(), Calendar.getInstance());
		    	  informationLabel.setText(s);		    	  
		    	  actionPane.setBorder(BorderFactory.createMatteBorder(2,0,0,0,
		    			  new Color(incrementR, incrementG, incrementB)));
		    	  if(passwordPane.isVisible()) {
		    		  passwordField.setBorder(BorderFactory.createMatteBorder(0,0,1,0,
			    			  new Color(incrementR, incrementG, incrementB)));
		    	  }	    	  		    	  
		    	  TitledBorder messageLabelBorder = BorderFactory.createTitledBorder(messageLabelBeveledBorder, "Message", TitledBorder.RIGHT, 
			  				TitledBorder.DEFAULT_POSITION, new Font("Aerial", Font.ITALIC, 14),
			  				new Color((incrementR+150)%255, (incrementG+150)%255, (incrementB+150)%255));	    	  
		    	  textCipherTextArea.setBorder(BorderFactory.createCompoundBorder(messageLabelBorder, 
		  				BorderFactory.createEmptyBorder(10, 25, 10, 50)));
		    	  textCipherTextArea.setCaretColor(new Color((incrementR+50)%255, (incrementG+50)%255, (incrementB+50)%255));
		    	  incrementR = random.nextInt(255);
		    	  incrementG = random.nextInt(255);
		    	  incrementB = random.nextInt(255);		    	 
		      }
		  };new Timer(delay, taskPerformer).start();
	}	
	
	protected void exitButtonActionPerformed(ActionEvent e) throws InterruptedException{		
		if(GameSoundPlayback.secureUplinkSoundClip.isActive()||
				GameSoundPlayback.welcomeSoundClip.isActive()) {
					GameSoundPlayback.secureUplinkSoundClip.stop();
					GameSoundPlayback.welcomeSoundClip.stop();
		}			
		GameSoundPlayback.closingRootSoundClip.start();		
		Thread.sleep(3000);
		System.exit(0);
	}
}
