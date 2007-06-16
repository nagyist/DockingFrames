/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.theme.bubble;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonHandler;
import bibliothek.gui.dock.themes.basic.action.BasicDropDownButtonModel;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A button which can be pressed by the user either to execute 
 * a {@link bibliothek.gui.dock.action.DockAction} or to show a popup-menu
 * with a selection of <code>DockActions</code>. This button uses a 
 * {@link BubbleDropDownView} to trigger the various actions.
 * @author Benjamin Sigg
 */
public class RoundDropDownButton extends JComponent{
	/** the animation that changes the colors */
    private BubbleColorAnimation animation;
    
    /** a model containing all information needed to paint this button */
    private BasicDropDownButtonModel model;
    
    /** a handler reacting if this button is pressed */
    private BasicDropDownButtonHandler handler;
    
    /** the icon to show for the area in which the popup-menu could be opened */
    private Icon dropIcon;
    /** a disabled version of {@link #dropIcon} */
    private Icon disabledDropIcon;
    
    /**
     * Creates a new button
     * @param theme the theme which delivers the colors used to paint this button
     * @param handler a handler used to announce that this button is clicked
     */
    public RoundDropDownButton( BubbleTheme theme, BasicDropDownButtonHandler handler ){
        animation = new BubbleColorAnimation( theme );
        animation.putColor( "background", "dropdown" );
        animation.addTask( new Runnable(){
            public void run() {
                repaint();
            }
        });
        
        this.handler = handler;
        dropIcon = createDropIcon();
        
        model = new BasicDropDownButtonModel( this, handler ){
            @Override
            public void changed() {
                updateColors();
                repaint();
            }
            
            @Override
            protected boolean inDropDownArea( int x, int y ) {
                return overDropIcon( x, y );
            }
        };
    }
    
    public BasicDropDownButtonModel getModel() {
        return model;
    }
    
    @Override
    public void updateUI() {
        disabledDropIcon = null;
        
        super.updateUI();
        
        if( handler != null )
            handler.updateUI();
    }
    
    @Override
    public Dimension getPreferredSize() {
        if( isPreferredSizeSet() )
            return super.getPreferredSize();
        
        Dimension icon = model.getMaxIconSize();
        int w = Math.max( icon.width, 10 );
        int h = Math.max( icon.height, 10 );
        
        if( model.getOrientation().isHorizontal() )
            return new Dimension( (int)(1.5 * w + 1 + 1.5*dropIcon.getIconWidth()), (int)(1.5 * h));
        else
            return new Dimension( (int)(1.5 * w), (int)(1.5 * h + 1 + 1.5 * dropIcon.getIconHeight()) );
    }
    
    @Override
    public boolean contains( int x, int y ){
    	if( !super.contains( x, y ))
    		return false;
    	
    	int w = getWidth();
    	int h = getHeight();
    	RoundRectangle2D rect;
    	
    	if( model.getOrientation().isHorizontal() )
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, h, h );
    	else
    		rect = new RoundRectangle2D.Double( 0, 0, w, h, w, w );
    	
    	return rect.contains( x, y );
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        Icon drop = dropIcon;
        if( !isEnabled() ){
            if( disabledDropIcon == null )
                disabledDropIcon = DockUtilities.disabledIcon( this, dropIcon );
            drop = disabledDropIcon;
        }
        
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        
        Dimension size = model.getMaxIconSize();
        Icon icon = model.getPaintIcon();
        
        int iconWidth = size.width < 10 ? 10 : size.width;
        int iconHeight = size.height < 10 ? 10 : size.height;
        
        int dropIconWidth = drop == null ? 5 : drop.getIconWidth();
        int dropIconHeight = drop == null ? 5 : drop.getIconHeight();
        
        if( model.getOrientation().isHorizontal() ){
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, h, h );
            
