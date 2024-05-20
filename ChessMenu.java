import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*; 
import java.awt.image.BufferedImage;

public class ChessMenu extends JPanel
{
    private Image background;
    

    public ChessMenu()
    {
    setSize(704,710);
    setLayout(new FlowLayout(FlowLayout.CENTER,500,85));
    try {
        background = ImageIO.read(new File("./images/wood.png"));
    } catch (IOException e) {
    
        e.printStackTrace();
    }

        JLabel title;
        BufferedImage bimage;
        ImageIcon icon = new ImageIcon();
        try
        {
        bimage= ImageIO.read(new File("./images/title.png"));
        
        Image image = bimage.getScaledInstance(250, 200, Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        }
        catch
        (IOException e)
        {
            e.printStackTrace();
        }

        title = new JLabel();
        title.setIcon(icon);
        add(title);

        JButton player1 = new JButton();
        try
        {
        bimage= ImageIO.read(new File("./images/play.png"));
        
        Image image = bimage.getScaledInstance(165, 63, Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        }
        catch
        (IOException e)
        {
            e.printStackTrace();
        }  

        player1 = new JButton();
        player1.setIcon(icon);
        add(player1);
        player1.setMargin(new Insets(0, 0, 0, 0));
        player1.setContentAreaFilled(false);
        player1.setBorder(null);
        player1.setBounds(255, 150, icon.getIconWidth(), icon.getIconHeight());
        
        JButton exit = new JButton();
        try
        {
        bimage= ImageIO.read(new File("./images/exit.png"));
        
        Image image = bimage.getScaledInstance(165, 63, Image.SCALE_DEFAULT);
        icon = new ImageIcon(image);
        }
        catch
        (IOException e)
        {
            e.printStackTrace();
        }  

        exit = new JButton();
        exit.setIcon(icon);
        add(exit);
        exit.setMargin(new Insets(0, 0, 0, 0));
        exit.setContentAreaFilled(false);
        exit.setBorder(null);
        
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), null); 
    }
}
