package bibliothek.gui.dock.support.menu;

import java.awt.Component;
import java.util.List;

import javax.swing.JPopupMenu;

/**
 * A piece which envelops another piece with separators.
 * @author Benjamin Sigg
 */
public class SeparatingMenuPiece extends MenuPiece{
	/** whether to make a separator above this menupiece or not */
    private boolean topSeparator = false;
    /** whether to make a separator below this menupiece or not */
    private boolean bottomSeparator = false;
    /** whether to show a separator when there are no items in this menupiece */
    private boolean emptySeparator = false;
    
    /** the separator shown at the top */
    private Component separatorTop = null;
    /** the separator shown at the bottom */
    private Component separatorBottom = null;
    
    /** the source of items */
    private MenuPiece piece;
    
    /**
     * Creates a new piece
     */
    public SeparatingMenuPiece(){
    	this( null );
    }

    
    /**
     * Creates a new piece
     * @param topSeparator whether to show a separator at the top
     * @param emptySeparator whether to show a separator if <code>piece</code> is empty
     * @param bottomSeparator whether to show a separator at the bottom
     */
    public SeparatingMenuPiece( boolean topSeparator, boolean emptySeparator, boolean bottomSeparator ){
    	this( null, topSeparator, emptySeparator, bottomSeparator );
    }
    
    /**
     * Creates a new piece
     * @param piece the piece which might be covered by separators
     */
    public SeparatingMenuPiece( MenuPiece piece ){
    	this( piece, false, false, false );
    }
    
    /**
     * Creates a new piece
     * @param piece the piece which might be covered by separators
     * @param topSeparator whether to show a separator at the top
     * @param emptySeparator whether to show a separator if <code>piece</code> is empty
     * @param bottomSeparator whether to show a separator at the bottom
     */
    public SeparatingMenuPiece( MenuPiece piece, boolean topSeparator, boolean emptySeparator, boolean bottomSeparator ){
    	setPiece( piece );
    	setTopSeparator( topSeparator );
    	setEmptySeparator( emptySeparator );
    	setBottomSeparator( bottomSeparator );
    }
    
    
    /**
     * Gets the piece which is embraced by separators.
     * @return the piece
     */
    public MenuPiece getPiece(){
		return piece;
	}
    
    /**
     * Sets the piece which will be embraced by separators.
     * @param piece the child of this piece
     */
    public void setPiece( MenuPiece piece ){
    	if( this.piece != piece ){
    		if( this.piece != null ){
    			remove( piece, 0, piece.getItemCount() );
    			this.piece.setParent( null );
    		}
    		
        	this.piece = piece;
        	
        	if( this.piece != null ){
        		piece.setParent( this );
        		insert( piece, 0, piece.items() );
        	}
    	}
	}
    
    @Override
    protected int getItemCount(){
    	return piece.getItemCount() + getSeparatorCount();
    }
    
    @Override
    protected void insert( MenuPiece child, int index, Component... component ){
    	MenuPiece parent = getParent();
    	if( parent != null && component.length > 0 ){
	    	int count = piece.getItemCount() - component.length;
	    	if( count == 0 ){
	    		if( emptySeparator  )
	    			parent.remove( this, 0, 1 );
	    		
	    		if( bottomSeparator )
	    			parent.insert( this, 0, getBottomSeparator() );
	    		
	    		if( topSeparator )
	    			parent.insert( this, 0, getTopSeparator() );
	    	}
	    	
    		if( topSeparator )
	    		index++;
	    	
	    	parent.insert( this, index, component );
    	}
    }
    
    @Override
    protected void remove( MenuPiece child, int index, int length ){
    	MenuPiece parent = getParent();
    	if( parent != null && length > 0 ){
    		if( topSeparator )
    			index++;
    	
    		parent.remove( this, index, length );
    		
    		if( child.getItemCount() == 0 ){
    			if( topSeparator && bottomSeparator )
    				parent.remove( this, 0, 2 );
    			
    			if( topSeparator || bottomSeparator )
    				parent.remove( this, 0, 1 );
    			
    			if( emptySeparator )
    				parent.insert( this, 0, getEmptySeparator() );
    		}
    	}
    }
    
    @Override
    protected void fill( List<Component> items ){
    	if( piece == null || piece.getItemCount() == 0 ){
    		if( emptySeparator )
    			items.add( getEmptySeparator() );
    	}
    	else{
    		if( topSeparator )
    			items.add( getTopSeparator() );
    		
    		piece.fill( items );
    		
    		if( bottomSeparator )
    			items.add( getBottomSeparator() );
    	}
    }
    
