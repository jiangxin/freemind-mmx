import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

public class SplashScreen extends Container {

	private FreeMindPDA main;
	private Button btnLogo;
	private Timer goAwayTimer;
	private Timer startLoadTimer;
	
	public SplashScreen(FreeMindPDA main) {
		super();
		this.main=main;
		
	}

	public void onStart() {
		add(new Label("http://freemind.sf.net"),CENTER,TOP);
		add(new Label("An LGPL'd  version of FreeMind"),CENTER,AFTER+3);
		add(new Label("for PDAs by vik@diamondage.co.nz"),CENTER,AFTER+2);
		btnLogo = new Button(new Image("fmlogo.bmp"));
		add(btnLogo, CENTER, CENTER);
		btnLogo.setBorder(Button.BORDER_NONE);
		add(new Label("http://www.superwaba.com"),CENTER,BOTTOM);
		add(new Label("Built with SuperWaba"),CENTER,BEFORE);
		goAwayTimer=addTimer(7000);
		startLoadTimer=addTimer(10);
	}

	/**
	 * Standard event handler. Trap cases when we've been too quick for
	 * our own good and the timers haven't been initialised yet. This happens
	 * infrequently, but it does happen.
	 */
	public void onEvent(Event event) {
		if (event.type == ControlEvent.PRESSED) {
			if (event.target == btnLogo) {
				/* Byebye */
				if (goAwayTimer!=null) removeTimer(goAwayTimer);
				getParentWindow().swap(null);
			}
		} else if (event.type==ControlEvent.TIMER) {
			if ((goAwayTimer!=null)&&(goAwayTimer.triggered)) {
				removeTimer(goAwayTimer);
				getParentWindow().swap(null);
			} else if ((startLoadTimer!=null)&&(startLoadTimer.triggered)) {
				/* Our container is now established. Redraw it and
				 * start loading the default file. */
				repaintNow();
				removeTimer(startLoadTimer);

				/* Check Superwaba version. Minimum 4.5 */
				if (Settings.version<450) {
					MessageBox mb=new MessageBox("Error", "This program requires SuperWaba|"+
						"version 4.5a or Better. Please|update from:|http://www.superwaba.com",
						new String[] {"Abort"});
					getParentWindow().popupBlockingModal(mb);
					mb.repaintNow();
					main.exit(0);
				}

				/* Build the icons in the main routine. */
				main.buildScaledIcons();
				repaintNow();
				/* Build a tree structure based on the last file. */
				main.loadFile("Test.mm");
			}				
		}
	}
}
