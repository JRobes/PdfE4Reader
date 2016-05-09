package com.alestis.pdfreader.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;



public class PDFReaderPreferences extends FieldEditorPreferencePage {

	public PDFReaderPreferences(){
		super(GRID);
		//noDefaultButton();

	}
	
	@Override
	protected void createFieldEditors() {
		addField(
				new BooleanFieldEditor(
						"pseudoContinuousScrolling",
					"&Enable pseudo continuous scrolling",
					getFieldEditorParent()));
		/*
		 * TODO to add the JPedal Renderer....	
		addField(new RadioGroupFieldEditor("pdfRenderer", "PDF renderer (document must be reopend for change to take effect)", 1, 
					new String[][]{{"SUN renderer (faster)", ""+1},  //$NON-NLS-2$
					{"JPedal renderer (higher compatibility)", ""+2},  //$NON-NLS-2$
					{"JPedal renderer for some document types that can not be displayed by SUN renderer, otherwise SUN renderer", 
						""+3}} //$NON-NLS-1$
					, getFieldEditorParent(), true));
		*/
	}

}