    /**
     * Gets the number of separators which were added by this piece.
     * @return the number of separators
     */
    protected int getSeparatorCount(){
    	if( piece.getItemCount() > 0 ){
    		if( topSeparator && bottomSeparator )
    			return 2;
    		
    		if( topSeparator || bottomSeparator )
    			return 1;
    		
    		return 0;
    	}
    	else{
    		if( emptySeparator )
    			return 1;
    		
    		return 0;
    	}
    }
    
    /**
     * Tells whether there is a separator below this piece.
     * @return <code>true</code> if there is a separator
     * @see #setBottomSeparator(boolean)
     */
    public boolean isBottomSeparator() {
        return bottomSeparator;
    }
    
    /**
     * Sets whether there should be a separator added to the menu after
     * the contents described in this piece. Note that there might not be
     * any separator if this piece is empty.
     * @param bottomSeparator <code>true</code> if there should be a separator
     */
    public void setBottomSeparator( boolean bottomSeparator ) {
        if( this.bottomSeparator != bottomSeparator ){
            this.bottomSeparator = bottomSeparator;
            putUpSeparators();
            
            MenuPiece parent = getParent();
            if( parent != null ){
	            if( piece != null && piece.getItemCount() > 0 ){
	                if( bottomSeparator ){
	                	parent.insert( this, getItemCount(), getBottomSeparator() );
	                }
	                else{
	                	parent.remove( this, getItemCount()-1, 1 );
	                }
	            }
            }
        }
    }
    
    /**
     * Tells whether there should be a single separator shown when this
     * piece is empty.
     * @return <code>true</code> if there is a separator
     */
    public boolean isEmptySeparator() {
        return emptySeparator;
    }
    
    /**
     * Sets whether there should be a separator shown when this piece
     * is empty.
     * @param emptySeparator <code>true</code> if a separator should be
     * made visible
     */
    public void setEmptySeparator( boolean emptySeparator ) {
        if( this.emptySeparator != emptySeparator ){
            this.emptySeparator = emptySeparator;
            putUpSeparators();
            
            MenuPiece parent = getParent();
            if( parent != null ){
	            if( piece == null || piece.getItemCount() == 0 ){
	                if( emptySeparator ){
	                	parent.insert( this, 0, getEmptySeparator() );
	                }
	                else{
	                	parent.remove( this, 0, 1 );
	                }
	            }
            }
        }
    }
    
    /**
     * Tells whether there is a separator shown above the content of this
     * piece.
     * @return <code>true</code> if there is a separator
     */
    public boolean isTopSeparator() {
        return topSeparator;
    }
    
    /**
     * Sets whether there should be a separator shown above the content of
     * this piece. Note that there might not be any separator if this piece
     * is empty.
     * @param topSeparator <code>true</code> if the separator should be shown
     */
    public void setTopSeparator( boolean topSeparator ) {
        if( this.topSeparator != topSeparator ){
            this.topSeparator = topSeparator;
            putUpSeparators();
            
            MenuPiece parent = getParent();
            if( parent != null ){
	            if( piece != null && piece.getItemCount() > 0 ){
	            	if( topSeparator ){
	                	parent.insert( this, 0, getTopSeparator() );
	                }
	                else{
	                	parent.remove( this, 0, 1 );
	                }
	            }
            }
        }
    }
    
    /**
     * Gets the separator which is shown at the top.
     * @return the separator or <code>null</code> if no separator is needed
     */
    private Component getTopSeparator(){
    	return separatorTop;
    }

    /**
     * Gets the separator which is shown at the bottom.
     * @return the separator or <code>null</code> if no separator is needed
     */
    private Component getBottomSeparator(){
    	return separatorBottom;
    }
    
    /**
     * Gets the separator which is shown at when this piece is empty.
     * @return the separator or <code>null</code> if no separator is needed
     */
    private Component getEmptySeparator(){
    	if( separatorTop != null )
    		return separatorTop;
    	else
    		return separatorBottom;
    }
    
    /**
     * Makes sure that there all separators needed for the menu are available. 
     */
    private void putUpSeparators(){
    	boolean top = topSeparator;
    	boolean bottom = bottomSeparator;
    	
    	if( emptySeparator ){
    		if( !top && !bottom )
    			top = true;
    	}
    	
    	if( top && separatorTop == null )
    		separatorTop = new JPopupMenu.Separator();
    	else if( !top && separatorTop != null )
    		separatorTop = null;
    	
    	if( bottom && separatorBottom == null )
    		separatorBottom = new JPopupMenu.Separator();
    	else if( !top && separatorBottom != null )
    		separatorBottom = null;
    }
}
