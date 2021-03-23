import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Umschließende Anwendung
 */
public class FireworksApplication extends JFrame implements MouseMotionListener, MouseListener, ActionListener {

	/**
	 * Wenn {@code true} feuern alle paar Sekunden automatisch zufällig generierte
	 * Raketen vom unteren Rand des Displays
	 */
	private static final boolean AUTO_MODE = true;

	/**
	 * Timer für die Auto-Raketen (Zeit in ms)
	 */
	Timer autoModeTimer = new Timer(1500, this);

	/**
	 * Das Hauptdisplay der Anwendung auf dem alle Darstellung stattfindet (JPanel)
	 */
	private Display display;

	public static void main(String[] args) {
		new FireworksApplication();
	}

	/**
	 * Constructor der eigentlichen Anwendung
	 */
	public FireworksApplication() {
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		// Panel wird in die Mitte des Bildschirms gesetzt mit 1/2 Maß der Auflösung
		// (Klappt nur wirklich auf Single-Screens, ansonsten width und height einafch
		// beliebig anpassen)
		getToolkit();
		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		setBounds(screenWidth / 4, screenHeight / 4, screenWidth / 2, screenHeight / 2);
		setTitle(getClass().getName());

		addMouseListener(this);
		addMouseMotionListener(this);
		setVisible(true);

		display = new Display(getContentPane().getWidth(), getContentPane().getHeight());
		add(display);

		if (AUTO_MODE) { // Wenn Auto-Mode, dann Timer starten bei Programmstart
			autoModeTimer.start();
		}
	}

	public Display getDisplay() {
		return display;
	}

	/**
	 * Dient nur zum intervallartigen Erstellen neuer Raketen auf dem Display
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (AUTO_MODE && e.getSource().equals(autoModeTimer)) {
			if (MathUtils.randomChance(0.75)) {
				display.addRocket();
			}
		}
	}

	/*
	 * MouseListener für die manuellen Funktionen Klick = Abschuss Halten =
	 * Leuchtspur/Funken Loslassen = Explosion Analog zu dem was die Rocket-Klasse
	 * eigenständig tut
	 */

	@Override
	public void mousePressed(MouseEvent e) {
		display.getParticles().addAll(FireworksFactory.getShotSparks(e.getX(), e.getY(), display));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		display.getParticles().addAll(FireworksFactory.getFireworksExplosion(e.getX(), e.getY(), display));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		display.getParticles().addAll(FireworksFactory.getTrailSparks(e.getX(), e.getY(), display));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//

	}

	@Override
	public void mouseExited(MouseEvent e) {
		//

	}

	@Override
	public void mouseMoved(MouseEvent e) {

		//
	}

}
