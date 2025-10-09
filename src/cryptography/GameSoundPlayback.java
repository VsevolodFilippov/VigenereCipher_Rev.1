package cryptography;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSoundPlayback implements Runnable{
	protected static File secureUplinkSound = new File("sound\\secureUplink.wav");
	protected static File welcomeSound = new File("sound\\welcome.wav");
	protected static File closingRootSound = new File("sound\\closingRoot.wav");
	protected static Clip secureUplinkSoundClip;
	protected static Clip welcomeSoundClip;
	protected static Clip closingRootSoundClip;
	protected AudioInputStream secureUplinkInputStream;
	protected AudioInputStream welcomeInputStream;
	protected AudioInputStream closingRootInputStream;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			secureUplinkInputStream = AudioSystem.getAudioInputStream(secureUplinkSound);
			secureUplinkSoundClip = AudioSystem.getClip();
			secureUplinkSoundClip.open(secureUplinkInputStream);
			secureUplinkSoundClip.start();
			welcomeInputStream = AudioSystem.getAudioInputStream(welcomeSound);
			welcomeSoundClip = AudioSystem.getClip();
			welcomeSoundClip.open(welcomeInputStream);
			closingRootInputStream = AudioSystem.getAudioInputStream(closingRootSound);
			closingRootSoundClip = AudioSystem.getClip();
			closingRootSoundClip.open(closingRootInputStream);			
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
