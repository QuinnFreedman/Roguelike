import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Input extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;
	
	private static JTextArea inputArea;
	
	private static JFrame window;
	
	public Input()
	{
		window = new JFrame();
		window.setSize(200,60);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setTitle("Input Seed");
		window.setVisible(true);
		window.addKeyListener(this);
		window.setFocusable(true);
		window.setFocusTraversalKeysEnabled(false);
		
		JPanel mainPanel = new JPanel();
		//mainPanel.setOpaque(true);
		mainPanel.setBackground(Color.black);
		inputArea = new JTextArea(0,10);
		inputArea.setBackground(Color.black);
		inputArea.setForeground(Color.getColor("greyish", 0x999999));
		inputArea.setText(""+Main.seed);
		mainPanel.add(inputArea);
		window.add(mainPanel);
		inputArea.addKeyListener(this);
		inputArea.addFocusListener(new java.awt.event.FocusAdapter() {
		    public void focusGained(java.awt.event.FocusEvent evt) {
		        SwingUtilities.invokeLater(new Runnable() {
		            @Override
		            public void run() {
		                inputArea.selectAll();
		            }
		        });
		    }
		});
		inputArea.requestFocus();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode ();
		if(c==KeyEvent.VK_ENTER) {
			Roguelike.init(Integer.parseInt(inputArea.getText()));
			window.dispose();
        }
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}