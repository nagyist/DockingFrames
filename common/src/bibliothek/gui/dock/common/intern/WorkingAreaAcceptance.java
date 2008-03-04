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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CWorkingArea;

/**
 * A {@link DockAcceptance} ensuring that the {@link CDockable#getWorkingArea()}
 * property is respected.
 * @author Benjamin Sigg
 */
public class WorkingAreaAcceptance implements DockAcceptance {
    /** access to the inner parts of the {@link CControl} */
    private CControlAccess control;
    
    /**
     * Creates a new acceptance
     * @param control access to the {@link CControl}
     */
    public WorkingAreaAcceptance( CControlAccess control ){
        this.control = control;
    }
    
    public boolean accept( DockStation parent, Dockable child ) {
        if( control.getStateManager().isOnTransition() )
            return true;
        
        if( control.getStateManager().childsExtendedMode( parent ) == CDockable.ExtendedMode.NORMALIZED ){
            CWorkingArea area = searchArea( parent );
            return match( area, child );
        }
        return true;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ) {
        if( control.getStateManager().isOnTransition() )
            return true;
        
        if( control.getStateManager().childsExtendedMode( parent ) == CDockable.ExtendedMode.NORMALIZED ){
            CWorkingArea area = searchArea( parent );
            return match( area, next );
        }
        return true;
    }
    
    /**
     * Searches the first {@link CWorkingArea} in the path to the root.
     * @param element some element
     * @return the first {@link CWorkingArea} that occurs on the path from
     * <code>element</code> to the root.
     */
    private CWorkingArea searchArea( DockElement element ){
        Dockable dockable = element.asDockable();
        while( dockable != null ){
            if( dockable instanceof CommonDockable ){
                CDockable fdock = ((CommonDockable)dockable).getDockable();
                if( fdock instanceof CWorkingArea )
                    return (CWorkingArea)fdock;
            }
            DockStation station = dockable.getDockParent();
            dockable = station == null ? null : station.asDockable();
        }
        return null;
    }
    
    /**
     * Checks all {@link CDockable}s and compares their
     * {@link CDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param dockable the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( CWorkingArea area, Dockable dockable ){
        if( dockable instanceof CommonDockable ){
            CDockable fdockable = ((CommonDockable)dockable).getDockable();
            CWorkingArea request = fdockable.getWorkingArea();
            if( request != area )
                return false;
            
            if( fdockable instanceof CWorkingArea )
                return true;
        }
        
        DockStation station = dockable.asDockStation();
        if( station != null )
            return match( area, station );
        else
            return true;
    }
    
    /**
     * Checks all {@link CDockable}s and compares their
     * {@link CDockable#getWorkingArea() working area}
     * with <code>area</code>.
     * @param area a possible new parent
     * @param station the root of the tree of elements to test
     * @return <code>true</code> if all elements have <code>area</code> as
     * preferred parent, <code>false</code> otherwise
     */
    private boolean match( CWorkingArea area, DockStation station ){
        for( int i = 0, n = station.getDockableCount(); i < n; i++ ){
            boolean result = match( area, station.getDockable( i ));
            if( !result )
                return false;
        }
        return true;
    }
}