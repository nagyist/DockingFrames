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
package bibliothek.gui.dock.common.intern.color;

import java.awt.Color;

import bibliothek.extension.gui.dock.theme.bubble.BubbleStackDockComponent;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.ColorMap;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * A color transmitter that connects {@link TitleColor}s with the common-project
 * and the {@link ColorMap} of the {@link CDockable}s.
 * @author Benjamin Sigg
 */
public abstract class TitleColorTransmitter extends ColorTransmitter<TitleColor>{
    private ColorManager manager;
    private String[] keys;
    
    /**
     * Creates a new transmitter.
     * @param keys the keys of the colors that are handled by this transmitter
     * @param manager the source of all colors
     */
    public TitleColorTransmitter( ColorManager manager, String... keys ){
        super( keys );
        this.keys = keys;
        this.manager = manager;
    }
    
    /**
     * Changes a background color such that it can be used on a {@link BubbleStackDockComponent}
     * @param source the original color
     * @param key the key for which the color is needed
     * @return the new color
     */
    protected abstract Color convert( Color source, String key );
    
    /**
     * Changes a background color such that it can be used on a {@link BubbleStackDockComponent} 
     * @param source the original color
     * @param key the key for which the color is needed, can only be one
     * of the focused kind
     * @return the new color
     */
    protected abstract Color convertFocused( Color source, String key );
    
    /**
     * Tells whether <code>id</code> represents a color that is used for
     * the foreground.
     * @param id some id
     * @return <code>true</code> if the color is used in the foreground
     */
    protected abstract boolean isForeground( String id );
    
    /**
     * Tells whether <code>id</code> represents a color that is used on
     * focused tabs.
     * @param id some id
     * @return <code>true</code> if the color is used on focused tabs
     */
    protected abstract boolean isFocused( String id );
    
    @Override
    protected Color get( Color color, String id, TitleColor observer ) {
        CDockable dockable = getDockable( observer );
        if( dockable != null ){
            return get( color, id, dockable );
        }
        
        return color;
    }

    @Override
    protected CDockable getDockable( TitleColor observer ) {
        Dockable dockable = observer.getTitle().getDockable();
        if( dockable instanceof CommonDockable )
            return ((CommonDockable)dockable).getDockable();
        
        return null;
    }
    
    
    /**
     * Searches the color <code>id</code> for <code>dockable</code>.
     * @param color the color to be returned if the search for <code>id</code> fails.
     * @param id the identifier of the color to search
     * @param dockable the element for which the color will be used
     * @return some color
     */
    protected Color get( Color color, String id, CDockable dockable ){
        Color check = null;
        ColorMap colors = dockable.getColors();
        
        boolean foreground = isForeground( id );
        boolean focused = isFocused( id );
        
        if( foreground ){
            if( focused ){
                check = colors.getColor( ColorMap.COLOR_KEY_TITLE_FOREGROUND_FOCUSED );
            }
            
            if( check == null ){
                check = colors.getColor( ColorMap.COLOR_KEY_TITLE_FOREGROUND );
            }
        }
        
        if( check == null ){
            if( focused ){
                check = colors.getColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED );
                if( check != null )
                    check = convertFocused( check, id );
            }
            
            if( check == null ){
                check = colors.getColor( ColorMap.COLOR_KEY_TITLE_BACKGROUND );
                if( check != null )
                    check = convert( check, id );
            }
        }
        
        if( check != null )
            return check;
        
        return color;
    }
    
    @Override
    protected void update( CDockable dockable, String key, Color color ) {
        boolean change = ColorMap.COLOR_KEY_TITLE_BACKGROUND.equals( key ) ||
            ColorMap.COLOR_KEY_TITLE_FOREGROUND.equals( key ) ||
            ColorMap.COLOR_KEY_TITLE_BACKGROUND_FOCUSED.equals( key ) ||
            ColorMap.COLOR_KEY_TITLE_FOREGROUND_FOCUSED.equals( key );

        if( change ){
            for( String check : keys )
                set( check, get( manager.get( check ), check, dockable ), dockable );
        }
    }

}