            g2.setColor( animation.getColor( "mouse" ) );
            int mx = x + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (w - 1.25 * dropIconWidth) );
            g2.drawLine( mx, y+1, mx, y+h-2 );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, (int)(x + 0.25 * iconWidth ), y+(h-iconHeight)/2 );
            }
            if( drop != null )
            	drop.paintIcon( this, g, (int)(x + w - 1.25 * dropIconWidth), y+(h-dropIconHeight)/2 );
        }
        else{
            g2.setColor( animation.getColor( "background" ) );
            g2.fillRoundRect( x, y, w, h, w, w );
            
            g2.setColor( animation.getColor( "mouse" ) );
            int my = y + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (h - 1.25 * dropIconHeight) );
            g2.drawLine( x+1, my, x+w-2, my );
            
            
            if( icon != null ){
                icon.paintIcon( this, g, x+(w-iconWidth)/2, (int)(y + 0.25*iconHeight));
            }
            if( drop != null )
            	drop.paintIcon( this, g, x + ( w - dropIconWidth ) / 2, (int)(y+h-1.25*dropIconHeight) );
        }
        
        g2.dispose();
    }
    
    /**
     * Creates an icon that is shown in the smaller subbutton of this button.
     * @return the icon
     */
    protected Icon createDropIcon(){
        return new Icon(){
            public int getIconHeight(){
                return 7;
            }
            public int getIconWidth(){
                return 7;
            }
            public void paintIcon( Component c, Graphics g, int x, int y ){
                x++;
                g.setColor( getForeground() );
                g.drawLine( x, y+1, x+4, y+1 );
                g.drawLine( x+1, y+2, x+3, y+2 );
                g.drawLine( x+2, y+3, x+2, y+3 );
            }
        };
    }
    
    /**
     * Tells whether the point x,y is over the icon that represents the drop-area.
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return <code>true</code> if pressing the mouse at that location would
     * open a popup menu
     */
    public boolean overDropIcon( int x, int y ){
    	if( !contains( x, y ))
    		return false;
    	
        int rx = 0;
        int ry = 0;
        int rw = getWidth();
        int rh = getHeight();
        
        Dimension icon = model.getMaxIconSize();
        
        int iconWidth = icon.width < 10 ? 10 : icon.width;
        int iconHeight = icon.height < 10 ? 10 : icon.height;
        
        int dropIconWidth = dropIcon == null ? 5 : dropIcon.getIconWidth();
        int dropIconHeight = dropIcon == null ? 5 : dropIcon.getIconHeight();
        
        if( model.getOrientation().isHorizontal() ){
        	int mx = rx + (int)( 0.5 * 1.25 * iconWidth + 0.5 * (rw - 1.25 * dropIconWidth) );
        	return x >= mx;
        }
        else{
        	int my = ry + (int)( 0.5 * 1.25 * iconHeight + 0.5 * (rh - 1.25 * dropIconHeight) );
        	return y >= my;
        }
    }

    /**
     * Updates the colors of the animation.
     */
    public void updateColors(){
        String postfix = "";
        
        boolean selected = model.isSelected();
        boolean enabled = model.isEnabled();
        boolean pressed = model.isMousePressed();
        boolean entered = model.isMouseInside();
        boolean mouseOverDrop = model.isMouseOverDropDown();
        
        if( selected )
            postfix = ".selected";
        
        if( enabled )
            postfix += ".enabled";
        
        String mouse;
        if( mouseOverDrop && enabled )
            mouse = "dropdown.line";
        else
            mouse = "dropdown";
        
        if( pressed && enabled ){
            animation.putColor( "background", "dropdown.pressed" + postfix );
            animation.putColor( "mouse", mouse + ".pressed" + postfix );
        }
        else if( entered && enabled ){
            animation.putColor( "background", "dropdown.mouse" + postfix );
            animation.putColor( "mouse", mouse + ".mouse" + postfix );
        }
        else{
            animation.putColor( "background", "dropdown" + postfix );
            animation.putColor( "mouse", mouse + postfix );
        }
    }
}