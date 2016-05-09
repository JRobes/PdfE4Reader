/*******************************************************************************
 * Copyright (c) 2010 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.demo.simpleide.editor;

import org.eclipse.core.runtime.IStatus;

public interface IInput {
	public static final String DIRTY = IInput.class.getName() + ".dirty";
	
	public interface Listener {
		public void propertyChanged(String property, Object oldValue, Object newValue);
	}
	
	public void addListener(Listener listener);
	public void removeListener(Listener listener);
	
	public IStatus save();
}
