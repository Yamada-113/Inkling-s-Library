/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PanelGradasi;

/**
 *
 * @author HELLO
 */
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author HELLO
 */
public class gradientPanel extends JPanel{
    private Color colorStart = Color.BLACK;
    private Color colorEnd = Color.WHITE;

    public Color getColorStart() {
        return colorStart;
    }

    public void setColorStart(Color colorStart) {
        this.colorStart = colorStart;
    }

    public Color getColorEnd() {
        return colorEnd;
    }

    public void setColorEnd(Color colorEnd) {
        this.colorEnd = colorEnd;
    }
    
    public gradientPanel(){
    
    }
    
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradasi = new GradientPaint(0, 0, colorStart, getWidth(), getHeight(), colorEnd);
        g2d.setPaint(gradasi);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
}
