package org.eclipse.e4.demo.simpleide.editor.internal;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.e4.demo.simpleide.editor.IInput;

public abstract class AbstractInput implements IInput {
	private ListenerList listeners = new ListenerList();
	private boolean dirty = false;
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void fireDirtyChange(boolean oldValue, boolean newValue) {
		this.dirty = newValue;
		for( Object l : listeners.getListeners() ) {
			((Listener)l).propertyChanged(DIRTY, oldValue, newValue);
		}
	}
	
	protected void setDirty(boolean dirty) {
		if( dirty != this.dirty ) {
			fireDirtyChange(this.dirty, this.dirty = dirty);
		}
	}
	
	protected boolean isDirty() {
		return dirty;
	}
}
