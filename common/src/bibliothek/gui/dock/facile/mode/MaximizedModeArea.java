/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.mode.AffectedSet;

/**
 * The parent of a dockable that is maximized. The {@link MaximizedMode}, which
 * is feed with this areas, assumes that a {@link MaximizedModeArea} is also
 * some other kind of area (e.g. a {@link NormalModeArea}).
 * @author Benjamin Sigg
 */
public interface MaximizedModeArea extends ModeArea{
	/**
	 * Informs this area that it is now managed by <code>mode</code>.
	 * @param mode the new mode, can be <code>null</code> to inform this
	 * area that it is no longer managed
	 * @throws IllegalStateException if already connected
	 */
	public void connect( MaximizedMode<? extends MaximizedModeArea> mode );
	
	/**
	 * This method is called before the method {@link LocationMode#apply(Dockable, Location, AffectedSet) apply}
	 * is executed of a {@link LocationMode} that is not the owner of this area. The element 
	 * <code>event.getDockable()</code> may or may not be a child of this station. This method is called
	 * before {@link #onApply(LocationModeEvent, Dockable)} is called.
	 * @param event detailed informaion about the event
	 * @return a piece of code executed once <code>apply</code> has finished its job 
	 */
	public Runnable onApply( LocationModeEvent event );
	
	/**
	 * This method is called before the method {@link LocationMode#apply(Dockable, Location, AffectedSet) apply}
	 * is executed of a {@link LocationMode} that is not the owner of this area. The element
	 * <code>event.getDockable()</code> is a direct or indirect child of this area and maximized. The 
	 * {@link MaximizedMode} suggests to use <code>replacement</code> as direct child
	 * once the old element has been removed. This method must decide how this area
	 * reacts on the pending change, e.g. set its maximized dockable to <code>null</code> and
	 * later re-maximize. The result of this method is a {@link Runnable} which will be executed
	 * once the <code>apply</code> method is finished.<br>
	 * This method is called after {@link #onApply(LocationModeEvent)} is called.
	 * @param event detailed information about the event 
	 * @param replacement the suggested new maximized element
	 * @return a piece of code executed once <code>apply</code> has finished its job
	 */
	public Runnable onApply( LocationModeEvent event, Dockable replacement );
	
	/**
	 * This method is called by {@link MaximizedMode} just before the mode is applied 
	 * to <code>dockable</code>.
	 * @param dockable the element which gets maximized
	 * @param affected collects dockables which might change their mode
	 */
	public void prepareApply( Dockable dockable, AffectedSet affected );
	
	/**
	 * Tells this parent to show <code>dockable</code> maximized,
	 * only one dockable may be maximized at any time.
	 * @param dockable the maximized element, <code>null</code> to indicate
	 * that no element should be maximized.
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.
	 */
	public void setMaximized( Dockable dockable, AffectedSet set );
	
	/**
	 * Gets the currently maximized element.
	 * @return the currently maximized dockable, can be <code>null</code>
	 */
	public Dockable getMaximized();
	
	/**
	 * Tells whether this area is representing <code>station</code>. It is
	 * legitimate for an area to represent more than one or no station at all.
	 * @param station some station
	 * @return <code>true</code> if this represents <code>station</code>
	 */
	public boolean isRepresenting( DockStation station );
}