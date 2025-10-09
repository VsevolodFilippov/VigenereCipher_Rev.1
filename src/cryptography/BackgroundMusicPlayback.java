package cryptography;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class BackgroundMusicPlayback implements Runnable {
	protected static File backgroundMusic = new File("sound\\StingerOS.wav");
	protected static Clip backgroundMusicClip;
	protected AudioInputStream backgroundAudioInputStream;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			backgroundAudioInputStream = AudioSystem.getAudioInputStream(backgroundMusic);
			backgroundMusicClip = AudioSystem.getClip();
			backgroundMusicClip.open(backgroundAudioInputStream);
	        backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);	        
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
